package org.eljaiek.jmira.app.controller;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.*;
import org.eljaiek.jmira.app.controller.util.*;
import org.eljaiek.jmira.app.model.PackageModel;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.app.util.AlertHelper;
import org.eljaiek.jmira.app.util.FileSystemHelper;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.ViewMode;
import org.eljaiek.jmira.app.view.Views;
import org.eljaiek.jmira.core.*;
import org.eljaiek.jmira.data.model.DebPackage;
import org.eljaiek.jmira.data.repositories.PackagesFileProvider;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author eduardo.eljaiek
 */
@Controller
public class HomeController implements Initializable, CloseRequestHandler, PackagesFileProvider {

    private static final String TITLE_TEMPLATE = "JMira 1.0 - %s";

    private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance(Locale.getDefault());

    private final PaginationHelper paginationHelper;

    private final Function<RepositoryModel, Void> open;

    private final Timer timer;

    private final ImageView startDownloadIcon;

    private final ImageView cancelDownloadIcon;

    private final BooleanProperty visibleLabels = new SimpleBooleanProperty(false);

    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private MessageResolver messages;

    @Autowired
    private RepositoryService repositories;

    @Autowired
    private PackageService packages;

    private DownloadScheduler downScheduler;

    @FXML
    private ListView<PackageModel> packagesListView;

    @FXML
    private ProgressBar homeIndicator;

    @FXML
    private ProgressBar downIndicator;

    @FXML
    private BorderPane mainPane;

    @FXML
    private BorderPane listViewPane;

    @FXML
    private Button openBtn;

    @FXML
    private Button newBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button syncBtn;
    @FXML
    private Button downBtn;

    @FXML
    private Pagination pagination;

    @FXML
    private Label availablePackages;

    @FXML
    private Label downPackages;

    private RepositoryModel current;

    public HomeController() {
        paginationHelper = new PaginationHelper();
        startDownloadIcon = new ImageView("/org/eljaiek/jmira/app/view/resources/icons/downloadRepo48.png");
        cancelDownloadIcon = new ImageView("/org/eljaiek/jmira/app/view/resources/icons/downloadCancel48.png");
        timer = FxTimer.runPeriodically(Duration.ofMillis(2000), () -> updateToolBar());
        timer.stop();
        open = (RepositoryModel t) -> {
            open(t);
            timer.restart();
            return null;
        };
    }

    public boolean isVisibleLabels() {
        return visibleLabels.get();
    }

    public void setVisibleLabels(boolean value) {
        visibleLabels.set(value);
    }

    public BooleanProperty visibleLabelsProperty() {
        return visibleLabels;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        downScheduler = new DownloadScheduler(packages);
        listViewPane.getChildren().remove(pagination);

        packagesListView.setPlaceholder(new Label("No Content In List"));
        packagesListView.setCellFactory((ListView<PackageModel> param) -> new PackageListCell());

        pagination.currentPageIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            paginationHelper.setPage(newValue.intValue());
            ObservableList<PackageModel> page = paginationHelper.createPage();
            packagesListView.setItems(page);
        });

        EventHandler onDownloadStop = evt -> {
            mainPane.getChildren().remove(downScheduler.getControl());
            mainPane.setCenter(listViewPane);
            downBtn.setGraphic(startDownloadIcon);
            downBtn.setOnAction(this::startDownload);
            disabledOnDownload(false);
        };

        downScheduler.setOnCancelled(onDownloadStop);
        downScheduler.setOnDone(onDownloadStop);

        downScheduler.setOnLoadSucceeded(evt -> {
            downBtn.setGraphic(cancelDownloadIcon);
            downBtn.setOnAction(e -> downScheduler.cancel());
            downBtn.setDisable(false);
        });

        downScheduler.setOnFail(evt -> {
            downBtn.setDisable(false);
            onDownloadStop.handle(evt);
            Exception error = ((DownloadFailEvent) evt).getError();
            AlertHelper.error(null, messages.getMessage("download.scheduler.fail"), error.getMessage(), error);
        });

        downScheduler.setOnDone(evt -> {
            downBtn.setDisable(false);
            onDownloadStop.handle(evt);
            AlertHelper.info(null, messages.getMessage("download.scheduler.done"), "", true);
        });
    }

    @FXML
    void newRepository(ActionEvent event) {
        RepositoryModel model = new RepositoryModel();
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        showRepositoryView(messages.getMessage("repository.newDialog.title"), window, model, ViewMode.CREATE);
        ((Stage) window).setTitle(String.format(TITLE_TEMPLATE, model.getName()));
    }

    @FXML
    void openRepository(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File dir = chooser.showDialog(window);

        if (dir != null) {
            OpenService service = new OpenService(dir.getAbsolutePath(), repositories);
            service.setOnSucceeded(evt -> {
                disableOnOpen(false);
                updateView();
            });
            service.setOnFailed(evt -> {
                String error = messages.getMessage("repository.open.errorContext", service.getException().getMessage());
                LOG.error(error, service.getException());
                AlertHelper.error(window, messages.getMessage("repository.open.errorHeader"), error, service.getException());
            });

            service.setOnOpen(evt -> current = evt.getModel());

            AlertHelper.progress(messages.getMessage("repository.open.progressHeader"),
                    messages.getMessage("repository.open.progressContext"),
                    service);
        }
    }

    @FXML
    void editRepository(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        showRepositoryView(current.getName(), window, new RepositoryModel(current), ViewMode.EDIT);
        ((Stage) window).setTitle(String.format(TITLE_TEMPLATE, current.getName()));
    }

    @FXML
    void synchronize(ActionEvent event) {
        timer.stop();
        final Service service = new SynchronizeService(current, repositories);
        service.setOnSucceeded(evt -> {
            updateView();
            downBtn.setDisable(false);
        });

        service.setOnFailed(evt -> {
            String error = messages.getMessage("repository.sync.errorContext", current.getName());
            LOG.error(error, service.getException());
            AlertHelper.error(null, messages.getMessage("repository.sync.errorHeader"), null, new RuntimeException(error, service.getException()));
            timer.restart();
        });

        AlertHelper.progress(messages.getMessage("repository.sync.progressHeader"),
                messages.getMessage("repository.sync.progressContext", current.getName()),
                service);
    }

    @FXML
    void startDownload(ActionEvent event) {
        downScheduler.downloadedProperty().bindBidirectional(current.downloadedProperty());
        downScheduler.downloadedCountProperty().bindBidirectional(current.downloadedCountProperty());
        disabledOnDownload(true);
        downBtn.setDisable(true);
        mainPane.getChildren().remove(listViewPane);
        mainPane.setCenter(downScheduler.getControl());
        downScheduler.start();

        File f = new File(current.getHome());

        if (f.getFreeSpace() < current.getSize() - current.getDownloaded()) {
            LOG.warn(messages.getMessage("diskSpace.warn", current.getHome()));
        }
    }

    @FXML
    final void exit(ActionEvent evt) {
        Window window = ((Node) evt.getTarget()).getScene().getWindow();
        close(window);
    }

    private void disableOnOpen(boolean disable) {
        editBtn.setDisable(disable);
        syncBtn.setDisable(disable);
        downBtn.setDisable(disable);
    }

    private void disabledOnDownload(boolean disable) {
        openBtn.setDisable(disable);
        newBtn.setDisable(disable);
        editBtn.setDisable(disable);
        syncBtn.setDisable(disable);
    }

    private void showRepositoryView(String title, Window owner, RepositoryModel model, ViewMode mode) {
        Map<String, Object> bindings = new HashMap<>(1);
        bindings.put("model", model);
        bindings.put("acceptAction", open);
        bindings.put("viewMode", mode);
        Parent parent = (Parent) viewLoader.load(Views.EDIT_REPOSITORY, bindings);
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.setWidth(489);
        stage.setHeight(272);
        stage.showAndWait();
    }

    private void open(RepositoryModel model) {
        try {
            repositories.save(model.getRepository());
            current = model;
            disableOnOpen(false);
        } catch (RepositoryAccessException ex) {
            AlertHelper.error(null, messages.getMessage("repository.createError"), ex.getMessage(), ex);
        }
    }

    private void updateView() {
        pagination.setPageCount(paginationHelper.getPageCount());
        pagination.setCurrentPageIndex(0);
        packagesListView.setItems(paginationHelper.createPage());

        if (current.getPackagesCount() > paginationHelper.pageSize) {
            listViewPane.setBottom(pagination);
        } else {
            listViewPane.getChildren().remove(pagination);
        }

        visibleLabels.set(true);
        updateToolBar();
        timer.restart();
    }

    private void updateToolBar() {
        visibleLabels.set(true);
        availablePackages.setText(NUMBER_FORMAT.format(current.getPackagesCount()));
        downPackages.setText(NUMBER_FORMAT.format(current.getDownloadedCount()));

        double percent = FileSystemHelper.getUsedSpacePercent(current.getHome());
        homeIndicator.setProgress(percent * 0.01);

        if (current.getDownloaded() != 0) {
            double downPercent = current.getDownloaded() * 100 / current.getSize();
            downIndicator.setProgress(downPercent * 0.1);
        }
    }

    @Override
    public final Optional<File> getFile() {
        File file = null;

        if (current != null) {
            file = new File(String.join("/", current.getHome(), NamesUtils.PACKAGES_DAT));
        }

        return Optional.ofNullable(file);
    }

    @Override
    public void close(Window window) {
        try {

            if (current != null) {
                repositories.save(current.getRepository());
            }

        } catch (RepositoryAccessException | IllegalArgumentException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        Platform.exit();
    }

    private class PaginationHelper {

        private static final int DEFAULT_SIZE = 50;

        private final int pageSize;

        private int pageIndex = 0;

        public PaginationHelper() {
            this(DEFAULT_SIZE);
        }

        public PaginationHelper(int pageSize) {
            this.pageSize = pageSize;
        }

        public void setPage(int page) {
            this.pageIndex = page;
        }

        public int getPageCount() {
            return current.getPackagesCount() / pageSize;
        }

        public ObservableList<PackageModel> createPage() {
            List<DebPackage> list = packages.list(pageIndex * pageSize, pageSize);
            List<PackageModel> models = list.stream().map(PackageModel::create).collect(Collectors.toList());
            return FXCollections.observableArrayList(models);
        }
    }
}

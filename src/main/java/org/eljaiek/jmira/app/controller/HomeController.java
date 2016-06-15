package org.eljaiek.jmira.app.controller;

import org.eljaiek.jmira.app.controls.PackageListCell;
import org.eljaiek.jmira.app.model.RepositoryModel;
import org.eljaiek.jmira.app.model.PackageModel;
import org.eljaiek.jmira.core.logs.MessageResolver;
import org.eljaiek.jmira.core.util.NamesUtils;
import org.eljaiek.jmira.app.events.CloseRequestHandler;
import org.eljaiek.jmira.app.controls.DownloadScheduler;
import org.eljaiek.jmira.app.controls.DownloadFailEvent;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.eljaiek.jmira.app.util.AlertHelper;
import org.eljaiek.jmira.app.util.FileSystemHelper;
import org.eljaiek.jmira.app.view.ViewLoader;
import org.eljaiek.jmira.app.view.ViewMode;
import org.eljaiek.jmira.app.view.Views;
import org.eljaiek.jmira.core.*;
import org.eljaiek.jmira.core.model.DebPackage;
import org.eljaiek.jmira.core.io.PackagesFileProvider;
import org.reactfx.util.FxTimer;
import org.reactfx.util.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.IOException;
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

    private final BooleanProperty visibleRepoStatus = new SimpleBooleanProperty(false);

    private final BooleanProperty visibleDownStatus = new SimpleBooleanProperty(false);

    @Autowired
    private ViewLoader viewLoader;

    @Autowired
    private MessageResolver messages;

    @Autowired
    private RepositoryService repositories;

    @Autowired
    private PackageService packages;

    @Autowired
    private Environment env;

    @Autowired
    private DownloadScheduler downloadScheduler;

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
    private Label availableCount;

    @FXML
    private Label successCount;

    @FXML
    private Label errorsCount;

    private RepositoryModel currentRepository;

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

    public boolean isVisibleRepoStatus() {
        return visibleRepoStatus.get();
    }

    public void setVisibleRepoStatus(boolean value) {
        visibleRepoStatus.set(value);
    }

    public BooleanProperty visibleRepoStatusProperty() {
        return visibleRepoStatus;
    }

    public boolean isVisibleDownStatus() {
        return visibleDownStatus.get();
    }

    public void setVisibleDownStatus(boolean value) {
        visibleDownStatus.set(value);
    }

    public BooleanProperty visibleDownStatusProperty() {
        return visibleDownStatus;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {        
        listViewPane.getChildren().remove(pagination);

        packagesListView.setPlaceholder(new Label("No Content In List"));
        packagesListView.setCellFactory((ListView<PackageModel> param) -> new PackageListCell());

        pagination.currentPageIndexProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            paginationHelper.setPage(newValue.intValue());
            ObservableList<PackageModel> page = paginationHelper.createPage();
            packagesListView.setItems(page);
        });

        EventHandler onDownloadStop = evt -> {
            mainPane.getChildren().remove(downloadScheduler.getControl());
            mainPane.setCenter(listViewPane);
            downBtn.setGraphic(startDownloadIcon);
            downBtn.setOnAction(this::startDownload);
            disabledOnDownload(false);
            visibleDownStatus.set(false);
            ObservableList<PackageModel> page = paginationHelper.createPage();
            packagesListView.setItems(page);
        };

        downloadScheduler.setOnCancelled(onDownloadStop);
        downloadScheduler.setOnDone(onDownloadStop);

        downloadScheduler.setOnLoadSucceeded(evt -> {
            downBtn.setGraphic(cancelDownloadIcon);
            downBtn.setOnAction(e -> downloadScheduler.cancel());
            downBtn.setDisable(false);
        });

        downloadScheduler.setOnFail(evt -> {
            downBtn.setDisable(false);
            onDownloadStop.handle(evt);
            Exception error = ((DownloadFailEvent) evt).getError();
            AlertHelper.error(null, messages.getMessage("download.scheduler.fail"), error.getMessage(), error);
        });

        downloadScheduler.setOnDone(evt -> {
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

            service.setOnOpen(evt -> {
                currentRepository = evt.getModel();
                RepositoryService.Status status = repositories.refresh(currentRepository.getRepository());
                currentRepository.setAvailable(status.getAvailable());
                currentRepository.setAvailableSize(status.getAvailableSize());
                currentRepository.setDownloads(status.getDownloads());
                currentRepository.setDownloadsSize(status.getDownloadsSize());
            });

            AlertHelper.progress(messages.getMessage("repository.open.progressHeader"),
                    messages.getMessage("repository.open.progressContext"),
                    service);
        }
    }

    @FXML
    void editRepository(ActionEvent event) {
        Window window = ((Node) event.getTarget()).getScene().getWindow();
        showRepositoryView(currentRepository.getName(), window, new RepositoryModel(currentRepository), ViewMode.EDIT);
        ((Stage) window).setTitle(String.format(TITLE_TEMPLATE, currentRepository.getName()));
    }

    @FXML
    void synchronize(ActionEvent event) {
        timer.stop();
        final Service service = new SynchronizeService(currentRepository, repositories);
        service.setOnSucceeded(evt -> {
            updateView();
            downBtn.setDisable(false);
        });

        service.setOnFailed(evt -> {
            String error = messages.getMessage("repository.sync.errorContext", currentRepository.getName());
            LOG.error(error, service.getException());
            AlertHelper.error(null, messages.getMessage("repository.sync.errorHeader"), null, new RuntimeException(error, service.getException()));
            timer.restart();
        });

        AlertHelper.progress(messages.getMessage("repository.sync.progressHeader"),
                messages.getMessage("repository.sync.progressContext", currentRepository.getName()),
                service);
    }

    @FXML
    void startDownload(ActionEvent event) {
        downloadScheduler.downloadedProperty().bindBidirectional(currentRepository.downloadsSizeProperty());
        downloadScheduler.downloadedCountProperty().bindBidirectional(currentRepository.downloadsProperty());
        disabledOnDownload(true);
        downBtn.setDisable(true);
        visibleDownStatus.set(true);
        mainPane.getChildren().remove(listViewPane);
        mainPane.setCenter(downloadScheduler.getControl());
        downloadScheduler.start(currentRepository.getSettings());

        File f = new File(currentRepository.getHome());

        if (f.getFreeSpace() < currentRepository.getAvailableSize() - currentRepository.getDownloadsSize()) {
            LOG.warn(messages.getMessage("diskSpace.warn", currentRepository.getHome()));
        }
    }

    @FXML
    final void showAboutBox(ActionEvent evt) {
        Parent parent = (Parent) viewLoader.load(Views.ABOUT_BOX);
        Window owner = ((Node) evt.getTarget()).getScene().getWindow();
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.setTitle(messages.getMessage("aboutBox.title", env.getProperty("app.title")));
        stage.getIcons().add(Views.APP_ICON);
        stage.setResizable(false);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
        stage.showAndWait();
    }

    @FXML
    final void exit(ActionEvent evt) {
        Window window = ((Node) evt.getTarget()).getScene().getWindow();
        onClose(window);
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
        Parent parent = (Parent) viewLoader.load(Views.EDIT_REPOSITORY, Optional.of(bindings));
        Scene scene = new Scene(parent);
        Stage stage = new Stage();
        stage.getIcons().add(Views.APP_ICON);
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
            currentRepository = model;
            disableOnOpen(false);
        } catch (IOException ex) {
            AlertHelper.error(null, messages.getMessage("repository.createError"), ex.getMessage(), ex);
        }
    }

    private void updateView() {
        pagination.setPageCount(paginationHelper.getPageCount());
        pagination.setCurrentPageIndex(0);
        packagesListView.setItems(paginationHelper.createPage());

        if (currentRepository.getAvailable() > paginationHelper.pageSize) {
            listViewPane.setBottom(pagination);
        } else {
            listViewPane.getChildren().remove(pagination);
        }

        visibleRepoStatus.set(true);
        updateToolBar();
        timer.restart();
    }

    private void updateToolBar() {
        visibleRepoStatus.set(true);
        availableCount.setText(NUMBER_FORMAT.format(currentRepository.getAvailable()));
        successCount.setText(NUMBER_FORMAT.format(currentRepository.getDownloads()));
        errorsCount.setText(NUMBER_FORMAT.format(downloadScheduler.getErrors()));
        double percent = FileSystemHelper.getUsedSpacePercent(currentRepository.getHome());
        homeIndicator.setProgress(percent * 0.01);

        if (currentRepository.getDownloadsSize() != 0) {
            double downPercent = currentRepository.getDownloadsSize() * 100 / currentRepository.getAvailableSize();
            downIndicator.setProgress(downPercent * 0.1);
        }
    }

    @Override
    public final Optional<File> getFile() {
        File file = null;

        if (currentRepository != null) {
            file = new File(String.join("/", currentRepository.getHome(), NamesUtils.PACKAGES_DAT));
        }

        return Optional.ofNullable(file);
    }

    @Override
    public void onClose(Window window) {
        try {

            if (currentRepository != null) {
                repositories.save(currentRepository.getRepository());
            }

        } catch (IOException | IllegalArgumentException ex) {
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
            return currentRepository.getAvailable() / pageSize;
        }

        public ObservableList<PackageModel> createPage() {
            List<DebPackage> list = packages.list(pageIndex * pageSize, pageSize);
            List<PackageModel> models = list.stream().map(PackageModel::create).collect(Collectors.toList());
            return FXCollections.observableArrayList(models);
        }
    }
}

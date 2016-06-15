package org.eljaiek.jmira.app.controls;

import org.eljaiek.jmira.app.model.PackageModel;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.eljaiek.jmira.app.util.FileSystemHelper;
import org.eljaiek.jmira.core.logs.MessageResolver;

/**
 *
 * @author eljaiek
 */
public final class PackageListCell extends ListCell<PackageModel> {
    
    private static final String DOWN_IMAGE_URL = "/org/eljaiek/jmira/app/view/resources/icons/downloadedPackage32.png";
      
    private static final String FXML_URL = "/org/eljaiek/jmira/app/view/PackageListCell.fxml";

    @FXML
    private Label nameLabel;

    @FXML
    private Label sizeLabel;

    @FXML
    private ImageView imageView;

    public PackageListCell() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_URL));
        loader.setController(PackageListCell.this);

        try {
            loader.load();
            setGraphic(loader.getRoot());
        } catch (IOException ex) {
            throw new JFXControlLoadException(ex);
        }
    }

    @Override
    protected void updateItem(PackageModel item, boolean empty) {
        super.updateItem(item, empty);
        
        
        if (empty || item == null) {
            return;
        }
        
        if (PackageModel.Status.DOWNLOADED == item.getStatus()) {
            imageView.setImage(new Image(DOWN_IMAGE_URL));
        }        

        nameLabel.setText(item.getName());
        String size = MessageResolver.getDefault()
                .getMessage("packageListCell.sizeLabel.text", FileSystemHelper.formatSize(item.getSize()));
        sizeLabel.setText(size);
    }
}

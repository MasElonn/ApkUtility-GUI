package org.codex.apktoolgui.view.tab;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.codex.apktoolgui.services.ApkToolService;
import org.codex.apktoolgui.view.MainView;

import java.io.File;

public class OtherToolsTab {
    private final MainView mainView;
    private final ApkToolService apkToolService;

    public OtherToolsTab(MainView mainView, ApkToolService apkToolService) {
        this.mainView = mainView;
        this.apkToolService = apkToolService;
    }

    public Tab createTab() {
        Tab tab = new Tab("Other Tools");
        tab.setClosable(false);
        tab.setGraphic(mainView.createIcon("🔨"));

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.getStyleClass().add("dark-container");

        TextField apkPathField = new TextField();
        apkPathField.setPromptText("Select APK file...");
        apkPathField.getStyleClass().add("dark-text-field");
        apkPathField.setPrefWidth(400);

        Button browseApkButton = mainView.createStyledButton("Browse", "primary");
        browseApkButton.setOnAction(e -> {
            File file = mainView.fileChooser.showOpenDialog(null);
            if (file != null) {
                apkPathField.setText(file.getAbsolutePath());
            }
        });

        Label outputLabel = new Label("Output Directory:");
        outputLabel.getStyleClass().add("dark-label");

        TextField outputPathField = new TextField();
        outputPathField.setPromptText("(Optional) Default: apk.out");
        outputPathField.getStyleClass().add("dark-text-field");

        Button browseOutputButton = mainView.createStyledButton("Browse", "secondary");
        browseOutputButton.setOnAction(e -> {
            File dir = mainView.directoryChooser.showDialog(null);
            if (dir != null) {
                outputPathField.setText(dir.getAbsolutePath());
            }
        });

        mainBox.getChildren().addAll(
                apkPathField,
                browseApkButton,
                outputLabel,
                outputPathField,
                browseOutputButton
        );

        tab.setContent(mainBox);
        return tab;
    }
}

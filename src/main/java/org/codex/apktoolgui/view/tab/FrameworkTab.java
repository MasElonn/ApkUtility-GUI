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

public class FrameworkTab {
    private final MainView mainView;
    private final ApkToolService apkToolService;

    public FrameworkTab(MainView mainView, ApkToolService apkToolService) {
        this.mainView = mainView;
        this.apkToolService = apkToolService;
    }

    public Tab createTab() {
        Tab frameworkTab = new Tab("Framework");
        frameworkTab.setClosable(false);
        frameworkTab.setGraphic(mainView.createIcon("📦"));

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.getStyleClass().add("dark-container");

        VBox installSection = new VBox(15);
        installSection.getStyleClass().add("section-box");

        Label installLabel = new Label("Install Framework");
        installLabel.getStyleClass().add("section-label");

        Label frameworkApkLabel = new Label("Framework APK:");
        frameworkApkLabel.getStyleClass().add("dark-label");

        TextField frameworkApkField = new TextField();
        frameworkApkField.setPromptText("Select framework APK...");
        frameworkApkField.getStyleClass().add("dark-text-field");

        Button browseFrameworkButton = mainView.createStyledButton("Browse", "primary");
        browseFrameworkButton.setOnAction(e -> {
            File file = mainView.fileChooser.showOpenDialog(null);
            if (file != null) {
                frameworkApkField.setText(file.getAbsolutePath());
            }
        });

        Label tagLabel = new Label("Tag (optional):");
        tagLabel.getStyleClass().add("dark-label");

        TextField tagField = new TextField();
        tagField.setPromptText("e.g., android-30");
        tagField.getStyleClass().add("dark-text-field");

        Button installButton = mainView.createStyledButton("Install Framework", "success");
        installButton.setPrefWidth(200);
        installButton.setOnAction(e -> apkToolService.executeInstallFramework(
                frameworkApkField.getText(),
                tagField.getText()
        ));

        installSection.getChildren().addAll(
                installLabel,
                frameworkApkLabel,
                frameworkApkField,
                browseFrameworkButton,
                tagLabel,
                tagField,
                installButton
        );

        VBox actionsSection = new VBox(15);
        actionsSection.getStyleClass().add("section-box");

        Label actionsLabel = new Label("Framework Management");
        actionsLabel.getStyleClass().add("section-label");

        Button listFrameworksButton = mainView.createStyledButton("📋 List Installed Frameworks", "secondary");
        listFrameworksButton.setPrefWidth(250);
        listFrameworksButton.setOnAction(e -> apkToolService.executeListFrameworks());

        Button emptyFrameworkButton = mainView.createStyledButton("🗑️ Empty Framework Directory", "danger");
        emptyFrameworkButton.setPrefWidth(250);
        emptyFrameworkButton.setOnAction(e -> apkToolService.executeEmptyFrameworkDir());

        actionsSection.getChildren().addAll(
                actionsLabel,
                listFrameworksButton,
                emptyFrameworkButton
        );

        mainBox.getChildren().addAll(installSection, actionsSection);

        frameworkTab.setContent(mainBox);
        return frameworkTab;
    }
}

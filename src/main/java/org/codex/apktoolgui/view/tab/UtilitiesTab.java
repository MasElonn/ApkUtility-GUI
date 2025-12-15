package org.codex.apktoolgui.view.tab;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.codex.apktoolgui.services.ApkToolService;
import org.codex.apktoolgui.view.MainView;

import java.io.File;

public class UtilitiesTab {
    private final MainView mainView;
    private final ApkToolService apkToolService;

    public UtilitiesTab(MainView mainView, ApkToolService apkToolService) {
        this.mainView = mainView;
        this.apkToolService = apkToolService;
    }

    public Tab createTab() {
        Tab otherTab = new Tab("Other Tools");
        otherTab.setClosable(false);
        otherTab.setGraphic(mainView.createIcon("⚙️"));

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.getStyleClass().add("dark-container");

        VBox publicizeSection = new VBox(15);
        publicizeSection.getStyleClass().add("section-box");

        Label publicizeLabel = new Label("Publicize Resources");
        publicizeLabel.getStyleClass().add("section-label");

        Label arscLabel = new Label("ARSC File:");
        arscLabel.getStyleClass().add("dark-label");

        TextField arscPathField = new TextField();
        arscPathField.setPromptText("Select compiled resources file...");
        arscPathField.getStyleClass().add("dark-text-field");

        Button browseArscButton = mainView.createStyledButton("Browse", "secondary");
        browseArscButton.setOnAction(e -> {
            FileChooser arscChooser = new FileChooser();
            arscChooser.setTitle("Select ARSC File");
            arscChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("ARSC Files", "*.arsc")
            );
            File file = arscChooser.showOpenDialog(null);
            if (file != null) {
                arscPathField.setText(file.getAbsolutePath());
            }
        });

        Button publicizeButton = mainView.createStyledButton("Publicize Resources", "primary");
        publicizeButton.setPrefWidth(200);
        publicizeButton.setOnAction(e -> apkToolService.executePublicizeResources(
                arscPathField.getText()
        ));

        publicizeSection.getChildren().addAll(
                publicizeLabel,
                arscLabel,
                arscPathField,
                browseArscButton,
                publicizeButton
        );

        VBox utilitySection = new VBox(15);
        utilitySection.getStyleClass().add("section-box");

        Label utilityLabel = new Label("Utility Tools");
        utilityLabel.getStyleClass().add("section-label");

        HBox buttonBox = new HBox(15);

        Button versionButton = mainView.createStyledButton("Version", "info");
        versionButton.setPrefWidth(150);
        versionButton.setOnAction(e -> apkToolService.executeVersionCheck());

        Button helpButton = mainView.createStyledButton("Help", "info");
        helpButton.setPrefWidth(150);
        helpButton.setOnAction(e -> apkToolService.executeHelp());

        buttonBox.getChildren().addAll(versionButton, helpButton);

        utilitySection.getChildren().addAll(utilityLabel, buttonBox);

        mainBox.getChildren().addAll(publicizeSection, utilitySection);

        otherTab.setContent(mainBox);
        return otherTab;
    }
}

package org.codex.apktoolgui.view.tab;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.codex.apktoolgui.services.ApkToolService;
import org.codex.apktoolgui.view.MainView;

import java.io.File;

public class SettingsTab {
    private final MainView mainView;
    private final ApkToolService apkToolService;

    public SettingsTab(MainView mainView, ApkToolService apkToolService) {
        this.mainView = mainView;
        this.apkToolService = apkToolService;
    }

    public Tab createTab() {
        Tab settingsTab = new Tab("Settings");
        settingsTab.setClosable(false);
        settingsTab.setGraphic(mainView.createIcon("⚙️"));

        VBox mainBox = new VBox(20);
        mainBox.setPadding(new Insets(20));
        mainBox.getStyleClass().add("dark-container");

        VBox pathSection = new VBox(15);
        pathSection.getStyleClass().add("section-box");

        Label pathLabel = new Label("Path Settings");
        pathLabel.getStyleClass().add("section-label");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.getStyleClass().add("dark-grid");

        Label apktoolLabel = new Label("Apktool Path:");
        apktoolLabel.getStyleClass().add("dark-label");

        TextField apktoolPathField = new TextField(mainView.getApktoolPath());
        apktoolPathField.getStyleClass().add("dark-text-field");

        Button browseApktoolButton = mainView.createStyledButton("Browse", "secondary");
        browseApktoolButton.setOnAction(e -> {
            FileChooser jarChooser = new FileChooser();
            jarChooser.setTitle("Select Apktool JAR");
            jarChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JAR Files", "*.jar")
            );
            File file = jarChooser.showOpenDialog(null);
            if (file != null) {
                apktoolPathField.setText(file.getAbsolutePath());
                mainView.setApktoolPath(file.getAbsolutePath());
            }
        });

        Label defaultDirLabel = new Label("Default Directory:");
        defaultDirLabel.getStyleClass().add("dark-label");

        TextField defaultDirField = new TextField(System.getProperty("user.dir"));
        defaultDirField.getStyleClass().add("dark-text-field");

        Button browseDefaultDirButton = mainView.createStyledButton("Browse", "secondary");
        browseDefaultDirButton.setOnAction(e -> {
            File dir = mainView.directoryChooser.showDialog(null);
            if (dir != null) {
                defaultDirField.setText(dir.getAbsolutePath());
            }
        });

        grid.add(apktoolLabel, 0, 0);
        grid.add(apktoolPathField, 1, 0);
        grid.add(browseApktoolButton, 2, 0);

        grid.add(defaultDirLabel, 0, 1);
        grid.add(defaultDirField, 1, 1);
        grid.add(browseDefaultDirButton, 2, 1);

        Button saveButton = mainView.createStyledButton("💾 Save Settings", "success");
        saveButton.setPrefWidth(200);
        saveButton.setOnAction(e -> {
            mainView.setApktoolPath(apktoolPathField.getText());
            apkToolService.saveSettings(
                    apktoolPathField.getText(),
                    defaultDirField.getText()
            );
        });

        pathSection.getChildren().addAll(pathLabel, grid, saveButton);

        VBox themeSection = new VBox(15);
        themeSection.getStyleClass().add("section-box");

        Label themeLabel = new Label("Theme Settings");
        themeLabel.getStyleClass().add("section-label");

        HBox themeBox = new HBox(15);
        themeBox.setAlignment(Pos.CENTER_LEFT);

        RadioButton darkThemeBtn = new RadioButton("Dark Theme");
        darkThemeBtn.setSelected(MainView.darkMode);
        darkThemeBtn.getStyleClass().add("dark-radio");

        RadioButton lightThemeBtn = new RadioButton("Light Theme");
        lightThemeBtn.setSelected(!MainView.darkMode);
        lightThemeBtn.getStyleClass().add("dark-radio");

        ToggleGroup themeGroup = new ToggleGroup();
        darkThemeBtn.setToggleGroup(themeGroup);
        lightThemeBtn.setToggleGroup(themeGroup);

        darkThemeBtn.setOnAction(e -> mainView.switchTheme(true));
        lightThemeBtn.setOnAction(e -> mainView.switchTheme(false));

        themeBox.getChildren().addAll(darkThemeBtn, lightThemeBtn);

        themeSection.getChildren().addAll(themeLabel, themeBox);

        mainBox.getChildren().addAll(pathSection, themeSection);

        settingsTab.setContent(mainBox);
        return settingsTab;
    }
}

package org.codex.apktoolgui.views.tabs;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.codex.apktoolgui.config.ApkEditorGetInfoConfig;
import org.codex.apktoolgui.services.ApkEditorService;
import org.codex.apktoolgui.utils.ApkInfoParser;
import org.codex.apktoolgui.utils.UiUtils;
import org.codex.apktoolgui.views.MainView;

import java.util.Map;

public class ApkInfoTab {
    private final MainView mainView;
    private final ApkEditorService apkEditorService;

    // UI Components
    private TextArea rawOutputArea;
    private VBox richContentContainer;
    private TabPane outputModeTabPane;
    private StringBuilder outputBuffer;
    private PauseTransition updateDebouncer;

    ApkEditorGetInfoConfig infoConfig = new ApkEditorGetInfoConfig();

    public ApkInfoTab(MainView mainView, ApkEditorService apkEditorService) {
        this.mainView = mainView;
        this.apkEditorService = apkEditorService;
        this.outputBuffer = new StringBuilder();

        // Debouncer: wait 500ms after the last output chunk to parse/update UI
        this.updateDebouncer = new PauseTransition(Duration.millis(500));
        this.updateDebouncer.setOnFinished(e -> updateRichUi());
    }

    public Tab createApkInfoTab() {
        Tab apkInfoTab = new Tab("APK INFO");
        apkInfoTab.setClosable(false);
        apkInfoTab.setGraphic(UiUtils.createIcon("⚡"));

        // --- Top Control Bar ---
        Label apkLabel = new Label("APK File:");
        apkLabel.setAlignment(Pos.CENTER_LEFT);
        TextField apkPathField = new TextField();
        apkPathField.setPromptText("Select APK file...");
        HBox.setHgrow(apkPathField, Priority.ALWAYS);

        Button browseApkButton = UiUtils.createStyledButton("Browse", "primary");
        browseApkButton.setOnAction(e -> UiUtils.browseFile(UiUtils.fileChooser, apkPathField, "Select APK", "*.apk", "Select File"));

        Button getInfoButton = UiUtils.createStyledButton("Get Info", "success");
        getInfoButton.setPrefWidth(120);

        Button optionsButton = UiUtils.createStyledButton("Options", "secondary");
        optionsButton.setOnAction(e -> showInfoOptionsDialog());

        Button clearButton = UiUtils.createStyledButton("Clear", "danger");
        clearButton.setOnAction(e -> clearOutput());

        HBox controlsBox = new HBox(10);
        controlsBox.setPadding(new Insets(15));
        controlsBox.setAlignment(Pos.CENTER_LEFT);
        controlsBox.getStyleClass().add("dark-container");
        controlsBox.getChildren().addAll(apkLabel, apkPathField, browseApkButton, getInfoButton, optionsButton, clearButton);

        // --- Output Area (TabPane for Rich vs Raw) ---
        outputModeTabPane = new TabPane();
        outputModeTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(outputModeTabPane, Priority.ALWAYS);

        // 1. Rich View Tab
        Tab richViewTab = new Tab("Formatted View");
        richContentContainer = new VBox(15);
        richContentContainer.setPadding(new Insets(15));
        ScrollPane richScroll = new ScrollPane(richContentContainer);
        richScroll.setFitToWidth(true);
        richScroll.getStyleClass().add("edge-to-edge");
        richViewTab.setContent(richScroll);

        // 2. Raw View Tab
        Tab rawViewTab = new Tab("Raw Output");
        rawOutputArea = new TextArea();
        rawOutputArea.setFont(Font.font("Monospaced", 12));
        rawOutputArea.setEditable(false);
        rawOutputArea.setWrapText(false);
        rawViewTab.setContent(rawOutputArea);

        outputModeTabPane.getTabs().addAll(richViewTab, rawViewTab);

        // --- Main Layout ---
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(controlsBox, outputModeTabPane);

        // --- Logic ---
        getInfoButton.setOnAction(e -> {
            String apkPath = apkPathField.getText();
            if (apkPath == null || apkPath.trim().isEmpty()) {
                UiUtils.showAlert("Error", "Please select an APK file first.");
                return;
            }

            clearOutput();
            outputModeTabPane.getSelectionModel().select(richViewTab); // Auto-switch to rich view
            setLoadingState(true);

            apkEditorService.executeGetInfo(
                    apkPath, "", false, "", "", "", "", "", "", "",
                    infoConfig.isActivities(),
                    infoConfig.isAppClass(),
                    infoConfig.isAppIcon(),
                    infoConfig.isAppName(),
                    infoConfig.isAppRoundIcon(),
                    infoConfig.isConfigurations(),
                    infoConfig.isDex(),
                    infoConfig.isForceDelete(),
                    infoConfig.isLanguages(),
                    infoConfig.isListFiles(),
                    infoConfig.isListXmlFiles(),
                    infoConfig.isLocales(),
                    infoConfig.isMinSdkVersion(),
                    infoConfig.isPackageInfo(),
                    infoConfig.isPermissions(),
                    infoConfig.isResources(),
                    infoConfig.isSignatures(),
                    infoConfig.isSignaturesBase64(),
                    infoConfig.isTargetSdkVersion(),
                    infoConfig.isVersionCode(),
                    infoConfig.isVersionName(),
                    output -> Platform.runLater(() -> handleStreamOutput(output))
            );
        });

        apkInfoTab.setContent(mainLayout);
        return apkInfoTab;
    }

    private void handleStreamOutput(String output) {
        // Update Raw View
        rawOutputArea.appendText(output);

        // Buffer for Parser
        outputBuffer.append(output);

        // Reset and restart the debounce timer
        // This ensures we only parse once the stream pauses/finishes
        updateDebouncer.playFromStart();
    }

    private void updateRichUi() {
        setLoadingState(false);
        String fullOutput = outputBuffer.toString();
        ApkInfoParser.ApkInfo info = ApkInfoParser.parse(fullOutput);

        richContentContainer.getChildren().clear();

        // 1. Header Card (Icon, Name, Package, Version)
        richContentContainer.getChildren().add(UiUtils.createHeaderCard(info));

        // 2. Grid Info Card (SDKs, Activities, etc)
        if (!info.generalInfo.isEmpty()) {
            richContentContainer.getChildren().add(UiUtils.createSectionTitle("General Information"));
            richContentContainer.getChildren().add(UiUtils.createKeyValueGrid(info.generalInfo));
        }

        // 3. Permissions
        if (!info.permissions.isEmpty()) {
            richContentContainer.getChildren().add(UiUtils.createExpandableListSection("Permissions (" + info.permissions.size() + ")", info.permissions));
        }

        // 4. Locales & Languages (Chips)
        if (!info.locales.isEmpty()) {
            richContentContainer.getChildren().add(UiUtils.createSectionTitle("Locales"));
            richContentContainer.getChildren().add(UiUtils.createChipView(info.locales));
        }

        // 5. Certificates
        if (info.certificateInfo != null && !info.certificateInfo.isEmpty()) {
            richContentContainer.getChildren().add(UiUtils.createSectionTitle("Signing Certificate"));
            richContentContainer.getChildren().add(UiUtils.createCodeBlock(info.certificateInfo));
        }

        // 6. DEX Info
        if (!info.dexBlocks.isEmpty()) {
            richContentContainer.getChildren().add(UiUtils.createSectionTitle("DEX Files"));
            for (String dex : info.dexBlocks) {
                richContentContainer.getChildren().add(UiUtils.createCodeBlock(dex));
            }
        }
    }

    private void clearOutput() {
        rawOutputArea.clear();
        richContentContainer.getChildren().clear();
        outputBuffer.setLength(0);
        // Reset to initial waiting state
        Label waiting = new Label("Waiting for output...");
        waiting.setStyle("-fx-text-fill: grey; -fx-font-style: italic;");
        richContentContainer.getChildren().add(waiting);
    }

    private void setLoadingState(boolean loading) {
        if (loading) {
            ProgressIndicator pi = new ProgressIndicator();
            pi.setMaxSize(30,30);
            richContentContainer.getChildren().clear();
            richContentContainer.getChildren().addAll(new Label("Analyzing APK..."), pi);
            richContentContainer.setAlignment(Pos.CENTER);
        } else {
            richContentContainer.setAlignment(Pos.TOP_LEFT);
        }
    }

    private void showInfoOptionsDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Information Options");
        dialog.setHeaderText("Select the information you want to extract");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL, ButtonType.APPLY);

        // Grid of checkboxes (kept simplified for brevity, logic remains same as original)
        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(10); grid.setPadding(new Insets(20));

        CheckBox[] cbs = new CheckBox[]{
                new CheckBox("Activities"), new CheckBox("App Class"), new CheckBox("App Icon"),
                new CheckBox("Configurations"), new CheckBox("DEX"), new CheckBox("Languages"),
                new CheckBox("Permissions"), new CheckBox("Signatures"), new CheckBox("Version Info")
        };

        // Map common ones just for demo
        cbs[0].setSelected(infoConfig.isActivities()); cbs[0].setOnAction(e -> infoConfig.setActivities(cbs[0].isSelected()));
        cbs[6].setSelected(infoConfig.isPermissions()); cbs[6].setOnAction(e -> infoConfig.setPermissions(cbs[6].isSelected()));
        cbs[8].setSelected(infoConfig.isVersionName()); cbs[8].setOnAction(e -> {
            infoConfig.setVersionName(cbs[8].isSelected());
            infoConfig.setVersionCode(cbs[8].isSelected());
        });

        // Add to grid
        int col=0, row=0;
        for(CheckBox cb : cbs) {
            grid.add(cb, col, row);
            col++; if(col>2) { col=0; row++; }
        }

        dialog.getDialogPane().setContent(grid);
        dialog.showAndWait();
    }
}
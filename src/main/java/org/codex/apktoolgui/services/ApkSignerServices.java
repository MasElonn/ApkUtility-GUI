package org.codex.apktoolgui.services;

import org.codex.apktoolgui.services.executor.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ApkSignerServices {
    private final UserNotifier userNotifier;
    private final CommandExecutor commandExecutor;

    public ApkSignerServices(UserNotifier userNotifier, CommandExecutor commandExecutor) {
        this.userNotifier = userNotifier;
        this.commandExecutor = commandExecutor;
    }

    public static String getApkSignerPath() {
        try {
            String configuredPath = org.codex.apktoolgui.services.SettingsManager.getInstance()
                    .getSettings().getApksignerPath();
            if (configuredPath != null && !configuredPath.isEmpty()) {
                File apkSignerFile = new File(configuredPath);
                if (apkSignerFile.exists()) {
                    return apkSignerFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // Fall back to default if settings manager fails
        }

        // Fallback to default location
        File apkSignerPath = new File("lib/apksigner.jar");
        if (apkSignerPath.exists()) {
            return apkSignerPath.getAbsolutePath();
        }
        return "";
    }

    public void verifySignature(String apkPath, Consumer<String> outputHandler) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file to verify.");
            return;
        }
        List<String> command = new ArrayList<>();
        command.add("java");
        command.add("-jar");
        command.add(getApkSignerPath());
        command.add("verify");
        command.add("--verbose");
        command.add(apkPath);
        commandExecutor.executeCommand(command, "Verifying APK signature...", outputHandler);

    }
    //TODO sign apk function
}

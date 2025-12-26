package org.codex.apktoolgui.services;


import org.codex.apktoolgui.services.executor.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ZipAlignService {
    
    private final CommandExecutor commandExecutor;

    public ZipAlignService(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public static String getZipalignPath(){
        // Use settings manager for path retrieval
        try {
            String configuredPath = org.codex.apktoolgui.services.SettingsManager.getInstance()
                .getSettings().getZipalignPath();
            if (configuredPath != null && !configuredPath.isEmpty()) {
                File zipalignFile = new File(configuredPath);
                if (zipalignFile.exists()) {
                    return zipalignFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // Fall back to default if settings manager fails
        }
        
        // Fallback to default location
        File zipalignPath = new File("lib/zipalign");
        if(zipalignPath.exists()){
            return zipalignPath.getAbsolutePath();
        }
        return "";
    }

    public void alignApk(String apkPath, String outputPath){
        List<String> command = new ArrayList<>();
        command.add(getZipalignPath());
        command.add("-v");
        command.add("4");
        command.add(apkPath);
        command.add(outputPath);
        commandExecutor.executeCommand(command, "Aligning APK...");
    }

}

package org.codex.apktoolgui.services;


import java.util.List;
import java.util.ArrayList;
import org.codex.apktoolgui.services.executor.CommandExecutor;


public class ZipAlignService {
    private static String zipalignPath = "zipalign";

    public static void alighApk(String apkPath, String outputPath){
        List<String> command = new ArrayList<>();
        command.add(zipalignPath);
        command.add("-v");
        command.add("4");
        command.add(apkPath);
        command.add(outputPath);
        CommandExecutor.executeCommand(command, "Aligning APK...");
    }

}

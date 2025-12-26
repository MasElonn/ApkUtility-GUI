package org.codex.apktoolgui.services;

import org.codex.apktoolgui.services.executor.CommandExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service for AAPT and AAPT2 operations
 * Supports resource dumping, listing, compilation, and linking
 */
public class AaptService {
    
    private final LogOutput logOutput;
    private final UserNotifier userNotifier;
    private final CommandExecutor commandExecutor;
    
    public AaptService(LogOutput logOutput, UserNotifier userNotifier, CommandExecutor commandExecutor) {
        this.logOutput = logOutput;
        this.userNotifier = userNotifier;
        this.commandExecutor = commandExecutor;
    }
    
    public static String getAaptPath() {
        try {
            String configuredPath = SettingsManager.getInstance()
                .getSettings().getAaptPath();
            if (configuredPath != null && !configuredPath.isEmpty()) {
                File aaptFile = new File(configuredPath);
                if (aaptFile.exists()) {
                    return aaptFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // Fall back to default
        }
        
        File aaptPath = new File("lib/aapt");
        if (aaptPath.exists()) {
            return aaptPath.getAbsolutePath();
        }
        return "aapt";
    }
    
    public static String getAapt2Path() {
        try {
            String configuredPath = SettingsManager.getInstance()
                .getSettings().getAapt2Path();
            if (configuredPath != null && !configuredPath.isEmpty()) {
                File aapt2File = new File(configuredPath);
                if (aapt2File.exists()) {
                    return aapt2File.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // Fall back to default
        }
        
        File aapt2Path = new File("lib/aapt2");
        if (aapt2Path.exists()) {
            return aapt2Path.getAbsolutePath();
        }
        return "aapt2";
    }
    
    // ========================
    // AAPT Operations
    // ========================
    
    /**
     * Dump APK badging information (package name, version, activities, etc.)
     */
    public void dumpBadging(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("dump");
        command.add("badging");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Dumping APK badging...");
    }
    
    /**
     * Dump APK permissions
     */
    public void dumpPermissions(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("dump");
        command.add("permissions");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Dumping APK permissions...");
    }
    
    /**
     * Dump APK resources
     */
    public void dumpResources(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("dump");
        command.add("resources");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Dumping APK resources...");
    }
    
    /**
     * Dump APK configurations
     */
    public void dumpConfigurations(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("dump");
        command.add("configurations");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Dumping APK configurations...");
    }
    
    /**
     * Dump XML tree from APK
     */
    public void dumpXmlTree(String apkPath, String assetPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("dump");
        command.add("xmltree");
        command.add(apkPath);
        if (assetPath != null && !assetPath.trim().isEmpty()) {
            command.add(assetPath);
        } else {
            command.add("AndroidManifest.xml");
        }
        
        commandExecutor.executeCommand(command, "Dumping XML tree...");
    }
    
    /**
     * List APK contents
     */
    public void listContents(String apkPath, boolean verbose) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("list");
        if (verbose) {
            command.add("-v");
        }
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Listing APK contents...");
    }
    
    /**
     * Get AAPT version
     */
    public void getVersion() {
        List<String> command = new ArrayList<>();
        command.add(getAaptPath());
        command.add("version");
        
        commandExecutor.executeCommand(command, "Getting AAPT version...");
    }
    
    // ========================
    // AAPT2 Operations
    // ========================
    
    /**
     * AAPT2 dump APK info (badging equivalent)
     */
    public void aapt2DumpBadging(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("badging");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping APK badging...");
    }
    
    /**
     * AAPT2 dump permissions
     */
    public void aapt2DumpPermissions(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("permissions");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping permissions...");
    }
    
    /**
     * AAPT2 dump resources
     */
    public void aapt2DumpResources(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("resources");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping resources...");
    }
    
    /**
     * AAPT2 dump configurations
     */
    public void aapt2DumpConfigurations(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("configurations");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping configurations...");
    }
    
    /**
     * AAPT2 dump xmltree
     */
    public void aapt2DumpXmlTree(String apkPath, String assetPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("xmltree");
        command.add("--file");
        if (assetPath != null && !assetPath.trim().isEmpty()) {
            command.add(assetPath);
        } else {
            command.add("AndroidManifest.xml");
        }
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping XML tree...");
    }
    
    /**
     * AAPT2 dump strings
     */
    public void aapt2DumpStrings(String apkPath) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("dump");
        command.add("strings");
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "AAPT2: Dumping strings...");
    }
    
    /**
     * Get AAPT2 version
     */
    public void getAapt2Version() {
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("version");
        
        commandExecutor.executeCommand(command, "Getting AAPT2 version...");
    }
    
    /**
     * AAPT2 compile a single resource file
     */
    public void aapt2Compile(String resourcePath, String outputDir) {
        if (resourcePath == null || resourcePath.trim().isEmpty()) {
            userNotifier.showError("Please select a resource file.");
            return;
        }
        if (outputDir == null || outputDir.trim().isEmpty()) {
            userNotifier.showError("Please select an output directory.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("compile");
        command.add("-o");
        command.add(outputDir);
        command.add(resourcePath);
        
        commandExecutor.executeCommand(command, "AAPT2: Compiling resource...");
    }
    
    /**
     * AAPT2 compile all resources in a directory
     */
    public void aapt2CompileDir(String resourceDir, String outputDir) {
        if (resourceDir == null || resourceDir.trim().isEmpty()) {
            userNotifier.showError("Please select a resource directory.");
            return;
        }
        if (outputDir == null || outputDir.trim().isEmpty()) {
            userNotifier.showError("Please select an output directory.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("compile");
        command.add("--dir");
        command.add(resourceDir);
        command.add("-o");
        command.add(outputDir + "/compiled_resources.zip");
        
        commandExecutor.executeCommand(command, "AAPT2: Compiling resources directory...");
    }
    
    /**
     * AAPT2 link compiled resources into an APK
     */
    public void aapt2Link(String compiledResources, String manifestPath, String outputApk, 
                          String minSdk, String targetSdk, String androidJar) {
        if (compiledResources == null || manifestPath == null || outputApk == null) {
            userNotifier.showError("Missing required parameters for linking.");
            return;
        }
        
        List<String> command = new ArrayList<>();
        command.add(getAapt2Path());
        command.add("link");
        command.add("-o");
        command.add(outputApk);
        command.add("--manifest");
        command.add(manifestPath);
        
        if (minSdk != null && !minSdk.isEmpty()) {
            command.add("--min-sdk-version");
            command.add(minSdk);
        }
        
        if (targetSdk != null && !targetSdk.isEmpty()) {
            command.add("--target-sdk-version");
            command.add(targetSdk);
        }
        
        if (androidJar != null && !androidJar.isEmpty()) {
            command.add("-I");
            command.add(androidJar);
        }
        
        command.add(compiledResources);
        
        commandExecutor.executeCommand(command, "AAPT2: Linking resources...");
    }
}

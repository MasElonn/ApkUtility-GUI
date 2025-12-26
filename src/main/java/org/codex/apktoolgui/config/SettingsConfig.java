package org.codex.apktoolgui.config;

import java.io.File;

/**
 * Settings configuration model for ApkTool GUI
 * Holds all configurable paths and application preferences
 */
public class SettingsConfig {
    
    // Tool Paths
    private String apktoolPath;
    private String apkEditorPath;
    private String adbPath;
    private String zipalignPath;
    private String apksignerPath;
    private String aaptPath;
    private String aapt2Path;
    private String javaPath;
    
    // Framework and Working Directories
    private String frameworkPath;
    private String defaultWorkingDir;
    
    // Application Preferences
    private boolean darkMode;
    private boolean autoSave;
    
    // Default values
    public static final String DEFAULT_APKTOOL_PATH = "lib/apktool.jar";
    public static final String DEFAULT_APKEDITOR_PATH = "lib/APKEditor.jar";
    public static final String DEFAULT_ADB_PATH = getDefaultAdbPath();
    public static final String DEFAULT_ZIPALIGN_PATH = "lib/zipalign";
    public static final String DEFAULT_APKSIGNER_PATH = "lib/apksigner";
    public static final String DEFAULT_AAPT_PATH = "lib/aapt";
    public static final String DEFAULT_AAPT2_PATH = "lib/aapt2";
    public static final String DEFAULT_JAVA_PATH = "java";
    public static final String DEFAULT_FRAMEWORK_PATH = System.getProperty("user.home") + "/.apktool/framework";
    public static final String DEFAULT_WORKING_DIR = System.getProperty("user.home") + "/apktool-workspace";
    public static final boolean DEFAULT_DARK_MODE = true;
    public static final boolean DEFAULT_AUTO_SAVE = false;
    
    private static String getDefaultAdbPath() {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            return "lib/platform-tools/adb.exe";
        }
        return "lib/platform-tools/adb";
    }
    
    public SettingsConfig() {
        // Initialize with default values
        this.apktoolPath = DEFAULT_APKTOOL_PATH;
        this.apkEditorPath = DEFAULT_APKEDITOR_PATH;
        this.adbPath = DEFAULT_ADB_PATH;
        this.zipalignPath = DEFAULT_ZIPALIGN_PATH;
        this.apksignerPath = DEFAULT_APKSIGNER_PATH;
        this.aaptPath = DEFAULT_AAPT_PATH;
        this.aapt2Path = DEFAULT_AAPT2_PATH;
        this.javaPath = DEFAULT_JAVA_PATH;
        this.frameworkPath = DEFAULT_FRAMEWORK_PATH;
        this.defaultWorkingDir = DEFAULT_WORKING_DIR;
        this.darkMode = DEFAULT_DARK_MODE;
        this.autoSave = DEFAULT_AUTO_SAVE;
    }
    
    // Getters
    public String getApktoolPath() {
        return apktoolPath != null && !apktoolPath.isEmpty() ? apktoolPath : DEFAULT_APKTOOL_PATH;
    }
    
    public String getApkEditorPath() {
        return apkEditorPath != null && !apkEditorPath.isEmpty() ? apkEditorPath : DEFAULT_APKEDITOR_PATH;
    }
    
    public String getAdbPath() {
        return adbPath != null && !adbPath.isEmpty() ? adbPath : DEFAULT_ADB_PATH;
    }
    
    public String getZipalignPath() {
        return zipalignPath != null && !zipalignPath.isEmpty() ? zipalignPath : DEFAULT_ZIPALIGN_PATH;
    }
    public String getApksignerPath(){
        return apksignerPath != null && !apksignerPath.isEmpty() ? apksignerPath : DEFAULT_APKSIGNER_PATH;
    }
    public String getAaptPath(){
        return aaptPath != null && !aaptPath.isEmpty() ? aaptPath : DEFAULT_AAPT_PATH;
    }
    public String getAapt2Path(){
        return aapt2Path != null && !aapt2Path.isEmpty() ? aapt2Path : DEFAULT_AAPT2_PATH;
    }
    
    public String getJavaPath() {
        return javaPath != null && !javaPath.isEmpty() ? javaPath : DEFAULT_JAVA_PATH;
    }
    
    public String getFrameworkPath() {
        return frameworkPath != null && !frameworkPath.isEmpty() ? frameworkPath : DEFAULT_FRAMEWORK_PATH;
    }
    
    public String getDefaultWorkingDir() {
        return defaultWorkingDir != null && !defaultWorkingDir.isEmpty() ? defaultWorkingDir : DEFAULT_WORKING_DIR;
    }
    
    public boolean isDarkMode() {
        return darkMode;
    }
    
    public boolean isAutoSave() {
        return autoSave;
    }
    
    // Setters
    public void setApktoolPath(String apktoolPath) {
        this.apktoolPath = apktoolPath;
    }
    
    public void setApkEditorPath(String apkEditorPath) {
        this.apkEditorPath = apkEditorPath;
    }
    
    public void setAdbPath(String adbPath) {
        this.adbPath = adbPath;
    }
    
    public void setZipalignPath(String zipalignPath) {
        this.zipalignPath = zipalignPath;
    }
    public void setApksignerPath(String apksignerPath){this.apksignerPath = apksignerPath;}
    public void setAaptPath(String aaptPath){this.aaptPath = aaptPath;}
    public void setAapt2Path(String aapt2Path){this.aapt2Path = aapt2Path;}
    
    public void setJavaPath(String javaPath) {
        this.javaPath = javaPath;
    }
    
    public void setFrameworkPath(String frameworkPath) {
        this.frameworkPath = frameworkPath;
    }
    
    public void setDefaultWorkingDir(String defaultWorkingDir) {
        this.defaultWorkingDir = defaultWorkingDir;
    }
    
    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }
    
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }
    
    // Validation helpers
    public boolean validateApktoolPath() {
        return new File(getApktoolPath()).exists();
    }
    
    public boolean validateApkEditorPath() {
        return new File(getApkEditorPath()).exists();
    }
    
    public boolean validateAdbPath() {
        File adb = new File(getAdbPath());
        if (adb.exists()) return true;
        // Check if it's in system PATH
        try {
            Process p = new ProcessBuilder(getAdbPath(), "version").start();
            p.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateZipalignPath() {
        return new File(getZipalignPath()).exists();
    }
    public boolean validateApksignerPath(){return new File(getApksignerPath()).exists();}
    public boolean validateAaptPath(){return new File(getAaptPath()).exists();}
    public boolean validateAapt2Path(){return new File(getAapt2Path()).exists();}
    public boolean validateJavaPath() {
        try {
            Process p = new ProcessBuilder(getJavaPath(), "-version").start();
            p.waitFor();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean validateFrameworkPath() {
        File dir = new File(getFrameworkPath());
        return dir.exists() && dir.isDirectory();
    }
}

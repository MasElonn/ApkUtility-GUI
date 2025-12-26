package org.codex.apktoolgui.services;

import org.codex.apktoolgui.services.executor.CommandExecutor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for APK signing operations using apksigner
 * Supports v1 (JAR), v2, v3, v4 signature schemes
 */
public class ApkSignerService {
    
    private final LogOutput logOutput;
    private final UserNotifier userNotifier;
    private final CommandExecutor commandExecutor;
    
    // Test keystore constants
    private static final String TEST_KEYSTORE_NAME = "debug.keystore";
    private static final String TEST_KEYSTORE_PASSWORD = "android";
    private static final String TEST_KEY_ALIAS = "androiddebugkey";
    private static final String TEST_KEY_PASSWORD = "android";
    
    public ApkSignerService(LogOutput logOutput, UserNotifier userNotifier, CommandExecutor commandExecutor) {
        this.logOutput = logOutput;
        this.userNotifier = userNotifier;
        this.commandExecutor = commandExecutor;
    }
    
    public static String getApkSignerPath() {
        try {
            String configuredPath = SettingsManager.getInstance()
                .getSettings().getApksignerPath();
            if (configuredPath != null && !configuredPath.isEmpty()) {
                File signerFile = new File(configuredPath);
                if (signerFile.exists()) {
                    return signerFile.getAbsolutePath();
                }
            }
        } catch (Exception e) {
            // Fall back to default
        }
        
        File signerPath = new File("lib/apksigner.jar");
        if (signerPath.exists()) {
            return signerPath.getAbsolutePath();
        }
        return "";
    }
    
    /**
     * Get or create test keystore for debug signing
     */
    public String getOrCreateTestKeystore() {
        Path keystorePath = Path.of(System.getProperty("user.home"), ".apktool-gui", TEST_KEYSTORE_NAME);
        
        if (Files.exists(keystorePath)) {
            return keystorePath.toString();
        }
        
        // Create directory if needed
        try {
            Files.createDirectories(keystorePath.getParent());
        } catch (Exception e) {
            logOutput.append("❌ Failed to create keystore directory: " + e.getMessage());
            return null;
        }
        
        // Generate debug keystore using keytool
        logOutput.append("🔑 Generating test keystore...");
        
        List<String> command = new ArrayList<>();
        command.add("keytool");
        command.add("-genkeypair");
        command.add("-v");
        command.add("-keystore");
        command.add(keystorePath.toString());
        command.add("-storepass");
        command.add(TEST_KEYSTORE_PASSWORD);
        command.add("-alias");
        command.add(TEST_KEY_ALIAS);
        command.add("-keypass");
        command.add(TEST_KEY_PASSWORD);
        command.add("-keyalg");
        command.add("RSA");
        command.add("-keysize");
        command.add("2048");
        command.add("-validity");
        command.add("10000");
        command.add("-dname");
        command.add("CN=Android Debug,O=Android,C=US");
        
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && Files.exists(keystorePath)) {
                logOutput.append("✅ Test keystore created at: " + keystorePath);
                return keystorePath.toString();
            } else {
                logOutput.append("❌ Failed to create test keystore");
                return null;
            }
        } catch (Exception e) {
            logOutput.append("❌ Failed to create test keystore: " + e.getMessage());
            return null;
        }
    }
    
    public String getTestKeystorePassword() {
        return TEST_KEYSTORE_PASSWORD;
    }
    
    public String getTestKeyAlias() {
        return TEST_KEY_ALIAS;
    }
    
    public String getTestKeyPassword() {
        return TEST_KEY_PASSWORD;
    }
    
    /**
     * Sign APK with specified keystore or test key
     */
    public void signApk(String inputApk, String outputApk, String keystorePath, 
                        String keystorePassword, String keyAlias, String keyPassword,
                        boolean v1Enabled, boolean v2Enabled, boolean v3Enabled, boolean v4Enabled,
                        boolean useTestKey) {
        
        if (inputApk == null || inputApk.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file to sign.");
            return;
        }
        
        // Use test key if requested or no keystore provided
        if (useTestKey || keystorePath == null || keystorePath.trim().isEmpty()) {
            keystorePath = getOrCreateTestKeystore();
            if (keystorePath == null) {
                userNotifier.showError("Failed to create test keystore.");
                return;
            }
            keystorePassword = TEST_KEYSTORE_PASSWORD;
            keyAlias = TEST_KEY_ALIAS;
            keyPassword = TEST_KEY_PASSWORD;
            logOutput.append("🔑 Using test key for signing");
        }
        
        // Generate output path if not specified
        if (outputApk == null || outputApk.trim().isEmpty()) {
            outputApk = inputApk.replace(".apk", "_signed.apk");
        }
        
        String javaPath = SettingsManager.getInstance().getSettings().getJavaPath();
        
        List<String> command = new ArrayList<>();
        command.add(javaPath);
        command.add("-jar");
        command.add(getApkSignerPath());
        command.add("sign");
        
        // Signature schemes
        command.add("--v1-signing-enabled");
        command.add(String.valueOf(v1Enabled));
        command.add("--v2-signing-enabled");
        command.add(String.valueOf(v2Enabled));
        command.add("--v3-signing-enabled");
        command.add(String.valueOf(v3Enabled));
        command.add("--v4-signing-enabled");
        command.add(String.valueOf(v4Enabled));
        
        // Keystore
        command.add("--ks");
        command.add(keystorePath);
        command.add("--ks-pass");
        command.add("pass:" + keystorePassword);
        command.add("--ks-key-alias");
        command.add(keyAlias);
        command.add("--key-pass");
        command.add("pass:" + keyPassword);
        
        // Output
        command.add("--out");
        command.add(outputApk);
        
        // Input
        command.add(inputApk);
        
        commandExecutor.executeCommand(command, "Signing APK...");
    }
    
    /**
     * Quick sign with test key (all schemes enabled)
     */
    public void quickSignWithTestKey(String inputApk, String outputApk) {
        signApk(inputApk, outputApk, null, null, null, null, true, true, true, false, true);
    }
    
    /**
     * Verify APK signature
     */
    public void verifyApk(String apkPath, boolean verbose, boolean printCerts) {
        if (apkPath == null || apkPath.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file to verify.");
            return;
        }
        
        String javaPath = SettingsManager.getInstance().getSettings().getJavaPath();
        
        List<String> command = new ArrayList<>();
        command.add(javaPath);
        command.add("-jar");
        command.add(getApkSignerPath());
        command.add("verify");
        
        if (verbose) {
            command.add("-v");
        }
        
        if (printCerts) {
            command.add("--print-certs");
        }
        
        command.add(apkPath);
        
        commandExecutor.executeCommand(command, "Verifying APK signature...");
    }
    
    /**
     * Get APK signer version
     */
    public void getVersion() {
        String javaPath = SettingsManager.getInstance().getSettings().getJavaPath();
        
        List<String> command = new ArrayList<>();
        command.add(javaPath);
        command.add("-jar");
        command.add(getApkSignerPath());
        command.add("version");
        
        commandExecutor.executeCommand(command, "Getting apksigner version...");
    }
    
    /**
     * Rotate signing key (lineage-based key rotation)
     */
    public void rotateSigningKey(String inputApk, String outputApk, 
                                  String oldKeystorePath, String oldKeystorePassword,
                                  String oldKeyAlias, String oldKeyPassword,
                                  String newKeystorePath, String newKeystorePassword,
                                  String newKeyAlias, String newKeyPassword) {
        
        if (inputApk == null || inputApk.trim().isEmpty()) {
            userNotifier.showError("Please select an APK file.");
            return;
        }
        
        if (oldKeystorePath == null || newKeystorePath == null) {
            userNotifier.showError("Both old and new keystores are required for key rotation.");
            return;
        }
        
        String javaPath = SettingsManager.getInstance().getSettings().getJavaPath();
        
        // First, create lineage
        logOutput.append("🔄 Creating signing certificate lineage...");
        
        List<String> lineageCommand = new ArrayList<>();
        lineageCommand.add(javaPath);
        lineageCommand.add("-jar");
        lineageCommand.add(getApkSignerPath());
        lineageCommand.add("lineage");
        lineageCommand.add("--old-signer");
        lineageCommand.add("--ks");
        lineageCommand.add(oldKeystorePath);
        lineageCommand.add("--ks-pass");
        lineageCommand.add("pass:" + oldKeystorePassword);
        lineageCommand.add("--ks-key-alias");
        lineageCommand.add(oldKeyAlias);
        lineageCommand.add("--new-signer");
        lineageCommand.add("--ks");
        lineageCommand.add(newKeystorePath);
        lineageCommand.add("--ks-pass");
        lineageCommand.add("pass:" + newKeystorePassword);
        lineageCommand.add("--ks-key-alias");
        lineageCommand.add(newKeyAlias);
        
        commandExecutor.executeCommand(lineageCommand, "Rotating signing key...");
    }
}

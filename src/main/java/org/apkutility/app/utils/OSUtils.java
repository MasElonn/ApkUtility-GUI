package org.apkutility.app.utils;

import java.util.Locale;

public class OSUtils {

    private static final String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isMac() {
        return OS.contains("mac") || OS.contains("darwin");
    }

    public static boolean isLinux() {
        return OS.contains("nux");
    }

    public static String getOsDirName() {
        if (isWindows()) return "windows";
        if (isMac()) return "mac";
        return "linux";
    }

    public static String getExecutableExtension() {
        return isWindows() ? ".exe" : "";
    }
}

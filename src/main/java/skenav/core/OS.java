package skenav.core;

public class OS {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static String pathSeparator() {
        if (isWindows()) {
            return "\\";
        }
        return "/";
    }

    public static String pathListSeparator() {
        if (isWindows()) {
            return ";";
        }
        return ":";
    }

    public static String getTempDirectory() {
        return System.getProperty("java.io.tmpdir");
    }

    public static String getHomeDirectory() {
        return System.getProperty("user.home") + pathSeparator();
    }
//TODO: make getusercontentdirectory get the user specified content directory instead of the hardcoded one
    public static String getUserContentDirectory() {
        String output = getHomeDirectory() + "usercontent" + pathSeparator();
        return output;
    }

    public static boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    public static boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    public static boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }
}

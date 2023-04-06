package skenav.core;

public class OS {
    private static String OS = System.getProperty("os.name").toLowerCase();
    private String usercontentdirectory;

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
    public static String getUserContentDirectory() {
        String output = Cache.INSTANCE.getUploaddirectory();
        return output;
    }
    public static String getSkenavDirectory(){
        String skenavfoldername;
        if (isWindows()) {
            skenavfoldername = "skenav";
        }
        else {
            skenavfoldername = ".skenav";
        }
        String skenavdirectory = getHomeDirectory() + skenavfoldername + pathSeparator();
        return skenavdirectory;
    }
    public static String getUserHlsDirectory(String username){
        String userhlsdirectory = Cache.INSTANCE.getUploaddirectory() + username + pathSeparator() + "hls" + pathSeparator();
        return userhlsdirectory;
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

    public static String checkPathSeparator(String input){
        String output;
        if (!input.endsWith(pathSeparator())){
            output = input + pathSeparator();
            return output;
        }
        else {
            return input;
        }
    }

    public static String getUserFilesDirectory(String username) {
        String userdirectory = Cache.INSTANCE.getUploaddirectory() + username + pathSeparator() + "files" + pathSeparator();
        return userdirectory;
    }
}

package khmtk60.miniprojects.G15.Solution1.src;

public class OSValidator {
	private static String OS = System.getProperty("os.name").toLowerCase();
	 
	public static boolean isWindows() {
 
		return (OS.indexOf("win") >= 0);
 
	}
 
	public static boolean isUnix() {
 
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
 
	}
}

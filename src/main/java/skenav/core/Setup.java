package skenav.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import skenav.core.db.Database;
import skenav.core.security.Crypto;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Setup {
	static String breadcrumbdirectory = OS.getHomeDirectory() + "skenav-breadcrumb-do-not-delete.json";
	static File breadcrumb = new File(breadcrumbdirectory);
	public static boolean checkBreadcrumb(){
		//File breadcrumb = new File(breadcrumbdirectory);
		if (!breadcrumb.exists()){
			try {
				java.awt.Desktop.getDesktop().browse(new URI("http://localhost:8080/setup"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			return false;
		}
		else{
			return true;
		}
	}
	// TODO: reformat so usercontent is in a subfolder of skenav folder
	public static void finalizeSetup(String skenavdirectory, boolean firsttime, String username, String passwordhash) {
		File skenavDirectory = new File(skenavdirectory);
		File dbFile = new File(skenavdirectory + "database.mv.db");
		File hlsDirectory = new File(skenavdirectory + "hlstestfolder");
		//File breadCrumb = new File(breadcrumbdirectory);
		if (!skenavDirectory.exists()) {
			final boolean mkdirs = skenavDirectory.mkdirs();
			System.out.println(mkdirs);
		}
		if (!dbFile.exists()) {
			Database.createTable(skenavdirectory);
			Database database = new Database();
			if (firsttime == true){
				database.addToAppData("upload directory", skenavdirectory);
				database.addUser(username,passwordhash,0);
				Crypto.setCryptoSeed();
			}
		}
		if (!hlsDirectory.exists()) {
			final boolean mkdirs = hlsDirectory.mkdirs();
			System.out.println("----" + mkdirs);
		}
		if (!breadcrumb.exists()) {
			try {
				writeBreadcrumb(skenavdirectory);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	public static void writeBreadcrumb(String uploaddirectory) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(new File(breadcrumbdirectory), uploaddirectory);
	}


}

	/*Database database = new Database();
	String uploaddirectory = database.getAppData("upload directory");
		System.out.println(uploaddirectory);
				File uploadDirectory = new File(uploaddirectory);
				File dbFile = new File(uploaddirectory + "database.mv.db");
				File hlsDirectory = new File(config.getUploadDirectory() + "usercontent" + OS.pathSeparator() + "hlstestfolder");

				if (!uploadDirectory.exists()) {
final boolean mkdirs = uploadDirectory.mkdirs();
		System.out.println("----" + mkdirs);

		}
		if (!dbFile.exists()) {
		Database.createTable();
		//TODO: Check if crypto seed specifically exists
		Crypto.setCryptoSeed();

		}
		if (!hlsDirectory.exists()) {
final boolean mkdirs = hlsDirectory.mkdirs();
		System.out.println("----" + mkdirs);
		}*/
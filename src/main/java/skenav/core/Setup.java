package skenav.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.setup.Bootstrap;
import skenav.core.db.Database;
import skenav.core.security.Crypto;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Setup {
	static String breadcrumbdirectory = OS.getHomeDirectory() + "skenav-breadcrumb-do-not-delete.json";
	static File breadcrumb = new File(breadcrumbdirectory);
	String skenavdirectory;
	public Setup() {
		skenavdirectory = OS.getUserContentDirectory();
	}
	public static boolean checkBreadcrumb(){
		//File breadcrumb = new File(breadcrumbdirectory);
		if (breadcrumb.exists()){
			return true;
		}
		else{
			return false;
		}
	}
	// TODO: reformat so usercontent is in a subfolder of skenav folder
	public void finalizeSetup(boolean firsttime, String username, String passwordhash) {
		File skenavDirectory = new File(skenavdirectory);
		File dbFile = new File(skenavdirectory + "database.mv.db");
		File hlsDirectory = new File(skenavdirectory + "hlstestfolder");
		//File breadCrumb = new File(breadcrumbdirectory);
		if (!skenavDirectory.exists()) {
			final boolean mkdirs = skenavDirectory.mkdirs();
			System.out.println(mkdirs);

		}
		if (!dbFile.exists()) {
			Database.createTable();
			Database database = new Database();
			if (firsttime == true){
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
	public void writeBreadcrumb(String uploaddirectory) throws IOException {
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
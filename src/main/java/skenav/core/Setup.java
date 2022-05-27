package skenav.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.dropwizard.setup.Bootstrap;
import skenav.core.db.Database;
import skenav.core.security.Crypto;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Map;

public class Setup {
	static String breadcrumbdirectory = OS.getHomeDirectory() + "skenav-breadcrumb-do-not-delete.json";
	static File breadcrumb = new File(breadcrumbdirectory);
	String skenavdirectory;

	public Setup() {
		skenavdirectory = OS.getUserContentDirectory();
	}

	public static boolean checkBreadcrumb() {
		//File breadcrumb = new File(breadcrumbdirectory);
		if (breadcrumb.exists()) {
			return true;
		} else {
			return false;
		}
	}
	public static void addUserHlsDirectory (String username) {
		File userhlsdirectory = new File(OS.getUserContentDirectory() + "hls" + OS.pathSeparator() + username);
		userhlsdirectory.mkdirs();
	}

	// TODO: reformat so usercontent is in a subfolder of skenav folder
	public void finalizeSetup(boolean firsttime, String username, String password) {
		File skenavDirectory = new File(skenavdirectory);
		File dbFile = new File(skenavdirectory + "database.mv.db");
		File hlsDirectory = new File(skenavdirectory + "hls");
		//File breadCrumb = new File(breadcrumbdirectory);
		if (!skenavDirectory.exists()) {
			final boolean mkdirs = skenavDirectory.mkdirs();
			System.out.println(mkdirs);

		}
		if (!dbFile.exists()) {
			Database.createTable();
			System.out.println("table created");
			Database database = new Database();
			if (firsttime == true) {
				Crypto.setCryptoSeed();
				Crypto crypto = new Crypto();
				byte[] key = crypto.newKey();
				String base64key = Crypto.base64Encode(key);
				System.out.println("key right after generation is: " + key);
				System.out.println(("base64 key immediately after encoding is" + base64key));
				database.addToAppData("cookie key", base64key);
				String passwordhash = Crypto.hashPassword(password);
				database.addUser(username, passwordhash, 0);
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
		Json json = new Json();
		json.setUploaddirectory(uploaddirectory);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.writeValue(new File(breadcrumbdirectory), json);
	}

	public void readBreadcrumb() throws IOException {
		String uploaddirectory;
		ObjectMapper mapper = new ObjectMapper();
		Json json = mapper.readValue(new File(breadcrumbdirectory), Json.class);
		uploaddirectory = json.getUploaddirectory();
		Cache.INSTANCE.setUploaddirectory(uploaddirectory);

	}
}
class Json {
	private String uploaddirectory;

	public void setUploaddirectory (String uploaddirectory) {
		this.uploaddirectory = uploaddirectory;
	}

	public String getUploaddirectory() {
		return uploaddirectory;
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
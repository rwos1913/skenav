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
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

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

	public void setupWithCli() {
		String username = null;
		String password = null;
		String uploadDirectory = OS.getHomeDirectory() + "usercontent" + OS.pathSeparator();
		for (int i = 0; i < 3; i++) {
			String outputtext = null;
			switch (i) {
				case 0:
					outputtext = "create admin username:";
					username = scanForInput(outputtext);
					break;
				case 1:
					outputtext = "create admin password:";
					password = scanForInput(outputtext);
					break;
				case 2:
					outputtext = "enter an upload directoy. press return to use default (" +uploadDirectory + ")";
					String inputtext = scanForInput(outputtext);
					if( !inputtext.equals("")) {
						uploadDirectory = inputtext;
					}
					break;
			}
		}
		System.out.println(username);
		System.out.println(password);
		System.out.println(uploadDirectory);
	}

	private String scanForInput(String outputtext){
		Scanner scanner = new Scanner(System.in);
		boolean validdata = false;
		String inputtext = null;
		do {
			System.out.println(outputtext);
			try {
				inputtext = scanner.nextLine();
				validdata = true;
			}catch (InputMismatchException e) {
				System.out.println("input must be a string");
			}
		}while (validdata==false);
		return inputtext;
	}

	// TODO: reformat so usercontent is in a subfolder of skenav folder
	public void finalizeSetup(boolean firsttime, String username, String password) {
		File skenavDirectory = new File(skenavdirectory);
		File dbFile = new File(skenavdirectory + "database.mv.db");
		File hlsDirectory = new File(skenavdirectory + "hls");
		File certificateDirectory = new File(skenavdirectory + "certificates");
		//File breadCrumb = new File(breadcrumbdirectory);
		if (!skenavDirectory.exists()) {
			final boolean mkdirs = skenavDirectory.mkdirs();
			System.out.println(mkdirs);

		}
		if (!dbFile.exists()) {
			Database database = new Database();
			database.createTable();
			System.out.println("table created");
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
		if (!certificateDirectory.exists()) {
			final boolean mkdirs = certificateDirectory.mkdirs();
			System.out.println(mkdirs);
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
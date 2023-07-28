package skenav.core;

import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;
import org.shredzone.acme4j.exception.AcmeException;
import skenav.core.db.Database;
//import skenav.core.security.CertificateManagement;
import skenav.core.security.CertificateManagement;
import skenav.core.security.Crypto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Setup {

	public Setup() {
	}


	public static void addNewUserDirectories(String username) {
		String userdirectorystring = OS.getUserContentDirectory() + username + OS.pathSeparator();
		File userdirectory = new File(userdirectorystring);
		File userhlsdirectory = new File(userdirectorystring + "hls");
		File userfiledirectory = new File(userdirectorystring + "files");
		if(!userdirectory.exists()){
			userdirectory.mkdirs();
		}
		if(!userhlsdirectory.exists()){
			userhlsdirectory.mkdirs();
		}
		if(!userfiledirectory.exists()){
			userfiledirectory.mkdirs();
		}
	}


	public void setupWithCli() {
		String username = null;
		String password = null;
		String uploadDirectory = OS.getHomeDirectory() + "usercontent" + OS.pathSeparator();
		String contact = null;
		String domain = null;
		for (int i = 0; i < 5; i++) {
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
					outputtext = "enter an upload directory. press return to use default (" +uploadDirectory + ")";
					String inputtext = scanForInput(outputtext);
					if( !inputtext.equals("")) {
						uploadDirectory = inputtext;
					}
					break;
				case 3:
					outputtext = "enter an email for Certificate Authority";
					contact = scanForInput(outputtext);
					break;
				case 4:
					outputtext = "please enter the domain name you are associating with this server's ip address";
					domain = scanForInput(outputtext);
					break;
			}
		}
		System.out.println(username);
		System.out.println(password);
		System.out.println(uploadDirectory);
		System.out.println(contact);
		System.out.println(domain);
		Cache.INSTANCE.setUploaddirectory(uploadDirectory);
		//TODO: get domain and contact from front end
		//TODO: support IP address certs
		Cache.INSTANCE.setContact(contact);
		Cache.INSTANCE.setDomain(domain);
		finalizeSetup(true, username, password);
		addNewUserDirectories(username);
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
		// this sets TLS
		SkenavConfiguration config = new SkenavConfiguration();
		boolean usetls = Boolean.parseBoolean(config.getUseTls());
		String usercontentdirectory = OS.getUserContentDirectory();
		File skenavdirectory = new File(OS.getSkenavDirectory());
		File usercontentdirectoryfile = new File(usercontentdirectory);
		File dbFile = new File(OS.getSkenavDirectory() + "database.mv.db");
		File certificateDirectory = new File(skenavdirectory + "certificates");
		if (!usercontentdirectoryfile.exists()) {
			final boolean mkdirs = usercontentdirectoryfile.mkdirs();
			System.out.println(mkdirs);

		}
		if (!skenavdirectory.exists()){
			final boolean mkdirs = skenavdirectory.mkdirs();
			System.out.println(mkdirs);
			if (mkdirs && OS.isWindows()) {
				Path path = Paths.get(OS.getSkenavDirectory());
				try {
					Files.setAttribute(path, "dos:hidden", Boolean.TRUE, LinkOption.NOFOLLOW_LINKS);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		if (!dbFile.exists()) {
			Database database = new Database();
			database.createTable();
			System.out.println("table created");
			if (firsttime) {
				Crypto.setCryptoSeed();
				Crypto crypto = new Crypto();
				byte[] key = crypto.newKey();
				String base64key = Crypto.base64Encode(key);
				System.out.println("key right after generation is: " + key);
				System.out.println(("base64 key immediately after encoding is" + base64key));
				database.addToAppData("cookie key", base64key);
				database.addToAppData("CA contact", Cache.INSTANCE.getContact());
				database.addToAppData("CA domain", Cache.INSTANCE.getDomain());
				database.addToAppData("usercontent directory", usercontentdirectory);
				database.addToAppData("usetls", Boolean.toString(usetls));
				String passwordhash = Crypto.hashPassword(password);
				database.addUser(username, passwordhash, 0);
			}
		}
		Database database = new Database();
		if(database.getAppData("usetls").equals("true")){usetls = true;}
		if (!new File(OS.getSkenavDirectory() + "SkenavKeyStore.jks").exists() && usetls == true && SkenavApplication.serverrunning) {
			try {
				CertificateManagement.createKeyStore();
				CertificateManagement.createAccount();
			} catch (AcmeException e) {
					throw new RuntimeException(e);
			} catch (CertificateException e) {
				throw new RuntimeException(e);
			} catch (KeyStoreException e) {
				throw new RuntimeException(e);
			} catch (IOException e) {
				throw new RuntimeException(e);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
			CertificateManagement.orderCert();
			System.out.println("certificate accquired, please restart to install it");
			System.exit(0);

		}

	}
	public static boolean checkDatabaseFile(){
		boolean result = false;
		if(new File(OS.getSkenavDirectory() + "database.mv.db").exists()){
			result = true;
		}
		return result;
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
package skenav.core;

import skenav.core.db.Database;
import skenav.core.security.Crypto;

public enum Cache {
	INSTANCE;

	private String uploaddirectory;

	private byte[] cookiekey;

	private String owner;

	private String tlsauthcontent;

	private String contact;

	private String domain;

	private int port;





	public int getPort(){
		if (port == 0) {
			//TODO: add port number setting
			//Database database = new Database();
			//port = Integer.parseInt(database.getAppData("port"));
		}
		return port;
	}
	public void setPort(int port){
		this.port = port;
	}
	public String getDomain(){return domain;}
	public void setDomain(String domain){
		this.domain = domain;
	}
	public String getContact(){
		System.out.println("contact from cache getter is " + contact);
		return contact;}
	public void setContact(String contact) {
		this.contact = contact;
	}
	public String getTlsAuthContent() {
		return tlsauthcontent;
	}
	public void setTlsAuthContent (String tlsauthcontent) {
		this.tlsauthcontent = tlsauthcontent;
	}

	public String getUploaddirectory() {
		System.out.println("upload directory from cache get method is: " + uploaddirectory);
		if (uploaddirectory == null) {
			Database database = new Database();
			uploaddirectory = database.getAppData("usercontent directory");
			System.out.println("cache database appdata triggered");
		}
		return uploaddirectory;
	}

	public void setUploaddirectory (String uploaddirectory) {
		this.uploaddirectory = uploaddirectory;
		System.out.println("uploaddirectory in cache set method is: " + uploaddirectory);
	}
	public String getOwner() {
		if (owner == null) {
			Database database = new Database();
			owner = database.getSkenavOwner();
		}
		return owner;
	}
	public byte[] getCookieKey() {
		System.out.println("cookie key from cache is" + cookiekey);
		if (cookiekey == null) {
			System.out.println("cookie key was null and enum was updated");
			Database database = new Database();
			String base64Key = database.getAppData("cookie key");
			System.out.println("base64 from cache db call is" + base64Key);
			cookiekey = Crypto.base64Decode(base64Key);
			System.out.println("key from cache is" + cookiekey);
		}
		return cookiekey;
	}

	private boolean tlsalreadyset = false;

	public boolean getTlsAlreadySet () {
		if (!tlsalreadyset){
			tlsalreadyset = true;
			return false;
		}
		else{
			return true;
		}

	}


}

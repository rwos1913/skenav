package skenav.core;

import skenav.core.db.Database;
import skenav.core.security.Crypto;

public enum Cache {
	INSTANCE;

	private String uploaddirectory;

	private byte[] cookiekey;

	private String owner;

	private String tlsauthcontent;

	public String getTlsAuthContent() {
		return tlsauthcontent;
	}
	public void setTlsAuthContent (String tlsauthcontent) {
		this.tlsauthcontent = tlsauthcontent;
	}

	public String getUploaddirectory() {
		System.out.println("upload directory from cache get method is: " + uploaddirectory);
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


}

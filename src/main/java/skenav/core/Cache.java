package skenav.core;

public enum Cache {
	INSTANCE;

	private String uploaddirectory;

	public String getUploaddirectory() {
		return uploaddirectory;
	}

	public void setUploaddirectory (String uploaddirectory) {
		this.uploaddirectory = uploaddirectory;
	}
}

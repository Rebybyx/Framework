package com.zh.activiti.entity;

public class Version implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5766873310660948816L;
	
	private String version;
	private String name;
	private String date;
	private String url;
	private int imageVersion;
	private String imageUrl;
	private String msg;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}


	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getImageVersion() {
		return imageVersion;
	}

	public void setImageVersion(int imageVersion) {
		this.imageVersion = imageVersion;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
}

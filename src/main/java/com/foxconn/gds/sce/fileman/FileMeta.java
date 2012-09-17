package com.foxconn.gds.sce.fileman;

public class FileMeta {

	private String name;
	private long size ;
	private String url;
	private String thumbnail_url ;
	private String delete_url ;
	private String delete_type = "DELETE";

	public FileMeta(String filename, long size, String url) {
		this.name = filename;
		this.size = size;
		this.url = url;
		this.delete_url = url;
		this.delete_type = "DELETE";
	}

	public FileMeta() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDelete_url() {
		return delete_url;
	}

	public void setDelete_url(String delete_url) {
		this.delete_url = delete_url;
	}

	public String getDelete_type() {
		return delete_type;
	}

	public void setDelete_type(String delete_type) {
		this.delete_type = delete_type;
	}

	public String getThumbnail_url() {
		return thumbnail_url;
	}

	public void setThumbnail_url(String thumbnail_url) {
		this.thumbnail_url = thumbnail_url;
	}
	
	
	
}

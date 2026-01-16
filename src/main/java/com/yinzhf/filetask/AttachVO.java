package com.yinzhf.filetask;

import java.io.Serializable;

/**
 * 附件信息对象
 * @author yinzhf
 * @since 2023-03-01
 */
public class AttachVO implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 3613257627240073551L;
	private String fid;
	private String viewUrl;
	private String name;
	private String contentType;
	private byte[] contents;

	public AttachVO() {

	}

	public AttachVO(String fid, String viewUrl) {
		super();
		this.fid = fid;
		this.viewUrl = viewUrl;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getViewUrl() {
		return viewUrl;
	}
	public void setViewUrl(String viewUrl) {
		this.viewUrl = viewUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getContents() {
		return contents;
	}

	public void setContents(byte[] contents) {
		this.contents = contents;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}

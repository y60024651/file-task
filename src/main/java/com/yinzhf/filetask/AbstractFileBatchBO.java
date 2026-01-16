package com.yinzhf.filetask;

public abstract class AbstractFileBatchBO implements FileBusinessObject {
	private String operType;
	private TaskCursor cursor;
	private BOFileBatchTask fileBatchTask;
	private String submitParams;
	private String operUser;

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public TaskCursor getCursor() {
		return cursor;
	}

	public void setCursor(TaskCursor cursor) {
		this.cursor = cursor;
	}

	public BOFileBatchTask getFileBatchTask() {
		return fileBatchTask;
	}

	public void setFileBatchTask(BOFileBatchTask fileBatchTask) {
		this.fileBatchTask = fileBatchTask;
	}

	public String getSubmitParams() {
		return submitParams;
	}

	public void setSubmitParams(String submitParams) {
		this.submitParams = submitParams;
	}

	public String getOperUser() {
		return operUser;
	}

	public void setOperUser(String operUser) {
		this.operUser = operUser;
	}
}

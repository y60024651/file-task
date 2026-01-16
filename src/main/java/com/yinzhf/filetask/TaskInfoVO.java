package com.yinzhf.filetask;

public class TaskInfoVO {
	private String id;
	private String name;
	private String type;//任务类型
	private String operatorId;
	private String operatorAccount;
	private String operatorName;
	private String operType;//操作类型
	private String processWay;//处理方式：前台或后台
	private String bsBusinessObject;//后台业务对象
	private int totalNum;
	private int successNum;
	private int failNum;
	private long userTime;
	private String failLog;
	private String processFileName;//任务处理的文件名

	public TaskInfoVO() {

	}

	public TaskInfoVO(String id) {
		this.id = id;
	}

	public TaskInfoVO(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProcessWay() {
		return processWay;
	}

	public void setProcessWay(String processWay) {
		this.processWay = processWay;
	}

	public String getBsBusinessObject() {
		return bsBusinessObject;
	}

	public void setBsBusinessObject(String bsBusinessObject) {
		this.bsBusinessObject = bsBusinessObject;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getOperatorName() {
		return operatorName;
	}

	public void setOperatorName(String operatorName) {
		this.operatorName = operatorName;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getSuccessNum() {
		return successNum;
	}

	public void setSuccessNum(int successNum) {
		this.successNum = successNum;
	}

	public int getFailNum() {
		return failNum;
	}

	public void setFailNum(int failNum) {
		this.failNum = failNum;
	}

	public long getUserTime() {
		return userTime;
	}

	public void setUserTime(long userTime) {
		this.userTime = userTime;
	}

	public String getFailLog() {
		return failLog;
	}

	public void setFailLog(String failLog) {
		this.failLog = failLog;
	}

	public String getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getOperatorAccount() {
		return operatorAccount;
	}

	public void setOperatorAccount(String operatorAccount) {
		this.operatorAccount = operatorAccount;
	}

	public String getProcessFileName() {
		return processFileName;
	}

	public void setProcessFileName(String processFileName) {
		this.processFileName = processFileName;
	}
}

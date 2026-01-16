package com.yinzhf.filetask;

import java.util.HashMap;
import java.util.Map;

public class GeneralBatchResult implements BatchResult {

	private static final long serialVersionUID = -2110195895091030143L;

	private Integer code;

    private int totalCount;

    private int current;

    private int ok;

    private int fail;

    private boolean success;

    private String msg;

    private String uploadFileName;

    private String saveFileName;

    private String errorFileName;

    private String successFileName;

    private Object data;

    private Map<Integer,String> failRecords = new HashMap<>();//失败记录位置，失败原因

    public GeneralBatchResult() {
        if(success) {
    		this.code = 200;
    	} else {
    		this.code = 1001;
    	}
    }

	public GeneralBatchResult(boolean success, String msg) {
		this.success = success;
		this.msg = msg;
		if(success) {
    		this.code = 200;
    	} else {
    		this.code = 1001;
    	}
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurrent() {
		return current;
	}

	public void setCurrent(int current) {
		this.current = current;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
		if(success) {
    		this.code = 200;
    	} else {
    		this.code = 1001;
    	}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getInputFileName() {
		return uploadFileName;
	}

	public void setInputFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	public String getOutputFileName() {
		return saveFileName;
	}

	public void setOutputFileName(String saveFileName) {
		this.saveFileName = saveFileName;
	}

	public int getOk() {
		return ok;
	}

	public void setOk(int ok) {
		this.ok = ok;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getErrorFileName() {
		return errorFileName;
	}

	public void setErrorFileName(String errorFileName) {
		this.errorFileName = errorFileName;
	}

	public String getSuccessFileName() {
		return successFileName;
	}

	public void setSuccessFileName(String successFileName) {
		this.successFileName = successFileName;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return msg;
	}

	public void setMessage(String msg) {
		this.msg = msg;
	}

	public Map<Integer, String> getFailRecords() {
		return failRecords;
	}

	public void setFailRecords(Map<Integer, String> failRecords) {
		this.failRecords = failRecords;
	}

	public void addFailRecord(Integer position, String msg) {
		this.failRecords.put(position, msg);
	}


}

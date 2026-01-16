package com.yinzhf.filetask;

public class GeneralResult implements Result{
	private static final long serialVersionUID = -940139127925466172L;

	private Integer code;

	private String msg;

    private boolean success = false;

    private int totalCount;

    private int current;

    private Object data;

    public GeneralResult() {
    }

    public GeneralResult(boolean succ) {
    	success = succ;
    	if(success) {
    		this.code = 200;
    	} else {
    		this.code = 1001;
    	}
    }

    public void setSuccess(boolean ok) {
        this.success = ok;
        if(success) {
    		this.code = 200;
    	} else {
    		this.code = 1001;
    	}
    }

    public boolean isSuccess() {
        return success;
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

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
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
}

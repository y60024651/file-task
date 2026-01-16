package com.yinzhf.filetask;

import java.io.Serializable;

public interface Result extends Serializable{

	public void setSuccess(boolean ok);

	public boolean isSuccess();

	public String getMsg();

	public void setMsg(String msg);

	public Object getData();
	public void setData(Object data);

	public Integer getCode();

	public void setCode(Integer code);

	public String getMessage();
	public void setMessage(String msg);
}

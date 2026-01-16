package com.yinzhf.filetask;

import java.io.UnsupportedEncodingException;

public abstract class AbstractResultFile {

    public int resultType = TOW_RES_FILE;//结果文件用一个还是两个
    public static final int ONE_RES_FILE = 1;
    public static final int TOW_RES_FILE = 2;

    protected String resultFileName;

    protected String sucResultFileName;

    protected String errResultFileName;

    protected int all_count;

    protected int ok_count;

    protected int fail_count;

    protected boolean needSuccFile;//是否需要写成功文件

    protected String contentType;


    public int getResultType() {
        return resultType;
    }

    public void setResultType(int resultType) {
        this.resultType = resultType;
    }

	public boolean isNeedSuccFile() {
		return needSuccFile;
	}

	public void setNeedSuccFile(boolean needSuccFile) {
		this.needSuccFile = needSuccFile;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

    protected String newLine(String line) throws UnsupportedEncodingException {
        return new String(line.getBytes("GBK"), "iso8859_1") + "\r\n";
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public String getSucResultFileName() {
        return sucResultFileName;
    }

    public void setSucResultFileName(String sucResultFileName) {
        this.sucResultFileName = sucResultFileName;
    }

    public String getErrResultFileName() {
        return errResultFileName;
    }

    public void setErrResultFileName(String errResultFileName) {
        this.errResultFileName = errResultFileName;
    }

    public int getSuccessSerial() {
        if(ONE_RES_FILE == resultType) {
            return all_count + 1;
        } else if(TOW_RES_FILE == resultType) {
            return ok_count + 1;
        }
        return 0;
    }

    public int getErrorSerial() {
        if(ONE_RES_FILE == resultType) {
            return all_count + 1;
        } else if(TOW_RES_FILE == resultType) {
            return fail_count + 1;
        }
        return 0;
    }

    public int getCurrentCount() {
    	return all_count;
    }

    public int getSuccessCount() {
        return ok_count;
    }

    public int getErrorCount() {
        return fail_count;
    }

}

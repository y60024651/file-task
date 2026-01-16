package com.yinzhf.filetask;

import java.io.Serializable;

public class TaskCursor implements Serializable{
	private static final long serialVersionUID = 2636651758820376149L;
	private int totalRecords; //总需要处理数据量（如文件的行数）
	private int currentRecord = 0;//当前已处理的数据量
	private int totalProcedure; //总步骤数,用于计算完成率，由于处理完文件不一定表示批量结束，可能还有后续的处理
    private int currentProcedure = 0;
    private int fail;
    private int ok;

    public float getPercent() {
        if ( totalProcedure == 0 ) {
            return 0;
        }
        float res = (float)currentRecord / (float)totalRecords;
        return res;
    }

    public boolean isCompleted() {
        if ( totalRecords == 0 ) {
            return true;
        }
        return currentRecord >= totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
		this.totalRecords = totalRecords;
		//默认总步骤数为总数据量+1，即增加一个结尾步骤
		if(this.totalRecords == 0) {
			this.totalProcedure = 0;
		} else {
			this.totalProcedure = this.totalRecords + 1;
		}
	}

    public void addCurrentRecord(int num) {
		currentRecord += num;
		currentProcedure += num;
	}

    public int getTotalRecords() {
        return totalRecords;
    }

	public int getCurrentRecord() {
		return currentRecord;
	}

	public void setCurrentRecord(int currentRecord) {
		this.currentRecord = currentRecord;
	}

	public void addCurrentProcedure(int num) {
		currentProcedure += num;
	}

	public int getTotalProcedure() {
		return totalProcedure;
	}

	public void setTotalProcedure(int totalProcedure) {
		this.totalProcedure = totalProcedure;
	}

	public int getCurrentProcedure() {
		return currentProcedure;
	}

	public void resset() {
		this.currentProcedure = 0;
		this.currentRecord = 0;
		this.ok = 0;
		this.fail = 0;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}

	public void setOk(int ok) {
		this.ok = ok;
	}

    public int getFail() {
        return fail;
    }

    public int getOk() {
        return ok;
    }
}

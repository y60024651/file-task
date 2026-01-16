package com.yinzhf.filetask;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.time.StopWatch;

import java.io.Serializable;

/**
 * 批量数据处理任务基本信息：处理进度、成功失败数、时间等
 * @author yinzf
 * @since 2023-02-28
 */
public abstract class BaseBatchTask implements Runnable, Serializable {
	private static final long serialVersionUID = -2922357744190939650L;
	protected String id;
	protected String name;
	protected String type;//任务类型
	protected String way;//处理方式，例如：前台or后台

	public boolean started;
	public boolean finished;
	public boolean running;

	protected long useTime;

	protected Serializable user;
	protected transient StopWatch watch;

    protected BatchResult result;
    /**
     * 从文件第几行开始处理,第一行是0
     */
    protected int beginIndex;

    protected TaskCursor cursor = new TaskCursor();
    /**
     * 一个事务中处理的数据量，默认为1
     */
    protected int batchSize = 1;
	protected boolean autoBatchSize;
	/** 操作类型 */
	protected String operType;

    public BaseBatchTask() {
        super();
        watch = new StopWatch();
    }

    public abstract void run();

    public float getPercent() {
        return cursor.getPercent();
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * 游标判断
     */
    public boolean isCompleted() {
        return cursor.isCompleted();
    }

    public boolean isRunning() {
        return running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    protected synchronized void start() {
    	started = true;
    	this.finished = false;
    	watch.reset();
        watch.start();
        this.running = true;
    }

    protected synchronized void finish() {
    	this.started = false;
    	this.finished = true;
    	watch.stop();
    	this.running = false;
    }

    public int getTotalRecords() {
        return cursor.getTotalRecords();
    }

	public int getCurrentRecord() {
		return cursor.getCurrentRecord();
	}

	public void setTotalRecords(int totalRecords) {
		cursor.setTotalRecords(totalRecords);
		if(isAutoBatchSize()) {
			if(getBatchSize() < 100) {
				int avg = totalRecords / 100;
				if(avg <= 0 ) {
					avg = 1;
				}
				if(avg > 100) {
					avg = 100;
				}
				setBatchSize(avg);
			}
		}
	}

	public BatchResult getResult() {
		return result;
	}

	public void setResult(BatchResult result) {
		this.result = result;
	}

	public long getUseTime() {
		if(watch != null) {
			useTime = watch.getTime();
			return useTime / 1000;
		}
		return useTime;
	}

	public void setCurrentRecord(int currentRecord) {
		cursor.setCurrentRecord(currentRecord);
	}

	protected void addCurrentRecord(int num) {
		cursor.addCurrentRecord(num);
	}

	protected void addCurrentProcedure(int num) {
		cursor.addCurrentProcedure(num);
	}

	public int getBeginIndex() {
		return beginIndex;
	}

	public void setBeginIndex(int beginIndex) {
		this.beginIndex = beginIndex;
	}

	public boolean isFinished() {
		return finished;
	}

	public void stop() {
		this.running = false;
	}

	public int getTotalProcedure() {
		return cursor.getTotalProcedure();
	}

	protected void setTotalProcedure(int totalProcedure) {
		cursor.setTotalProcedure(totalProcedure);
	}

	public int getCurrentProcedure() {
		return cursor.getCurrentProcedure();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Serializable getUser() {
		return user;
	}

	public void setUser(Serializable user) {
		this.user = user;
	}

	public boolean equals(Object other) {
		if (!(other instanceof BaseBatchTask))
			return false;
		BaseBatchTask castOther = (BaseBatchTask) other;
		return new EqualsBuilder().append(this.getId(),
				castOther.getId()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(getId()).toHashCode();
	}

	public void setFail(int fail) {
		this.cursor.setFail(fail);
	}

	public void setOk(int ok) {
		this.cursor.setOk(ok);
	}

    public int getFail() {
        return cursor.getFail();
    }

    public int getOk() {
        return cursor.getOk();
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TaskCursor getCursor() {
		return cursor;
	}

	public String getWay() {
		return way;
	}

	public void setWay(String way) {
		this.way = way;
	}

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchCount) {
		this.batchSize = batchCount;
	}

	public String getOperType() {
		return operType;
	}

	public void setOperType(String operType) {
		this.operType = operType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAutoBatchSize() {
		return autoBatchSize;
	}

	public void setAutoBatchSize(boolean autoBatchSize) {
		this.autoBatchSize = autoBatchSize;
	}

}

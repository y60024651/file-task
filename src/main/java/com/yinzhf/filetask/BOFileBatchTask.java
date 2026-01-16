package com.yinzhf.filetask;

import jxl.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 把任务常用的处理步骤使用事件的方式调用，事件方法由传入的BusinessObject实现。
 *
 * 已定义的事件类型有：
 * initialization：任务开始时的初始化操作
 *
 * destory：任务结束时的资源释放操作
 *
 * getTitle：获取文件的标题头，即要处理的数据格式，字段之间默认用‘|’分隔
 *
 * doProcessRecord：txt文件数据的处理方法
 *
 * doProcessRecord&excel：excel03文件数据的处理方法
 *
 * finalWork：文件处理完成功后运行此方法
 *
 * @author yinzhf
 * @since 2023-03-01
 */
public class BOFileBatchTask extends FileBatchTask {
	private static final long serialVersionUID = 1059345974980129705L;
	protected transient Logger log = LoggerFactory.getLogger(this.getClass());
	protected transient FileBusinessObject bo;
	/**
	 * 是否整个任务都成功，即所有子任务都成功
	 */
	protected Boolean wholeTaskSuccess;

	public BOFileBatchTask() {
		super();
	}

	public BOFileBatchTask(FileBusinessObject bo) {
		super();
		this.bo = bo;
	}

	@Override
	protected void destory() {
		bo.destory();
	}

	@Override
	protected String getTitle() {
		return bo.getTitle();
	}

	@Override
	protected Boolean initialization() {
		boolean parent = super.initialization();
		if(parent) {
			return bo.initialization();
		}
		return false;
	}

	@Override
	protected Boolean finalWork() {
		Boolean flag = super.finalWork();
		if(flag) {
			try {
				flag = bo.finalWork();
				if(flag != null) {
					wholeTaskSuccess = flag;
				}
				return flag;
			} catch (Exception e) {
				log.error("finalWork", e);
			}
		}
		return false;
	}

	@Override
	protected BatchResult doProcessRecord(int sheetLocation, String[] line, int size) {
		try {
			return (BatchResult)bo.doProcessRecord(sheetLocation, line, size);
		} catch (Exception e) {
			log.error("doProcessRecord", e);
			BatchResult result = new GeneralBatchResult();
			result.setSuccess(false);
			result.setMsg(e.getMessage());
			return result;
		}
	}

	/**
	 * excel2007
	 */
	@Override
	protected BatchResult doProcessRecord(int sheetLocation, Row[] rows, int size) {
		try {
			BatchResult res = (BatchResult)bo.doProcessRecord(sheetLocation, rows, size);
			if(res == null) {
				return super.doProcessRecord(sheetLocation, rows, size);
			}
			return res;
		} catch (Exception e) {
			log.error("doProcessRecord_Cell", e);
			BatchResult result = new GeneralBatchResult();
			result.setSuccess(false);
			result.setMsg(e.getMessage());
			return result;
		}
    }

	/**
	 * excel2003
	 */
	@Override
	protected BatchResult doProcessRecord(Cell[][] row, int size) {
		try {
			BatchResult res = (BatchResult)bo.doProcessRecord(row, size);
			if(res == null) {
				return super.doProcessRecord(row, size);
			}
			return res;
		} catch (Exception e) {
			log.error("doProcessRecord_Cell", e);
			BatchResult result = new GeneralBatchResult();
			result.setSuccess(false);
			result.setMsg(e.getMessage());
			return result;
		}
	}

	public Boolean getWholeTaskSuccess() {
		return wholeTaskSuccess;
	}

	public void setWholeTaskSuccess(Boolean wholeTaskSuccess) {
		this.wholeTaskSuccess = wholeTaskSuccess;
	}

	public FileBusinessObject getBo() {
		return bo;
	}

}

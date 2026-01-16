package com.yinzhf.filetask;

import jxl.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

/**
 * 处理文件数据的业务对象接口
 * @author yinzhf
 * @since 2023-03-01
 */
public interface FileBusinessObject {
	String getTitle();

	void destory();

	Boolean initialization();

	/**
	 * 所有子任务完成后的最后处理工作，返回整个任务是否成功完成，
	 * 只要有一个子任务失败，则应该返回false;
	 * @return
	 */
	Boolean finalWork();

//	public BatchResult doProcessRecord(String[] line, int size);

	BatchResult doProcessRecord(int sheetLocation, String[] line, int size);

	BatchResult doProcessRecord(int sheetLocation, Row[] rows, int size);

	BatchResult doProcessRecord(Cell[][] row, int size);

	BatchResult doProcessRecord(Map<Integer, ?> datas);
}

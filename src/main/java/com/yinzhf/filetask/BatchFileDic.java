package com.yinzhf.filetask;

public final class BatchFileDic {
	//任务状态字典
	/** 正在入库 */
	public final static String IMPORTING = "IMPORTING";
	/** 入库失败 */
	public final static String IMPORTERROR = "IMPORTERROR";
	/** 入库成功，待处理 */
	public final static String WAIT = "WAIT";
	/** 处理中 */
	public final static String PROCESSING = "PROCESSING";
	/** 暂停 */
	public final static String SUSPEND = "SUSPEND";
	/** 已完成 */
	public final static String FINISH = "FINISH";
	/** 非正常结束，可能部分数据已丢失 */
	public final static String FAILEND = "FAILEND";

	//任务处理方式字典
	/** 前台处理 */
	public final static String PW_F = "F";
	/** 前台把数据入库，后台从库读数据处理业务 */
	public final static String PW_FB = "F+B";
	/** 前台入库中 */
	public final static String PW_FB_F = "F+B(F)";
	/** 后台台处理中 */
	public final static String PW_FB_B = "F+B(B)";

}

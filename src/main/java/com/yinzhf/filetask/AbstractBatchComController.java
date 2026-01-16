package com.yinzhf.filetask;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * 文件组件处理器基础层
 * @author yinzhf
 * @since 2023-03-01
 */
public abstract class AbstractBatchComController {
	private Logger log = LoggerFactory.getLogger(this.getClass());

	protected FileBatchTask getBatchTask(FileBusinessObject bo) {
		BOFileBatchTask batchTask = new BOFileBatchTask(bo);
		batchTask.setShowRate(true);//默认后台打印进度
		return batchTask;
	}

	protected void initBatchTask(FileBatchTask batchTask, FileBusinessObject bo) {
		try {
			BeanUtils.setProperty(bo, "cursor", batchTask.getCursor());
			// BeanUtils.setProperty(bo, "userInfo", SecurityUtils.getLoginUser().getUser());
			BeanUtils.setProperty(bo, "fileBatchTask", batchTask);
		} catch (Exception e) {
			log.error("设置业务对象参数失败",e);
		}
	}

	protected void initializeBatchTask(FileBatchTask batch,
			String fileFullName, String fileName, String batchType, int totalCount) {
		//设置任务主键ID
		batch.setId(UUID.randomUUID().toString().replace("-", ""));
		//设置任务的操作者
//		if(batch.getUser() == null) {
//			batch.setUser(SecurityUtils.getLoginUser().getUser());
//		}
		//设置任务类型
		batch.setType(batchType);
		//前台处理
		batch.setWay(BatchFileDic.PW_F);
		//设置处理的文件全路径
		batch.setFilename(fileFullName);
		batch.setUploadFileName(fileName);
		batch.setName(fileName);
		batch.setAutoBatchSize(false);
		//获取文件总需要处理的行数
		if (totalCount > 0) {
			batch.setTotalRecords(totalCount);
		} else {
			batch.updateTotalRecords();
			log.info(fileFullName+",beginIndex="+batch.getBeginIndex()+",totalrecords="+batch.getTotalRecords());
		}
		try {
			Object bo = PropertyUtils.getProperty(batch, "bo");
			if(BeanUtils.getProperty(bo, "operType") == null) {
				BeanUtils.setProperty(bo, "operType", batch.getOperType());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void beginProcess(BaseBatchTask batch) throws Exception {
		batch.start();
	}

	protected void beginBatchTask(FileBatchTask batch,
			String fileFullName, String fileName,String batchType, int totalCount) throws Exception {
		initializeBatchTask(batch, fileFullName, fileName, batchType, totalCount);

		beginProcess(batch);
	}

	/**
	 * 接收文件请求，提交文件至批处理任务
	 * @param bo 前台业务对象（负责把文件数据存至数据库）
	 * @param fileFullName  完整文件路径
	 * @param fileName		上传的文件名
	 * @param totalCount    文件行数
	 * @return
	 */
	public Result doBatchProcess(FileBusinessObject bo,
			String fileFullName, String fileName,String batchType, int totalCount) {
		Result res = new GeneralResult();
		try {
			FileBatchTask batch = getBatchTask(bo);
			initBatchTask(batch, bo);
			beginBatchTask(batch, fileFullName, fileName, batchType, totalCount);
			res.setData(batch.getId());
			res.setSuccess(true);
		} catch (Exception e) {
			log.error("doBatchProcess", e);
			res.setSuccess(false);
			res.setMsg(e.getMessage());
		}
		return res;
	}
}

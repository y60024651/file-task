package com.yinzhf.filetask;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Map;

/**
 * 文件数据批量处理任务，支持xls、txt文件
 * 逻辑：
 * 对于文件里的每条数据，调用doProcessRecord方法（由子类实现），
 * 根据doProcessRecord返回的Result记录成功失败数，并更新处理进度信息
 * @author yinzhf
 * @since 2023-03-01
 */
public abstract class FileBatchTask extends BaseBatchTask {
	private static final long serialVersionUID = 2285103252677431960L;

	private transient Logger log = LoggerFactory.getLogger(FileBatchTask.class);
	protected String filename;//待处理的文件
    protected String uploadFileName;//上传的文件名
    protected String fileContentType;

    protected int resultType = AbstractResultFile.TOW_RES_FILE;// 结果文件用一个还是两个

    protected boolean needSuccFile = false;//是否需要写成功文件,resultType为TOW_RES_FILE时才有用

    protected transient AbstractResultFile resultFile;//存放处理结果文件

    protected boolean deleteAfterProcess = false;

    protected int currentReadRow = 0;//当前正在读取的行号
	protected boolean fileEnd = false;

    protected boolean showRate = false;//是否显示进度
    protected int rateRadix = 10;//显示进度的间隔数

	protected transient String bsBusinessObject;//后台业务对象

	protected int sheetNumber = 1;//处理几个sheet

    public FileBatchTask() {
    	super();
    }

    /**
	 * 线程运行
	 */
    @SuppressWarnings("unchecked")
	public void run() {
		result = new GeneralBatchResult();
		try {
			start();
			if(!initialization()) {
				result.setSuccess(false);
				result.setMsg("初始化失败");
				finalWork();
				return;
			}
			try {
				while (isRunning() && !isCompleted() && !isFileEnd()) {
					work();
					printRateOfProcess();
				}
			} catch (Throwable ex) {
				while (ex.getCause() != null) {
					ex = ex.getCause();
				}
				log.error(ex.getMessage(), ex);
				result.setSuccess(false);
				result.setMsg(ex.toString());
			}
			Boolean finalSucc = false;
//			if(isRunning()) {
			finalSucc = finalWork();
			addCurrentProcedure(1);
//			}
			result.setSuccess(finalSucc);
			if(!finalSucc) {
				result.setMsg("文件处理完成，结束步骤出错");
			}
			result.setTotalCount(getTotalRecords());
			result.setCurrent(getCurrentRecord());
			result.setInputFileName(getFilename());
			if(resultType == AbstractResultFile.ONE_RES_FILE) {
				result.setOutputFileName(getResultFile());
			} else {
				//失败成功分开两个文件
				result.setOutputFileName(getErrResultFile());
				result.setErrorFileName(getErrResultFile());
				result.setSuccessFileName(getSucResultFile());
			}
			result.setOk(getOk());
			result.setFail(getFail());
		} finally {
			destory();
			finish();
			printRateOfProcess();
		}
	}

    protected Boolean initialization() {
    	return true;
    }

    protected abstract void destory();

    /**
     * 最后的工作。导入完成后可能还有些后续的处理
     * @throws Exception
     */
    protected Boolean finalWork() {
    	return true;
    }

    /**
	 * 工作
	 */
    private void work() throws Exception {
		if (null == fileContentType) {
			fileContentType = ContentType.getContentType(filename);
		}
		if (fileContentType.equals(ContentType.TEXT)) {
			doProcessFile(ContentType.TEXT);
		} else if (fileContentType.equals(ContentType.CSV)) {
			doProcessFile(ContentType.CSV);
		} else if (fileContentType.equals(ContentType.EXCEL)) {
			doProcessExcelFile();
		} else if (fileContentType.equals(ContentType.EXCELX)) {
			doProcessExcelXFile();
		} else {
			throw new UnsupportedOperationException("不支持此类型文件的处理");
		}
	}

    /**
	 * 默认处理text文件
	 *
	 */
    private void doProcessFile(String contentType) throws Exception {
        resultFile = new TxtXlsResultFile(resultType);
        TxtXlsResultFile txtRf = (TxtXlsResultFile)resultFile;
        resultFile.setNeedSuccFile(needSuccFile);
        resultFile.setContentType(contentType);
        txtRf.initialize(filename);
        RandomAccessFile rin = new RandomAccessFile(filename, "r");
        try {
        	txtRf.openFile();
            long length = rin.length(); // 文件长度
            long filePointer = rin.getFilePointer(); // 文件游标
            if ( length == 0 ) {
                return;
            }
            // 写结果处理文件的标头
            String title = getTitle();
            if(title != null) {
            	txtRf.writeTitle(title);
            }
            int nowReadRow = 0;
            while (filePointer < length && !isCompleted() && isRunning()) {
            	String[] records = new String[batchSize];
            	int currentBatchNum = 0;
            	while(currentBatchNum < batchSize && filePointer < length) {
            		String line = rin.readLine();
            		++nowReadRow;
            		if(nowReadRow <= beginIndex || nowReadRow <= currentReadRow) {
            			continue;
            		}
            		if ( StringUtils.isNotBlank(line) ) {
            			line = new String(line.getBytes("ISO-8859-1"), "GBK");
            			records[currentBatchNum++] = line;
            			++currentReadRow;
            		}
            		filePointer = rin.getFilePointer();
            	}

            	// 关键处理
                BatchResult resultVO = doProcessRecord(0, records, currentBatchNum);
                if ( resultVO.isSuccess() ) {
                    // 组合字段写成功记录
                	Map<Integer,String> failRecords = resultVO.getFailRecords();
                	for(int i = 0; i < currentBatchNum; ++i) {
                		String line = records[i];
                		if(failRecords != null && failRecords.containsKey(i)) {//存在部分失败记录
                			String resultStr = makeFailLine(contentType, failRecords.get(i), line, resultFile.getCurrentCount());
                			txtRf.writeErrorRecord(resultStr);
                		} else {
                			String resultStr = makeResultLine(contentType, resultVO, line,
                					resultFile.getSuccessSerial());
                			txtRf.writeSuccessRecord(resultStr);
                		}
                	}
                } else {
                	BatchResult transactionError = new GeneralBatchResult();
//                	BeanUtils.copyProperties(transactionError, resultVO);
                	transactionError.setMsg("事务回滚");
                    // 组合字段,写失败记录
                	for(int i = 0; i < currentBatchNum; ++i) {
                		String line = records[i];
                		String resultStr;
                		if(i == resultVO.getCurrent() || resultVO.getCurrent() == -1) {
                			resultStr = makeResultLine(contentType, resultVO, line,
                					resultFile.getCurrentCount());
                		} else {
                			resultStr = makeResultLine(contentType, transactionError, line,
                					resultFile.getCurrentCount());
                		}
                		txtRf.writeErrorRecord(resultStr);
                	}
                }
                addCurrentRecord(currentBatchNum);

                filePointer = rin.getFilePointer();
                setFail(resultFile.getErrorCount());
                setOk(resultFile.getSuccessCount());
                printRateOfProcess();
            }
            if(filePointer >= length) {
            	setFileEnd(true);
            }
        } finally {
            if ( rin != null ) {
                rin.close();
            }
            txtRf.close();
            if(deleteAfterProcess) {
            	FileUtils.delete(filename);
            }
            if(getFail() == 0) {
            	txtRf.deleteErrorFile();
            }
        }
    }

	protected void printRateOfProcess() {
		if(showRate) {
			if(getCurrentRecord() % rateRadix == 0 || getCurrentRecord() == getTotalRecords()) {
				NumberFormat nf = NumberFormat.getPercentInstance();
		    	nf.setMaximumFractionDigits(1);
		    	nf.setMinimumFractionDigits(1);
		    	String precent = nf.format(getCursor().getPercent());
				log.info(precent+":File="+getName()+
						",Total:"+getTotalRecords()+",Success:"+getOk()+",Fail:"+getFail());
			}
		}
	}

    /**
     * 处理Excel文件
     * @throws Exception
     */
    private void doProcessExcelFile() throws Exception{
    	resultFile = new TxtXlsResultFile(AbstractResultFile.TOW_RES_FILE);
        resultFile.setNeedSuccFile(needSuccFile);
        resultFile.setContentType(ContentType.EXCEL);
        TxtXlsResultFile xlsRf = (TxtXlsResultFile)resultFile;
        xlsRf.initialize(filename);
        jxl.Workbook wwb = null;
    	try {
    		xlsRf.openFile();
    		String title = getTitle();
            if(title != null) {
            	xlsRf.writeTitle(title);
            }
			wwb = jxl.Workbook.getWorkbook(new BufferedInputStream(new FileInputStream(filename)));
			jxl.Sheet sheet = wwb.getSheet(0);
			if(currentReadRow < beginIndex){
				currentReadRow = beginIndex;
			}
			while( currentReadRow < sheet.getRows() && !isCompleted() && isRunning() ) {
				int currentBatchNum = 0;
				jxl.Cell[][] records = new jxl.Cell[batchSize][];
				for(;currentBatchNum < batchSize && currentReadRow < sheet.getRows(); ++currentReadRow) {
					jxl.Cell[] cells = sheet.getRow(currentReadRow);
					//不处理空行
					if(JxlUtil.isEmptyRow(cells)) {
						continue;
					}
					records[currentBatchNum++] = cells;
				}
				BatchResult resultVO = doProcessRecord(records, currentBatchNum);
				if ( resultVO.isSuccess() ) {
					Map<Integer,String> failRecords = resultVO.getFailRecords();
					for(int j = 0; j < currentBatchNum; ++j) {
						jxl.Cell[] cells = records[j];
						if(failRecords != null && failRecords.containsKey(j)) {//存在部分失败记录
							jxl.Cell[] resultCell = makeResultLine(new GeneralBatchResult(false,failRecords.get(j)) , cells, null);
							xlsRf.writeErrorRecord(resultCell);
						} else {
							jxl.Cell[] resultCell = makeResultLine(resultVO, cells, null);
							xlsRf.writeSuccessRecord(resultCell);
						}
					}
				} else {
					BatchResult transactionError = new GeneralBatchResult();
//                	BeanUtils.copyProperties(transactionError, resultVO);
                	transactionError.setMsg("事务回滚");
					for(int j = 0; j < currentBatchNum; ++j) {
						jxl.Cell[] cells = records[j];
						jxl.Cell[] resultCell;
						if(j == resultVO.getCurrent() || resultVO.getCurrent() == -1) {
							resultCell = makeResultLine(resultVO, cells, null);
						} else {
							resultCell = makeResultLine(transactionError, cells, null);
						}
						xlsRf.writeErrorRecord(resultCell);
					}
				}
				addCurrentRecord(currentBatchNum);
				setFail(resultFile.getErrorCount());
				setOk(resultFile.getSuccessCount());
				printRateOfProcess();
			}
			if(currentReadRow >= sheet.getRows()) {
				setFileEnd(true);	//如果已经遍历了整个文件，就算没处理完也结束
			}
			sheet = null;
		} finally {
			if(wwb != null) {
				wwb.close();
	        }
			xlsRf.close();
            if(deleteAfterProcess) {
            	FileUtils.delete(filename);
            }
            if(getFail() == 0) {
            	xlsRf.deleteErrorFile();
            }
		}

    }

    @SuppressWarnings("unchecked")
	protected void doProcessExcelXFile() throws Exception{
    	POIResultFile poiRf = new POIResultFile();
    	resultFile = poiRf;
    	resultFile.setNeedSuccFile(needSuccFile);
        resultFile.setContentType(ContentType.EXCELX);
        poiRf.initialize(filename, sheetNumber);
        Workbook xwb = null;
        InputStream input = null;
    	try {
    		poiRf.openFile(false);//false:只是创建excel，先不创建sheet
    		String title = getTitle();
//            if(title != null) {
//            	poiRf.writeTitle(title);
//            }
            input = new FileInputStream(filename);
            //xwb = WorkbookFactory.create(input);
            //支持大数据量文件
            xwb = POIExcelUtil.create(input);
            Row[] records = new Row[batchSize];
//            RedisTemplate redisTemplate = SpringUtils.getBean("redisTemplate");
            for(int slocation = 0; slocation < sheetNumber; slocation++) {
            	Sheet sheet = xwb.getSheetAt(slocation);
            	if(beginIndex < 0) {
            		throw new RuntimeException("起始行不能为负数");
            	}
            	int lastRowNum = sheet.getLastRowNum();
            	poiRf.createErrorSheet(slocation, sheet.getSheetName());//创建对应的失败记录sheet
            	poiRf.writeErrorTitle(slocation, title);
            	int currentBatchNum = 0;
            	for(Row row : sheet) {
            		//过滤表头（表头默认为第一行）和小于指定开始行
            		int rowNum = row.getRowNum();
            		if(rowNum == 0 || rowNum < beginIndex) {
            			continue;
            		}
            		records[currentBatchNum++] = row;
            		if((currentBatchNum != 0 && currentBatchNum % batchSize == 0) || rowNum == lastRowNum) {
            			BatchResult resultVO = doProcessRecord(slocation, records, currentBatchNum);
            			if (resultVO.isSuccess()) {
        					Map<Integer,String> failRecords = resultVO.getFailRecords();
        					for(int j = 0; j < currentBatchNum; ++j) {
        						if(failRecords!= null && failRecords.containsKey(j)) {//存在部分失败记录
        							String[] resultInfo = makeFailMsg(failRecords.get(j));
        							poiRf.writeErrorRecord(slocation, records[j], resultInfo, title.split("\\|").length);
        						} else {
        							String[] resultInfo = makeResultMsg(resultVO);
        							poiRf.writeSuccessRecord(records[j], resultInfo);
        						}
        					}
        				} else {
        					BatchResult transactionError = new GeneralBatchResult();
//                        	BeanUtils.copyProperties(transactionError, resultVO);
                        	transactionError.setMsg("事务回滚");
        					for(int j = 0; j < currentBatchNum; ++j) {
        						String[] resultInfo;
        						if(j == resultVO.getCurrent() || resultVO.getCurrent() == -1) {
        							resultInfo = makeResultMsg(resultVO);
        						} else {
        							resultInfo = makeResultMsg(transactionError);
        						}
        						poiRf.writeErrorRecord(slocation, records[j], resultInfo);
        					}
        				}
        				addCurrentRecord(currentBatchNum);
        				setFail(resultFile.getErrorCount());
        				setOk(resultFile.getSuccessCount());
//        				redisTemplate.opsForValue().set(this.getId(), this, 0);
        				printRateOfProcess();
        				if(rowNum < lastRowNum) {
        					Arrays.fill(records, null);
        					currentBatchNum = 0;
        				}
            		}
            	}
            }
            setFileEnd(true);	//如果已经遍历了整个文件，就算没处理完也结束
		} finally {
			if(input != null) {
				input.close();
			}
			poiRf.close();
            if(deleteAfterProcess) {
            	FileUtils.delete(filename);
            }
            if(xwb != null) {
				try {
					xwb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }

    /**
     * 获取结果文件的标题以便写入结果文件的标题
     */
    protected abstract String getTitle();

    /**
     * 生成一行处理结果数据
     *
     */
    protected String makeResultLine(String contentType, BatchResult resultVO, String line, int i) {
    	String resLine = line;
    	if(contentType.equals(ContentType.TEXT)) {
    		if(resultVO.isSuccess()) {
    			resLine += "|成功|";
    		} else {
    			resLine += "|失败|" + i + "|" + resultVO.getMsg();
    		}
    	} else if(contentType.equals(ContentType.CSV)) {
    		if(resultVO.isSuccess()) {
    			resLine += ",成功";
            } else {
            	resLine += ",失败," + i + "," + resultVO.getMsg();
            }
    	}
		return resLine;
    }
    protected String makeFailLine(String contentType, String failMsg, String line, int i) {
    	String resLine = line;
    	if(contentType.equals(ContentType.TEXT)) {
    		resLine += "|失败|" + i + "|" + failMsg;
    	} else if(contentType.equals(ContentType.CSV)) {
            resLine += ",失败," + i + "," + failMsg;
    	}
		return resLine;
    }

    protected jxl.Cell[] makeResultLine(BatchResult resultVO, jxl.Cell[] row, Integer lastColumn) {
    	jxl.Cell[] cells = new jxl.Cell[row.length+3];
    	for(int i = 0; i < row.length; ++i) {
    		cells[i] = row[i];
    	}
    	jxl.Cell lastc = row[row.length - 1];
    	int lc;
    	if(lastColumn == null) {
    		lc = lastc.getColumn() + 1;
    	} else {
    		lc = lastColumn.intValue();
    	}
    	if(resultVO.isSuccess()) {
    		String msg = "成功";
    		if(StringUtils.isNotBlank(resultVO.getMsg())) {
    			msg = resultVO.getMsg();
    		}
    		cells[row.length] = new jxl.write.Label(lc, lastc.getRow(), msg);
    	} else {
    		cells[row.length] = new jxl.write.Label(lc, lastc.getRow(), "失败");
    		cells[row.length + 1] = new jxl.write.Label(lc + 1, lastc.getRow(), resultFile.getCurrentCount() + 1 + "");
    		cells[row.length + 2] = new jxl.write.Label(lc + 2, lastc.getRow(), resultVO.getMsg());
    	}
    	return cells;
    }

    /**
     * 生成结果信息
     * @param resultVO
     * @return
     */
    protected String[] makeResultMsg(BatchResult resultVO) {
    	String[] items = new String[3];

    	if(resultVO.isSuccess()) {
    		String msg = "成功";
    		if(StringUtils.isNotBlank(resultVO.getMsg())) {
    			msg = resultVO.getMsg();
    		}
    		items[0] = msg;
    	} else {
    		items[0] = "失败";
    		items[1] = String.valueOf(resultFile.getCurrentCount() + 1);
    		items[2] = resultVO.getMsg();
    	}
    	return items;
    }

    protected String[] makeFailMsg(String errorMsg) {
    	String[] items = new String[3];
    	items[0] = "失败";
		items[1] = String.valueOf(resultFile.getCurrentCount() + 1);
		items[2] = errorMsg;
    	return items;
    }

    /**
     * 批处理记录
     */
    protected abstract BatchResult doProcessRecord(int sheetLocation, String[] line, int size);

    /**
     * 默认情况下将把每行Excel数据转换成用“|”分隔的字符串形式（a|b|c|...）处理。
     * 性能不好，也不能准确读取Excel数据，建议自己实现
     * 处理Excel2003格式
     * @param row
     * @return
     */
    protected BatchResult doProcessRecord(jxl.Cell[][] row, int size) {
    	String[] records = new String[size];
    	for(int i = 0; i < size; ++i) {
    		records[i] = JxlUtil.toStringLine(row[i]);
    	}
    	return doProcessRecord(0, records, size);
    }

    /**
     * 把每行Excel数据转换成用“|”分隔的字符串形式
     * 性能不好，也不能准确读取Excel数据，建议自己实现
     * 处理新版本Excel格式（用POI）
     * @param rows
     * @param size
     * @return
     */
    protected BatchResult doProcessRecord(int sheetLocation, Row[] rows, int size) {
    	String[] records = new String[size];
    	for(int i = 0; i < size; ++i) {
    		records[i] = POIExcelUtil.toStringLine(rows[i]);
    	}
    	return doProcessRecord(sheetLocation, records, size);
    }

    /**
     * 记录数
     */
    public void updateTotalRecords() {
        super.setTotalRecords(getRecordNumber(filename));
        if(batchSize > 1) {
        	rateRadix = batchSize;
        } else {
        	rateRadix = getTotalRecords() / 100;
        	if(rateRadix <= 0) {
        		rateRadix = 1;
        	}
        }
    }

    /**
     * 获取文件要处理的行数
     */
    protected int getRecordNumber(String filename) {
        int numbers = 0;
		if (null == fileContentType) {
			fileContentType = ContentType.getContentType(filename);
		}
		try {
			if (fileContentType.equals(ContentType.TEXT) || fileContentType.equals(ContentType.CSV)) {
				BufferedReader reader = new BufferedReader(new FileReader(new File(filename)));
				int currentRow = 0;
				while(true) {
					String line = reader.readLine();
					if(line == null) {
						break;
					}
					++currentRow;
					if(currentRow <= getBeginIndex()) {
						continue;
					}
					if (StringUtils.isNotBlank(line)) {
						numbers++;
					}
				}
				reader.close();
			} else if (fileContentType.equals(ContentType.EXCEL)) {
				jxl.Workbook wwb = jxl.Workbook.getWorkbook(new FileInputStream(
						filename));
				for(int s = 0; s < sheetNumber; s++) {
					jxl.Sheet sheet = wwb.getSheet(0);
					if(sheet != null) {
						for(int i = beginIndex; i < sheet.getRows(); ++i) {
							jxl.Cell[] cells = sheet.getRow(i);
							if( !JxlUtil.isEmptyRow(cells) ) {
								++numbers;
							}
						}
					}
				}
				wwb.close();
			} else if (fileContentType.equals(ContentType.EXCELX)) {
				InputStream input = new FileInputStream(filename);
				Workbook wb = WorkbookFactory.create(input);
				for(int s = 0; s < sheetNumber; s++) {
					Sheet sheet = wb.getSheetAt(s);
					if(sheet != null) {
						for(int i = beginIndex; i <= sheet.getLastRowNum(); ++i) {
							Row row = sheet.getRow(i);
							if( !POIExcelUtil.isEmptyRow(row)) {
								++numbers;
							}
						}
					}
				}
				input.close();
			} else {
				throw new UnsupportedOperationException("不支持此类型文件的处理");
			}
		} catch (Exception ex) {
			log.error("计算数据量异常", ex);
			return 0;
		}
		return numbers;
    }

    /**
     * 异常翻译
     *
     */
    public static String transException(Exception ex) {
        String message = ex.getMessage();
        if ( ex.getMessage().indexOf(
                "java.sql.BatchUpdateException: Unique constraint") >= 0 ) {
            message = "主键重复";
        }
        return message;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getResultFile() {
    	if(null == resultFile) {
    		return null;
    	}
        return resultFile.getResultFileName();
    }

    public String getSucResultFile() {
    	if(null == resultFile) {
    		return null;
    	}
        return resultFile.getSucResultFileName();
    }

    public String getPageSucResultFile() {
    	if(getSucResultFile() == null) {
    		return null;
    	}
        return FileUtils.getFileName(getSucResultFile());
    }

    public String getErrResultFile() {
    	if(null == resultFile) {
    		return null;
    	}
        return resultFile.getErrResultFileName();
    }

    public String getPageErrResultFile() {
    	if(getErrResultFile() == null) {
    		return null;
    	}
        return FileUtils.getFileName(getErrResultFile());
    }

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

	public boolean isDeleteAfterProcess() {
		return deleteAfterProcess;
	}

	public void setDeleteAfterProcess(boolean deleteAfterProcess) {
		this.deleteAfterProcess = deleteAfterProcess;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public boolean isFileEnd() {
		return fileEnd;
	}

	public void setFileEnd(boolean fileEnd) {
		this.fileEnd = fileEnd;
	}

	public String getUploadFileName() {
		return uploadFileName;
	}

	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}

	/**
	 * 是否在日志显示进度
	 * @return
	 */
	public boolean isShowRate() {
		return showRate;
	}

	public void setShowRate(boolean showRate) {
		this.showRate = showRate;
	}

	/**
	 * 显示进度的间隔数。默认值为=总数/100
	 * @return
	 */
	public int getRateRadix() {
		return rateRadix;
	}

	public void setRateRadix(int rateRadix) {
		this.rateRadix = rateRadix;
	}

	public String getBsBusinessObject() {
		return bsBusinessObject;
	}

	public void setBsBusinessObject(String bsBusinessObject) {
		this.bsBusinessObject = bsBusinessObject;
	}

	public int getSheetNumber() {
		return sheetNumber;
	}

	public void setSheetNumber(int sheetNumber) {
		this.sheetNumber = sheetNumber;
	}

}

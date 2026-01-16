package com.yinzhf.filetask;

import jxl.Cell;
import jxl.Workbook;
import jxl.write.*;
import jxl.write.biff.RowsExceededException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class TxtXlsResultFile extends AbstractResultFile {
    private WritableWorkbook  successWorkbook;
    private WritableSheet successSheet;
    private WritableWorkbook  errorWorkbook;
    private WritableSheet errorSheet;

    protected RandomAccessFile resultFile;

    protected RandomAccessFile sucResultFile;

    protected RandomAccessFile errResultFile;

    public TxtXlsResultFile() {

    }

    public TxtXlsResultFile(int type) {
        this.resultType = type;
        this.contentType = null;
    }

    public void initialize(String impoFileName) {
        this.all_count = 0;
        this.ok_count = 0;
        this.fail_count = 0;
//        impoFileName = impoFileName.toLowerCase();
        if(null == contentType) {
        	contentType = ContentType.getContentType(impoFileName);
        }
        if(contentType.equals(ContentType.TEXT)) {
        	if(resultType == ONE_RES_FILE) {
        		resultFileName = impoFileName.replaceFirst("\\.txt$", "_result.txt");
        	} else if(resultType == TOW_RES_FILE) {
        		sucResultFileName = impoFileName.replaceFirst("\\.txt$", "_suc.txt");
        		errResultFileName = impoFileName.replaceFirst("\\.txt$", "_err.txt");
        	}
        } else if(contentType.equals(ContentType.EXCEL)) {
        	if(resultType == ONE_RES_FILE) {
        		resultFileName = impoFileName.replaceFirst("\\.xls$", "_result.xls");
        	} else if(resultType == TOW_RES_FILE) {
        		sucResultFileName = impoFileName.replaceFirst("\\.xls$", "_suc.xls");
        		errResultFileName = impoFileName.replaceFirst("\\.xls$", "_err.xls");
        	}
        } else if(contentType.equals(ContentType.CSV)) {
        	if(resultType == ONE_RES_FILE) {
        		resultFileName = impoFileName.replaceFirst("\\.csv$", "_result.csv");
        	} else if(resultType == TOW_RES_FILE) {
        		sucResultFileName = impoFileName.replaceFirst("\\.csv$", "_suc.csv");
        		errResultFileName = impoFileName.replaceFirst("\\.csv$", "_err.csv");
        	}
        }
    }

    public void openFile() throws IOException {
    	if(contentType.equals(ContentType.TEXT) || contentType.equals(ContentType.CSV)) {
    		if(resultType == ONE_RES_FILE) {
    			resultFile = new RandomAccessFile(new File(resultFileName), "rw");
    		} else if(resultType == TOW_RES_FILE) {
    			if(needSuccFile) {
    				sucResultFile = new RandomAccessFile(new File(sucResultFileName), "rw");
    			}
    			errResultFile = new RandomAccessFile(new File(errResultFileName), "rw");
    		}
    	} else if(contentType.equals(ContentType.EXCEL)) {
    		if(needSuccFile) {
    			successWorkbook = Workbook.createWorkbook(new File(sucResultFileName));
    			successSheet = successWorkbook.createSheet("成功", 0);
			}
    		errorWorkbook = Workbook.createWorkbook(new File(errResultFileName));
    		errorSheet = errorWorkbook.createSheet("失败", 0);
    	}
    }

    public void writeTitle(String title) throws IOException, RowsExceededException, WriteException {
    	if(contentType.equals(ContentType.TEXT) || contentType.equals(ContentType.CSV)) {
    		if(ONE_RES_FILE == resultType) {
    			resultFile.writeBytes( title );
    		} else if(TOW_RES_FILE == resultType) {
    			if(needSuccFile) {
    				sucResultFile.writeBytes( newLine(title) );
    			}
    			errResultFile.writeBytes( newLine(title) );
    		}
    	} else if(contentType.equals(ContentType.EXCEL)) {
    		String[] items = title.split("\\|");
    		for(int i = 0; i < items.length; ++i) {
    			if(needSuccFile) {
    				Label label = new Label(i, ok_count, items[i]);
    				successSheet.addCell(label);
    			}
    			Label label = new Label(i, fail_count, items[i]);
    			errorSheet.addCell(label);
    		}
    	}
    }

    public void writeSuccessRecord(String msg) throws IOException {
        if(ONE_RES_FILE == resultType) {
            resultFile.writeBytes( newLine(msg) );
        } else if(TOW_RES_FILE == resultType) {
        	if(needSuccFile) {
        		sucResultFile.writeBytes( newLine(msg) );
        	}
        }
        this.all_count++;
        this.ok_count++;
    }

    public void writeSuccessRecord(Cell[] cells) throws RowsExceededException, WriteException {
        if(needSuccFile) {
        	for(int i = 0; i < cells.length; ++i) {
        		Cell cell = cells[i];
        		if(cell != null) {
        			WritableCell newCell = JxlUtil.copyCell(cell, getSuccessSerial());
        			successSheet.addCell(newCell);
        		} else {
        			successSheet.addCell(new Label(i, cells[0].getRow(), ""));
        		}
        	}
        }
        this.all_count++;
        this.ok_count++;
    }

    public void writeErrorRecord(String msg) throws IOException {
        if(ONE_RES_FILE == resultType) {
            resultFile.writeBytes( newLine(msg) );
        } else if(TOW_RES_FILE == resultType) {
            errResultFile.writeBytes( newLine(msg) );
        }
        this.all_count++;
        this.fail_count++;
    }

    public void writeErrorRecord(Cell[] cells) throws RowsExceededException, WriteException {
    	writeErrorRecord(cells, getErrorSerial());
    }

    public void writeErrorRecord(Cell[] cells, int row) throws RowsExceededException, WriteException {
    	for(int i = 0; i < cells.length; ++i) {
    		Cell cell = cells[i];
    		if(cell != null) {
    			WritableCell newCell = JxlUtil.copyCell(cell, row);
    			errorSheet.addCell(newCell);
    		} else {
    			errorSheet.addCell(new Label(i, cells[0].getRow(), ""));
    		}
    	}
        this.all_count++;
        this.fail_count++;
    }

    public void close() throws IOException, WriteException {
        if(resultFile != null) {
            resultFile.close();
        }
        if(sucResultFile != null) {
            sucResultFile.close();
        }
        if(errResultFile != null) {
            errResultFile.close();
        }
        if(successWorkbook != null) {
        	successWorkbook.write();
        	successWorkbook.close();
        }
        if(errorWorkbook != null) {
        	errorWorkbook.write();
        	errorWorkbook.close();
        }
    }

    public void deleteErrorFile() {
    	new File(errResultFileName).delete();
    	errResultFile = null;
    	errorWorkbook = null;
    }

//    public RandomAccessFile getResultFile() {
//        return resultFile;
//    }
//
//    public void setResultFile(RandomAccessFile resultFile) {
//        this.resultFile = resultFile;
//    }
//
//    public RandomAccessFile getSucResultFile() {
//        return sucResultFile;
//    }
//
//    public void setSucResultFile(RandomAccessFile sucResultFile) {
//        this.sucResultFile = sucResultFile;
//    }
//
//    public RandomAccessFile getErrResultFile() {
//        return errResultFile;
//    }
//
//    public void setErrResultFile(RandomAccessFile errResultFile) {
//        this.errResultFile = errResultFile;
//    }

}

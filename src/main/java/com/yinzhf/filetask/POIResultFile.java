package com.yinzhf.filetask;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class POIResultFile extends AbstractResultFile {
	private SXSSFWorkbook workbook;
	private Sheet successSheet;
	private Sheet[] errorSheet;
	private Integer[] errorSerial;
	private int sheetNumber;
	/**
	 * 样式列表
	 */
	private Map<String, CellStyle> styles;


	public POIResultFile() {

    }

	public void initialize(String impoFileName) {
		this.initialize(impoFileName, 1);
	}

    public void initialize(String impoFileName, int sheetNumber) {
        this.all_count = 0;
        this.ok_count = 0;
        this.fail_count = 0;
        this.sheetNumber = sheetNumber;
        if(null == contentType) {
        	contentType = ContentType.getContentType(impoFileName);
        }
        if(contentType.equals(ContentType.EXCELX)) {
        	resultFileName = impoFileName.replaceFirst("\\.xlsx$", "_result.xlsx");
        	sucResultFileName = resultFileName;
        	errResultFileName = resultFileName;
        } else {
        	throw new RuntimeException("只支持Excel2007 以上版本");
        }
    }

    public void openFile() throws IOException {
    	this.openFile(true);
    }

    public void openFile(boolean createsheet) throws IOException {
    	workbook = new SXSSFWorkbook();
    	errorSheet = new Sheet[sheetNumber];
    	errorSerial = new Integer[sheetNumber];
    	for(int i = 0; i < sheetNumber; ++i) {
    		errorSerial[i] = 1;//默认从第一行开始写
    	}
    	if(createsheet) {
    		for(int i = 1; i <= sheetNumber; ++i) {
    			errorSheet[i-1] = workbook.createSheet("失败记录："+i);
    		}
			if (this.styles == null) {
				this.styles = createStyles(workbook);
			}
    	}
    	if(needSuccFile) {
    		successSheet = workbook.createSheet("成功记录");
    		if (this.styles == null) {
				this.styles = createStyles(workbook);
			}
    	}
    }

    public void createErrorSheet(int sheet, String name) {
    	errorSheet[sheet] = workbook.createSheet("失败记录"+(sheet+1)+"-"+name);
		if (this.styles == null) {
			this.styles = createStyles(workbook);
		}
    }

    public void writeTitle(String title) {
    	String[] items = title.split("\\|");
    	Row errRow = null;
    	if(errorSheet[0] != null) {
    		errRow = errorSheet[0].createRow((short)fail_count);
    	}
    	Row succRow = null;
    	if(needSuccFile) {
    		succRow = successSheet.createRow((short)ok_count);
    	}
		for(int i = 0; i < items.length; ++i) {
			if(needSuccFile) {
				Cell cell = succRow.createCell(i);
				cell.setCellValue(items[i]);
			}
			if(errRow != null) {
				Cell cell = errRow.createCell(i);
				cell.setCellValue(items[i]);
			}
		}
    }

    public void writeErrorTitle(int sheet, String title) {
    	if(StringUtils.isNotBlank(title)) {
    		List<String> titles = new ArrayList<String>(Arrays.asList(title.split("\\|")));
    		titles.add("行号");
    		titles.add("导入结果");
    		titles.add("失败原因");
    		Sheet sheetObj = errorSheet[sheet];
    		if(sheetObj != null) {
    			Row errRow = sheetObj.createRow((short)0);
    			//冻结第一行
    			sheetObj.createFreezePane(0, 1, 0, 1);
    			for(int i = 0; i < titles.size(); ++i) {
    				String headName = titles.get(i);
    				errRow.createCell(i).setCellValue(headName);
    				sheetObj.setColumnWidth(i, headName.getBytes().length*256);
    			}
    		}
    	}
    }

    public void writeErrorTitle(int sheet) {
		List<String> titles = new ArrayList<String>();
		titles.add("行");
		titles.add("列");
		titles.add("失败原因");
		Sheet sheetObj = errorSheet[sheet];
		if(sheetObj != null) {
			Row errRow = sheetObj.createRow((short)0);
			//冻结第一行
			sheetObj.createFreezePane(0, 1, 0, 1);
			for(int i = 0; i < titles.size(); i++) {
				String headName = titles.get(i);
				Cell cell = errRow.createCell(i);
				cell.setCellValue(headName);
				// sheetObj.setColumnWidth(i, headName.getBytes().length*256);
				cell.setCellStyle(styles.get("header"));
			}
		}
    }

    /**
     * 在成功的sheet创建一新行，并把给定其他Row的值复制进来了
     * @param sourceRow
     * @param extInfos
     */
    public void writeSuccessRecord(Row sourceRow, String[] extInfos) {
    	if(needSuccFile) {
    		Iterator<Cell> it = sourceRow.cellIterator();
    		Row newRow = successSheet.createRow(getSuccessSerial());
    		int i = 0;
    		while (it.hasNext()) {
				Cell cell = (Cell) it.next();
				if(cell != null) {
					Cell newCell = newRow.createCell(cell.getColumnIndex());
					POIExcelUtil.copyCell(newCell, cell);
				} else {
					newRow.createCell(i).setCellValue("-null");
				}
    			i++;
			}
    		newRow.createCell(i).setCellValue(extInfos[0]);
        }
        this.all_count++;
        this.ok_count++;
    }

    public void writeSuccessRecord() {
        this.all_count++;
        this.ok_count++;
    }

	public void writeSuccessRecord(int num) {
		this.all_count += num;
		this.ok_count += num;
	}

    public void writeErrorRecord(Row sourceRow, String[] extInfos) {
    	this.writeErrorRecord(0, sourceRow, extInfos);
    }

    /**
     * 在失败的sheet创建一新行，并把给定其他Row的值复制进来了
     * @param sourceRow
     * @param extInfos
     */
    public void writeErrorRecord(int currentSheet, Row sourceRow, String[] extInfos) {
    	Iterator<Cell> it = sourceRow.cellIterator();
		Row newRow = errorSheet[currentSheet].createRow(getNextErrorSerial(currentSheet));
		int i = 0;
		while (it.hasNext()) {
			Cell cell = (Cell) it.next();
			Cell newCell = newRow.createCell(cell.getColumnIndex());
			POIExcelUtil.copyCell(newCell, cell);
			i++;
		}
		newRow.createCell(i).setCellValue(extInfos[0]);
		newRow.createCell(i+1).setCellValue(extInfos[1]);
		newRow.createCell(i+2).setCellValue(extInfos[2]);

        this.all_count++;
        this.fail_count++;
    }

    /**
     * 在失败的sheet创建一新行，并把给定其他Row的值复制进来了
     * @param currentSheet
     * @param sourceRow
     * @param extInfos
     * @param headColCount 表头总列数
     */
    public void writeErrorRecord(int currentSheet, Row sourceRow, String[] extInfos, int headColCount) {
    	Iterator<Cell> it = sourceRow.cellIterator();
		Row newRow = errorSheet[currentSheet].createRow(getNextErrorSerial(currentSheet));
		while (it.hasNext()) {
			Cell cell = (Cell) it.next();
			Cell newCell = newRow.createCell(cell.getColumnIndex());
			POIExcelUtil.copyCell(newCell, cell);
		}
		newRow.createCell(headColCount).setCellValue(extInfos[0]);
		newRow.createCell(headColCount + 1).setCellValue(extInfos[1]);
		newRow.createCell(headColCount + 2).setCellValue(extInfos[2]);

        this.all_count++;
        this.fail_count++;
    }

    public void writeErrorRecord(int currentSheet, String[] extInfos) {
    	Sheet errSheet = errorSheet[currentSheet];
		Row newRow = errSheet.createRow(getNextErrorSerial(currentSheet));
		Cell cell = newRow.createCell(0);
		cell.setCellValue(Integer.valueOf(extInfos[0]));
		cell.setCellStyle(this.styles.get("data2"));

		Cell cell2 = newRow.createCell(1);
		cell2.setCellStyle(this.styles.get("data2"));
		if (extInfos[1] != null) {
			cell2.setCellValue(Integer.valueOf(extInfos[1]));
		}

		Cell cell3 = newRow.createCell(2);
		cell3.setCellValue(extInfos[2]);
		cell3.setCellStyle(this.styles.get("data1"));

		errSheet.setColumnWidth(2, (int) ((16 + 0.72) * 256));

        this.all_count++;
        this.fail_count++;
    }

    public int getNextErrorSerial(int currentSheet) {
    	int s = errorSerial[currentSheet];
    	errorSerial[currentSheet] += 1;
    	return s;
    }

    public void close() throws IOException {
    	if(this.fail_count > 0) {
    		try(
				FileOutputStream out = new FileOutputStream(resultFileName);
			){
    			workbook.write(out);
    			workbook.dispose(); // dispose of temporary files backing this workbook on disk
    		}
    	}
    	workbook.close();
    }

	/**
	 * 创建表格样式
	 *
	 * @param wb 工作薄对象
	 * @return 样式列表
	 */
	private Map<String, CellStyle> createStyles(Workbook wb)
	{
		// 写入各条记录,每条记录对应excel表中的一行
		Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
		CellStyle style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		Font dataFont = wb.createFont();
		dataFont.setFontName("Arial");
		dataFont.setFontHeightInPoints((short) 10);
		style.setFont(dataFont);
		styles.put("data", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		Font headerFont = wb.createFont();
		headerFont.setFontName("Arial");
		headerFont.setFontHeightInPoints((short) 10);
		headerFont.setBold(true);
		headerFont.setColor(IndexedColors.WHITE.getIndex());
		style.setFont(headerFont);
		styles.put("header", style);

		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		Font totalFont = wb.createFont();
		totalFont.setFontName("Arial");
		totalFont.setFontHeightInPoints((short) 10);
		style.setFont(totalFont);
		styles.put("total", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.LEFT);
		// 自动换行
		style.setWrapText(true);
		styles.put("data1", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.CENTER);
		styles.put("data2", style);

		style = wb.createCellStyle();
		style.cloneStyleFrom(styles.get("data"));
		style.setAlignment(HorizontalAlignment.RIGHT);
		styles.put("data3", style);

		return styles;
	}

}

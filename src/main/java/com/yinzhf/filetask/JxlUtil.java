package com.yinzhf.filetask;

import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.NumberCell;
import jxl.write.Number;
import jxl.write.*;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JxlUtil {

	public static WritableCell copyCell(Cell cell, int row) {
		CellType type = cell.getType();
		if (type.equals(CellType.NUMBER)) {
			Number num = new Number(cell.getColumn(), row,
					((NumberCell) cell).getValue());
			return num;
		} else if (type.equals(CellType.DATE)) {
			DateTime dt = new DateTime(cell.getColumn(), row,
					((DateCell) cell).getDate());
			return dt;
		} else if (type.equals(CellType.BOOLEAN)) {
			jxl.write.Boolean bool = new jxl.write.Boolean(cell.getColumn(),
					row, ((jxl.write.Boolean) cell).getValue());
			return bool;
		} else if (type.equals(CellType.BOOLEAN_FORMULA)
				|| type.equals(CellType.DATE_FORMULA)
				|| type.equals(CellType.NUMBER_FORMULA)
				|| type.equals(CellType.STRING_FORMULA)) {
			Formula formual = new Formula(cell.getColumn(), row, cell
					.getContents());
			return formual;
		} else if(type.equals(CellType.ERROR)) {
			Label label = new Label(cell.getColumn(), row, "");
			return label;
		} else {
			Label label = new Label(cell.getColumn(), row, cell
					.getContents());
			return label;
		}
	}

	public static String toStringLine(Cell[] row) {
		return toStringLine(row, null);
	}

	public static String toStringLine(Cell[] row, String dateFormat) {
		StringBuffer sb = new StringBuffer(row.length * 15);
		for (int j = 0; j < row.length; j++) {
			Cell cell = row[j];
			CellType type = cell.getType();
//			System.out.println(cell.getContents()+" "+type);
			if(type.equals(CellType.ERROR)) {
				sb.append("|");
			} else if (type.equals(CellType.NUMBER)) {
				double num = ((NumberCell) cell).getValue();
				NumberFormat nf = NumberFormat.getInstance();
				nf.setGroupingUsed(false);
				sb.append(nf.format(num)).append("|");
			} else if (type.equals(CellType.DATE)) {
				String dateStr = cellDateToString(cell, dateFormat);
				sb.append(dateStr).append("|");
			} else if (type.equals(CellType.EMPTY)) {
				sb.append(" |");
			} else {
				sb.append(cell.getContents()).append("|");
			}
		}
		return sb.toString();
	}

	public static StringBuffer toOrginStringBuffer(Cell[] row) {
		StringBuffer sb = new StringBuffer(row.length * 15);
		for (int j = 0; j < row.length; j++) {
			Cell cell = row[j];
			sb.append(cell.getContents()).append("|");
		}
		return sb;
	}

	public static String cellDateToString(Cell cell) {
		return cellDateToString(cell, null);
	}

	public static String cellDateToString(Cell cell, String dateFormat) {
		DateCell date = (DateCell) cell;
		String dateStr;
		if(StringUtils.isNotBlank(dateFormat)) {
			dateStr = DateFormater.formatDate(date.getDate(), dateFormat);
		} else {
			dateStr = DateFormater.formatDatetime(date.getDate());
		}
		return dateStr;
	}

	public static boolean isEmptyRow(Cell[] cells) {
		if (cells == null) {
			return true;
		}
		int c;
		for (c = 0; c < cells.length; ++c) {
			Cell cell = cells[c];
			if (!cell.getType().equals(CellType.EMPTY)) {
				break;
			}
		}
		if (c == cells.length) {
			return true;
		}
		return false;
	}

	private static String FormateData(Cell formatecell) {
		try {
			Date mydate = null;
			if (formatecell.getType().toString().equals("Date")) {
				DateCell datecll = (DateCell) formatecell;
				mydate = datecll.getDate();
				long time = (mydate.getTime() / 1000) - 60 * 60 * 8;
				mydate.setTime(time * 1000);
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				return formatter.format(mydate);
			}
			return formatecell.getContents();

		} catch (Exception e) {
			return null;
		}
	}

	public static Date FormateData1(Cell formatecell) {
		try {
			Date mydate = null;
			System.out.println(formatecell.getType());
			if (formatecell.getType().toString().equals("Date")) {
				DateCell datecll = (DateCell) formatecell;
				mydate = datecll.getDate();
				return mydate;
			} else {
				String dateStr = formatecell.getContents();
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-M-d H:mm");
				return formatter.parse(dateStr);
			}
		} catch (Exception e) {
			return null;
		}
	}

	public static String[] getFormatCell(Cell[] cel, int headLength) {

		String[] validateStr = new String[headLength];
		for (int i = 0; i < headLength; i++) {
			if (i < cel.length) {
				if (cel[i].getType().toString().equals("Date")) {

					validateStr[i] = FormateData(cel[i]);

				} else {
					validateStr[i] = cel[i].getContents();
				}
			} else {
				validateStr[i] = "";
			}

		}

		return validateStr;
	}
}

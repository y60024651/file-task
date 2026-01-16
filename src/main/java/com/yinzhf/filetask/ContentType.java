package com.yinzhf.filetask;

public class ContentType {

	public static final String TEXT = "text/plain";
	public static final String CSV = "text/csv";
	public static final String EXCEL = "application/vnd.ms-excel";
	public static final String EXCEL2 = "application/x-xls";
	public static final String EXCELX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String STREAM = "application/octet-stream";
	public static final String WORD = "application/msword";
	public static final String RTF = "application/rtf";
	public static final String XML = "application/xml";
	public static final String BMP = "image/bmp";
	public static final String JPEG = "image/jpeg";
	public static final String GIF = "image/gif";
	public static final String AVI = "video/x-msvideo";
	public static final String MP3 = "audio/mpeg";
	public static final String MPEG = "video/mpeg";
	public static final String PPT = "application/vnd.ms-powerpoint";
	public static final String PDF = "application/pdf";
	public static final String ZIP = "application/zip";
	public static final String JSON = "application/json";
	public static final String PNG = "application/x-png";
	public static final String GZIP = "application/gzip";
	public static final String MP4 = "video/mp4";

	/**
	 * 获取文件的ContentType,如果无法判断则抛出UnsupportedOperationException
	 * @param fileName
	 * @return
	 */
	public static String getContentType(String fileName) {
		if (fileName != null) {
			String ext = FileUtils.getExtName(fileName).toUpperCase();
			String ct = transform(ext);
			if(ct != null) {
				return ct;
			}
			if(ext.equals(".BIN") || ext.equals(".EXE")) {
				return STREAM;
			}
		}

		throw new UnsupportedOperationException("无法判断此文件类型:"+fileName);
	}

	/**
	 * 获取文件的ContentType,所有无法判断的文件都返回application/octet-stream
	 * @param fileName
	 * @return
	 */
	public static String get(String fileName) {
		if (fileName != null) {
			String ext = FileUtils.getExtName(fileName).toUpperCase();
			String ct = transform(ext);
			if(ct != null) {
				return ct;
			}
		}
		return STREAM;
	}

	public static String getExtFromType(String ctype) {
		if(EXCEL.equals(ctype) || EXCEL2.equals(ctype)) {
			return "xls";
		} else if(EXCELX.equals(ctype)) {
			return "xlsx";
		} else if(WORD.equals(ctype)) {
			return ".doc";
		} else if(RTF.equals(ctype)) {
			return ".rtf";
		} else if(TEXT.equals(ctype)) {
			return ".txt";
		} else if(XML.equals(ctype)) {
			return ".xml";
		} else if(BMP.equals(ctype)) {
			return ".bmp";
		} else if(JPEG.equals(ctype)) {
			return ".jpg";
		} else if(GIF.equals(ctype)) {
			return "gif";
		} else if(AVI.equals(ctype)) {
			return ".avi";
		} else if(MP3.equals(ctype)) {
			return ".mp3";
		} else if(MPEG.equals(ctype)) {
			return ".mpeg";
		} else if(PPT.equals(ctype)) {
			return ".ppt";
		} else if(PDF.equals(ctype)) {
			return ".pdf";
		} else if(ZIP.equals(ctype)) {
			return ".zip";
		} else if(JSON.equals(ctype)) {
			return ".json";
		} else if(PNG.equals(ctype)) {
			return ".png";
		} else if(GZIP.equals(ctype)) {
			return ".gzip";
		} else if(MP4.equals(ctype)) {
			return ".mp4";
		} else if(CSV.equals(ctype)) {
			return ".csv";
		}
		return ".unknow";
	}

	private static String transform(String ext) {
		if (ext.equals(".XLS")) {
			return EXCEL;
		} else if(ext.equals(".XLSX")) {
			return EXCELX;
		} else if (ext.equals(".DOC") || ext.equals(".DOCX")) {
			return WORD;
		} else if (ext.equals(".RTF")) {
			return RTF;
		} else if (ext.equals(".TEXT") || ext.equals(".TXT")) {
			return TEXT;
		} else if (ext.equals(".XML")) {
			return XML;
		} else if (ext.equals(".BMP")) {
			return BMP;
		} else if (ext.equals(".JPG") || ext.equals(".JPEG")) {
			return JPEG;
		} else if (ext.equals(".PNG")) {
			return PNG;
		} else if (ext.equals(".GIF")) {
			return GIF;
		} else if (ext.equals(".AVI")) {
			return AVI;
		} else if (ext.equals(".MP3")) {
			return MP3;
		} else if (ext.equals(".MPA") || ext.equals(".MPE")
				|| ext.equals(".MPEG") || ext.equals(".MPG")) {
			return MPEG;
		} else if (ext.equals(".PPT") || ext.equals(".PPTX") || ext.equals(".PPS")) {
			return PPT;
		} else if (ext.equals(".PDF")) {
			return PDF;
		} else if (ext.equals(".ZIP") || ext.equals(".RAR")) {
			return ZIP;
		} else if (ext.equals(".json") || ext.equals(".JSON")) {
			return JSON;
		} else if (ext.equals(".MP4")) {
			return MP4;
		} else if (ext.equals(".csv") || ext.equals(".CSV")) {
			return CSV;
		}
		return null;
	}

	public static String getContentTypeName(String type) {
		if (type.equals(TEXT)) {
			return "Txt文件";
		} else if (type.equals(EXCEL)) {
			return "Excel文件";
		} else if (type.equals(EXCELX)) {
			return "Excelx文件";
		} else if (type.equals(CSV)) {
			return "csv文件";
		}
		return "";
	}
}

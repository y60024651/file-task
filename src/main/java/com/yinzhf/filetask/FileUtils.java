package com.yinzhf.filetask;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.util.regex.Pattern;

public class FileUtils extends org.apache.commons.io.FileUtils{

	public static int lastIndexOfFileSeparator(String fullPath) {
		int a = fullPath.lastIndexOf('/');
		int b = fullPath.lastIndexOf('\\');
		return Math.max(a, b);
	}

    public static String getFileName(String fullPath){
    	int index = lastIndexOfFileSeparator(fullPath);
    	if(index != -1) {
    		return fullPath.substring(index + 1);
    	}
    	return fullPath;
    }

    public static String[] getPathAndName(String fullPath) {
    	int index = lastIndexOfFileSeparator(fullPath);
    	if(index == -1) {
    		return new String[] {"", fullPath};
    	}
    	String path = fullPath.substring(0, index + 1);
    	String name = fullPath.substring(index + 1);
    	return new String[] {path, name};
    }

    public static String getDirectory(String fullPath) {
    	String[] items = getPathAndName(fullPath);
    	return items[0];
    }

    /**
     * 读文件入缓冲区
     * @param file File
     * @return BufferedReader
     * @throws Exception
     */
    static public BufferedReader openFile(File file) throws Exception {
        return (new BufferedReader(new FileReader(file)));
    }

    /**
	 * 根据路径和正则表达式，获取指定路径的文件列表
	 *
	 * return null when path is null, otherwise return a list of file
	 *
	 * @param path
	 * @param regex
	 * @return
	 */
	public static String[] getFileList(final String path, final String regex) {
		if (path == null) {
			return null;
		}
		File filePath = new File(path);
		if (!filePath.exists()) {
			return new String[] {};
		}
		String[] list;
		if (regex == null || regex.trim().length() == 0) {
			list = filePath.list();
		} else {
			list = filePath.list(new FilenameFilter() {
				private Pattern pattern = Pattern.compile(regex);

				public boolean accept(File dir, String name) {
					return pattern.matcher(name).matches();
				}
			});
		}
		return list;
	}

	static final int LENGTH = 1024*8;

	public static boolean move(String sourcefilepath, String targetfilepath) {
		if( copy(sourcefilepath, targetfilepath) ) {
			File sf = new File(sourcefilepath);
			if( sf.delete() ) {
				return true;
			}
		}
		return false;
	}

	public static boolean move(File sourcefile, File targetfile) {
		if( copy(sourcefile, targetfile) ) {
			if( sourcefile.delete() ) {
				return true;
			}
		}
		return false;
	}

	public static boolean copy(String sourcefilepath, String targetfilepath) {
		return copy(new File(sourcefilepath), new File(targetfilepath));
	}

	/**
	 * 复制文件
	 *
	 * @param sourcefilepath
	 *            String 源文件绝对路径
	 * @param targetfilepath
	 *            String 目标文件绝对路径
	 * @return boolean
	 * @throws Exception
	 */
	public static boolean copy(File sourcefilepath, File targetfilepath) {
		try {
			if(sourcefilepath.exists()) {
				FileInputStream fin = new FileInputStream(sourcefilepath);
				FileChannel fcin = fin.getChannel();

				FileOutputStream fout = new FileOutputStream(targetfilepath);
				FileChannel fcout = fout.getChannel();
				fcin.transferTo(0,fcin.size(),fcout);

//				fcin.close();
//				fcout.close();
				fin.close();
				fout.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void copy(InputStream input, OutputStream output) throws IOException {
		int bufread = 0;
		byte[] memdata = new byte[LENGTH];
		while( (bufread = input.read(memdata, 0, LENGTH)) != -1) {
			output.write(memdata, 0, bufread);
		}
	}

	public static void close(Reader reader) {
		if(reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Writer writer) {
		if(writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String perfectDirectory(String dir) {
//		String fileSeparator = System.getProperty("file.separator");
//		if(StringUtils.isEmpty(dir)) {
//			return fileSeparator;
//		}
		String fileSeparator = "/";
		dir = dir.trim();
		char c = dir.charAt(dir.length() - 1);
		if(c != '\\' && c != '/') {
			dir = dir + fileSeparator;
		}
		return dir;
	}

	public static String joinDirectory(String dirA, String dirB) {
		if(dirB != null && dirB.length() > 0) {
			char c = dirB.charAt(0);
			String dirB2 = dirB;
			if(c == '\\' || c == '/') {
				dirB2 = dirB.substring(1, dirB.length());
			}
			return perfectDirectory(dirA)+dirB2;
		} else {
			return dirA;
		}
	}

	public static String joinDirectory(String... dirs) {
		String result = dirs[0];
		for(int i = 1; i < dirs.length; ++i) {
			result = joinDirectory(result, dirs[i]);
		}
		return result;
	}

	public static void delete(String filename) {
		File file = new File(filename);
        if ( file.exists() ) {
            file.delete();
        }
	}

	public static String[] getNameAndExt(String filename) {
		String fn = filename;
		String exname = "";
		int lastPoint = filename.lastIndexOf(".");
		if(lastPoint != -1) {
			fn = filename.substring(0, lastPoint);
			exname = filename.substring(lastPoint, filename.length());
		}
		String[] names = new String[2];
		names[0] = fn;
		names[1] = exname;
		return names;
	}

	/**
	 * 获取文件扩展名
	 * @param filename
	 * @return
	 */
	public static String getExtName(String filename) {
		return getNameAndExt(filename)[1];
	}

	public static String getExtName_(String filename) {
		String exname = "";
		int lastPoint = filename.lastIndexOf(".");
		if(lastPoint != -1) {
			exname = filename.substring(lastPoint+1, filename.length());
		}
		return exname;
	}

	/**
	 * 获取文件大小，单位：byte
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static double getFileSizeOfByte(File file) throws IOException {
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			double size = fis.available();
			fis.close();
			return size;
		} else {
			return 0;
		}
	}

	 /**
	   * java获取文件大小的类 保留两位小数
	   * 单位：KB
	   * @param file
	   * @return
	   * @throws Exception
	   */
	public static double getFileSizeOfKByte(File file) throws IOException {
		double size = getFileSizeOfByte(file) / 1024.0;
		BigDecimal b = new BigDecimal(size);
		double y1 = b.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		return y1;
	}

	public static void print(String filename, String charset) {
		try {
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), charset);
			char[] cbuf = new char[1024];
			while(reader.read(cbuf, 0, 1024) != -1) {
				System.out.print(cbuf);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

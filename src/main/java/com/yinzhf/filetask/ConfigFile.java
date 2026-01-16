package com.yinzhf.filetask;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

/**
 * <pre>
 * 该配置文件读取类在程序运行过程如果配置文件更改了的话
 * 会自动重新装载配置文件，不用停应用服务
 * </pre>
 *
 */
public class ConfigFile {

	private Properties properties;
	private long configLastModifiedTime; //上次修改时间
	private String config_file_name; //="application.properties";
	private String config_file_path;

	/**
	 *
	 * 该构造函数表示配置文件和resendClass这个类具有相同的目录
	 *
	 * @param configFileName
	 * @param presentClass
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public ConfigFile(String configFileName, Class presentClass){
		this(configFileName,getPathByPackage(presentClass));
	}

	/**
	 * 该构造函数表示配置文件在根目录，及在classes目录下
	 * @param configFileName
	 * @throws Exception
	 */
	public ConfigFile(String configFileName) {
		this(configFileName, "");
	}

	/**
	 * 该构造函数读取配置文件，文件目录由参数path指定<br>
	 * <pre>
	 * 例如：配置文件config.properties在包com.maywide.gcost.test包下
	 * 则传入的path参数为  "/com/maywide/gcost/test/"
	 * </pre>
	 * @param configFileName
	 * @param path
	 * @throws Exception
	 */
	public ConfigFile(String configFileName, String path) {
		setConfig_file_name(configFileName);
		setConfig_file_path(path);
		loadConfigFile();
		if(properties == null) {
			throw new NullPointerException("初始化文件失败:"+path+" "+configFileName);
		}
	}

	private void loadConfigFile() {
		try {
			String fullPathName = getFilePathName(config_file_name, config_file_path);
			URL url = ConfigFile.class.getResource(fullPathName);
			if (url == null) {
				throw new FileNotFoundException(fullPathName +" 文件没有找到");
			}
			File cfile = new File(url.getPath());

			if (configLastModifiedTime != 0L
					&& cfile.lastModified() <= configLastModifiedTime
					&& properties != null) {
				return ;
			}
			if(properties != null) {
				properties.clear();
				properties = null;
			}
			configLastModifiedTime = cfile.lastModified();
			properties = new Properties();
			InputStream in = ConfigFile.class.getResourceAsStream(fullPathName);
			properties.load(in);
			in.close();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public String getConfig_file_path() {
		return config_file_path;
	}

	public void setConfig_file_path(String config_file_path) {
		this.config_file_path = config_file_path;
	}

	public String getConfig_file_name() {
		return config_file_name;
	}

	public void setConfig_file_name(String config_file_name) {
		this.config_file_name = config_file_name;
	}

	private String getFilePathName(String filename, String path) {
		String fullPathName = filename;
		if (path == null || path.length() == 0 ){
			if(!filename.startsWith("/") && !filename.startsWith("\\")) {
				fullPathName = "/" + filename;
			}
		} else {
			fullPathName = FileUtils.perfectDirectory(path) + filename;
		}
		return fullPathName;
	}

	@SuppressWarnings("rawtypes")
	public static String getPathByPackage(Class presentClass){
		Package p = presentClass.getPackage();
		String path = p.getName().replaceAll("\\.", "/");
		return "/"+path+"/";
	}

	/**
	 * 返回对应的值
	 * @param name
	 * @return
	 */
	public synchronized String getValue(String name) {
		try {
			if(properties == null) {
				loadConfigFile();
			}
			return properties.getProperty(name);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 返回一个对应的值，如果配置文件中没有或定义为空，则返回默认值
	 * @param name
	 * @param defaultValue	默认值
	 * @return
	 */
	public synchronized String getValue(String name, String defaultValue) {
		try {
			if(properties == null) {
				loadConfigFile();
			}
			String result = properties.getProperty(name);
			if (defaultValue != null && StringUtils.isBlank(result)) {
				result = defaultValue;
			}
			return result ;
		} catch (Exception e) {
			return null;
		}
	}

	public synchronized void setValue(String key, String value) {
		properties.setProperty(key, value);
	}

}

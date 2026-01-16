package com.yinzhf.filetask;

public class Constant {
	/**
	 * 系统运行方式。
	 * TEST: 测试运行。
	 * FORMAL: 正式运行。
	 * NOSERVER: 独立应用程序
	 * DEV:开发环境
	 */
	public enum Run {TEST, FORMAL, NOSERVER, APPSERVER, NOLOGIN, DEV}

	/**
	 * 获取SessionFactory的方式。
	 * NEW: 新建
	 * JNDI: 在JNDI查找
	 * SPRING: 在spring的context中查找
	 * NC: 末配置
	 */
	public enum SessionFactoryGeter {NEW, JNDI, SPRING, NC}

	/**
     * 实现事务管理的方式
     * NO: 无事务管理
     * JDBC: 实现的一个单数据源事务管理
     * SPRING: 由Spring管理事务
     * NC: 末配置
     */
    public enum TransactionManager {NO, JDBC, SPRING, NC};

	/**
	 * 线程池大小
	 */
	public static final int FG_SIZE = 100;

	/**
	 * Redis key 文件信息 {0}：文件ID
	 */
	public static final String FILE_INFO = "file_info:{0}";

	/**
	 * Redis key 失败文件路径 {0} 任务ID
	 */
	public static final String FAIL_FILE_PATH = "fail_file_path:{0}";

	/**
	 * Redis key 任务信息
	 */
	public static final String TASK_INFO = "task_info:{0}";


	/** 任务过期时间 单位：分钟 */
	public static final int TASK_INFO_EXPIRE_TIME = 60;

	/** 任务上传文件过期时间 单位：分钟 */
	public static final int TASK_FILE_EXPIRE_TIME = 30;
}


package com.eebbk.tableshard;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

/**
 * @项目名称：DbRegionTableNum
 * @类名称：DbRegionTableNum
 * @类描述：db固定个数分库，即对传入参数dbShardParam取余，table固定个数分表， 即对传入tableShardParam取余
 * @创建人：Administrator
 * @创建时间：2017年6月21日 下午5:34:33
 * @company:步步高教育电子有限公司
 */
public abstract class AbstractTableDbShard {
	private Logger log = Logger.getLogger(this.getClass());
	private String dbPrefix;// 数据源前缀
	private String tablePrefix;// 表名前缀
	private Integer dbShardParam;// db分片参数
	private Integer tableShardParam;// 数据表分片参数

	public String getDbPrefix() {
		return dbPrefix;
	}

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setDbPrefix(String dbPrefix) {
		this.dbPrefix = dbPrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public Integer getDbShardParam() {
		return dbShardParam;
	}

	public void setDbShardParam(Integer dbShardParam) {
		this.dbShardParam = dbShardParam;
	}

	public Integer getTableShardParam() {
		return tableShardParam;
	}

	public void setTableShardParam(Integer tableShardParam) {
		this.tableShardParam = tableShardParam;
	}

	/**
	 * 方法执行之前进行分库分表的设置
	 * @description
	 * @author liupengfei
	 * @date 2016年12月27日 上午10:16:47
	 * @param jionpoint
	 */

	public void beforeAdvice(JoinPoint jionpoint) {
		Object[] args = null;
		try {
			args = jionpoint.getArgs();
			if (args[0] instanceof Long || args[0] instanceof Integer || args[0] instanceof String) {
				setParam(args[0]);
			} else {
				Object object = ((ShardId) args[0]).getShardId();
				setParam(object);
			}
		} catch (Exception e) {
			log.error("分库分表出错", e);
			// 如果传入的分表参数为null，把这个分配到默认的第一个库，第一个表里去
			if (args == null || args[0] == null) {
				if (getDbPrefix() == null || getDbPrefix() == "") {
					DatabaseContextHolder.setTableName(getTablePrefix());
					return;
				}
				if (getTablePrefix() == null || getTablePrefix() == "") {// 如果不分表
					DatabaseContextHolder.setDbName(getDbPrefix());
					return;
				}
				DatabaseContextHolder.setShardParam(getTablePrefix(), getDbPrefix());
			}
		}
	}

	public void afterAdvice() {
		DatabaseContextHolder.clearShardParam();
	}

	public abstract void setParam(Object object);

	/**
	 * @description
	 * @author liupengfei 默认的string类型的hash方法
	 * @date 2017年6月21日 下午3:41:23
	 * @param s
	 * @return
	 */
	public Integer hashString(String s) {
		return s.hashCode();
	}
}

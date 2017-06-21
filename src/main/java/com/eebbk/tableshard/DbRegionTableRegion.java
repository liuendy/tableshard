package com.eebbk.tableshard;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

/**
 * @项目名称：DbRegionTableNum
 * @类名称：DbRegionTableNum
 * @类描述：db按照区间分库，即除以传入参数dbShardParam得到的整数，table按照区间分表， 即除以传入tableShardParam得到的整数
 * @创建人：Administrator
 * @创建时间：2017年6月21日 下午5:34:33
 * @company:步步高教育电子有限公司
 */
public class DbRegionTableRegion {
	private Logger log = Logger.getLogger(this.getClass());
	private String dbPrefix;// 数据源前缀
	private String tablePrefix;// 表名前缀
	private Integer dbShardParam;// db分片区间
	private Integer tableShardParam;// 数据表分片个数

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
		try {
			Object[] args = jionpoint.getArgs();
			if (args[0] instanceof Long || args[0] instanceof Integer || args[0] instanceof String) {
				setParam(args[0]);
			} else {
				Object object = ((ShardId) args[0]).getShardId();
				setParam(object);
			}
		} catch (Exception e) {
			log.error("分库分表出错", e);
		}
	}

	public void afterAdvice() {
		DatabaseContextHolder.clearShardParam();
	}

	private void setParam(Object object) {
		Long args = null;
		if (object instanceof Long || object instanceof Integer) {
			args = (Long) object;
		}
		if (object instanceof String) {
			args = (long) hashString((String) object);
		}
		// 如果不分库
		if (dbPrefix == null || dbPrefix == "") {
			DatabaseContextHolder.setTableName(this.tablePrefix + ((Long) args) / tableShardParam);
			return;
		}
		if (tablePrefix == null || tablePrefix == "") {// 如果不分表
			DatabaseContextHolder.setDbName(this.dbPrefix + ((Long) args) / dbShardParam);
			return;
		}
		DatabaseContextHolder.setShardParam(this.tablePrefix + ((Long) args) / tableShardParam, this.dbPrefix + ((Long) args) / dbShardParam);
	}

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
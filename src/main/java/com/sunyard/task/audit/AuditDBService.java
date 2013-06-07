package com.sunyard.task.audit;

import java.util.Date;
import java.util.Set;


public interface AuditDBService {
	
	/**
	 * 判断指定日期跑批程序是否可以运行
	 * @param day	指定的日期
	 * @return	true 可以　false 不可以
	 */
	public boolean validate(Date day);
	
	/**
	 * 获取指定时间要重新执行的任务列表
	 * @param day
	 * @return
	 */
	public Set<AuditTask> retryTaskSet(Date day);

	/**
	 * 获取指定时间，指定颗粒度要执行的任务列表
	 * @param day	时间
	 * @param granularity	
	 * 1:日;2:周;3:旬;4:月;5:季;6:半年;7:年
	 * @return
	 */
	public Set<AuditTask> getTaskSet(Date day,Integer granularity);

}

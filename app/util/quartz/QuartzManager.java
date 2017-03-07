/**
 * @Description: 
 *
 * @Title: QuartzManager.java
 * @Package com.joyce.quartz
 * @Copyright: Copyright (c) 2014
 *
 * @author Comsys-LZP
 * @date 2014-6-26 下午03:15:52
 * @version V2.0
 */
package util.quartz;

import java.util.Map;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 * 
 * @ClassName：QuartzManager
 * @ClassDesc：Job quartz
 * 
 * @version
 * 
 */

public class QuartzManager {
	private static SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();
	private static String JOB_GROUP_NAME = "PATROL_JOBGROUP_NAME";
	private static String TRIGGER_GROUP_NAME = "PATROL_TRIGGERGROUP_NAME";

	/**
	 * @Title: addJob
	 * @Description: add a Job by normal
	 * @param @param
	 *            jobName job's name
	 * @param @param
	 *            jobClass job
	 * @param @param
	 *            time job's time
	 * @return void
	 */
	@SuppressWarnings("unchecked")
	public static void addJob(String jobName, Class jobClass, String time, Map jobMap) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, jobClass);
			jobDetail.getJobDataMap().putAll(jobMap);
			CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);
			trigger.setCronExpression(time);
			sched.scheduleJob(jobDetail, trigger);
			// startJobs();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Title: modifyJobTime @Description: modify job's time @param @param
	 *         jobName job's name @param @param time job's time @return
	 *         void @throws
	 */
	@SuppressWarnings("unchecked")
	public static void modifyJobTime(String jobName, String time) {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			CronTrigger trigger = (CronTrigger) sched.getTrigger(jobName, TRIGGER_GROUP_NAME);
			if (trigger == null) {
				return;
			}
			String oldTime = trigger.getCronExpression();
			if (!oldTime.equalsIgnoreCase(time)) {
				JobDetail jobDetail = sched.getJobDetail(jobName, JOB_GROUP_NAME);
				Map jobMap = jobDetail.getJobDataMap();
				Class objJobClass = jobDetail.getJobClass();
				removeJob(jobName);
				addJob(jobName, objJobClass, time, jobMap);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Title: removeJob
	 * @Description: remove job
	 * @param @param
	 *            jobName job's name
	 * @return void
	 * @throws @since
	 *             CloudWei v1.0.0
	 */
	public static void removeJob(String jobName) {
		try {
			System.out.println("delete: "+jobName);
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.pauseTrigger(jobName, TRIGGER_GROUP_NAME);
			sched.unscheduleJob(jobName, TRIGGER_GROUP_NAME);
			sched.deleteJob(jobName, JOB_GROUP_NAME);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Title: startJobs @Description: start all jobs @param @return
	 *         void @throws
	 */
	public static void startJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Title: shutdownJobs @Description: shutdown all jobs @param @return
	 *         void @throws
	 */
	public static void shutdownJobs() {
		try {
			Scheduler sched = gSchedulerFactory.getScheduler();
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}

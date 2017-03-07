package util.quartz;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import play.Logger;
import util.patrol.Patrol;

/**
 * @author wuchengkun
 * 
 */
public class QuartzJob implements Job {

	/**
	 * @Title: execute
	 * @Description: execute job
	 * @param @param
	 *            arg0
	 * @param @throws
	 *            JobExecutionException
	 * @return void
	 */
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = format.format(new Date());
		Logger.info(time);
		Long nodeTypeId = arg0.getJobDetail().getJobDataMap().getLong("NodeTypeId");
		Long jobId = arg0.getJobDetail().getJobDataMap().getLong("JobId");
		String localPath = arg0.getJobDetail().getJobDataMap().getString("LocalPath");

		Patrol patrol = new Patrol(nodeTypeId, jobId, localPath);
		try {
			patrol.autoPatrol();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}

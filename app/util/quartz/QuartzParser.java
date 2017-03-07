package util.quartz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.TJob;
import models.TNode;
import models.TNodeType;
import models.TScript;
import util.quartz.QuartzJob;
import util.quartz.QuartzManager;

public class QuartzParser {
	/**
	 * @Title: initialization
	 * @Description: initialization Job
	 * @param @param
	 *            tNodeType
	 * @param @param
	 *            tJob
	 * @param @param
	 *            localPath
	 * @return void
	 */
	public static void initialization(TNodeType tNodeType, TJob tJob, String localPath) {
		try {
			Map jobData = new HashMap<String, Object>();
			jobData.put("NodeTypeId", tNodeType.id);
			jobData.put("JobId", tJob.id);
			jobData.put("LocalPath", localPath);

			QuartzManager.addJob(tNodeType.id + "_" + tJob.id, QuartzJob.class, tJob.T_JOB_SCHEDULES, jobData);
//			System.out.println("add: "+tNodeType.id + "_" + tJob.id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

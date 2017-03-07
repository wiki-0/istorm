package jobs;

import java.util.List;
import java.util.Set;

import controllers.NodeType;
import models.DataDictionary;
import models.TJob;
import models.TNodeType;
import models.TScript;
import models.TUser;
import play.Logger;
import play.Play;
import play.jobs.Job;
import play.jobs.OnApplicationStart;
import play.test.Fixtures;
import util.CommonUtil;
import util.quartz.QuartzManager;
import util.quartz.QuartzParser;

/**
 * 
 * Project Name：istorm_orchestration Class Name：Bootstrap Class Desc：boot start first
 * @version
 * 
 */
/**
 * 
 * <p>
 * Title: Bootstrap
 * </p>
 * <p>
 * Description:
 * </p>
 * 
 * @author wuchengkun
 */
@OnApplicationStart
public class Bootstrap extends Job {

	public void doJob() {
		Logger.info("start to init the system table...");
		initSuperManagerData();
		initZtree();
		if (DataDictionary.count() == 0) {
			Fixtures.loadModels("data.yml");
		}
		initJob();
		System.out.println("start qutarz jobs...");
		Logger.info("Istorm_orchestration init successful...");
	}

	/**
	 * @Title: initJob
	 * @Description: Init quartz job
	 * @return void
	 */
	private void initJob() {
		String localPath = Play.applicationPath + "/conf/sh";
		QuartzManager.startJobs();
		List<Long> tNodetypeIds = NodeType.getLastFloorIds();
		for (Long tNodetypeId : tNodetypeIds) {
			TNodeType tNodeType = TNodeType.findById(tNodetypeId);
			Set<TJob> tJobs = tNodeType.tJobs;
			for (TJob tJob : tJobs) {
				for (TScript tScript : tJob.SCRIPT) {
					if (tScript.T_SCRIPT_TYPE != "command")
						try {
							CommonUtil.createShell(localPath + "/" + tScript.id + tScript.T_SCRIPT_FILENAME + "."
									+ tScript.T_SCRIPT_TYPE, tScript.T_SCRIPT_COMMAND);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
				if (tNodeType.tNodes.size() > 0) {
					QuartzParser.initialization(tNodeType, tJob, localPath);

				}
			}
		}
	}

	public static void initZtree() {
		List<TNodeType> list = TNodeType.findAll();
		if (list == null || list.size() == 0) {
			TNodeType node = new TNodeType();
			node.T_NODE_TYPE_NAME = "设备分层";
			node.T_NODE_TYPE_DESC = "树顶层";
			node.pId = 0;
			node.tBusinessType = "无";
			node.ROOM_POSITION = "无";
			node.save();
		}
	}

	// 初始化admin用户
	public static void initSuperManagerData() {
		TUser user = TUser.getByName("admin");
		if (user == null) {
			user = new TUser();
			user.T_USER_NAME = "admin";
			user.T_USER_PASSWORD = "8BA36C5B1DDB9E5FA393D0CC45C19DCC";
			user.T_USER_DISPLAY_NAME = "管理员";
			user.T_USER_PERMISSION = "admin";
			user.save();
		}
	}
}

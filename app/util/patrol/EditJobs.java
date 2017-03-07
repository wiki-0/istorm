package util.patrol;

import java.util.Set;

import models.TJob;
import models.TNodeType;
import models.TScript;
import play.mvc.Controller;
import util.CommonUtil;
import util.quartz.QuartzManager;
import util.quartz.QuartzParser;

public class EditJobs {

	public static void addJob(Long id, TNodeType tNodeType, String localpath) {
		TJob tJob = TJob.findById(id);
		for (TScript tScript : tJob.SCRIPT) {
			if (tScript.T_SCRIPT_TYPE != "command")
				try {
					CommonUtil.createShell(
							localpath + "/" + tScript.id + tScript.T_SCRIPT_FILENAME + "." + tScript.T_SCRIPT_TYPE,
							tScript.T_SCRIPT_COMMAND);
					// System.out.println(
					// localpath + "/" + tScript.id + tScript.T_SCRIPT_FILENAME
					// + "." + tScript.T_SCRIPT_TYPE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		QuartzParser.initialization(tNodeType, tJob, localpath);
	}
}

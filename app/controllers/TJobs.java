package controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.TJob;
import models.TJobScript;
import models.TNode;
import models.TNodeType;
import models.TRelevancyUserGroup;
import models.TResult;
import models.TScript;
import models.TScriptGroup;
import models.TUser;
import models.TUserGroup;
import play.Play;
import play.mvc.With;
import util.DataTableSource;
import util.patrol.EditJobs;
import util.patrol.ManualPatrol;
import util.quartz.QuartzManager;

@With(Secure.class)
public class TJobs extends CRUD {
	public static String localPath = Play.applicationPath + "/conf/sh";

	public static void index() {
		render();
	}

	public static void cronmaker() {
		render();
	}

	public static void multiselect() {
		render();
	}

	/**
	 * 获得任务列表
	 */
	public static void getJobList() {

		StringBuilder sql = new StringBuilder();
		if (params.get("jobName") != null && !(params.get("jobName").equals(""))) {
			sql.append("T_JOB_NAME like '%" + params.get("jobName") + "%'");
		}
		if (params.get("alarm") != null && !(params.get("alarm").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_ALARM is " + params.get("alarm") + " ");
		}
		if (params.get("release") != null && !(params.get("release").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_RELEASE is " + params.get("release") + " ");
		}
		List<TJob> list = TJob.find(sql.toString()).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		// admin 权限 显示所有
		if (user.T_USER_PERMISSION != null && user.T_USER_PERMISSION.equals("admin")) {
			for (TJob tJob : list) {
				obj = new JsonObject();
				obj.addProperty("T_JOB_NAME",
						"<a href='#' onclick='return showJobDetail(" + tJob.id + ")' >" + tJob.T_JOB_NAME + "</a>");
				obj.addProperty("T_JOB_SCHEDULES", tJob.T_JOB_SCHEDULES);
				if (tJob.T_ALARM) {
					obj.addProperty("T_ALARM", "是");
				} else {
					obj.addProperty("T_ALARM", "否");
				}
				List<TJobScript> tJobScripts = TJobScript.find("T_JOB_ID is ?1 order by id", tJob.id).fetch();
				StringBuilder sb = new StringBuilder("");
				int i = 1;
				for (TJobScript tJobScript : tJobScripts) {
					TScript tScript = TScript.findById(tJobScript.T_SCRIPT_ID);
					sb.append(i + "." + tScript.T_SCRIPT_NAME + "<br>");
					i++;
				}
				obj.addProperty("SCRIPT", sb.toString());
				obj.addProperty("REGROUP", tJob.MODIFY.T_GROUP_NAME);
				obj.addProperty("T_JOB_DESC", tJob.T_JOB_DESC);
				if (tJob.T_RELEASE) {
					obj.addProperty("T_RELEASE",
							"<button id='" + tJob.id + "rel' class='btn btn-primary btn-xs' onclick='releaseJobById("
									+ tJob.id + ")'>撤销发布</button>");
				} else {
					obj.addProperty("T_RELEASE",
							"<button id='" + tJob.id + "rel' class='btn btn-primary btn-xs' onclick='releaseJobById("
									+ tJob.id + ")'>发布</button>");
				}
				obj.addProperty("DELETE",
						"<button id='" + tJob.id
								+ "del' class='btn btn-danger btn-xs' data-toggle='modal' data-target='#modal-sizes-1' onclick='setJobById("
								+ tJob.id + ")'>删除</button>");
				arr.add(obj);

			}
			renderJSON(new DataTableSource(request, arr));
		}
		if (list != null && list.size() > 0 && user != null) {
			for (TJob tJob : list) {
				obj = new JsonObject();
				Boolean modify = false;
				Boolean show = false;
				List<TRelevancyUserGroup> groupList = TRelevancyUserGroup.find("byUSER_GROUP_ID", tJob.MODIFY).fetch();
				for (TRelevancyUserGroup group : groupList) {
					if (group.USER_ID.equals(Long.toString(user.id))) {
						modify = true;
						break;
					}
				}
				// 任务创建者可以看到
				if (tJob.CREATE == user) {
					modify = true;
				}
				// 发布后 可以修改
				if (modify) {
					obj.addProperty("T_JOB_NAME",
							"<a href='#' onclick='return showJobDetail(" + tJob.id + ")' >" + tJob.T_JOB_NAME + "</a>");
				} else {
					obj.addProperty("T_JOB_NAME", tJob.T_JOB_NAME);
				}
				obj.addProperty("T_JOB_SCHEDULES", tJob.T_JOB_SCHEDULES);
				if (tJob.T_ALARM) {
					obj.addProperty("T_ALARM", "是");
				} else {
					obj.addProperty("T_ALARM", "否");
				}
				List<TJobScript> tJobScripts = TJobScript.find("T_JOB_ID is ?1 order by id", tJob.id).fetch();
				StringBuilder sb = new StringBuilder("");
				int i = 1;
				for (TJobScript tJobScript : tJobScripts) {
					TScript tScript = TScript.findById(tJobScript.T_SCRIPT_ID);
					sb.append(i + "." + tScript.T_SCRIPT_NAME + "<br>");
					i++;
				}
				obj.addProperty("SCRIPT", sb.toString());
				obj.addProperty("REGROUP", tJob.MODIFY.T_GROUP_NAME);
				obj.addProperty("T_JOB_DESC", tJob.T_JOB_DESC);
				if (tJob.T_RELEASE) {
					obj.addProperty("T_RELEASE",
							"<button id='" + tJob.id + "rel' class='btn btn-primary btn-xs' onclick='releaseJobById("
									+ tJob.id + ")'>撤销发布</button>");
				} else {
					obj.addProperty("T_RELEASE",
							"<button id='" + tJob.id + "rel' class='btn btn-primary btn-xs' onclick='releaseJobById("
									+ tJob.id + ")'>发布</button>");
				}
				obj.addProperty("DELETE",
						"<button id='" + tJob.id
								+ "del' class='btn btn-danger btn-xs' data-toggle='modal' data-target='#modal-sizes-1' onclick='setJobById("
								+ tJob.id + ")'>删除</button>");
				if (!modify) {
					List<TRelevancyUserGroup> groupList1 = TRelevancyUserGroup.find("byUSER_GROUP_ID", tJob.SHOW)
							.fetch();
					for (TRelevancyUserGroup group : groupList1) {
						if (group.USER_ID.equals(Long.toString(user.id))) {
							show = true;
							break;
						}
					}
				}
				if (modify || show) {
					arr.add(obj);
				}
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	/**
	 * 获得脚本
	 */
	public static void getScriptGroup() {
		List<TScriptGroup> scriptGroupList = TScriptGroup.findAll();
		JsonArray arr = new JsonArray();
		Map map = new LinkedHashMap();
		String sid = params.get("sid");
		// 获得 任务对应 已选择脚本
		if (!sid.equals("")) {
			List<TJobScript> tJobScripts = TJobScript.find("T_JOB_ID is ?1 order by id", Long.parseLong(sid)).fetch();
			for (TJobScript tJobScript : tJobScripts) {
				TScript tScript = TScript.findById(tJobScript.T_SCRIPT_ID);
				map.put(tScript.id, tScript.T_SCRIPT_NAME);
			}
		}
		JsonArray group;
		JsonObject sc;
		for (TScriptGroup scriptGroup : scriptGroupList) {
			List<TScript> scriptList = TScript.find("byT_SCRIPT_GROUP_ID", new Long(scriptGroup.id).intValue()).fetch();
			group = new JsonArray();
			if (scriptList.size() > 0) {
				for (TScript script : scriptList) {
					sc = new JsonObject();
					sc.addProperty("group", scriptGroup.T_SCRIPT_GROUP_NAME);
					sc.addProperty("value", script.id);
					sc.addProperty("text", script.T_SCRIPT_NAME);
					if (null != map.get(script.id)) {
						sc.addProperty("select", "selected");
					} else {
						sc.addProperty("select", "");
					}
					group.add(sc);
				}
				arr.add(group);
			}
		}
		List<TScript> scripts = TScript.find("byT_SCRIPT_GROUP_ID", 0).fetch();
		group = new JsonArray();
		if (scripts.size() != 0) {
			for (TScript script : scripts) {
				sc = new JsonObject();
				sc.addProperty("group", "未分组");
				sc.addProperty("value", script.id);
				sc.addProperty("text", script.T_SCRIPT_NAME);
				if (null != map.get(script.id)) {
					sc.addProperty("select", "selected");
				} else {
					sc.addProperty("select", "");
				}
				group.add(sc);
			}
			arr.add(group);
		}
		List<TScript> scall = TScript.findAll();
		if (scall.size() == 0) {
			sc = new JsonObject();
			sc.addProperty("group", "未分组");
			sc.addProperty("value", "");
			sc.addProperty("text", "未选择");
			sc.addProperty("select", "");
			group.add(sc);
			arr.add(group);
		}
		renderText(arr);
	}

	/**
	 * 获得脚本选择顺序
	 */
	public static void getScriptSelect() {
		JsonArray arr = new JsonArray();
		String sid = params.get("sid");
		if (!sid.equals("")) {
			List<TJobScript> tJobScripts = TJobScript.find("T_JOB_ID is ?1 order by id", Long.parseLong(sid)).fetch();
			JsonObject obj;
			for (TJobScript tJobScript : tJobScripts) {
				obj = new JsonObject();
				TScript tScript = TScript.findById(tJobScript.T_SCRIPT_ID);
				obj.addProperty("value", tScript.id);
				arr.add(obj);
			}
		}
		renderText(arr);
	}

	public static void getNode() {
		List<TNode> List = TNode.findAll();
		JsonArray arr = new JsonArray();
		if (List != null && List.size() >= 0) {
			JsonObject obj;
			for (TNode o : List) {
				obj = new JsonObject();
				obj.addProperty("value", o.id);
				obj.addProperty("text", o.T_NODE_IP + o.T_NODE_NAME);
			}
		}
		renderText(arr);
	}

	/**
	 * 保存任务
	 */
	public static void saveJob(TJob tJob) {
		String scripList = params.get("sList");
		String jodId = params.get("jodId");
		if (jodId != null && !jodId.equals("")) {
			TJob job = TJob.findById(Long.parseLong(jodId));

			Set<TNodeType> tNodeTypes = job.tNodeTypes;
			for (TNodeType tNodeType : tNodeTypes) {
				if (tNodeType.tNodes.size() > 0) {
					QuartzManager.removeJob(tNodeType.id + "_" + job.id);
				}
			}
			job.T_JOB_NAME = tJob.T_JOB_NAME;
			job.T_ALARM = tJob.T_ALARM;
			job.DISTRIBUTE = TUserGroup.findById(Long.parseLong(params.get("distributeGroup")));
			job.SHOW = TUserGroup.findById(Long.parseLong(params.get("checkGroup")));
			job.MODIFY = TUserGroup.findById(Long.parseLong(params.get("editGroup")));
			job.T_JOB_CREATETIME = new Date();
			job.T_JOB_DESC = tJob.T_JOB_DESC;
			job.T_JOB_SCHEDULES = tJob.T_JOB_SCHEDULES;
			if (scripList != null && !scripList.equals("")) {
				List<TJobScript> jobScripts = TJobScript.find("byT_Job_Id", job.id).fetch();
				for (TJobScript jobScript : jobScripts) {
					jobScript._delete();
				}
				job.SCRIPT.clear();
				for (String hostId : scripList.split(",")) {
					TScript tScript = TScript.findById(Long.parseLong(hostId));
					job.SCRIPT.add(tScript);
				}
			}
			job.save();
			for (TNodeType tNodeType : tNodeTypes) {
				if (tNodeType.tNodes.size() > 0) {
					EditJobs.addJob(job.id, tNodeType, localPath);
				}
			}
		} else {
			if (scripList != null && !scripList.equals("")) {
				for (String hostId : scripList.split(",")) {
					TScript tScript = TScript.findById(Long.parseLong(hostId));
					tJob.SCRIPT.add(tScript);
				}
			}
			tJob.DISTRIBUTE = TUserGroup.findById(Long.parseLong(params.get("distributeGroup")));
			tJob.SHOW = TUserGroup.findById(Long.parseLong(params.get("checkGroup")));
			tJob.MODIFY = TUserGroup.findById(Long.parseLong(params.get("editGroup")));
			tJob.T_JOB_CREATETIME = new Date();
			// TUser user = TUser.find("byT_USER_NAME",
			// Security.connected()).first();
			// if (user!= null){
			// tJob.CREATE = TUser.findById(user.id);
			// }
			tJob.CREATE = TUser.find("byT_USER_NAME", Security.connected()).first();
			tJob.save();
		}
		index();
	}

	/**
	 * 编辑任务 获得任务内容
	 */
	public static void editJob(String id) {
		TJob tJob = TJob.find("byId", Long.parseLong(id)).first();
		JsonArray arr = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("id", tJob.id);
		obj.addProperty("T_JOB_NAME", tJob.T_JOB_NAME);
		obj.addProperty("T_JOB_DESC", tJob.T_JOB_DESC);
		obj.addProperty("T_JOB_SCHEDULES", tJob.T_JOB_SCHEDULES);
		obj.addProperty("T_ALARM", tJob.T_ALARM);
		obj.addProperty("DISTRIBUTE", tJob.DISTRIBUTE.id);
		obj.addProperty("SHOW", tJob.SHOW.id);
		obj.addProperty("MODIFY", tJob.MODIFY.id);
		arr.add(obj);
		renderText(arr);
	}

	/**
	 * 删除任务
	 */
	public static void deleteJob() throws Exception {
		String id = params.get("id");
		if (id != null) {
			TJob tJob = TJob.findById(Long.parseLong(id));
			Set<TNodeType> NODES = tJob.tNodeTypes;
			for (TNodeType tResNode : NODES) {
				if (tResNode.tNodes.size() > 0) {
					QuartzManager.removeJob(tResNode.id + "_" + tJob.id);
				}
				// TResult.delete("delete from TResult t where t.TNODETYPE.id =
				// "+tResNode.id+" and t.JOB.id="+tJob.id);
				tResNode.tJobs.remove(tJob);
				tResNode.save();
			}
			tJob.SCRIPT.remove(tJob);
			List<TResult> tResultList = TResult.find("from TResult t where JOB.id = " + tJob.id).fetch();
			for (TResult result : tResultList) {
				result.delete();
			}
			tJob._delete();
		}
		renderText("true");
	}

	/**
	 * 发布任务
	 */
	public static void releaseJob() throws Exception {
		String id = params.get("id");
		if (id != null) {
			TJob tJob = TJob.findById(Long.parseLong(id));
			if (tJob.T_RELEASE && tJob.tNodeTypes.size() == 0) {
				tJob.T_RELEASE = false;
				tJob.save();
				renderText("true");
			} else if (tJob.T_RELEASE && tJob.tNodeTypes.size() > 0) {
				renderText("error");
			} else {
				tJob.T_RELEASE = true;
				tJob.save();
				renderText("false");
			}
		}
		renderText("null");
	}

	/**
	 * 获得所有用户组
	 */
	public static void getGroup() {
		List<TUserGroup> groupList = TUserGroup.findAll();
		JsonArray arr = new JsonArray();
		for (TUserGroup group : groupList) {
			JsonObject obj = new JsonObject();
			obj.addProperty("id", group.id);
			obj.addProperty("name", group.T_GROUP_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	/****************************************************************************************************/
	/**
	 * 设备分层页面--点击分层获取到任务列表
	 */
	public static void nodeTypeGetJobList() {
		String id = params.get("node_type_id");
		String flag = params.get("flag");
		// System.out.println("ids: " + ids + ",flag: " + flag);

		JsonArray arr = new JsonArray();
		JsonObject obj;
		// 页面任务列表标志位
		if (flag.equals("0")) {
			if (id.equals("1")) {
				obj = new JsonObject();
				obj.addProperty("T_JOB_NAME", "");
				obj.addProperty("delete", "");
				obj.addProperty("executeNow", "");
				arr.add(obj);
			} else {
				TNodeType nodeType = TNodeType.findById(Long.parseLong(id));
				Set<TJob> totalJobs = nodeType.tJobs;
				if (totalJobs != null && totalJobs.size() > 0) {
					for (TJob job : totalJobs) {
						obj = new JsonObject();
						obj.addProperty("T_JOB_NAME", job.T_JOB_NAME);
						obj.addProperty("delete",
								"<button class='btn btn-danger btn-xs' type='button' onclick='deleteJob(" + job.id
										+ ")' >" + "删除</button>");
						if(nodeType.tNodes.size() > 0){
							obj.addProperty("executeNow",
									"<button id='job_"+job.id+"' class='btn btn-primary btn-xs' type='button' onclick='executeJob(" + job.id
											+ ")' >" + "立即执行</button>");
						}else{
							obj.addProperty("executeNow",
									"<button class='btn btn-primary btn-xs' type='button' onclick='executeJob(" + job.id
											+ ")' disabled >" + "立即执行</button>");
						}
						
						arr.add(obj);
					}
				} else {
					obj = new JsonObject();
					obj.addProperty("T_JOB_NAME", " ");
					obj.addProperty("delete", "");
					obj.addProperty("executeNow", "");
					arr.add(obj);
				}
			}
		}
		// 新增任务列表标志位
		if (flag.equals("1")) {
			// 默认加载整个Job表
			List<TJob> list = TJob.find("select t from TJob t where T_RELEASE= ?", true).fetch();

			TNodeType nodeType = TNodeType.findById(Long.parseLong(id));
			Set<TJob> tJobs = nodeType.tJobs;// 该分层下所有的设备
			Iterator<TJob> iterator = tJobs.iterator();

			List<Long> selectIds = new ArrayList<>();
			List<Long> unSelectIds = new ArrayList<>();
			while (iterator.hasNext()) {
				selectIds.add(iterator.next().id);
			}
			for (TJob job : list) {
				if (!selectIds.contains(job.id)) {
					unSelectIds.add(job.id);
				}
			}
			for (Long unId : unSelectIds) {
				TJob job = TJob.findById(unId);
				obj = new JsonObject();
				obj.addProperty("id",
						"<input type='checkbox' class='checkedID' name='jobCheckbox' value='" + job.id + "'></input>");
				obj.addProperty("T_JOB_NAME", job.T_JOB_NAME);
				arr.add(obj);
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	/**
	 * 把任务挂载到分层上(新增任务的保存按钮)
	 */
	public static void addJobToNodeType() {
		String jobIds = params.get("nodeIds");
		String treeNodeId = params.get("treeNodeId");
		String[] _jobIds = jobIds.split(",");
		// System.out.println(jobIds+" treeNodeId: "+treeNodeId);

		TNodeType nodeType = TNodeType.findById(Long.parseLong(treeNodeId));

		// 本层保存jobs
		for (String jobId : _jobIds) {
			TJob tJob = TJob.findById(Long.parseLong(jobId));
			nodeType.tJobs.add(tJob);
			nodeType.save();
			if (nodeType.tNodes.size() > 0) {
				EditJobs.addJob(tJob.id, nodeType, localPath);
			}
		}
		/************ 保存子层的jobs **************/
		NodeType tool = new NodeType();
		tool.ids.clear();
		tool.getChildrenExtend(Long.parseLong(treeNodeId));
		Set<Long> ids = tool.ids;
		Iterator<Long> iterator = ids.iterator();
		while (iterator.hasNext()) {
			Long id1 = iterator.next();
			TNodeType node = TNodeType.findById(id1);
			for (String jobId : _jobIds) {
				TJob tJob = TJob.findById(Long.parseLong(jobId));
				node.tJobs.add(tJob);
				node.save();
				if (node.tNodes.size() > 0) {
					EditJobs.addJob(tJob.id, node, localPath);
				}
			}
		}
		renderText("OK");
	}

	/**
	 * 设备分层--把任务从任务列表中删除（点击删除图标生效）
	 */
	public static void deleteJobById() {
		String id = params.get("id");
		String treeNodeId = params.get("treeNodeId");

		String jobName = treeNodeId + "_" + id;

		TNodeType nodeType = TNodeType.findById(Long.parseLong(treeNodeId));
		if (nodeType.tNodes.size() > 0) {
			QuartzManager.removeJob(jobName);
		}
		TJob tJob = TJob.findById(Long.parseLong(id));

		nodeType.tJobs.remove(tJob);
		nodeType.save();

		NodeType tool = new NodeType();
		tool.ids.clear();
		tool.getChildrenExtend(Long.parseLong(treeNodeId));
		Set<Long> ids = tool.ids;
		Iterator<Long> iterator = ids.iterator();
		while (iterator.hasNext()) {
			Long id1 = iterator.next();
			TNodeType node = TNodeType.findById(id1);

			if (node.tNodes.size() > 0) {
				QuartzManager.removeJob(jobName);
			}
			node.tJobs.remove(tJob);
			node.save();
		}

		renderText("OK");
	}
	
	/**
	 * 立即执行任务
	 */
	public static void executeJob(){
		//1、删掉定时调度
		String jobId = params.get("jobId");
		String nodeTypeId = params.get("treeNodeId");
		
//		String jobName = nodeTypeId + "_" + jobId;
		
		/*TNodeType nodeType = TNodeType.findById(Long.parseLong(nodeTypeId));
		if (nodeType.tNodes.size() > 0) {
			QuartzManager.removeJob(jobName);
		}*/
		//2、立即执行
		ManualPatrol manualPatrol = new ManualPatrol(Long.parseLong(nodeTypeId), Long.parseLong(jobId), localPath);
		String status="";
		try {
			status = manualPatrol.manualPatrol();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		renderText(status);
	}
	/**
	 * 从下往上找出 继承的 任务totalJobs
	 * 
	 * @param totalJobs
	 * @param id
	 * @return
	 */
	public Set<TJob> extendJobs(Set<TJob> totalJobs, Long id) {
		TNodeType nodeType = TNodeType.findById(id);// 本层
		Set<TJob> jobs = nodeType.tJobs;
		if (jobs != null && jobs.size() > 0) {
			for (TJob job : jobs) {
				totalJobs.add(job);
			}
		}
		if (nodeType.isExtend) {
			return extendJobs(totalJobs, nodeType.pId);// 传入上一层的id
		}
		return totalJobs;
	}

	/**
	 * 从向上下找出所有 子节点ids
	 * 
	 * @param ids
	 * @param id
	 * @return
	 */
	public List<Long> findChildrenIds(List<Long> ids, Long id) {
		List<TNodeType> cNodeType = TNodeType.find("byPId", id).fetch();// 子层
		if (cNodeType != null && cNodeType.size() > 0) {
			for (TNodeType temp : cNodeType) {
				ids.add(temp.id);
				return findChildrenIds(ids, temp.id);
			}
		}
		return ids;
	}

}

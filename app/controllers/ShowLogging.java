package controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.*;
import util.DataTableSource;

public class ShowLogging extends CRUD {

	public static void index() {
		render();
	}

	public static void getStatistics() {
		StringBuilder sql = new StringBuilder();
		if (params.get("jobName") != null && !(params.get("jobName").equals(""))) {
			sql.append("JOB.T_JOB_NAME like '%" + params.get("jobName") + "%'");
		}
		if (params.get("nodeLayered") != null && !(params.get("nodeLayered").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("TNODETYPE.T_NODE_TYPE_NAME like '%" + params.get("nodeLayered") + "%' ");
		}
		if (params.get("scriptName") != null && !(params.get("scriptName").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("tScript.T_SCRIPT_NAME like '%" + params.get("scriptName") + "%' ");
		}
		if (params.get("nName") != null && !(params.get("nName").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("NODE.T_NODE_NAME like '%" + params.get("nName") + "%' ");
		}
		if (params.get("ip") != null && !(params.get("ip").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("NODE.T_NODE_IP like '%" + params.get("ip") + "%' ");
		}
		if (params.get("resultType") != null && !(params.get("resultType").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_RESULT_ALARM_LEVEL like '%" + params.get("resultType") + "%' ");
		}
		if (params.get("queryTime") != null && !(params.get("queryTime").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_RESULT_TIME between '" + params.get("queryTime") + " 00:00:00' and '"
					+ params.get("queryTime") + " 23:59:59'");
		} else {
			// 默认显示当天的告警
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			String dateTime = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			sql.append("T_RESULT_TIME between '" + dateTime + " 00:00:00' and '" + dateTime + " 23:59:59'");
		}
		List<TResult> list = TResult.find(sql.toString()).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj = null;
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		if (list != null && list.size() > 0) {
			for (TResult tResult : list) {
				obj = new JsonObject();
				if (tResult.T_RESULT_ALARM_LEVEL != null) {
					if (tResult.T_RESULT_ALARM_LEVEL.equals("1")) {
						obj.addProperty("T_RESULT_ALARM_LEVEL",
								"<img src='/new_public/img/alarm/alert_critical.png' width='30px' height='30px'/> 严重");
					} else if (tResult.T_RESULT_ALARM_LEVEL.equals("2")) {
						obj.addProperty("T_RESULT_ALARM_LEVEL",
								"<img src='/new_public/img/alarm/alert_major.png' width='30px' height='30px'/> 主要");
					} else if (tResult.T_RESULT_ALARM_LEVEL.equals("3")) {
						obj.addProperty("T_RESULT_ALARM_LEVEL",
								"<img src='/new_public/img/alarm/alert_secondary.png' width='30px' height='30px'/> 次要");
					} else if (tResult.T_RESULT_ALARM_LEVEL.equals("4")) {
						obj.addProperty("T_RESULT_ALARM_LEVEL",
								"<img src='/new_public/img/alarm/alert_normal.png' width='30px' height='30px'/> 提示");
					} else if (tResult.T_RESULT_ALARM_LEVEL.equals("0")) {
						obj.addProperty("T_RESULT_ALARM_LEVEL",
								"<img src='/new_public/img/alarm/alert_green.png' width='30px' height='30px'/> 正常");
					}
				} else {
					obj.addProperty("T_RESULT_ALARM_LEVEL", "未解析");
				}
				if (tResult.T_RESULT_ALARM_STATUS.equals("1") && tResult.T_RESULT_ALARM_LEVEL == null
						&& tResult.T_RESULT_STATUS.equalsIgnoreCase("OK")) {
					obj.addProperty("T_RESULT_ALARM_LEVEL", "未配置告警");
				}
				if (tResult.T_RESULT_STATUS.equals("ERROR")) {
					obj.addProperty("T_RESULT_ALARM_LEVEL", "连接失败");
				}
				obj.addProperty("T_JOB_NAME", tResult.JOB.T_JOB_NAME);
				obj.addProperty("T_NODE_TYPE_NAME", tResult.TNODETYPE.T_NODE_TYPE_NAME);
				obj.addProperty("T_NODE_NAME", tResult.NODE.T_NODE_NAME);
				obj.addProperty("T_NODE_IP", tResult.NODE.T_NODE_IP);
				obj.addProperty("T_SCRIPT_NAME", tResult.tScript.T_SCRIPT_NAME);
				String outPut = tResult.T_RESULT_OUTPUT.replaceAll("\r", "").replaceAll("\n", "");
				if (outPut.length() > 100) {
					obj.addProperty("T_RESULT_OUTPUT", "<a href='#' onclick='return showResultDetail(" + tResult.id
							+ ")' >" + outPut.substring(0, 100) + "..." + "</a>");
				} else {
					obj.addProperty("T_RESULT_OUTPUT", outPut);
				}
				obj.addProperty("T_RESULT_TIME",
						new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tResult.T_RESULT_TIME));
				if (tResult.USER != null) {
					obj.addProperty("DISTRIBUTE", tResult.USER.T_USER_DISPLAY_NAME);
				} else {
					obj.addProperty("DISTRIBUTE",
							"<button id='" + tResult.id + "res' class='btn btn-primary btn-xs' onclick='setDistribute("
									+ tResult.id + ")'>选择人员</button>");
				}
				// 任务报告发送组 可以查看 和 任务创建者
				Boolean show = false;
				TJob job = TJob.findById(tResult.JOB.id);
				TUserGroup group = TUserGroup.findById(job.DISTRIBUTE.id);
				List<TRelevancyUserGroup> userGroups = TRelevancyUserGroup.find("byUSER_GROUP_ID", group.id).fetch();
				for (TRelevancyUserGroup userGroup : userGroups) {
					TUser tUser = TUser.findById(Long.parseLong(userGroup.USER_ID));
					if (user.T_USER_NAME != null && user.T_USER_NAME.equals(tUser.T_USER_NAME)) {
						show = true;
						break;
					}
				}
				// 任务创建人
				if (job.CREATE == user) {
					show = true;
				}
				// admin 权限
				if (user.T_USER_PERMISSION != null && user.T_USER_PERMISSION.equals("admin")) {
					show = true;
				}
				if (show) {
					arr.add(obj);
				}
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	/**
	 * 分派人员
	 */
	public static void setDistributeUser() {
		if (params.get("user_id") == null || params.get("user_id").equals("")) {
			renderText("false");
		}
		TResult result = TResult.findById(Long.parseLong(params.get("resultId")));
		TUser tUser = TUser.findById(Long.parseLong(params.get("user_id")));
		result.USER = tUser;
		result.save();
		renderText("true");
	}

	/**
	 * 获取告警通知内容
	 */
	public static void getBell() {
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		if (user != null) {
			List<TResult> results = TResult
					.find("select t from TResult t where t.USER.id=?1 and t.T_RESULT_ISREAD=null", user.id).fetch();
			if (results != null && results.size() > 0) {
				StringBuffer sb = new StringBuffer();
				for (TResult result : results) {
					sb.append("<li id='" + result.id
							+ "bell' data-toggle='modal' data-target='#modal-bell' onclick='delBell(" + result.id
							+ ")'><a><div>");
					if (result.T_RESULT_ALARM_LEVEL == null) {
						sb.append("<i class='fa fa-circle text-warning'></i>");
					} else if (result.T_RESULT_ALARM_LEVEL.equals("1")) {
						sb.append("<i class='fa fa-circle text-danger'></i>");
					} else if (result.T_RESULT_ALARM_LEVEL.equals("2")) {
						sb.append("<i class='fa fa-circle text-warning'></i>");
					} else if (result.T_RESULT_ALARM_LEVEL.equals("3")) {
						sb.append("<i class='fa fa-circle text-warning2'></i>");
					} else if (result.T_RESULT_ALARM_LEVEL.equals("4")) {
						sb.append("<i class='fa fa-circle text-info'></i>");
					}
					sb.append(" " + result.NODE.T_NODE_NAME + " " + result.NODE.T_NODE_IP);
					sb.append("<span class='pull-right'> ");
					sb.append(formatDateToZH(result.T_RESULT_TIME));
					sb.append("</span></div>");
					if (result.T_RESULT_OUTPUT.length() > 24) {
						sb.append(result.T_RESULT_OUTPUT.substring(0, 24) + "...");
					} else {
						sb.append(result.T_RESULT_OUTPUT);
					}
					sb.append(
							"<span class='pull-right'><i class='fa fa-trash-o'></i></span></a></li><li class='divider'></li>");
				}
				sb.append("<li><div class='text-center link-block'data-toggle='modal' data-target='#modal-delALL'>");
				sb.append("<strong>清空所有短消息</strong></a></div></li>");
				renderText(sb.toString());
			}
		}
		renderText("null");
	}

	public static void getBellSize() {
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		if (user != null) {
			List<TResult> results = TResult
					.find("select t from TResult t where t.USER.id=?1 and t.T_RESULT_ISREAD=null", user.id).fetch();
			if (results != null && results.size() > 0) {
				renderText(results.size());
			}
		}
		renderText("0");
	}

	public static void delResult() {
		if (params.get("id") != null && !params.get("id").equals("")) {
			TResult result = TResult.findById(Long.parseLong(params.get("id")));
			result.T_RESULT_ISREAD = "1";
			result.save();
			renderText("true");
		}
		renderText("false");
	}

	public static void deleteAll() {
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		if (user != null) {
			List<TResult> results = TResult
					.find("select t from TResult t where t.USER.id=?1 and t.T_RESULT_ISREAD=null", user.id).fetch();
			if (results != null && results.size() > 0) {
				for (TResult result : results) {
					result.T_RESULT_ISREAD = "1";
					result.save();
				}
				renderText("true");
			}
		}
		renderText("false");
	}

	/**
	 * 获取用户组
	 */
	public static void getUser() {
		JsonArray arr = new JsonArray();
		JsonObject obj;
		if (params.get("groupId") != null && !params.get("groupId").equals("")) {
			List<TRelevancyUserGroup> userGroups = TRelevancyUserGroup
					.find("byUSER_GROUP_ID", Long.parseLong(params.get("groupId"))).fetch();
			for (TRelevancyUserGroup userGroup : userGroups) {
				TUser user = TUser.findById(Long.parseLong(userGroup.USER_ID));
				obj = new JsonObject();
				obj.addProperty("id", user.id);
				obj.addProperty("name", user.T_USER_NAME);
				arr.add(obj);
			}
		}
		renderText(arr);
	}

	public static void getResultDeatil() {
		if (params.get("id") != null && !params.get("id").equals("")) {
			TResult result = TResult.findById(Long.parseLong(params.get("id")));
			renderText(result.T_RESULT_OUTPUT.replaceAll("\r", "").replaceAll("\n", ""));
		}
		renderText("null");
	}

	public static StringBuffer formatDateToZH(Date date) {
		Date now = new Date();
		long l = now.getTime() - date.getTime();
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		StringBuffer sb = new StringBuffer();
		sb.append("");
		if (day == 0 && hour == 0 && min == 0) {
			sb.append("刚刚");
		} else if (day == 0 && hour == 0 && min >= 0) {
			sb.append(min + "分钟前");
		} else if (day == 0 && hour >= 0) {
			sb.append(hour + "小时前");
		} else if (day >= 31) {
			sb.append((int) day / 30 + "个月前");
		} else if (day >= 0) {
			sb.append(day + "天前");
		}
		return sb;
	}
}

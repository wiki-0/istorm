package controllers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import play.Play;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.CRUD.ObjectType;
import models.*;
import util.DataTableSource;

public class ReportDs extends CRUD {

	public static void index() {
		render();
	}
	
	public static void downloadDoc() throws Exception {
		String localPath = Play.applicationPath +"/public/Template/Changeling.doc";
//		System.out.println("localPath = " + localPath);
//		localPath = localPath.replaceAll("file:/", "");		
//		String xmPath = localPath.replaceAll("conf/", "public/Template/angeling.doc");		
		File file = new File(localPath);		
		FileInputStream inputStream = new FileInputStream(file);
		OutputStream outputStream = null;
		outputStream = response.out;
		InputStream fis = new BufferedInputStream(new FileInputStream(file));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
		response.setHeader("Content-Disposition",
				"attachment;filename=".concat(String.valueOf(URLEncoder.encode("Template" + (int) (Math.random() * 10000) + ".doc", "UTF-8"))));		
		OutputStream toClient = response.out;
		response.contentType ="application/octet-stream";
		toClient.write(buffer);
		toClient.flush();
		toClient.close();		
		
	}
	public static void deleteFile() {
		ReportD pep = ReportD.findById(Long.parseLong(params.get("id")));
		pep.delete();
		renderText("true");

	}
	public static void getJobList() {
		List<TJob> list = TJob.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		//顺序选择 默认为空
		obj = new JsonObject();
		obj.addProperty("id", "");
		obj.addProperty("name", "未选择");
		arr.add(obj);
		for (TJob job : list) {
			Boolean show = false;
			// 任务创建者和admin可以选择对应任务
			if (user.T_USER_PERMISSION != null && user.T_USER_PERMISSION.equals("admin")) {
				show = true;
			}
			else if (job.CREATE == user) {
				show = true;
			}
			obj = new JsonObject();
			obj.addProperty("id", job.id);
			obj.addProperty("name", job.T_JOB_NAME);
			if(show){
				arr.add(obj);
			}
		}
		renderText(arr);
	}	
	
	public static void getNodetypeList() {
		String jobId = params.get("id");
		JsonArray arr = new JsonArray();
		JsonObject obj;
		obj = new JsonObject();
		obj.addProperty("id", "");
		obj.addProperty("name", "未选择");
		arr.add(obj);
		if (jobId != null && !jobId.equals("")){
			TJob tjob = TJob.findById(Long.parseLong(jobId));
			Set<TNodeType> list = tjob.tNodeTypes;
			for (TNodeType nodetype : list) {
				obj = new JsonObject();
				obj.addProperty("id", nodetype.id);
				obj.addProperty("name", nodetype.T_NODE_TYPE_NAME);
				if (nodetype.pId!=0){
					arr.add(obj);
				}
			}
		}
		renderText(arr);
	}

	
	public static void getNodeList() {
		String nodetypeId = params.get("nodetypeId");
		JsonArray arr = new JsonArray();
		JsonObject obj;
		obj = new JsonObject();
		obj.addProperty("id", "");
		obj.addProperty("name", "未选择");
		arr.add(obj);
		if (nodetypeId != null && !nodetypeId.equals("")){
			TNodeType nodeType = TNodeType.findById(Long.parseLong(nodetypeId));
			Set<TNode> list = nodeType.tNodes;
			for (TNode node : list) {
				obj = new JsonObject();
				obj.addProperty("id", node.id);
				obj.addProperty("name", node.T_NODE_NAME);
				arr.add(obj);
			}
		}
		renderText(arr);
	}	
	
	public static void getScriptList() {
		String jobId = params.get("jobId");
		JsonArray arr = new JsonArray();
		JsonObject obj;
		obj = new JsonObject();
		obj.addProperty("id", "");
		obj.addProperty("name", "未选择");
		arr.add(obj);
		if (jobId != null && !jobId.equals("")){
			TJob tjob = TJob.findById(Long.parseLong(jobId));
			List<TScript> list = tjob.SCRIPT;
			for (TScript script : list) {
				obj = new JsonObject();
				obj.addProperty("id", script.id);
				obj.addProperty("name", script.T_SCRIPT_NAME);
				arr.add(obj);
			}
		}
		renderText(arr);
	}

	public static void saveReportSet() {
		ReportD report;
		if (params.get("sid") != null && !params.get("sid").equals("")) {
			report = ReportD.findById(Long.parseLong(params.get("sid")));
		} else {
			report = new ReportD();
		}
		report.T_Report_VAR = params.get("varId");
		TJob job = TJob.findById(Long.parseLong(params.get("jobId")));
		report.T_Report_JOB = job.T_JOB_NAME;
		TNodeType nodeType = TNodeType.findById(Long.parseLong(params.get("nodetypeId")));
		report.T_Report_NODETYPE = nodeType.T_NODE_TYPE_NAME;
		TNode node = TNode.findById(Long.parseLong(params.get("nodeId")));
		report.T_Report_NODE = node.T_NODE_NAME;
		TScript script = TScript.findById(Long.parseLong(params.get("scriptId")));
		report.T_Report_SCRIPT = script.T_SCRIPT_NAME;
		report.T_Report_STARTTIME = params.get("startTime");
		report.T_Report_ENDTIME = params.get("endTime");
		report.save();
		index();
	}

	public static void getReportList() {

		StringBuilder sql = new StringBuilder();
		if (params.get("T_Report_VAR") != null && !(params.get("T_Report_VAR").equals(""))) {
			sql.append("T_Report_VAR like '%" + params.get("T_Report_VAR") + "%'");
		}
		if (params.get("T_Report_JOB") != null && !(params.get("T_Report_JOB").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_Report_JOB like '%" + params.get("T_Report_JOB") + "%' ");
		}
		if (params.get("T_Report_NODETYPE") != null && !(params.get("T_Report_NODETYPE").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_Report_NODETYPE like '%" + params.get("T_Report_NODETYPE") + "%' ");
		}
		List<ReportD> list = ReportD.find(sql.toString()).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		if (list != null && list.size() > 0 && user != null) {
			for (ReportD report : list) {
				obj = new JsonObject();
				obj.addProperty("T_Report_VAR","<a href='#' onclick='return showReportDetail(" + report.id + ")' >" + report.T_Report_VAR + "</a>");
				obj.addProperty("T_Report_JOB", report.T_Report_JOB);
				obj.addProperty("T_Report_NODETYPE", report.T_Report_NODETYPE);
				obj.addProperty("T_Report_NODE", report.T_Report_NODE);
				obj.addProperty("T_Report_SCRIPT", report.T_Report_SCRIPT);
				obj.addProperty("T_Report_STARTTIME", report.T_Report_STARTTIME);
				obj.addProperty("T_Report_ENDTIME", report.T_Report_ENDTIME);
				obj.addProperty("DELETE", "<button id='" + report.id + "del' class='btn btn-danger btn-xs' data-toggle='modal' data-target='#modal-sizes-1' onclick='deleteinfo(" + report.id + ")'>删除</button>");
				arr.add(obj);
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	public static void editReport(String id) {
		ReportD report = ReportD.find("byId", Long.parseLong(id)).first();
		JsonArray arr = new JsonArray();
		JsonObject obj = new JsonObject();
		obj.addProperty("id", report.id);
		TJob job = TJob.find("byT_JOB_NAME",report.T_Report_JOB).first();
		obj.addProperty("T_Report_JOB", job.id);
		TNode node = TNode.find("byT_NODE_NAME",report.T_Report_NODE).first();
		obj.addProperty("T_Report_NODE",node.id);
		TNodeType nodeType = TNodeType.find("byT_NODE_TYPE_NAME",report.T_Report_NODETYPE).first();
		obj.addProperty("T_Report_NODETYPE", nodeType.id);
		TScript script = TScript.find("byT_SCRIPT_NAME",report.T_Report_SCRIPT).first();
		obj.addProperty("T_Report_SCRIPT", script.id);
		obj.addProperty("T_Report_VAR", report.T_Report_VAR);
		obj.addProperty("T_Report_STARTTIME", report.T_Report_STARTTIME);
		obj.addProperty("T_Report_ENDTIME", report.T_Report_ENDTIME);
		arr.add(obj);
		renderText(arr);
	}
	
	//验证替换变量方法
	public static void verifyReport(){
		String reportVar = params.get("reportVar");
//		System.out.println(reportVar);
		List<ReportD> report = ReportD.find("select t from ReportD t where t.T_Report_VAR='"+reportVar+"'").fetch();
		if(report.size() != 0){
			renderText("ERROR");
		}else{
//			System.out.println("OK");
			renderText("OK");
		}
	}

	//变量名称唯一
	public static void getName() {
		List<ReportD> reportDs = ReportD.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (ReportD reportD : reportDs) {
			obj = new JsonObject();
			obj.addProperty("T_Report_VAR", reportD.T_Report_VAR);
			arr.add(obj);
		}
		renderText(arr);
	}
}
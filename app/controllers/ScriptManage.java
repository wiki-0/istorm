package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.TAlarmLV;
import models.TJobScript;
import models.TLicense;
import models.TRelevancyUserGroup;
import models.TScript;
import models.TScriptGroup;
import models.TUser;
import models.TUserGroup;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Files;
import play.mvc.Before;

@CRUD.For(TScript.class)
public class ScriptManage extends CRUD {
	public static void index() {
		render();
	}

	public static void getUserInGroup() {
		String username = session.get("username");
		TUser tUser = TUser.find("T_USER_NAME", username).first();
		JsonObject obj = new JsonObject();

		if (tUser != null) {
			String id = tUser.id.toString();
			List<TRelevancyUserGroup> tRelevancyUserGroups = TRelevancyUserGroup.find("USER_ID", id).fetch();
			if (tRelevancyUserGroups.isEmpty()) {
				obj.addProperty("status", "false");
			} else {
				obj.addProperty("status", "true");
			}
		}
		renderText(obj);
	}

	public static void saveScriptCommand(TScript tScript) {
		String[] colors = { "红色", "橙色", "黄色", "蓝色" };

		tScript.T_SCRIPT_DATE = new Date();

		tScript.save();

		for (int i = 1; i <= 4; i++) {
			TAlarmLV tAlarmLV = new TAlarmLV();
			if (params.get("T_ALARM_LEVEL" + i) != null && params.get("T_ALARM_THRESHOLD" + i) != null) {
				tAlarmLV.tScript = tScript;
				tAlarmLV.T_ALARM_LEVEL = params.get("T_ALARM_LEVEL" + i);
				tAlarmLV.T_ALARM_THRESHOLD = params.get("T_ALARM_THRESHOLD" + i);
				tAlarmLV.T_ALARM_RELATION = params.get("T_ALARM_RELATION" + i);
				tAlarmLV.T_ALARM_COLOR = colors[i - 1];
				tAlarmLV.save();
			}

		}

		index();

	}

	public static void saveFile(File file, TScript tScript) {
		String fileName = file.getName();
		File storeFile = new File("./scripts/" + fileName);
		Files.copy(file, storeFile);

		String[] fileNames = fileName.split("\\.");

		BufferedReader br = null;
		String command = null;
		StringBuffer buffer = new StringBuffer();
		try {

			InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("./scripts/" + fileName)), "utf-8");
			br = new BufferedReader(isr);
			int s;
			while ((s = br.read()) != -1) {
				buffer.append((char) s);
			}
			command = buffer.toString();
			br.close();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		int dot = fileName.lastIndexOf('.');
		if ((dot > -1) && (dot < (fileName.length()))) {
			tScript.T_SCRIPT_FILENAME = fileName.substring(0, dot);
			tScript.T_SCRIPT_NAME = fileName.substring(0, dot);
		}

		tScript.T_SCRIPT_COMMAND = command;
		tScript.T_SCRIPT_TYPE = fileNames[fileNames.length - 1];
		tScript.T_SCRIPT_USER = session.get("displayName");
		tScript.T_SCRIPT_DATE = new Date();
		tScript.save();
		index();

	}

	public static void getScriptGroup() {
		List<TScriptGroup> tScriptGroups = TScriptGroup.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TScriptGroup tScriptGroup : tScriptGroups) {
			obj = new JsonObject();
			obj.addProperty("T_SCRIPT_GROUP_ID", tScriptGroup.id);
			obj.addProperty("T_SCRIPT_GROUP_NAME", tScriptGroup.T_SCRIPT_GROUP_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getInJob() {
		String id = params.get("id");
		JsonObject obj = new JsonObject();
		List<TJobScript> tJobScripts = TJobScript.find("T_SCRIPT_ID", Long.parseLong(id)).fetch();
		if (!tJobScripts.isEmpty()) {
			obj.addProperty("id", "true");
		} else {
			obj.addProperty("id", "false");
		}
		renderText(obj);
	}

	public static void getScript() {
		List<TScript> tScripts = TScript.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TScript tScript : tScripts) {
			obj = new JsonObject();
			obj.addProperty("T_SCRIPT_NAME", tScript.T_SCRIPT_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getScriptLength() {
		List<TScript> tScripts = TScript.findAll();
		JsonObject obj = new JsonObject();
		obj.addProperty("ScriptLength", tScripts.size());
		if (session.get("username") != null) {
			TLicense tLicense = TLicense.find("T_LICENSE_USER_NAME", session.get("username")).first();
			if (tLicense != null) {
				obj.addProperty("LicenseStatus", tLicense.T_LICENSE_STATUS);
			} else {
				obj.addProperty("LicenseStatus", "0");
			}
		}
		renderText(obj);
	}

	public static void getUser() {
		List<TUser> tUsers = TUser.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TUser tUser : tUsers) {
			obj = new JsonObject();
			obj.addProperty("T_USER_ID", tUser.id);
			obj.addProperty("T_USER_DISPLAY_NAME", tUser.T_USER_DISPLAY_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getUserGroup() {
		List<TUserGroup> tUserGroups = TUserGroup.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TUserGroup tUserGroup : tUserGroups) {
			obj = new JsonObject();
			obj.addProperty("T_GROUPT_ID", tUserGroup.id);
			obj.addProperty("T_GROUP_NAME", tUserGroup.T_GROUP_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getT_ALARM_RELATION() {
		String id = params.get("id");
		List<TAlarmLV> tAlarmLVs = TAlarmLV.find("T_SCRIPT_ID", id).fetch();
		TScript tScript = TScript.findById(Long.parseLong(id));
		JsonObject obj = new JsonObject();
		obj.addProperty("T_ALARM_THRESHOLD_TYPE", tScript.T_ALARM_THRESHOLD_TYPE);
		for (TAlarmLV tAlarmLV : tAlarmLVs) {
			if (tAlarmLV.T_ALARM_LEVEL.equals("严重")) {
				obj.addProperty("T_ALARM_RELATION1", tAlarmLV.T_ALARM_RELATION);
			}
			if (tAlarmLV.T_ALARM_LEVEL.equals("主要")) {
				obj.addProperty("T_ALARM_RELATION2", tAlarmLV.T_ALARM_RELATION);
			}
			if (tAlarmLV.T_ALARM_LEVEL.equals("次要")) {
				obj.addProperty("T_ALARM_RELATION3", tAlarmLV.T_ALARM_RELATION);
			}
			if (tAlarmLV.T_ALARM_LEVEL.equals("提示")) {
				obj.addProperty("T_ALARM_RELATION4", tAlarmLV.T_ALARM_RELATION);
			}
		}
		 renderText(obj);
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		List<TScriptGroup> tScriptGroups = TScriptGroup.findAll();
		List<TUserGroup> tUserGroups = TUserGroup.findAll();

		try {
			render(type, object, tScriptGroups, tUserGroups);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
	}

	@Before
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		StringBuilder sql = new StringBuilder();
		if (params.get("T_SCRIPT_NAME") != null && !(params.get("T_SCRIPT_NAME").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_SCRIPT_NAME like '%" + params.get("T_SCRIPT_NAME") + "%'");
		}
		if (params.get("T_SCRIPT_GROUP_ID") != null && !(params.get("T_SCRIPT_GROUP_ID").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_SCRIPT_GROUP_ID like '%" + params.get("T_SCRIPT_GROUP_ID") + "%'");
		}
		if (params.get("T_SCRIPT_USER") != null && !(params.get("T_SCRIPT_USER").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_SCRIPT_USER like '%" + params.get("T_SCRIPT_USER") + "%'");
		}
		if (params.get("T_SCRIPT_TYPE") != null && !(params.get("T_SCRIPT_TYPE").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_SCRIPT_TYPE like '%" + params.get("T_SCRIPT_TYPE") + "%'");
		}

		if (sql != null && !(sql.toString().equals(""))) {
			request.args.put("where", sql.toString());
		}
	}

	public static void getFile() throws Exception {
		Long id = Long.parseLong(params.get("T_SCRIPT_ID"));
		TScript tScript = TScript.findById(id);
		if (tScript.T_SCRIPT_TYPE.equals("command")) {
			tScript.T_SCRIPT_TYPE = "txt";
		}
		byte[] fileBytes = tScript.T_SCRIPT_COMMAND.getBytes();
		response.setHeader("Content-Disposition", "attachment;filename=".concat(String.valueOf(URLEncoder.encode(tScript.T_SCRIPT_FILENAME + "." + tScript.T_SCRIPT_TYPE, "UTF-8"))));
		OutputStream toClient = response.out;
		response.contentType = "application/octet-stream";
		toClient.write(fileBytes);
		toClient.flush();
		toClient.close();

	}

	public static void saveUpdate(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		Binder.bindBean(params.getRootParamNode(), "object", object);
		validation.valid(object);
		if (validation.hasErrors()) {
			renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
			try {
				render(request.controller.replace(".", "/") + "/show.html", type, object);
			} catch (TemplateNotFoundException e) {
				render("CRUD/show.html", type, object);
			}
		}
		object._save();

		TScript tScript = TScript.findById(Long.parseLong(id));
		tScript.T_SCRIPT_NAME = params.get("T_SCRIPT_NAME");
		tScript.T_SCRIPT_FILENAME = params.get("T_SCRIPT_FILENAME");
		tScript.T_SCRIPT_COMMAND = params.get("T_SCRIPT_COMMAND");
		tScript.T_SCRIPT_TYPE = params.get("T_SCRIPT_TYPE");
		if (params.get("T_SCRIPT_GROUP_ID") == null) {
			tScript.T_SCRIPT_GROUP_ID = 0;
		} else {
			tScript.T_SCRIPT_GROUP_ID = Integer.parseInt(params.get("T_SCRIPT_GROUP_ID"));
		}
		if (params.get("T_USER_GROUP_ID") == null) {
			tScript.T_USER_GROUP_ID = 0;
		} else {
			tScript.T_USER_GROUP_ID = Integer.parseInt(params.get("T_USER_GROUP_ID"));
		}
		tScript.T_ALARM_THRESHOLD_TYPE = params.get("T_ALARM_THRESHOLD_TYPE");
		tScript.merge();

		List<TAlarmLV> tAlarmLVs = TAlarmLV.find("T_SCRIPT_ID", id).fetch();
		for (TAlarmLV tAlarmLV : tAlarmLVs) {
			tAlarmLV.delete();
		}
		String[] colors = { "红色", "橙色", "黄色", "蓝色" };
		String[] LVS = { "严重", "主要", "次要", "提示" };
		for (int i = 1; i <= 4; i++) {
			TAlarmLV tAlarmLV = new TAlarmLV();
			if (!params.get("T_ALARM_THRESHOLD" + i).equals("")) {
				tAlarmLV.tScript = tScript;
				tAlarmLV.T_ALARM_LEVEL = LVS[i - 1];
				tAlarmLV.T_ALARM_THRESHOLD = params.get("T_ALARM_THRESHOLD" + i);
				tAlarmLV.T_ALARM_RELATION = params.get("T_ALARM_RELATION" + i);
				tAlarmLV.T_ALARM_COLOR = colors[i - 1];
				tAlarmLV.save();
			}
		}

		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("_save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}
}

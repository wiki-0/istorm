package controllers;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.TScript;
import models.TScriptGroup;
import play.db.Model;
import play.mvc.Before;

@CRUD.For(TScriptGroup.class)
public class ScriptGroup extends CRUD {
	public static void index() {
		render();
	}

	public static void saveScriptGroup(TScriptGroup tScriptGroup) {
		tScriptGroup.save();
		index();
	}

	public static void getScriptGroup() {
		List<TScriptGroup> tScriptGroups = TScriptGroup.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TScriptGroup tScriptGroup : tScriptGroups) {
			obj = new JsonObject();
			obj.addProperty("T_SCRIPT_GROUP_NAME", tScriptGroup.T_SCRIPT_GROUP_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void delete(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		try {
			List<TScript> tScripts = TScript.find("T_SCRIPT_GROUP_ID",Integer.parseInt(id)).fetch();
			if (!tScripts.isEmpty()) {
				for(TScript tScript : tScripts)
				{
					tScript.T_SCRIPT_GROUP_ID=0;
					tScript.save();
				}
			}
			TScriptGroup tScriptGroup = TScriptGroup.findById(Long.parseLong(id));
			tScriptGroup.delete();
		} catch (Exception e) {
			flash.error(play.i18n.Messages.get("crud.delete.error", type.modelName));
			redirect(request.controller + ".show", object._key());
		}
		flash.success(play.i18n.Messages.get("crud.deleted", type.modelName));
		redirect(request.controller + ".list");
	}

	@Before
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		StringBuilder sql = new StringBuilder();
		if (params.get("T_SCRIPT_GROUP_NAME") != null && !(params.get("T_SCRIPT_GROUP_NAME").equals(""))) {
			sql.append("T_SCRIPT_GROUP_NAME like '%" + params.get("T_SCRIPT_GROUP_NAME") + "%'");
		}

		if (sql != null && !(sql.toString().equals(""))) {
			request.args.put("where", sql.toString());
		}
	}
}

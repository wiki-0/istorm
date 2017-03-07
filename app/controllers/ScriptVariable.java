package controllers;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.TScriptVariable;
import play.mvc.Before;

@CRUD.For(TScriptVariable.class)
public class ScriptVariable extends CRUD {
	public static void index() {
		render();
	}

	public static void saveScriptVariable(TScriptVariable tScriptVariable) {
		tScriptVariable.save();
		index();
	}
	
	public static void getVariable() {
		List<TScriptVariable> tScriptVariables = TScriptVariable.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TScriptVariable tScriptVariable : tScriptVariables) {
			obj = new JsonObject();
			obj.addProperty("T_SCRIPT_VARIABLE_NAME", tScriptVariable.T_SCRIPT_VARIABLE_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}
	
	@Before
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		StringBuilder sql = new StringBuilder();
		if (params.get("T_SCRIPT_VARIABLE_NAME") != null && !(params.get("T_SCRIPT_VARIABLE_NAME").equals(""))) {
			sql.append("T_SCRIPT_VARIABLE_NAME like '%" + params.get("T_SCRIPT_VARIABLE_NAME") + "%'");
		}

		if (sql != null && !(sql.toString().equals(""))) {
			request.args.put("where", sql.toString());
		}
	}

}

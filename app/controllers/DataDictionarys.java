package controllers;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.DataDictionary;
import models.TUser;
import play.db.jpa.JPABase;
import play.mvc.Controller;
import util.DataTableSource;

public class DataDictionarys extends Controller{
	public static void index(){
		render();
	}
	public static void showDD(){
		JsonArray array = new JsonArray();
		JsonObject obj;
		List<DataDictionary> list = DataDictionary.find("byT_DD_NAMEIsNotNull").fetch();
		if(list!=null&&list.size()>0){
			for(DataDictionary dd : list){
				obj = new JsonObject();
				obj.addProperty("name","<a href='#' onclick='return editDD(" + dd.id + ")' >"
				+ dd.T_DD_NAME + "</a>");
				if(dd.T_DD_TYPE.equals("1")){
					obj.addProperty("type", "下拉框");
				}
				if(dd.T_DD_TYPE.equals("2")){
					obj.addProperty("type", "单选框");
				}
				if(dd.T_DD_TYPE.equals("3")){
					obj.addProperty("type", "多选框");
				}
				obj.addProperty("edit", "<button class='btn btn-w-m btn-primary btn-xs' onclick='return showValues(" + dd.id + ")' >"
				+"查看</button></br>"+
						"<input id='editId' type='hidden' value='"+dd.id+"' />");
				if(dd.id==1 || dd.id==2 || dd.id==3 || dd.id==4)
					obj.addProperty("delete", "无");
				else{
					obj.addProperty("delete", "<button class='btn btn-danger btn-xs' type='button' onclick='deleteDD("
							+ dd.id + ")' >" + "删除</button>");
				}
				array.add(obj);
			}
		}
		renderJSON(new DataTableSource(request, array));
	}
	public static void saveDD(DataDictionary tDD){
		String id = params.get("ddId");
		System.out.println(id);
		if(id==null || id.equals("")){
			tDD.T_DD_TYPE = params.get("select_type");
			tDD.save();
		}else{
			DataDictionary olderDD = DataDictionary.findById(Long.parseLong(id));
			olderDD.T_DD_TYPE = params.get("select_type");
			olderDD.T_DD_NAME = tDD.T_DD_NAME;
			olderDD.save();
		}
		index();
	}
	public static void editDD(){
		DataDictionary dd = DataDictionary.findById(Long.parseLong(params.get("id")));
		JsonArray array = new JsonArray();
		JsonObject object = new JsonObject();
		object.addProperty("id", dd.id);
		object.addProperty("name", dd.T_DD_NAME);
		object.addProperty("type", dd.T_DD_TYPE);
		array.add(object);
		renderText(array);
	}
	public static void deleteDD(){
		DataDictionary dd = DataDictionary.findById(Long.parseLong(params.get("id")));
		dd.delete();
		List<DataDictionary> list = DataDictionary.find("byT_DD_PARENTID",Long.parseLong(params.get("id"))).fetch();
		for(DataDictionary tDataDictionary : list){
			tDataDictionary.delete();
		}
		renderText("OK");
	}
	/*********************************************************************/
	public static void showValues(){
		String id = params.get("id");
		StringBuffer json = new StringBuffer("[");
		String data = null;
		List<DataDictionary> list = DataDictionary.find("byT_DD_PARENTID",Long.parseLong(id)).fetch();
		if(list!=null&&list.size()>0){
			for(DataDictionary dd : list){
				json.append("{id:" + dd.id + ",");
				if(dd.USER==null){
					json.append("name:\"" + dd.T_DD_VALUE + "\",");
				}else{
					json.append("name:\"" + dd.T_DD_VALUE +"_负责人："+dd.USER.T_USER_NAME+ "\",");
				}
				json.append("icon:\" /new_public/img/cfolder.png \",");
				data = json.substring(0, json.lastIndexOf(",")) + "},";
				json = new StringBuffer(data);
			}
			data = json.substring(0, json.length() - 1)+"]";
		}else{
			data = "[]";
		}
//		System.out.println(data);
		renderText(data);
	}
	public static void saveVal(){
		DataDictionary dd = new DataDictionary();
		dd.T_DD_PARENTID = Long.parseLong(params.get("parent_id"));
		dd.T_DD_VALUE = params.get("tDD_val");
		if(params.get("parent_id").equals("1")){
			dd.USER = TUser.findById(Long.parseLong(params.get("user")));
		}
		dd.save();
		renderText("OK");
	}
	public static void deleteVal(){
		DataDictionary dd =DataDictionary.findById(Long.parseLong(params.get("id")));
		dd.delete();
		renderText("OK");
	}
	public static void saveUser(){
		DataDictionary dd = DataDictionary.findById(Long.parseLong(params.get("id")));
		dd.USER =(TUser) TUser.findById(Long.parseLong(params.get("userId")));
		dd.save();
		renderText("OK");
	}



}

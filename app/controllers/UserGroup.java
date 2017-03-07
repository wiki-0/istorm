package controllers;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.CRUD.ObjectType;
import models.TJob;
import models.TJobScript;
import models.TMenu;
import models.TNodeType;
import models.TRelevancyUserGroup;
import models.TScript;
import models.TScriptGroup;
import models.TUser;
import models.TUserGroup;
import play.data.binding.Binder;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Controller;
import util.DataTableSource;
@CRUD.For(TUserGroup.class)
public class UserGroup extends Controller {
	public static void index(){
		render();
	}
	public static void deleteById(){
		String GroupId = params.get("ids");
		TUserGroup tUserGroup = TUserGroup.findById(Long.parseLong(GroupId));
		if (tUserGroup != null) {
			tUserGroup.delete();
		}
		renderText("OK");
	}
	/**
	 * 添加用户到用户组
	 */
	public static void addUser(){
		String GroupId = params.get("g_id");
		String userids=params.get("userids");
		
		if(userids!=null&&!userids.equals("")&&GroupId!=null){
			String[] uids= userids.split(";");
			if (uids!=null&&uids.length>0) {
				for (String string : uids) {
					TRelevancyUserGroup tRelevancyUserGroup=TRelevancyUserGroup.find("from TRelevancyUserGroup t where USER_ID='"+string+"' and USER_GROUP_ID='"+GroupId+"'").first();
						if (tRelevancyUserGroup==null) {
							tRelevancyUserGroup=new TRelevancyUserGroup();
							tRelevancyUserGroup.USER_GROUP_ID=GroupId;
							tRelevancyUserGroup.USER_ID=string;
							tRelevancyUserGroup.save();
						}
					}
				}
			}
		
		renderText("OK");
	}
	public static void getTreeData() {
		List<TUserGroup> list = TUserGroup.findAll();
		StringBuffer json = new StringBuffer("[");
		String data="";
		for (int i = 0; i < list.size(); i++) {  
            json.append("{id:"+list.get(i).id+",");  
            json.append("name:\""+list.get(i).T_GROUP_NAME+"\",");  
			json.append("icon:\" /new_public/img/cfolder.png \",");
            data=json.substring(0,json.lastIndexOf(","))+"},";  
            json=new StringBuffer(data);  
        }  
		 data=json.substring(0, json.length()-1)+"]";  
		 if (data.equals("]")) {
			data = null;
		     renderText(data);
		}else {
		     renderText(data);
		}
	    
	}
	
	public static void saveUserGroup(TUserGroup tUserGroup) {
		tUserGroup.save();
	}
	

	//添加菜单
	public static void addUserGroup(){
		String name = params.get("t_group_name");
		TUserGroup tUserGroup = new TUserGroup();
		tUserGroup.T_GROUP_NAME = name ; 
		
		tUserGroup.save();
		index();
	}
}

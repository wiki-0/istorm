package controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.CRUD.ObjectType;
import models.TJob;
import models.TNode;
import models.TNodeType;
import models.TResult;
import models.TUser;
import play.db.Model;
import play.db.jpa.JPABase;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.Controller;
import util.DataTableSource;
import util.quartz.QuartzManager;

/**
 * 设备分层
 *
 * @author twx
 *
 */
@CRUD.For(TNodeType.class)
public class NodeType extends CRUD {
	public static Set<Long> ids = new HashSet<>();

	public static void index() {
		render();
	}

	/**
	 * 设备分层--左侧树
	 */
	public static void getTreeData() {
		List<TNodeType> list = TNodeType.findAll();
		StringBuffer json = new StringBuffer("[");
		String data = "";
		for (int i = 0; i < list.size(); i++) {
			json.append("{id:" + list.get(i).id + ",");
			json.append("pId:" + list.get(i).pId + ",");
			json.append("name:\"" + list.get(i).T_NODE_TYPE_NAME + "\",");
			json.append("open: true,");
			if(list.get(i).id==1){
				json.append("icon:\" /new_public/img/folder.png \",");
			}else{
				json.append("icon:\" /new_public/img/cfolder.png \",");
			}
			if(list.get(i).isExtend){
				json.append("icon:\" /new_public/img/up.png \",");
			}
			data = json.substring(0, json.lastIndexOf(",")) + "},";
			json = new StringBuffer(data);
		}
		data = json.substring(0, json.length() - 1) + "]";
		// System.out.println(data);
		renderText(data);
	}
	
	public static void searchNode(){
		JsonArray arr = new JsonArray();
		JsonObject obj;
		
		String name = params.get("name");
		List<TNode> list = TNode.find("byT_NODE_NAMELike", "%"+name+"%").fetch();
		if(list!=null&&list.size()>0){
			for(TNode node : list){
				Set<TNodeType> tNodeTypes = node.tNodeTypes;
				System.out.println("size2:"+tNodeTypes.size());
				for(TNodeType nodeType : tNodeTypes){
					obj = new JsonObject();
					obj.addProperty("name",nodeType.T_NODE_TYPE_NAME);
					arr.add(obj);
				}
			}
		}
		renderText(arr);
	}

	/**
	 * 设备分层——新增分层——确认按钮触发
	 */
	public static void addNodeType(TNodeType tNodeType) {
		// String name = params.get("t_node_type_name");
		String pId = params.get("parentId");// 上层的id
		String user = params.get("t_node_type_user");

		boolean _isExtend = tNodeType.isExtend;

		TNodeType newNodeType = new TNodeType();
		newNodeType.T_NODE_TYPE_NAME = tNodeType.T_NODE_TYPE_NAME;
		newNodeType.pId = Long.parseLong(pId);

		TUser tUser = TUser.findById(Long.parseLong(user));
		newNodeType.T_NODE_TYPE_USER = tUser;
		newNodeType.tBusinessType = tNodeType.tBusinessType;

		//如果继承任务
		if( _isExtend){
			newNodeType.isExtend =true;
			TNodeType pNodeType = TNodeType.findById(Long.parseLong(pId));
			newNodeType.tJobs.addAll(pNodeType.tJobs);
		}
		else
			newNodeType.isExtend =false;
		newNodeType.save();

		index();
	}

	/**
	 * 修改设备分层---保存按钮触发
	 */
	public static void editNodeType() {
		String edit_id = params.get("edit_id");
		String edit_name = params.get("edit_name");
		String edit_user = params.get("edit_user");
		String edit_type = params.get("edit_type");
		String room_position = params.get("room_position");
		String isExtend = params.get("isExtend");

		TNodeType nodeType = TNodeType.findById(Long.parseLong(edit_id));
		nodeType.T_NODE_TYPE_NAME = edit_name;
		nodeType.T_NODE_TYPE_USER = (TUser) TUser.findById(Long.parseLong(edit_user));
		nodeType.tBusinessType =edit_type;
		nodeType.ROOM_POSITION = room_position ;

		if(isExtend.equals("0")){
			nodeType.isExtend = false;
		}
		else {
			nodeType.isExtend = true;
		}
		nodeType.save();
		index();
	}

	/**
	 * 删除--设备分层
	 */
	public static void removeNodeType() {
		String ids = params.get("ids");
//		System.out.println("删除设备分层 ids:" + ids);
		String[] idArray = ids.split(",");
		for(int i=0;i<idArray.length;i++){
			List<TResult> reLists = TResult.find("TNODETYPE.id", Long.parseLong(idArray[i])).fetch();
			if(reLists!=null&&reLists.size()>0){
				for(TResult tResult : reLists){
					tResult.delete();
				}
			}
			TNodeType node = TNodeType.findById(Long.parseLong(idArray[i]));
			if(node.tNodes.size()>0){
				Set<TJob> tJobs = node.tJobs;
				for(TJob job : tJobs){
					String jobName = idArray[i] + "_" + job.id;
					QuartzManager.removeJob(jobName);
				} 
				
			}
			node.delete();
		}
		renderText("OK");
	}

	/**
	 * 编辑按钮——打开表单--触发此方法： 用于填充表单数据
	 */
	public static void getNodeTypeById() {
		String id = params.get("id");
		if(!id.equals("1")){
			TNodeType node = TNodeType.findById(Long.parseLong(id));
			JsonArray array = new JsonArray();
			JsonObject json = new JsonObject();
			
			json.addProperty("id", node.id);
			json.addProperty("name", node.T_NODE_TYPE_NAME);
			json.addProperty("room", node.ROOM_POSITION);
			json.addProperty("type", node.tBusinessType);
			json.addProperty("isExtend", node.isExtend);
			array.add(json);
			
			TUser user = node.T_NODE_TYPE_USER;
			JsonObject obj = new JsonObject();
			obj.addProperty("id", user.id);
			obj.addProperty("name",user.T_USER_NAME);
			array.add(obj);
			renderText(array);
		}else{
			renderText("null");
		}
	}

	/**
	 * 获取所有用户信息
	 */
	public static void getAllUserJSON() {
		List<TUser> list = TUser.all().fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TUser user : list) {
			obj = new JsonObject();
			obj.addProperty("id", user.id);
			obj.addProperty("name", user.T_USER_DISPLAY_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	/**
	 * 判断该层设备上是否有设备
	 */
	public static void isHaveNodes(){
		String treeNodeId = params.get("treeNodeId");
		TNodeType nodeType = TNodeType.findById(Long.parseLong(treeNodeId));
		if(nodeType.tNodes.size() == 0){
			renderText(false);
		}else{
			renderText(true);
		}
	}

	/**
	 * 获取当前分层下 具有继承属性的子层的id
	 * 若子层的子层也具有继承属性  也会添加它的id<br>
	 * 使用方法：<br>
	 * 	 NodeType tool = new NodeType();
     *   tool.ids.clear();
     *   tool.getChildrenExtend(Long.parseLong(treeNodeId));
     *    Set<Long> ids = tool.ids;
	 * @param id
	 * @return
	 */
	public static String getChildrenExtend(long id) {
		List<TNodeType> list = TNodeType.find("byPId", id).fetch();
		if (list != null) {
			for (TNodeType nodeType : list) {
				if (nodeType.isExtend) {
					ids.add(nodeType.id);
					getChildrenExtend(nodeType.id);
				}
			}
		}
		return "";
	}


	private static Set<Long> getNotLastFloorIds(Long id){
		List<TNodeType> list = TNodeType.find("byPId", id).fetch();
		if(list!=null&&list.size()>0){
			ids.add(id);
			for(TNodeType cNodeType : list){
				getNotLastFloorIds(cNodeType.id);
			}
		}
		return ids;
	}

	/**
	 *  遍历整棵树
	 * -----获取层次不是父节点的 id的集合
	 * @return
	 */
	public static List<Long> getLastFloorIds(){
		getNotLastFloorIds((long)1);
//		System.out.println("111111");
		List<Long> ids1 = new ArrayList<>();
		List<TNodeType> all = TNodeType.findAll();
		for(TNodeType node : all){
			if(!ids.contains(node.id)){
				ids1.add(node.id);
			}
		}
		return ids1;
	}
}

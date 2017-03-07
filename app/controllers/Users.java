package controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.persistence.criteria.From;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import controllers.CRUD.ObjectType;
import models.DataDictionary;
import models.TNodeType;
import models.TRelevancyUserGroup;
import models.TUser;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.mvc.Before;
import play.mvc.results.Result;
import util.DataTableSource;
import util.EncrypAES;

@CRUD.For(TUser.class)
public class Users extends CRUD {

	// 页面跳转
	public static void index() {
		String departmentName = "[";
		DataDictionary data = DataDictionary.find("select t from DataDictionary t where t.T_DD_NAME='部门'").first();
		Long departmentId = data.id;
		List<DataDictionary> list = DataDictionary.find("select t from DataDictionary t where t.T_DD_PARENTID='"+departmentId+"'").fetch();
		for(int i=0;i<list.size();i++){
			if(i == list.size()-1){
				departmentName += "\'"+list.get(i).T_DD_VALUE+"\']";
			}else{
				departmentName += "\'"+list.get(i).T_DD_VALUE+"\',";
			}
		}
		
		render(departmentName);
	}

	@Before
	// 检索
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		String selectName = params.get("selectName");
		String selectDisplayName = params.get("selectDisplayName");
		String selectDepartment = params.get("selectDepartment");
		if (selectName != null && !"".equals(selectName) && selectDisplayName != null && !"".equals(selectDisplayName)
				&& selectDepartment != null && !"".equals(selectDepartment)) {
			request.args.put("where", "  T_USER_NAME like '%" + selectName + "%' and  T_USER_DISPLAY_NAME like '%"
					+ selectDisplayName + "%' and T_USER_DEPARTMENT like '%" + selectDepartment + "%'  ");

		} else if (selectDisplayName != null && !"".equals(selectDisplayName) && selectDepartment != null
				&& !"".equals(selectDepartment)) {

			request.args.put("where", " T_USER_DISPLAY_NAME like '%" + selectDisplayName
					+ "%' and T_USER_DEPARTMENT like '%" + selectDepartment + "%'  ");

		} else if (selectName != null && !"".equals(selectName) && selectDepartment != null
				&& !"".equals(selectDepartment)) {

			request.args.put("where", "  T_USER_NAME like '%" + selectName + "%' and T_USER_DEPARTMENT like '%"
					+ selectDepartment + "%'  ");

		} else if (selectName != null && !"".equals(selectName) && selectDisplayName != null
				&& !"".equals(selectDisplayName)) {

			request.args.put("where", "  T_USER_NAME like '%" + selectName + "%' and  T_USER_DISPLAY_NAME like '%"
					+ selectDisplayName + "%' ");

		} else if (selectName != null && !"".equals(selectName)) {

			request.args.put("where", "  T_USER_NAME like '%" + selectName + "%' ");

		} else if (selectDisplayName != null && !"".equals(selectDisplayName)) {

			request.args.put("where", " T_USER_DISPLAY_NAME like '%" + selectDisplayName + "%'  ");

		} else if (selectDepartment != null && !"".equals(selectDepartment)) {

			request.args.put("where", "  T_USER_DEPARTMENT like '%" + selectDepartment + "%'  ");

		}

	}

	/**
	 * @Title: save
	 * @Description: 添加用户
	 * @param @param
	 *            tConfigUser
	 * @return void
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws InvalidKeyException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPaddingException
	 * @throws NoSuchAlgorithmException
	 * @throws @since
	 *             CloudWei v1.0.0
	 */
	public static void saveUser(TUser tUser) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException {
		String[] str = params.getAll("users");
		String T_USER_NAME = params.get("T_USER_NAME");
		String T_USER_TELPHONE = params.get("T_USER_TELPHONE");
		String T_USER_MAIL = params.get("T_USER_MAIL");
		String T_USER_DISPLAY_NAME = params.get("T_USER_DISPLAY_NAME");
		String T_USER_DEPARTMENT = params.get("T_USER_DEPARTMENT");
		System.out.println("T_USER_DEPARTMENT="+T_USER_DEPARTMENT);
//		String T_USER_PERMISSION = params.get("T_USER_PERMISSION");
		String T_USER_PASSWORD = params.get("T_USER_PASSWORD");
		// 加密
		EncrypAES de1 = new EncrypAES();
		// System.out.println(T_USER_PASSWORD);
		String encryptResultStr = de1.parseByte2HexStr(de1.Encrytor(T_USER_PASSWORD));
		// System.out.println("加密后：" + encryptResultStr);
		// //解密
		// byte[] decryptFrom = EncrypAES.parseHexStr2Byte(encryptResultStr);
		// byte[] decontent = de1.Decryptor(decryptFrom);
		// System.out.println("解密后:" + new String(decontent));

		tUser.T_USER_NAME = T_USER_NAME;
		tUser.T_USER_PASSWORD = encryptResultStr;
		tUser.T_USER_TELPHONE = T_USER_TELPHONE;
		tUser.T_USER_MAIL = T_USER_MAIL;
		tUser.T_USER_DISPLAY_NAME = T_USER_DISPLAY_NAME;
		tUser.T_USER_DEPARTMENT = T_USER_DEPARTMENT;
//		tUser.T_USER_PERMISSION = T_USER_PERMISSION;
		if (str != null && str.length > 0) {
			TUser.delete("from TUser t where t.T_USER_ID='" + tUser.id + "' ");
		}
		tUser.save();
		Users.index();
	}

	public static void update(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		String T_USER_NAME = params.get("UserName");
		String T_USER_TELPHONE = params.get("TELPHONE");
		String T_USER_MAIL = params.get("EMAIL");
		String T_USER_DISPLAY_NAME = params.get("DisplayName");
		String T_USER_DEPARTMENT = params.get("Department");
//		String T_USER_PERMISSION = params.get("authorityManage");
		String T_USER_PASSWORD = params.get("Password");

		// 解密
		EncrypAES de1 = new EncrypAES();
		// byte[] decryptFrom = EncrypAES.parseHexStr2Byte(T_USER_PASSWORD);
		// byte[] decontent = de1.Decryptor(decryptFrom);
		// System.out.println("解密后:" + new String(decontent));
		// 加密
		String encryptResultStr = de1.parseByte2HexStr(de1.Encrytor(T_USER_PASSWORD));
		// System.out.println("加密后：" + encryptResultStr);

		TUser tUser = (TUser) object;
		tUser.T_USER_NAME = T_USER_NAME;
		tUser.T_USER_PASSWORD = encryptResultStr;
		tUser.T_USER_TELPHONE = T_USER_TELPHONE;
		tUser.T_USER_MAIL = T_USER_MAIL;
		tUser.T_USER_DISPLAY_NAME = T_USER_DISPLAY_NAME;
		tUser.T_USER_DEPARTMENT = T_USER_DEPARTMENT;
//		tUser.T_USER_PERMISSION = T_USER_PERMISSION;
		tUser.save();
		flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
		if (params.get("save") != null) {
			redirect(request.controller + ".list");
		}
		redirect(request.controller + ".show", object._key());
	}

	public static void getAllUsers() {
		// System.out.println("fdsfsf");
		List<TUser> tUserList = TUser.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TUser tUser : tUserList) {
			obj = new JsonObject();
			obj.addProperty("id", tUser.id);
			obj.addProperty("name", tUser.T_USER_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getGroupList() {
		String id = params.get("id");
		List<TRelevancyUserGroup> list = TRelevancyUserGroup
				.find("from TRelevancyUserGroup t where USER_GROUP_ID='" + id + "'").fetch();

		JsonArray arr = new JsonArray();
		JsonObject obj = null;
		if (list != null && list.size() > 0) {
			for (TRelevancyUserGroup tRelevancyUserGroup : list) {
				TUser tUser = TUser.findById(Long.parseLong(tRelevancyUserGroup.USER_ID));
				if (tUser != null) {
					obj = new JsonObject();
					obj.addProperty("T_USER_NAME", tUser.T_USER_NAME);
					obj.addProperty("T_USER_DISPLAY_NAME", tUser.T_USER_DISPLAY_NAME);
					obj.addProperty("T_USER_DEPARTMENT", tUser.T_USER_DEPARTMENT);
					obj.addProperty("delete",
							"<button class='btn btn-warning btn-circle btn-xs' type='button' onclick='deleteUser("
									+ tUser.id + ")' >" + "<i class='fa fa-times'></i></button>");
					arr.add(obj);
				}
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	// 删除用户组用户
	public static void deleteUserById() {
		String id = params.get("id");
		String gid = params.get("gid");
		TRelevancyUserGroup.delete("USER_ID = " + id + " and USER_GROUP_ID = " + gid);

		renderText("OK");
	}

	public static void getChooseList() {
		List<TUser> users = TUser.findAll();
		String gid = params.get("gid");
		
		JsonArray arr = new JsonArray();
		for (TUser user : users) {
			List<TRelevancyUserGroup> tRelevancyUserGroups = TRelevancyUserGroup
					.find("from  TRelevancyUserGroup t where t.USER_GROUP_ID = " + gid+" and t.USER_ID="+user.id).fetch();
				if( tRelevancyUserGroups.size()==0){
					JsonObject obj = new JsonObject();
					obj.addProperty("T_USER_NAME", user.T_USER_NAME);
					obj.addProperty("T_USER_DISPLAY_NAME", user.T_USER_DISPLAY_NAME);
					obj.addProperty("Choose", "<input name='Choose' type='checkbox' value='" + user.id + "' />");
					arr.add(obj);
				}
			}
		renderJSON(new DataTableSource(request, arr));
	}

	public static void verification() {
		String userName = params.get("userNameverification");
		TUser tUser = TUser.find("from TUser t where t.T_USER_NAME ='"+userName+"'")
				.first();
		if (tUser != null ) {
			renderText("false");
		} else {
			renderText("true");
		}
	}
	public static void verificationUpdate() {
//		String userName = params.get("userNameverification");
//			List<TUser> tUser = TUser.find("from TUser t where t.T_USER_NAME ='"+userName+"'")
//					.fetch();
//			if (tUser.size() != 0 ) {
//				renderText("false");
//			} else {
//				renderText("true");
//			}	
		List<TUser> tUsers = TUser.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TUser tUser : tUsers) {
			obj = new JsonObject();
			obj.addProperty("T_USER_NAME", tUser.T_USER_NAME);
			arr.add(obj);
		}
		renderText(arr);
	}
	// 密码解密展示
	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		try {
			TUser tUser = (TUser) object;
			String password = tUser.T_USER_PASSWORD;
			// System.out.println(password);

			EncrypAES de1 = new EncrypAES();
			String encrypAES = de1.deAES(password);
			tUser.T_USER_PASSWORD = new String(encrypAES);
			// System.out.println(tUser.T_USER_PASSWORD);
			render("Users/show.html", type, tUser);
		} catch (TemplateNotFoundException e) {
			render("Users/show.html", type, object);
		}
	}

	//修改用户时部门下拉框
	public static void examine(){
		List<HashMap<String,String>> departmentList = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = new HashMap<String,String>();
		String departmentName = "";
		DataDictionary data = DataDictionary.find("select t from DataDictionary t where t.T_DD_NAME='部门'").first();
		Long departmentId = data.id;
		List<DataDictionary> list = DataDictionary.find("select t from DataDictionary t where t.T_DD_PARENTID='"+departmentId+"'").fetch();
		for(DataDictionary d : list){
			departmentName = d.T_DD_VALUE;
			map.put("name", departmentName);
			departmentList.add(map);
		}
		
		render(departmentName);
	}
	
	// 权限汉化
//	public static void list(int page, String search, String searchFields, String orderBy, String order) {
//
//		ObjectType type = ObjectType.get(getControllerClass());
//		notFoundIfNull(type);
//		if (page < 1) {
//			page = 1;
//		}
//		List<Model> objects = type.findPage(page, search, searchFields, orderBy, order,
//				(String) request.args.get("where"));
//		for (Model model : objects) {
//			TUser tUser = (TUser) model;
//			String permission = tUser.T_USER_PERMISSION;
//			if (permission.equals("manager")) {
//				permission = "管理员";
//				tUser.T_USER_PERMISSION = permission;
//			}
//			if (permission.equals("operator")) {
//				permission = "操作员";
//				tUser.T_USER_PERMISSION = permission;
//			}
//		}
//		Long count = type.count(search, searchFields, (String) request.args.get("where"));
//		Long totalCount = type.count(null, null, (String) request.args.get("where"));
//		try {
//			render(type, objects, count, totalCount, page, orderBy, order);
//		} catch (TemplateNotFoundException e) {
//			render("CRUD/list.html", type, objects, count, totalCount, page, orderBy, order);
//		}
//	}
}

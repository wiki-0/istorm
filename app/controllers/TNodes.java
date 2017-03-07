package controllers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.*;
import org.apache.commons.net.telnet.TelnetClient;
import play.Play;
import play.db.Model;
import play.exceptions.TemplateNotFoundException;
import play.libs.Files;
import play.mvc.Before;
import util.CommonUtil;
import util.DataTableSource;
import util.patrol.EditJobs;
import util.patrol.PSClient;
import util.patrol.SSHClient;
import util.quartz.QuartzManager;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import org.eclipse.swt.SWT;
//import org.eclipse.swt.layout.GridData;

public class TNodes extends CRUD {
	public static String localPath = Play.applicationPath + "/conf/sh";

	public static void index() {
		render();
	}

	public static void saveTNode(TNode tNode) {
		tNode.save();
		index();
	}

	public static void show(String id) throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		notFoundIfNull(type);
		Model object = type.findById(id);
		notFoundIfNull(object);
		List<TUser> tUsers = TUser.findAll();
		List<DataDictionary> T_NODE_DEPARTMENTS = DataDictionary.find("T_DD_PARENTID", Long.parseLong("1")).fetch();
		List<DataDictionary> T_NODE_VENDORS = DataDictionary.find("T_DD_PARENTID", Long.parseLong("2")).fetch();
		List<DataDictionary> T_NODE_OSS = DataDictionary.find("T_DD_PARENTID", Long.parseLong("3")).fetch();
		List<DataDictionary> T_NODE_SYSTEMS = DataDictionary.find("T_DD_PARENTID", Long.parseLong("4")).fetch();

		try {
			render(type, object, tUsers, T_NODE_DEPARTMENTS, T_NODE_VENDORS, T_NODE_OSS, T_NODE_SYSTEMS);
		} catch (TemplateNotFoundException e) {
			render("CRUD/show.html", type, object);
		}
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

	public static void getT_NODE_DEPARTMENT() {
		List<DataDictionary> dataDictionarys = DataDictionary.find("T_DD_PARENTID", Long.parseLong("1")).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (DataDictionary dataDictionary : dataDictionarys) {
			obj = new JsonObject();
			obj.addProperty("T_NODE_DEPARTMENT", dataDictionary.T_DD_VALUE);
			arr.add(obj);
		}
		renderText(arr);
	}
	public static void getT_RESULT() {
		String id = params.get("nid");
		List<TResult> tResults = TResult.find("T_NODE_ID", Long.parseLong(id)).fetch();
		
		JsonObject obj = new JsonObject();
		if (!tResults.isEmpty()) {
			obj.addProperty("T_NODE_REPORT", "true");
			
		}else {
			obj.addProperty("T_NODE_REPORT", "false");
		}
		
		renderText(obj);
	}

	public static void getT_NODE_SYSTEM() {
		List<DataDictionary> dataDictionarys = DataDictionary.find("T_DD_PARENTID", Long.parseLong("4")).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (DataDictionary dataDictionary : dataDictionarys) {
			obj = new JsonObject();
			obj.addProperty("T_NODE_SYSTEM", dataDictionary.T_DD_VALUE);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getT_NODE_VENDOR() {
		List<DataDictionary> dataDictionarys = DataDictionary.find("T_DD_PARENTID", Long.parseLong("2")).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (DataDictionary dataDictionary : dataDictionarys) {
			obj = new JsonObject();
			obj.addProperty("T_NODE_VENDOR", dataDictionary.T_DD_VALUE);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getT_NODE_OS() {
		List<DataDictionary> dataDictionarys = DataDictionary.find("T_DD_PARENTID", Long.parseLong("3")).fetch();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (DataDictionary dataDictionary : dataDictionarys) {
			obj = new JsonObject();
			obj.addProperty("T_NODE_OS", dataDictionary.T_DD_VALUE);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void saveFile(File file, TScript tScript) {
		String fileName = file.getName();
		File storeFile = new File("./tnodefile/" + fileName);
		Files.copy(file, storeFile);
		BufferedReader bReader = null;
		String line = null;
		try {
			InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("./tnodefile/" + fileName)),
					"GBK");
			bReader = new BufferedReader(isr);

			String head = "设备名称,IP地址,登入账号,登入密码,登入方式,业务系统,设备负责人,操作系统,厂商,设备类型,上传脚本保存目录,上传脚本方式,机房,序列号,型号,部门（空值请输入null，请勿删除这一行）";
			if (head.equals(bReader.readLine())) {
				System.out.println("文件正确");
				while ((line = bReader.readLine()) != null) {
					boolean success = true;
					TNode tNode = new TNode();
					System.out.println(line.toString());
					// System.out.println(line);
					String[] strings = line.split(",");
					if (strings.length == 16) {
						if (!strings[0].equals("null")) {
							List<TNode> tNodes = TNode.findAll();
							boolean only = true;
							for (TNode tNode2 : tNodes) {
								if (strings[0].equals(tNode2.T_NODE_NAME)) {
									only = false;
									break;
								}
							}
							if (only) {
								tNode.T_NODE_NAME = strings[0];
							} else {
								System.out.println("设备名不唯一");
								success = false;
							}
						} else {
							System.out.println("设备名称为空");
							success = false;
						}

						if (strings[1] != "null") {
							tNode.T_NODE_IP = strings[1];
							// 正则
							// 唯一

						} else {
							System.out.println("IP地址为空");
							success = false;
						}

						if (strings[2].equals("null")) {
							tNode.T_NODE_ACCOUNT = "";
						} else {
							tNode.T_NODE_ACCOUNT = strings[2];
						}

						if (strings[3].equals("null")) {
							tNode.T_NODE_PWD = "";
						} else {
							tNode.T_NODE_PWD = strings[3];
						}

						if (!strings[4].equals("null")) {
							if (strings[4].equals("ssh") || strings[4].equals("telnet")) {
								tNode.T_NODE_LOGINTYPE = strings[4];
							} else {
								System.out.println("登入方式错误");
								success = false;
							}
						} else {
							System.out.println("登入方式为空");
							success = false;
						}
						// 业务系统
						if (!strings[5].equals("null")) {
							List<DataDictionary> T_NODE_SYSTEMS = DataDictionary
									.find("T_DD_PARENTID", Long.parseLong("4")).fetch();
							boolean outData = true;
							for (DataDictionary dataDictionary : T_NODE_SYSTEMS) {
								if (strings[5].equals(dataDictionary.T_DD_VALUE)) {
									tNode.T_NODE_SYSTEM = strings[5];
									outData = false;
								}
							}
							if (outData) {
								success = false;
								System.out.println("业务系统未在数据字典中");
							}

						} else {
							tNode.T_NODE_SYSTEM = "";
						}
						// 设备负责人
						if (!strings[6].equals("null")) {
							boolean outData = true;
							List<TUser> tUsers = TUser.findAll();
							for (TUser tUser : tUsers) {
								if (strings[6].equals(tUser.T_USER_DISPLAY_NAME)) {
									tNode.T_NODE_CONTACTS = strings[6];
									outData = false;
								}
							}
							if (outData) {
								success = false;
								System.out.println("用户不存在");
							}
						} else {
							tNode.T_NODE_CONTACTS = "";
						}

						// 操作系统
						if (!strings[7].equals("null")) {
							List<DataDictionary> T_NODE_OS = DataDictionary.find("T_DD_PARENTID", Long.parseLong("3"))
									.fetch();
							boolean outData = true;
							for (DataDictionary dataDictionary : T_NODE_OS) {
								if (strings[7].equals(dataDictionary.T_DD_VALUE)) {
									tNode.T_NODE_OS = strings[7];
									outData = false;
								}
							}

							if (outData) {
								success = false;
								System.out.println("操作系统不在数据字典");
							}

						} else {
							tNode.T_NODE_OS = "";
						}
						// 厂商
						if (!strings[8].equals("null")) {
							List<DataDictionary> T_NODE_VENDOR = DataDictionary
									.find("T_DD_PARENTID", Long.parseLong("2")).fetch();
							boolean outData = true;
							for (DataDictionary dataDictionary : T_NODE_VENDOR) {
								if (strings[8].equals(dataDictionary.T_DD_VALUE)) {
									tNode.T_NODE_VENDOR = strings[8];
									outData = false;
								}
							}

							if (outData) {
								success = false;
								System.out.println("厂商不在数据字典");
							}

						} else {
							tNode.T_NODE_VENDOR = "";
						}
						// 设备类型
						if (!strings[9].equals("null")) {
							if (strings[9].equals("x86物理机") || strings[9].equals("小型机") || strings[9].equals("VMWare")
									|| strings[9].equals("Hyper-V") || strings[9].equals("交换机")
									|| strings[9].equals("带外管理卡")) {
								tNode.T_NODE_DEVICETYPE = strings[9];
							} else {
								System.out.println("设备类型错误");
								success = false;
							}
						} else {
							System.out.println("设备类型为空");
							tNode.T_NODE_DEVICETYPE = "";
						}

						// 上传脚本保存目录
						if (!strings[10].equals("null")) {
							tNode.T_NODE_LOCALPATH = strings[10];
						} else {
							System.out.println("设备类型为空");
							tNode.T_NODE_DEVICETYPE = "";
						}
						// 上传脚本方式
						if (!strings[11].equals("null")) {
							if (strings[11].equals("FTP") || strings[11].equals("SFTP")) {
								tNode.T_NODE_UPLOADTYPE = strings[11];
							} else {
								System.out.println("上传脚本方式错误");
								success = false;
							}
						} else {
							System.out.println("上传脚本为空");
							success = false;
						}

						// 机房
						if (!strings[12].equals("null")) {
							tNode.T_NODE_ROOM = strings[12];
						} else {
							System.out.println("机房为空");
							tNode.T_NODE_ROOM = "";
						}
						// 序列号
						if (!strings[13].equals("null")) {
							tNode.T_NODE_SN = strings[13];
						} else {
							System.out.println("序列号为空");
							tNode.T_NODE_SN = "";
						}
						// 型号
						if (!strings[14].equals("null")) {
							tNode.T_NODE_MODEL = strings[14];
						} else {
							System.out.println("型号为空");
							tNode.T_NODE_MODEL = "";
						}
						// 部门
						if (!strings[15].equals("null")) {
							List<DataDictionary> T_NODE_DEPARTMENT = DataDictionary
									.find("T_DD_PARENTID", Long.parseLong("1")).fetch();
							boolean outData = true;
							for (DataDictionary dataDictionary : T_NODE_DEPARTMENT) {
								if (strings[15].equals(dataDictionary.T_DD_VALUE)) {
									tNode.T_NODE_DEPARTMENT = strings[15];
									outData = false;
								}
							}

							if (outData) {
								success = false;
								System.out.println("部门不在数据字典");
							}

						} else {
							tNode.T_NODE_DEPARTMENT = "";
						}

						// System.out.println("最后一个字符:" + strings[15]);
						// System.out.println("文件长度正确");
					} else {
						System.out.println("此行数据格式不正确");
						success = false;
					}
					if (success) {
						tNode.save();
					}
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		index();
	}

	/**
	 * 获取list表数据，传到页面
	 */
	public static void getTNodes() {
		List<TNode> tNodes = TNode.findAll();
		JsonArray arr = new JsonArray();
		JsonObject obj;
		for (TNode tNode : tNodes) {
			obj = new JsonObject();
			obj.addProperty("T_NODE_CONTACTS", tNode.T_NODE_CONTACTS);
			obj.addProperty("T_NODE_NAME", tNode.T_NODE_NAME);
			obj.addProperty("T_NODE_IP", tNode.T_NODE_IP);
			arr.add(obj);
		}
		renderText(arr);
	}

	public static void getTNodesLength() {
		List<TNode> tNodes = TNode.findAll();
		JsonObject obj = new JsonObject();
		obj.addProperty("TNodesLength", tNodes.size());
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

	/**
	 * 查询功能，通过以下查询条件查询相关的信息
	 * 
	 * @throws Exception
	 */
	@Before
	public static void addType() throws Exception {
		ObjectType type = ObjectType.get(getControllerClass());
		renderArgs.put("type", type);
		StringBuilder sql = new StringBuilder();
		if (params.get("T_NODE_NAME") != null && !(params.get("T_NODE_NAME").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_NAME like '%" + params.get("T_NODE_NAME") + "%'");
		}
		if (params.get("T_NODE_IP") != null && !(params.get("T_NODE_IP").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_IP like '%" + params.get("T_NODE_IP") + "%'");
		}
		if (params.get("T_NODE_OS") != null && !(params.get("T_NODE_OS").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_OS like '%" + params.get("T_NODE_OS") + "%'");
		}
		if (params.get("T_NODE_CONTACTS") != null && !(params.get("T_NODE_CONTACTS").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_CONTACTS like '%" + params.get("T_NODE_CONTACTS") + "%'");
		}
		if (params.get("T_NODE_DEPARTMENT") != null && !(params.get("T_NODE_DEPARTMENT").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_DEPARTMENT like '%" + params.get("T_NODE_DEPARTMENT") + "%'");
		}
		if (params.get("T_NODE_ROOM") != null && !(params.get("T_NODE_ROOM").equals(""))) {
			if (sql != null && !(sql.toString().equals(""))) {
				sql.append(" and ");
			}
			sql.append("T_NODE_ROOM like '%" + params.get("T_NODE_ROOM") + "%'");
		}

		if (sql != null && !(sql.toString().equals(""))) {
			request.args.put("where", sql.toString());
		}
		System.out.println(sql.toString());
	}

	/*************************************************************************************************************************/
	public static void getNodeList() {
		String id = params.get("node_type_id");
		String flag = params.get("flag");

		JsonArray arr = new JsonArray();
		JsonObject obj = null;
		List<TNode> list;

		// 页面设备列表标志位
		if (flag.equals("0")) {
			if (id.equals("1")) {
				obj = new JsonObject();
				obj.addProperty("T_NODE_NAME", "");
				obj.addProperty("T_NODE_IP", "");
				obj.addProperty("T_NODE_OS", "");
				obj.addProperty("delete", "");
				arr.add(obj);
			} else {
				TNodeType nodeType = TNodeType.findById(Long.parseLong(id));
				Set<TNode> tNodes = nodeType.tNodes;
				if (tNodes != null && tNodes.size() > 0) {
					for (TNode node : tNodes) {
						obj = new JsonObject();
						obj.addProperty("T_NODE_NAME", node.T_NODE_NAME);
						obj.addProperty("T_NODE_IP", node.T_NODE_IP);
						obj.addProperty("T_NODE_OS", node.T_NODE_OS);
						obj.addProperty("delete",
								"<button class='btn btn-danger btn-xs' type='button' onclick='deleteNode(" + node.id
										+ ")' >" + "删除</button>");
						arr.add(obj);
					}
				} else {
					obj = new JsonObject();
					obj.addProperty("T_NODE_NAME", " ");
					obj.addProperty("T_NODE_IP", " ");
					obj.addProperty("T_NODE_OS", " ");
					obj.addProperty("delete", " ");
					arr.add(obj);
				}
			}
		}
		// 新增设备按钮标志位
		if (flag.equals("1")) {
			// 默认加载整个Node表
			list = TNode.findAll();

			TNodeType nodeType = TNodeType.findById(Long.parseLong(id));
			Set<TNode> tNodes = nodeType.tNodes;// 该分层下所有的设备
			Iterator<TNode> iterator = tNodes.iterator();

			List<Long> selectIds = new ArrayList<>();
			List<Long> unSelectIds = new ArrayList<>();
			while (iterator.hasNext()) {
				selectIds.add(iterator.next().id);
			}
			for (TNode node : list) {
				if (!selectIds.contains(node.id)) {
					unSelectIds.add(node.id);
				}
			}
			for (Long unId : unSelectIds) {
				TNode node = TNode.findById(unId);
				obj = new JsonObject();
				obj.addProperty("id", "<input type='checkbox' class='checkedID' name='nodeCheckbox' value='" + node.id
						+ "'></input>");
				obj.addProperty("T_NODE_NAME", node.T_NODE_NAME);
				obj.addProperty("T_NODE_IP", node.T_NODE_IP);
				obj.addProperty("T_NODE_OS", node.T_NODE_OS);
				arr.add(obj);
			}
		}
		renderJSON(new DataTableSource(request, arr));
	}

	/**
	 * 把设备挂载到分层上（新增设备的保存按钮）
	 */
	public static void addNodeToNodeType() {
		String nodeIds = params.get("nodeIds");
		String treeNodeId = params.get("treeNodeId");
		String[] _nodeIds = nodeIds.split(",");
		// System.out.println(nodeIds + " treeNodeId: " + treeNodeId);

		TNodeType nodeType = TNodeType.findById(Long.parseLong(treeNodeId));

		Set<TJob> tJobs = nodeType.tJobs;// 要添加进定时调度的jobs
		int size = nodeType.tNodes.size();// 标志位(新增分层-选择继承--此时生成的新层下没有设备，但是有任务[如果父层上有任务]，需要把继承来的任务添加到定时调度里面)

		for (String nodeId : _nodeIds) {
			TNode tNode = TNode.findById(Long.parseLong(nodeId));
			nodeType.tNodes.add(tNode);
		}
		nodeType.save();

		if (size == 0) {
			// System.out.println("进来了");
			if (tJobs != null && tJobs.size() > 0) {
				for (TJob job : tJobs) {
					EditJobs.addJob(job.id, nodeType, localPath);
				}
			}
		}
		renderText("OK");
	}

	/**
	 * 设备分层页面--把设备从设备列表中删除（点击删除图标生效）
	 */
	public static void deleteNodeById() {
		String id = params.get("id");
		String treeNodeId = params.get("treeNodeId");

		TNodeType nodeType = TNodeType.findById(Long.parseLong(treeNodeId));
		TNode tNode = TNode.findById(Long.parseLong(id));

		nodeType.tNodes.remove(tNode);
		nodeType.save();

		int size = nodeType.tNodes.size();
		// System.out.println("删除后剩余的设备数量："+size);
		if (size == 0) {
			Set<TJob> tJobs = nodeType.tJobs;
			for (TJob job : tJobs) {
				String jobName = treeNodeId + "_" + job.id;
				QuartzManager.removeJob(jobName);
			}
		}

		renderText("OK");
	}

	/**
	 * 设备连接测试
	 */
	public static void conTest() {
		String nodeId = params.get("tNodeIdTest");
		System.out.println(nodeId);
		TNode tNode = TNode.findById(Long.parseLong(nodeId));
		String ip = tNode.T_NODE_IP;
		String user = tNode.T_NODE_ACCOUNT;
		String password = tNode.T_NODE_PWD;
		String type = tNode.T_NODE_LOGINTYPE;
		if (type.equalsIgnoreCase("ssh")) {
			// System.out.println("登陆类型" + type);
			SSHClient sshClient = new SSHClient();
			if (sshClient.connect(ip, user, password)) {
				sshClient.disconnect();
				renderText("true");
			} else {
				renderText("false");
			}

		} else if (type.equalsIgnoreCase("telnet")) {
			// System.out.println("登陆类型" + type);
			TelnetClient telnetClient = new TelnetClient();
			if (connect(ip, user, password, telnetClient)) {
				renderText("true");
			} else {
				renderText("false");
			}
		} else {
			// System.out.println("登陆类型" + type);
			String line1 = "$para_ip='" + ip + "'";
			String line2 = "$para_name='" + user + "'";
			String line3 = "$para_serverpass='" + password + "'";
			String line4 = "$para_password=ConvertTo-SecureString $para_serverpass -AsPlainText -Force";
			String line5 = "$para_object=New-Object system.management.automation.pscredential($para_name,$para_password)";
			String line6 = "Invoke-Command -computername $para_ip -credential $para_object -scriptblock {echo  '===ScriptOver==='}";
			String line7 = "remove-item $MyInvocation.MyCommand.Path -force";
			String[] infos = { line1, line2, line3, line4, line5, line6, line7 };
			try {
				StringBuffer buf = new StringBuffer();
				// 保存该行前面的内容
				for (int j = 0; j < infos.length; j++) {
					buf = buf.append(infos[j]);
					buf = buf.append(System.getProperty("line.separator"));
				}
				CommonUtil.createShell(localPath + "/" + tNode.id.toString() + ".ps1", buf.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
			String scriptTest = PSClient.execCommand("powershell -f " + localPath + "/" + tNode.id.toString() + ".ps1");
			if (scriptTest.trim().equals("===ScriptOver===")) {
				renderText("true");
			} else {
				renderText("false");
			}
		}

	}

	/**
	 * telnet连接
	 * 
	 * @param ip
	 * @param user
	 * @param password
	 * @param telnet
	 * @return
	 */
	public static boolean connect(String ip, String user, String password, TelnetClient telnet) {
		try {
			telnet.connect(ip, 23);
			telnet.setSoTimeout(2 * 1000);
			PrintStream out = new PrintStream(telnet.getOutputStream());
			out.println(user);
			out.flush();
			// TimeUnit.SECONDS.sleep(1);
			out.println(password);
			out.flush();
			// TimeUnit.SECONDS.sleep(1);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static void delete(String id) {
		TNode tNode = TNode.findById(Long.parseLong(id));
		List<TNodeType>  tNodeTypes = TNodeType.find("select t from TNodeType t,TNode s where s.id in elements ( t.tNodes) and s.id="+id).fetch();
		if(!tNodeTypes.isEmpty()){
			for(TNodeType tNodeType : tNodeTypes){
				tNodeType.tNodes.remove(tNode);
				tNodeType.save();
			}
		}
		tNode.delete();
		redirect(request.controller + ".list");
	}

}

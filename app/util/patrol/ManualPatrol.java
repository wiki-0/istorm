package util.patrol;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import play.Play;
import util.jdbc.JDBCParser;

public class ManualPatrol {

	private Long nodeTypeId;
	private Long jobId;
	private String localPath;
	private SSHClient sshClient;
	private List<String> stdout = new ArrayList<String>();
	private List<String> stderr = new ArrayList<String>();
	private String output = new String();
	private JDBCParser jdbc = new JDBCParser();

	private Long nodeId;
	private Long scriptId;
	private String ip;
	private String user;
	private String pwd;
	private String remotePath;
	private String type;
	private String uploadType;
	private String loginType;
	private String fileName;
	private String command;

	/**
	 * Create a new instance Patrol.
	 * 
	 * @param nodeTypeId
	 * @param jobId
	 * @param localPath
	 */
	public ManualPatrol(Long nodeTypeId, Long jobId, String localPath) {
		this.nodeTypeId = nodeTypeId;
		this.jobId = jobId;
		this.localPath = localPath;
	}

	/**
	 * @return
	 * @Title: ManualPatrol
	 * @Description: 自动巡检启动
	 * @return void
	 * @throws IOException
	 * @throws SQLException
	 */
	public String manualPatrol() throws IOException, SQLException {
		String jobScriptsQuery = "select tscr.id scriptId,tscr.T_SCRIPT_FILENAME fileName,tscr.T_SCRIPT_COMMAND command,tscr.T_SCRIPT_TYPE type "
				+ "from t_script tscr,t_res_job2script tjs where tjs.T_JOB_ID = " + jobId
				+ " and tjs.T_SCRIPT_ID = tscr.id";
		String nodeTypeNodesQuery = "select tnode.id nodeId,tnode.T_NODE_IP ip,tnode.T_NODE_ACCOUNT userName,"
				+ "tnode.T_NODE_PWD pwd,tnode.T_NODE_LOGINTYPE loginType,tnode.T_NODE_LOCALPATH remotePath,tnode.T_NODE_UPLOADTYPE uploadType "
				+ "from t_node tnode, t_res_type2node trtn where trtn.T_TYPE_ID = " + nodeTypeId
				+ " and tnode.id= trtn.T_NODE_ID";

		List<Object[]> scriptsList = new ArrayList<>();
		List<Object[]> nodesList = new ArrayList<>();
		if (jDBC(jdbc)) {
			ResultSet scriptsSet = null;
			ResultSet nodesSet = null;
			scriptsSet = jdbc.sqlQuery(jobScriptsQuery);
			nodesSet = jdbc.sqlQuery(nodeTypeNodesQuery);
			while (scriptsSet.next()) {
				Object[] script = new Object[4];
				script[0] = Long.parseLong(scriptsSet.getString("scriptId"));
				script[1] = scriptsSet.getString("command");
				script[2] = scriptsSet.getString("type");
				script[3] = script[0] + scriptsSet.getString("fileName") + "." + scriptsSet.getString("type");
				scriptsList.add(script);
			}
			while (nodesSet.next()) {
				Object[] object = new Object[7];
				object[0] = Long.parseLong(nodesSet.getString("nodeId"));
				object[1] = nodesSet.getString("ip");
				object[2] = nodesSet.getString("userName");
				object[3] = nodesSet.getString("pwd");
				object[4] = nodesSet.getString("loginType");
				object[5] = nodesSet.getString("remotePath");
				object[6] = nodesSet.getString("uploadType");
				nodesList.add(object);
			}
			jdbc.close();
		}
		int scripts = scriptsList.size();

		for (Object[] node : nodesList) {
			this.nodeId = (long) node[0];
			this.ip = node[1].toString();
			this.user = node[2].toString();
			this.pwd = node[3].toString();
			this.loginType = node[4].toString();
			this.remotePath = node[5].toString();
			this.uploadType = node[6].toString();
			// if (connect())
			for (int i = 0; i < scripts; i++) {
				Object[] object = scriptsList.get(i);
				this.scriptId = (long) object[0];
				this.command = object[1].toString();
				this.type = object[2].toString();
				this.fileName = object[3].toString();
				// System.out.println("文件名=" + fileName);
				if (type.equalsIgnoreCase("command")) {
					if (!runCommand())
						saveError(nodeTypeId, nodeId, jobId, scriptId);
					else
						saveResult(nodeTypeId, nodeId, jobId, scriptId, stdout, stderr);
				} else if (type.equalsIgnoreCase("ps1")) {
					PSClient.replaceLines(ip, user, pwd, localPath, fileName, nodeId, nodeTypeId);
					output = PSClient.execCommands(
							"powershell -f " + localPath + "/" + nodeTypeId.toString() + nodeId.toString() + "abcdefghijklmnopqrstuvwxyz.ps1");
					savePSResult(nodeTypeId, nodeId, jobId, scriptId, output);
				} else {
					if (!upload())
						saveError(nodeTypeId, nodeId, jobId, scriptId);
					else if (!runScript())
						saveError(nodeTypeId, nodeId, jobId, scriptId);
					else
						saveResult(nodeTypeId, nodeId, jobId, scriptId, stdout, stderr);
				}
			}

		}
		return "ok";

	}

	/**
	 * @Title: upload
	 * @Description: 上传脚本
	 * @return boolean 正常true，异常false
	 * @throws IOException
	 */
	private boolean upload() {
		if (uploadType.equalsIgnoreCase("sftp")) {
			sshClient = new SSHClient();
			if (!sshClient.connect(ip, user, pwd))
				return false;
			if (!sshClient.sftpUpdate(localPath + "/" + fileName, remotePath))
				return false;
			// System.out.println("Patrol upload sftp ");
			if (!loginType.equalsIgnoreCase("ssh"))
				sshClient.disconnect();
		} else if (uploadType.equalsIgnoreCase("ftp")) {
			FtpClient ftpClient = new FtpClient();
			if (!ftpClient.connect(ip, 21, user, pwd))
				return false;
			if (!ftpClient.upload(localPath + "/" + fileName, remotePath, fileName))
				return false;
			// System.out.println("Patrol upload ftp ");
			ftpClient.disconnect();
		}
		return true;
	}

	/**
	 * @Title: runCommand
	 * @Description: 运行指令
	 * @return boolean 正常true，异常false
	 * @throws IOException
	 */
	public boolean runCommand() throws IOException {
		if (loginType.equalsIgnoreCase("ssh")) {
			SSHClient sshClient = new SSHClient();
			if (!sshClient.connect(ip, user, pwd))
				return false;
			// System.out.println("runCommandssh登录··········");
			this.stdout.clear();
			sshClient.processHandles(command, stdout, stderr);
			sshClient.disconnect();
		} else if (loginType.equalsIgnoreCase("telnet")) {
			TELNETClient telnetClient = new TELNETClient();
			String promt = user.equals("root") ? "#" : "$";
			telnetClient.connect(ip, user, pwd, promt);
			stdout = telnetClient.processHandle(command);
			telnetClient.disconnect();
		}
		return true;
	}

	/**
	 * @Title: runScript
	 * @Description: 运行脚本
	 * @return boolean 正常true，异常false
	 * @throws IOException
	 */
	public boolean runScript() throws IOException {
		if (loginType.equalsIgnoreCase("ssh")) {
			if (!uploadType.equalsIgnoreCase("sftp")) {
				sshClient = new SSHClient();
				if (!sshClient.connect(ip, user, pwd))
					return false;
			}
			if (type.equalsIgnoreCase("sh")) {
				this.stdout.clear();
				sshClient.processHandles("sh " + remotePath + "/" + fileName, stdout, stderr);
			} else {
				this.stdout.clear();
				sshClient.processHandles(remotePath + "/" + fileName, stdout, stderr);
			}
			sshClient.disconnect();
		} else if (loginType.equalsIgnoreCase("telnet")) {
			TELNETClient telnetClient = new TELNETClient();
			String promt = user.equals("root") ? "#" : "$";
			telnetClient.connect(ip, user, pwd, promt);
			if (type.equalsIgnoreCase("sh")) {
				this.stdout.clear();
				stdout = telnetClient.processHandle("sh " + remotePath + "/" + fileName);
			} else {
				this.stdout.clear();
				stdout = telnetClient.processHandle(remotePath + "/" + fileName);
			}
			telnetClient.disconnect();
		}
		return true;
	}

	/**
	 * @Title: saveResult
	 * @param nodeTypeId
	 * @param nodeId
	 * @param jobId
	 * @param scriptId
	 * @param stdout
	 * @param stderr
	 */
	public void saveResult(Long nodeTypeId, Long nodeId, Long jobId, Long scriptId, List<String> stdout,
			List<String> stderr) {
		String stdouts = "";
		String stderrs = "";
		String status = "";
		for (String tmp : stdout)
			stdouts = stdouts + tmp + "\r\n";
		for (String string : stderr) {
			stderrs = stderrs + string + "\r\n";
		}
		status = "OK";
		stdouts = stdouts.replaceAll("'", "");
		// System.out.println("结果：" + stdouts);
		String sqlQuery = "INSERT INTO t_result (T_TNODETYPE_ID,T_JOB_ID,T_NODE_ID,T_SCRIPT_ID,T_RESULT_TIME,T_RESULT_STATUS,T_RESULT_OUTPUT,T_RESULT_ALARM_STATUS) VALUES ('"
				+ nodeTypeId + "','" + jobId + "','" + nodeId + "','" + scriptId + "',now(),'" + status + "','"
				+ stdouts + "','0')";
		stdout = new ArrayList<>();
		if (jDBC(jdbc))
			jdbc.insert(sqlQuery);
	}

	/**
	 * @Title: savePSResult
	 * @param nodeTypeId
	 * @param nodeId
	 * @param jobId
	 * @param scriptId
	 * @param stdout
	 * @param stderr
	 */
	public void savePSResult(Long nodeTypeId, Long nodeId, Long jobId, Long scriptId, String output) {
		String status = "";
		if (output.contains("===END===")) {
			output = output.replace("===END===", "");
			status = "OK";
		} else {
			status = "ERROR";
		}
		if (output.contains("Exception")) {
			status = "ERROR";
		}
		// System.out.println("结果：" + output);
		String sqlQuery = "INSERT INTO t_result (T_TNODETYPE_ID,T_JOB_ID,T_NODE_ID,T_SCRIPT_ID,T_RESULT_TIME,T_RESULT_STATUS,T_RESULT_OUTPUT,T_RESULT_ALARM_STATUS) VALUES ('"
				+ nodeTypeId + "','" + jobId + "','" + nodeId + "','" + scriptId + "',now(),'" + status + "','" + output
				+ "','0')";
		if (jDBC(jdbc))
			jdbc.insert(sqlQuery);
	}

	/**
	 * @Title: saveError
	 * @Description: save the result of connect's error
	 * @param @param
	 *            id Job_id
	 * @return void
	 */
	public void saveError(Long nodeTypeId, Long nodeId, Long jobId, Long scriptId) {

		String sqlQuery = "INSERT INTO t_result (T_TNODETYPE_ID,T_JOB_ID,T_NODE_ID,T_SCRIPT_ID,T_RESULT_TIME,T_RESULT_STATUS,T_RESULT_OUTPUT,T_RESULT_ALARM_STATUS) VALUES ('"
				+ nodeTypeId + "','" + jobId + "','" + nodeId + "','" + scriptId
				+ "',now(),'ERROR','Cannot connect to node','0')";

		if (jDBC(jdbc)) {
			jdbc.insert(sqlQuery);
		}
	}

	/**
	 * @Title jDBC
	 * @Description connect jdbc
	 * @param jdbc
	 * @return
	 */
	public boolean jDBC(JDBCParser jdbc) {
		String dbtype = Play.configuration.getProperty("db.default.driver");
		String url = Play.configuration.getProperty("db.default.url");
		String user = Play.configuration.getProperty("db.default.user");
		String pwd = Play.configuration.getProperty("db.default.pass");
		// System.out.println(dbtype);
		// System.out.println(url);
		// System.out.println(user);
		// System.out.println(pwd);
		if (jdbc.connect(dbtype, url, user, pwd)) {
			return true;
		} else {
			return false;
		}
	}

}

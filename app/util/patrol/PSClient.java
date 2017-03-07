package util.patrol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;

import util.CommonUtil;

public class PSClient {
	public static String execCommand(String command) {
		CommandLine cmd = CommandLine.parse(command);
		OutputStream outputStream = new ByteArrayOutputStream();
		DefaultExecutor executor = new DefaultExecutor();

		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		ShutdownHookProcessDestroyer processDestroyer = new ShutdownHookProcessDestroyer();
		executor.setStreamHandler(streamHandler);
		executor.setProcessDestroyer(processDestroyer);
		try {
			executor.execute(cmd);
			// PipedInputStream pis = new PipedInputStream(output);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = outputStream == null ? "" : outputStream.toString();
		int firstC = output.indexOf("<");
		if (firstC >= 0)
			output = output.substring(firstC);
		return output;
	}

	/**
	 * 执行powershell脚本，返回输出流
	 * 
	 * @param command
	 * @return
	 */
	public static String execCommands(String command) {
		CommandLine cmd = CommandLine.parse(command);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		DefaultExecutor executor = new DefaultExecutor();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		ShutdownHookProcessDestroyer processDestroyer = new ShutdownHookProcessDestroyer();
		executor.setStreamHandler(streamHandler);
		executor.setProcessDestroyer(processDestroyer);
		try {
			executor.execute(cmd);
			// PipedInputStream pis = new PipedInputStream(output);
		} catch (ExecuteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String output = null;
		try {
			byte[] lens = outputStream.toByteArray();
			output = new String(lens, "GBK");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	/**
	 * 设备信息脚本，调用相应
	 * 
	 * @param ipNew
	 * @param nameNew
	 * @param passwordNew
	 * @param localPath
	 * @param nodeId
	 * @param tnodeTypeId
	 */

	public static void replaceLines(String ipNew, String nameNew, String passwordNew, String localPath, String fileName,
			Long nodeId, Long tnodeTypeId) {

		String line1 = "$para_ip='" + ipNew + "'";
		String line2 = "$para_name='" + nameNew + "'";
		String line3 = "$para_serverpass='" + passwordNew + "'";
		String line4 = "$para_password=ConvertTo-SecureString $para_serverpass -AsPlainText -Force";
		String line5 = "$para_object=New-Object system.management.automation.pscredential($para_name,$para_password)";
		String line6 = "$para_session=New-PSSession -computername $para_ip -credential $para_object";
		String line7 = "Invoke-Command -Session $para_session -FilePath " + localPath + "/" + fileName;
		String line8 = "remove-item $MyInvocation.MyCommand.Path -force";
		String[] infos = { line1, line2, line3, line4, line5, line6, line7, line8 };
		try {
			StringBuffer buf = new StringBuffer();
			for (int j = 0; j < infos.length; j++) {
				buf = buf.append(infos[j]);
				buf = buf.append(System.getProperty("line.separator"));
			}
			CommonUtil.createShell(localPath + "/" + tnodeTypeId.toString() + nodeId.toString() + ".ps1",
					buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设备信息脚本，调用相应
	 * 
	 * @param ipNew
	 * @param nameNew
	 * @param passwordNew
	 * @param localPath
	 * @param nodeId
	 * @param tnodeTypeId
	 */

	public static void replaceLinesManual(String ipNew, String nameNew, String passwordNew, String localPath,
			String fileName, Long nodeId, Long tnodeTypeId) {

		String line1 = "$para_ip='" + ipNew + "'";
		String line2 = "$para_name='" + nameNew + "'";
		String line3 = "$para_serverpass='" + passwordNew + "'";
		String line4 = "$para_password=ConvertTo-SecureString $para_serverpass -AsPlainText -Force";
		String line5 = "$para_object=New-Object system.management.automation.pscredential($para_name,$para_password)";
		String line6 = "$para_session=New-PSSession -computername $para_ip -credential $para_object";
		String line7 = "Invoke-Command -Session $para_session -FilePath " + localPath + "/" + fileName;
		String line8 = "remove-item $MyInvocation.MyCommand.Path -force";
		String[] infos = { line1, line2, line3, line4, line5, line6, line7, line8 };
		try {
			StringBuffer buf = new StringBuffer();
			for (int j = 0; j < infos.length; j++) {
				buf = buf.append(infos[j]);
				buf = buf.append(System.getProperty("line.separator"));
			}
			CommonUtil.createShell(
					localPath + "/" + tnodeTypeId.toString() + nodeId.toString() + "abcdefghijklmnopqrstuvwxyz.ps1",
					buf.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

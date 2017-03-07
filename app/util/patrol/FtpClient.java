/**
 * File Name：FtpClient.java
 *
 * Version：
 * Date：2012-3-26
 * Copyright CloudWei Dev Team 2012 
 * All Rights Reserved.
 *
 */
package util.patrol;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import javassist.tools.framedump;
import models.TScript;


public class FtpClient {
	private FTPClient ftpClient = new FTPClient();

	public boolean connect(String hostname, int port, String username, String password) {
		try {
			ftpClient.connect(hostname, port);
			if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode()))
				if (ftpClient.login(username, password))
					return true;
			disconnect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean download(String remote, String local) {
		ftpClient.enterLocalPassiveMode();
		boolean result = false;
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			File f = new File(local);
			OutputStream out = new FileOutputStream(f);
			result = ftpClient.retrieveFile(remote, out);
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public boolean upload(String localPath, String remotePath, String fileName) {
		boolean result = false;
		try {
			ftpClient.enterLocalPassiveMode();
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			ftpClient.setControlEncoding("UTF-8");
			InputStream is = new FileInputStream(localPath);
			ftpClient.changeWorkingDirectory(remotePath);
			result = ftpClient.storeFile(fileName, is);
			is.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public void disconnect() {
		try {
			if (ftpClient.isConnected())
				ftpClient.disconnect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

/**
 * File Name：SSHClient.java
 *
 */
package util.patrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;
import play.Logger;

public class SSHClient {
	private Connection conn;
	private Session sess;
	private static final int TIME_OUT = 1000 * 60;
	private int port = 22;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * @Title: connect
	 * @Description: connect ssh2
	 * @param @param
	 *            ip
	 * @param @param
	 *            user
	 * @param @param
	 *            password
	 * @param @return
	 * @return boolean
	 */
	public boolean connect(String ip, String user, String password) {
		conn = new Connection(ip, port);
		try {
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(user, password);
			if (isAuthenticated == false) {
				conn.close();
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @Title: processHandles
	 * @Description: Run command by ssh2
	 * @param @param
	 *            command
	 * @param @param
	 *            stdout
	 * @param @param
	 *            stderr
	 * @param @return
	 * @return int
	 */
	public int processHandles(String command, List<String> stdout, List<String> stderr) {
		try {
			sess = conn.openSession();
			sess.execCommand(command);
			if (stdout != null && stderr != null) {
				// out stream
				InputStream output = new StreamGobbler(sess.getStdout());
				BufferedReader br = new BufferedReader(new InputStreamReader(output));
				String response = null;
				while ((response = br.readLine()) != null) {
					stdout.add(response);
				}
				// Logger.info(command + " is over");
				// err stream
				output = new StreamGobbler(sess.getStderr());
				br = new BufferedReader(new InputStreamReader(output));
				while ((response = br.readLine()) != null) {
					if (response.trim().length() > 0)
						stderr.add(response);
				}
				br.close();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.waitForCondition(ChannelCondition.EXIT_STATUS, TIME_OUT);
			Logger.debug("Session exit " + sess.getExitStatus());
			sess.close();
		}
		return sess.getExitStatus();
	}

	public String readin(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		int size = 1024;
		byte[] inputData = new byte[size];
		int t = 0;
		do {
			t = in.read(inputData);
			System.out.println(t);
			if (t > 0) {
				byte[] tempData = new byte[t];
				for (int i = 0; i < t; i++) {
					tempData[i] = inputData[i];
				}
				sb.append(new String(tempData, "UTF-8"));
			}
		} while (t == size);
		return sb.toString();
	}

	/**
	 * @Title: sftpUpdate
	 * @Description: update file by sftp
	 * @param localPath
	 *            = localPath+fileName
	 * @param remotePath
	 * @return void
	 */
	public boolean sftpUpdate(String localPath, String remotePath) {
		try {
			SCPClient scpClient = conn.createSCPClient();
			scpClient.put(localPath, remotePath);
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @Title: disconnect
	 * @Description: disconnect ssh2
	 * @param
	 * @return void
	 */
	public void disconnect() {
		conn.close();
	}

}

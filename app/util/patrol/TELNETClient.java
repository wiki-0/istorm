/**
 * File Name：TelnetClient.java
 */
package util.patrol;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;

import play.Logger;

public class TELNETClient {
	private TelnetClient telnet = new TelnetClient();
	private InputStream in;
	private PrintStream out;
	private String prompt = null;
	String logout;

	public boolean connect(String server, String user, String password, String prompt) {
		try {
			this.logout = "exit";
			this.prompt = prompt;
			telnet.connect(server, 23);
			telnet.setSoTimeout(60 * 1000);
			in = telnet.getInputStream();
			out = new PrintStream(telnet.getOutputStream());
			readUntil("login:");
			write(user);
	//		TimeUnit.SECONDS.sleep(2);
			readUntil("password:");
	//		TimeUnit.SECONDS.sleep(2);
			write(password);
	//		TimeUnit.SECONDS.sleep(10);
			readUntil(prompt);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public String readUntil(String pattern) {
		try {
			System.out.println("pattern:" + pattern);
			char lastChar = pattern.charAt(pattern.length() - 1);
			StringBuffer sb = new StringBuffer();
			char a = '>';
			char c;
			while (true) {
				c = (char) in.read();
				sb.append(c);
				
				if (c == lastChar || c == a) {
					if (sb.toString().endsWith(pattern) || sb.toString().endsWith("Desktop>")) {
						System.out.println("sb字符串：" + sb.toString());
						Logger.info("sb字符串：" + sb.toString());
					return sb.toString();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void write(String value) {
		try {
			out.write(value.getBytes("utf-8"));
			out.println();
			out.flush();
			// System.out.println("Write:" + value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<String> processHandle(String command) {
		List<String> list = null;
		try {
			write(command);
			String result = this.readUntil(this.prompt + "");
			list = new ArrayList<String>();
			if (result != null && !"".equals(result)) {
				for (String s : result.split("\r\n")) {
					s = s.replaceAll(">", "");
					list.add(s.trim());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println("~~~~~~~~list:" + list);
		return list;
	}

	public List<String> processHandleDumpel(String command) {
		List<String> list = null;
		try {
			write(command);
			String result = this.readUntil(this.prompt + "");
			list = new ArrayList<String>();
			for (String s : result.split("\r\n")) {
				// System.out.println(" s= " + s);
				String[] out = s.split("\t");
				// System.out.println(" out[0]= " + out[0]);
				if (out.length > 2 && out[2].equals("1")) {
					if (s.contains(" > > "))
						s = s.replaceAll(">", "");
					list.add(s.trim());
				}
			}
			list.remove(list.size() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public String processHandles(String command) {
		List<String> list = processHandle(command);
		String result = "";
		for (String s : list)
			result += s + "\n";
		return result;
	}

	public void disconnect() {
		try {
			write(this.logout);
			telnet.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String sendCommand(String command) {
		try {
			write(command);
			return readUntil(prompt + " ");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

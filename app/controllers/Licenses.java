package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.List;

import com.google.gson.JsonObject;

import models.TLicense;
import models.TScript;
import play.libs.Files;
import util.license.LicenseManager;

@CRUD.For(TLicense.class)
public class Licenses extends CRUD {

	public static void index() {
		render();
	}

	public static void saveLicense(File file, TLicense tLicense) throws Exception {
		TLicense tLicense2 = TLicense.find("T_LICENSE_USER_NAME", session.get("username")).first();
		LicenseManager licenseManager = new LicenseManager();
		InetAddress ia = InetAddress.getLocalHost();

		String T_LICENSE_MAC = getLocalMac(ia);
		if (tLicense2 == null) {
			if (file == null) {
				tLicense.T_LICENSE_CONTENT = params.get("T_LICENSE_CONTENT");
				tLicense.T_LICENSE_USER_NAME = session.get("username");
				tLicense.T_LICENSE_MAC = T_LICENSE_MAC;
				tLicense.T_LICENSE_STATUS = licenseManager.riddle(tLicense.T_LICENSE_CONTENT, tLicense.T_LICENSE_MAC);
				tLicense.save();
				index();
			} else {
				String fileName = file.getName();
				File storeFile = new File("./licenses/" + fileName);
				Files.copy(file, storeFile);

				BufferedReader bReader = null;
				String line = null;
				StringBuffer buffer = new StringBuffer();
				try {
					bReader = new BufferedReader(new FileReader(new File("./licenses/" + fileName)));
					while ((line = bReader.readLine()) != null) {
						buffer.append(line).append("\n");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				tLicense.T_LICENSE_FILE_NAME = fileName;
				tLicense.T_LICENSE_CONTENT = buffer.toString();
				tLicense.T_LICENSE_USER_NAME = session.get("username");
				tLicense.T_LICENSE_MAC = T_LICENSE_MAC;
				tLicense.T_LICENSE_STATUS = licenseManager.riddle(tLicense.T_LICENSE_CONTENT.trim(), tLicense.T_LICENSE_MAC);
				tLicense.save();
				index();
			}
		} else {
			if (file == null) {
				tLicense2.T_LICENSE_CONTENT = params.get("T_LICENSE_CONTENT");
				tLicense2.T_LICENSE_USER_NAME = session.get("username");
				tLicense2.T_LICENSE_MAC = T_LICENSE_MAC;
				tLicense2.T_LICENSE_STATUS = licenseManager.riddle(tLicense2.T_LICENSE_CONTENT,
						tLicense2.T_LICENSE_MAC);
				tLicense2.save();
				index();
			} else {
				String fileName = file.getName();
				File storeFile = new File("./licenses/" + fileName);
				Files.copy(file, storeFile);

				BufferedReader bReader = null;
				String line = null;
				StringBuffer buffer = new StringBuffer();
				try {
					bReader = new BufferedReader(new FileReader(new File("./licenses/" + fileName)));
					while ((line = bReader.readLine()) != null) {
						buffer.append(line).append("\n");
					}
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				tLicense2.T_LICENSE_FILE_NAME = fileName;
				tLicense2.T_LICENSE_CONTENT = buffer.toString();
				tLicense2.T_LICENSE_USER_NAME = session.get("username");
				tLicense2.T_LICENSE_MAC = T_LICENSE_MAC;
				tLicense2.T_LICENSE_STATUS = licenseManager.riddle(tLicense2.T_LICENSE_CONTENT.trim(),
						tLicense2.T_LICENSE_MAC);
				tLicense2.save();
				index();
			}
		}
	}

	public static void getLicenseStatus() {
		JsonObject obj = new JsonObject();
		if (session.get("username") != null) {
			TLicense tLicense = TLicense.find("T_LICENSE_USER_NAME", session.get("username")).first();
			if (tLicense != null) {
				obj.addProperty("LicenseStatus", tLicense.T_LICENSE_STATUS);
			} else {
				obj.addProperty("LicenseStatus", "");
			}

		}
		renderText(obj);
	}

	private static String getLocalMac(InetAddress ia) throws SocketException {
		// TODO Auto-generated method stub
		// 获取网卡，获取地址
		byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
		// System.out.println("mac数组长度："+mac.length);
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mac.length; i++) {
			if (i != 0) {
				sb.append("-");
			}
			// 字节转换为整数
			int temp = mac[i] & 0xff;
			String str = Integer.toHexString(temp);
			System.out.println("每8位:" + str);
			if (str.length() == 1) {
				sb.append("0" + str);
			} else {
				sb.append(str);
			}
		}
		// System.out.println("本机MAC地址:"+sb.toString().toUpperCase());
		return sb.toString().toUpperCase();
	}
}

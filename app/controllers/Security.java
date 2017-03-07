package controllers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import models.TLicense;
import models.TUser;
import util.EncrypAES;

public class Security extends Secure.Security{
	/**
	 * @Title: authenticate
	 * @Description: 登陆验证
	 * @param @param username 用户名
	 * @param @param password 密码
	 * @param @return
	 * @return boolean
	 * @throws
	 * @since CloudWei v1.0.0
	*/
	static boolean authenticate(String username, String password) {
		TUser tUser = TUser.find("byT_USER_NAME", username).first();
		EncrypAES de1;
		byte[] encontent;
		String encryptResultStr = password;
		try {
			de1 = new EncrypAES();
			encontent = de1.Encrytor(password);
			encryptResultStr = EncrypAES.parseByte2HexStr(encontent);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (InvalidKeyException | IllegalBlockSizeException
				| BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//非空判断
		if (tUser != null && tUser.T_USER_PASSWORD.equals(encryptResultStr)){
			session.put("username", tUser.T_USER_NAME);
			session.put("displayName", tUser.T_USER_DISPLAY_NAME);
		}
		return tUser != null && tUser.T_USER_PASSWORD.equals(encryptResultStr);
    }
}

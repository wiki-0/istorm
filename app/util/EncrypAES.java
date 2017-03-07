package util;
import java.io.File;
import java.io.FileWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class EncrypAES {
	
//	//KeyGenerator 提供对称密钥生成器的功能，支持各种算法
//	private KeyGenerator keygen;
	//SecretKey 负责保存对称密钥
	private SecretKey deskey;
	//Cipher负责完成加密或解密工作
	private Cipher c;
	//该字节数组负责保存加密的结果
	private byte[] cipherByte;
	
	public EncrypAES() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException{
//		//Security.addProvider(new com.sun.crypto.provider.SunJCE());
//		Security.addProvider(new com.ibm.crypto.provider.IBMJCE()); 
//		//实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
//		keygen = KeyGenerator.getInstance("AES");
		
		//生成固定密钥 32位 十六进制
		byte[] key = parseHexStr2Byte("aaaaaaaaaabbbbbbaaaaaaaaaabbbbbb");
		SecretKeySpec sKey = new SecretKeySpec(key, "AES");
		//密钥
		deskey = sKey;
		//生成Cipher对象,指定其支持的DES算法
		c = Cipher.getInstance("AES");
	}
	
	/**
	 * 对字符串加密
	 * 
	 * @param str
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Encrytor(String str) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
		c.init(Cipher.ENCRYPT_MODE, deskey);
		byte[] src = str.getBytes();
		// 加密，结果保存进cipherByte
		cipherByte = c.doFinal(src);
		return cipherByte;
	}

	/**
	 * 对字符串解密
	 * 
	 * @param buff
	 * @return
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] Decryptor(byte[] buff) throws InvalidKeyException,
			IllegalBlockSizeException, BadPaddingException {
		// 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
		c.init(Cipher.DECRYPT_MODE, deskey);
		cipherByte = c.doFinal(buff);
		return cipherByte;
	}

	
	/**
	 * 加密
	 * @param args
	 * @return 
	 */
	public static String parseByte2HexStr(byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			String hex = Integer.toHexString(buf[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}
	
	/*
	* 转为二进制
	*/
	public static byte[] parseHexStr2Byte(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2),
					16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 解密
	 * @param args
	 * @return 
	 */
	public static String deAES(String pwd) throws Exception {
		EncrypAES de1 = new EncrypAES();
		byte[] decryptFrom = parseHexStr2Byte(pwd);
		byte[] decontent = de1.Decryptor(decryptFrom);
		return new String(decontent);
	}
	
	//测试类
	public static void main(String[] args) throws Exception {
		EncrypAES de1 = new EncrypAES();
		String msg ="111111";
		byte[] encontent = de1.Encrytor(msg);
		String encryptResultStr = parseByte2HexStr(encontent);
		System.out.println("加密后：" + encryptResultStr);
		//解密   
		byte[] decryptFrom = parseHexStr2Byte(encryptResultStr); 
		byte[] decontent = de1.Decryptor(decryptFrom);
		System.out.println("解密后:" + new String(decontent));
	}

}

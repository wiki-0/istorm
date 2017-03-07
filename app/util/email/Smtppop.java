package util.email;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 邮件发送主函数
 * 
 * @author IBM
 * 
 */
public class Smtppop
{

	private static Log logger = LogFactory.getLog("Smtppop");
	/**
	 * 发送EMAIL
	 * 
	 */
	public static HashMap<String, String> smtpEmail(HashMap<String, String> para)
	{
		// 取得参数
		// server host
		String host = String.valueOf(para.get("MAIL_SERVER_HOST"));
		String port = String.valueOf(para.get("MAIL_SERVER_PORT"));
		String timeout = String.valueOf(para.get("TIME_OUT"));
		// wangsla edit tb_sys_para --> USER_NAME value is changed 
		String userName = String.valueOf(para.get("USER_NAME"));
		// end
		String password = String.valueOf(para.get("PASSWORD"));
		String fromAddress = String.valueOf(para.get("FROM_ADDRESS"));
		String toAddress = String.valueOf(para.get("TO_ADDRESS"));
		String subject = String.valueOf(para.get("subject"));
		String content = String.valueOf(para.get("content"));

		String proxySet = String.valueOf(para.get("PROXY_SET"));
		String socksProxyHost = String.valueOf(para.get("SOCKS_PROXY_HOST"));
		String socksProxyPort = String.valueOf(para.get("SOCKS_PROXY_PORT"));
		String proxyHost = String.valueOf(para.get("PROXY_HOST"));
		String proxyPort = String.valueOf(para.get("PROXY_PORT"));
		logger.info("邮件发送 host=" + host + ",port = " + port + ",timeout=" + timeout + 
				",userName=" + userName + ",password=" + password + ",fromAddress=" + fromAddress +
				",toAddress=" + toAddress + ",subject=" + subject + ",content=" + content);
		logger.debug("邮件发送 host=" + host + ",port = " + port + ",timeout=" + timeout + 
				",userName=" + userName + ",password=" + password + ",fromAddress=" + fromAddress + 
				",toAddress=" + toAddress + ",subject=" + subject + ",content=" + content);
		System.out.println(" ***************888  邮件发送 host=" + host + ",port = " + port + ",timeout=" + timeout + 
				",userName=" + userName + ",password=" + password + ",fromAddress=" + fromAddress + 
				",toAddress=" + toAddress + ",subject=" + subject + ",content=" + content);
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost(host);
		mailInfo.setMailServerPort(port);
		// 发送的timeout
		mailInfo.setTimeout(timeout);
		mailInfo.setConnectiontimeout(timeout);
		mailInfo.setValidate(true);
		mailInfo.setUserName(userName);
		mailInfo.setPassword(password);// 您的邮箱密码

		mailInfo.setFromAddress(fromAddress);
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);

		mailInfo.setProxySet(proxySet);
		mailInfo.setSocksProxyHost(socksProxyHost);
		mailInfo.setSocksProxyPort(socksProxyPort);
		mailInfo.setProxyHost(proxyHost);
		mailInfo.setProxyPort(proxyPort);
		// 这个类主要来发送邮件
		// System.out.println("mailInfo" + mailInfo.toString());
		HashMap<String, String> retHashMap = new HashMap<String, String>();
		MailSenderInfo ret = null;
		try
		{
//			ret = MailSender.sendTextMail(mailInfo);// 发送文体格式
			boolean flag = MailSender.sendHtmlMail(mailInfo);
			retHashMap.put("isSuccess", ""+flag);
			retHashMap.put("failure", ""+flag);
			// 连接用时
//			retHashMap.put("useTime", ret.getUseTime());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return retHashMap;
	}

	/**
	 * 接受EMAIL
	 * 
	 */
	public static HashMap<String, String> pop3Email(HashMap<String, String> para)
	{
		// 接受参数
		String host = String.valueOf(para.get("host"));
		int port = Integer.parseInt(String.valueOf(para.get("port")));
		String timeout = String.valueOf(para.get("timeout"));
		String userName = String.valueOf(para.get("userName"));
		String password = String.valueOf(para.get("password"));

		// 这个类主要是设置邮件
		MailReceiveInfo mailInfo = new MailReceiveInfo();
		mailInfo.setHost(host);
		mailInfo.setPort(port);
		mailInfo.setUser(userName);
		mailInfo.setPwd(password);
		mailInfo.setTimeout(timeout);
		mailInfo.setConnectiontimeout(timeout);
		MailReceiveInfo ret = new GmailFetch().getMail(mailInfo);

		HashMap<String, String> retHashMap = new HashMap<String, String>();
		// 测试结果
		retHashMap.put("isSuccess", ret.getIsSuccess());
		// 失败原因
		retHashMap.put("failure", ret.getFailure());
		// 连接用时
		retHashMap.put("useTime", ret.getUseTime());

		return retHashMap;
	}
}

package util.email;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailSender {
	
	private static String endCode = "utf-8";
//	private static String endCode = "ISO8859_1";
	public static MailSenderInfo sendTextMail(MailSenderInfo mailInfo) {

		// 返回信息设置
		MailSenderInfo retInfo = new MailSenderInfo();

		// 判断是否需要身份认证֤
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		if (mailInfo.isValidate()) {
			// 如果需要身份认证，则创建一个密码验证器
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);

		int startTime = 0;
		int endTime = 0;
		try {
			startTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));
			// 根据session创建一个邮件消息
			MimeMessage mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			mailMessage.setSubject(mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// 设置邮件消息的主要内容
			String mailContent = mailInfo.getContent();
			mailMessage.setText(mailContent, endCode);
			// 发送邮件
			Transport.send(mailMessage);
			endTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));
			
		} catch (MessagingException e) {
			endTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));

			// 失败原因
			retInfo.setFailure(e.getMessage());
			// 失败标志
			retInfo.setIsSuccess("0");
			// 使用时间
			retInfo.setUseTime(String.valueOf(endTime - startTime));
		}

		// 使用时间
		retInfo.setUseTime(String.valueOf(endTime - startTime));

		return retInfo;
	}

	/** */
	/**
	 * 以HTML格式发送邮件
	 * 
	 * @param mailInfo
	 *            待发送的邮件信息
	 */
	public static boolean sendHtmlMail(MailSenderInfo mailInfo) {
		// 判断是否需要身份认证֤
		MyAuthenticator authenticator = null;
		Properties pro = mailInfo.getProperties();
		// 如果需要身份认证，则创建一个密码验证器
		if (mailInfo.isValidate()) {
			authenticator = new MyAuthenticator(mailInfo.getUserName(),
					mailInfo.getPassword());
		}
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session sendMailSession = Session
				.getDefaultInstance(pro, authenticator);
		try {
			// 根据session创建一个邮件消息
			Message mailMessage = new MimeMessage(sendMailSession);
			// 创建邮件发送者地址ַ
			Address from = new InternetAddress(mailInfo.getFromAddress());
			// 设置邮件消息的发送者
			mailMessage.setFrom(from);
			// 创建邮件的接收者地址，并设置到邮件消息中
			Address to = new InternetAddress(mailInfo.getToAddress());
			// Message.RecipientType.TO属性表示接收者的类型为TO
			mailMessage.setRecipient(Message.RecipientType.TO, to);
			// 设置邮件消息的主题
			sun.misc.BASE64Decoder base64Decoder = new sun.misc.BASE64Decoder();
			String subject = new String(base64Decoder.decodeBuffer(mailInfo.getSubject()));
		//	mailMessage.setSubject(MimeUtility.encodeText(mailInfo.getSubject(), "UTF-8", "B"));
			mailMessage.setSubject(mailInfo.getSubject());
			System.out.println("  **********************888 mailInfo.getSubject() = " +  mailInfo.getSubject());
			// 设置邮件消息发送的时间
			mailMessage.setSentDate(new Date());
			// MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
			Multipart mainPart = new MimeMultipart();
			// 创建一个包含HTML内容的MimeBodyPart
			BodyPart html = new MimeBodyPart();
			// 设置HTML内容
			html.setContent(mailInfo.getContent(), "text/html; charset=\""
					+ endCode + "\"");
			System.out.println("  **********************888 mailInfo.getContent() = " +  mailInfo.getContent());
			System.out.println("  **********************888 html.getContent().toString() = " +  html.getContent().toString());
			mainPart.addBodyPart(html);
			// 将MiniMultipart对象设置为邮件内容
			mailMessage.setContent(mainPart);
			// 发送邮件
			System.out.println("  **********************888 mailMessage.getSubject() = " +  mailMessage.getSubject());
			System.out.println("  **********************888 mailMessage.getContent().toString() = " +  mailMessage.getContent().toString());
			Transport.send(mailMessage);
			return true;
		} catch (MessagingException ex) {
			ex.printStackTrace();
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return false;
	}

}

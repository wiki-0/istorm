package util.email;

import java.io.UnsupportedEncodingException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * 收EMAIL
 * 
 * @author IBM
 * 
 */
public class GmailFetch {

	public MailReceiveInfo getMail(MailReceiveInfo mailInfo) {
		Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());

		// 返回信息设置
		MailReceiveInfo retInfo = new MailReceiveInfo();

		Properties prop = mailInfo.getProperties();

		// 以下步骤跟一般的JavaMail操作相同
		Session session = Session.getDefaultInstance(prop, null);

		// 请将红色部分对应替换成你的邮箱帐号和密码
		URLName urln = new URLName("pop3", mailInfo.getHost(), mailInfo
				.getPort(), null, mailInfo.getUser(), mailInfo.getPwd()); // 这里替换密码
		Folder inbox = null;
		Store store = null;
		int startTime = 0;
		int endTime = 0;
		try {
			startTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));
			store = session.getStore(urln);

			store.connect();
			inbox = store.getFolder("INBOX");
			// 读模式
			// inbox.open(Folder.READ_ONLY);
			inbox.open(Folder.READ_WRITE);
			FetchProfile profile = new FetchProfile();
			profile.add(FetchProfile.Item.ENVELOPE);
			Message[] messages = inbox.getMessages();
			inbox.fetch(messages, profile);
			endTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));
			//System.out.println("收件箱的邮件数：" + messages.length);
			for (int i = 0; i < messages.length; i++) {
				// 邮件发送者
				String from = decodeText(messages[i].getFrom()[0].toString());
				InternetAddress ia = new InternetAddress(from);
				//System.out.println("发信人:" + ia.getAddress());
				// ia.getPersonal()
				// 邮件标题
				//System.out.println("主题:"+ base64Decoder(messages[i].getSubject()));
				// 邮件大小
				//System.out.println("邮件大小:" + messages[i].getSize());
				//邮件发送时间
				//System.out.println("发送日期:" + messages[i].getSentDate());
				messages[i].setFlag(Flags.Flag.FLAGGED, true);
				// messages[i].setFlag(Flags.Flag.DELETED, true);
			}

		} catch (Exception e) {
			endTime = Integer.parseInt(new SimpleDateFormat("mmssSSS")
					.format(new Date()));

			// 失败原因
			retInfo.setFailure(e.getMessage());
			// 失败标志
			retInfo.setIsSuccess("0");
			// 使用时间
			retInfo.setUseTime(String.valueOf(endTime - startTime));

		} finally {
			try {
				inbox.close(true);
				store.close();
			} catch (Exception e) {

			}
		}
		// 使用时间
		retInfo.setUseTime(String.valueOf(endTime - startTime));

		return retInfo;
	}

	// base64解码
	private static String base64Decoder(String s) throws Exception {
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] b = decoder.decodeBuffer(s);
		return (new String(b));
	}

	protected static String decodeText(String text)
			throws UnsupportedEncodingException {
		if (text == null)
			return null;
		if (text.startsWith("=?GB") || text.startsWith("=?gb"))
			text = MimeUtility.decodeText(text);
		else
			text = new String(text.getBytes("ISO8859_1"));
		return text;
	}
}

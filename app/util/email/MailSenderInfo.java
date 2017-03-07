package util.email;

import java.util.Properties;

public class MailSenderInfo
{

	// 发送邮件的服务器的IP和端口
	private String mailServerHost;

	private String mailServerPort = "25";

	// Socket I/O timeout value in milliseconds. Default is infinite timeout
	private String timeout;

	// 邮件发送者的地址
	private String fromAddress;

	// 邮件接收者的地址
	private String toAddress;

	// 登陆邮件发送服务器的用户名和密码
	private String userName;

	private String password;

	// 是否需要身份验证
	private boolean validate = false;

	// 邮件主题
	private String subject;

	// 邮件的文本内容
	private String content;

	// 邮件附件的文件名
	private String[] attachFileNames;

	// 发送是否成功
	private String isSuccess = "1";

	// 失败原因
	private String failure = "";

	// 发送所用时间
	private String useTime = "";

	private String proxySet = "";

	private String socksProxyHost = "";

	private String socksProxyPort = "";

	private String proxyHost = "";

	private String proxyPort = "";

	// Socket connection timeout value in milliseconds. Default is infinite
	// timeout
	private String connectiontimeout = "";

	public String getFailure()
	{
		return failure;
	}

	public void setFailure(String failure)
	{
		this.failure = failure;
	}

	public String getIsSuccess()
	{
		return isSuccess;
	}

	public void setIsSuccess(String isSuccess)
	{
		this.isSuccess = isSuccess;
	}

	public String getUseTime()
	{
		return useTime;
	}

	public void setUseTime(String useTime)
	{
		this.useTime = useTime;
	}

	// 获得邮件会话属性
	public Properties getProperties()
	{
		Properties p = new Properties();
		p.put("mail.smtp.host", this.mailServerHost);
		p.put("mail.smtp.port", this.mailServerPort);
		p.put("mail.smtp.auth", validate ? "true" : "false");
		// 发送timeout
		p.put("mail.pop3.connectiontimeout", this.connectiontimeout);
		p.put("mail.smtp.timeout", this.timeout);

		// 代理设置
		p.put("proxySet", this.proxySet);
		p.put("socksProxyHost", this.socksProxyHost);
		p.put("socksProxyPort", this.socksProxyPort);

		p.put("proxyHost", this.proxyHost);
		p.put("proxyPort", this.proxyPort);

		return p;
	}

	public String getMailServerHost()
	{
		return mailServerHost;
	}

	public void setMailServerHost(String mailServerHost)
	{
		this.mailServerHost = mailServerHost;
	}

	public String getMailServerPort()
	{
		return mailServerPort;
	}

	public void setMailServerPort(String mailServerPort)
	{
		this.mailServerPort = mailServerPort;
	}

	public String[] getAttachFileNames()
	{
		return attachFileNames;
	}

	public void setAttachFileNames(String[] fileNames)
	{
		this.attachFileNames = fileNames;
	}

	public String getFromAddress()
	{
		return fromAddress;
	}

	public void setFromAddress(String fromAddress)
	{
		this.fromAddress = fromAddress;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getToAddress()
	{
		return toAddress;
	}

	public void setToAddress(String toAddress)
	{
		this.toAddress = toAddress;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getSubject()
	{
		return subject;
	}

	public void setSubject(String subject)
	{
		this.subject = subject;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String textContent)
	{
		this.content = textContent;
	}

	public String getTimeout()
	{
		return timeout;
	}

	public void setTimeout(String timeout)
	{
		this.timeout = timeout;
	}

	public String getConnectiontimeout()
	{
		return connectiontimeout;
	}

	public void setConnectiontimeout(String connectiontimeout)
	{
		this.connectiontimeout = connectiontimeout;
	}

	public String getSocksProxyHost()
	{
		return socksProxyHost;
	}

	public void setSocksProxyHost(String socksProxyHost)
	{
		this.socksProxyHost = socksProxyHost;
	}

	public String getSocksProxyPort()
	{
		return socksProxyPort;
	}

	public void setSocksProxyPort(String socksProxyPort)
	{
		this.socksProxyPort = socksProxyPort;
	}

	public String getProxyHost()
	{
		return proxyHost;
	}

	public void setProxyHost(String proxyHost)
	{
		this.proxyHost = proxyHost;
	}

	public String getProxyPort()
	{
		return proxyPort;
	}

	public void setProxyPort(String proxyPort)
	{
		this.proxyPort = proxyPort;
	}

	public String getProxySet()
	{
		return proxySet;
	}

	public void setProxySet(String proxySet)
	{
		this.proxySet = proxySet;
	}

	public boolean isValidate()
	{
		return validate;
	}

	public void setValidate(boolean validate)
	{
		this.validate = validate;
	}

}

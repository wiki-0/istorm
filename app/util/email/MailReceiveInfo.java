package util.email;

import java.util.Properties;

public class MailReceiveInfo {
	
    // 发送邮件的服务器的IP    
    private String host=null; 
    // 接受邮件的服务器的端口
    private int port = 110; 
    // 接受邮件的服务器的端口
    private String user=null;   
    private String pwd=null; 
    // Socket connection timeout value in milliseconds. Default is infinite timeout
    private String connectiontimeout=null;   
    
    //发送是否成功
    private String isSuccess="1";
    //失败原因
    private String failure="";
    //发送所用时间
    private String useTime ="";
    //Socket I/O timeout value in milliseconds. Default is infinite timeout
    private String timeout ="";
    
    public String getTimeout() {
		return timeout;
	}


	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}


	//获得邮件会话属性
    public Properties getProperties(){    
      Properties p = new Properties();    
//      p.put("mail.pop3.host", this.host);    
//      p.put("mail.pop3.port", this.port);    
//      p.put("mail.pop3.user", this.user);
    
      //发送timeout
      p.put("mail.pop3.connectiontimeout", this.connectiontimeout);
      p.put("mail.pop3.timeout", this.timeout);

      //代理设置
//      p.put( "http.proxySet", "true" ); 
//      p.put( "http.proxyHost", "172.16.9.13" ); 
//      p.put( "http.proxyPort", "80"); 
      
//      p.put("socksProxySet","true");   
//      p.put("socksProxyHost","10.100.0.3");   
//      p.put("socksProxyPort","1080");
      
      return p;    
    }


	public String getConnectiontimeout() {
		return connectiontimeout;
	}


	public void setConnectiontimeout(String connectiontimeout) {
		this.connectiontimeout = connectiontimeout;
	}


	public String getHost() {
		return host;
	}


	public void setHost(String host) {
		this.host = host;
	}





	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getPwd() {
		return pwd;
	}


	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	public String getFailure() {
		return failure;
	}


	public void setFailure(String failure) {
		this.failure = failure;
	}


	public String getIsSuccess() {
		return isSuccess;
	}


	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}


	public String getUseTime() {
		return useTime;
	}


	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}    
   

}

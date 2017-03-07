package controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import play.PlayPlugin;
import play.mvc.Controller;
import play.mvc.Http;
import util.CheckUtil;

public class WeixinServlet extends Controller{
	
	public static void doGet(){
		String signature = params.get("signature");
		String timestamp = params.get("timestamp");
		String nonce = params.get("nonce");
		String echostr = params.get("echostr");
		
		if(CheckUtil.checkSignature(signature, timestamp, nonce)){
			renderText(echostr);
			System.out.println("接入成功！！");
		}
		renderText("error");
	}
	
	public static void doPost(){
		
	}
	
	public static void index(){
		render();
	}
}

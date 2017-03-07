package controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.*;
import play.db.helper.JdbcHelper;
import play.db.jpa.JPA;
import play.mvc.Controller;
import util.Tools;
import util.quartz.CronDate;

public class DashBoard extends Controller 
{
	public static void index(){
		String username = session.get("username");
		TUser tUser = TUser.find("byT_USER_NAME", username).first();
		String displayName=tUser.T_USER_DISPLAY_NAME;
		List<Integer> list = new ArrayList<Integer>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		String nowDay = sdf.format(d);
//		System.out.println("nowDay="+nowDay);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		Date date = calendar.getTime();
		String lastDay = sdf.format(date);
//		System.out.println("lastDay="+lastDay);
		String s = "";
		List<TResult> resultlist = null;
		ResultSet results = null;
		if (tUser != null) {
			if("admin".equals(tUser))
			{
				resultlist = TResult.find( "select t from TResult t where t.T_RESULT_TIME between '" + lastDay + "' and '" + nowDay + "'" ).fetch();
				results = play.db.DB.executeQuery("select count(*) from t_result where T_RESULT_TIME between '" + lastDay + "' and '" + nowDay + "' and T_USER_ID="+tUser.id);
			} else {
				resultlist = TResult.find( "select t from TResult t where t.T_RESULT_TIME between '" + lastDay + "' and '" + nowDay + "' and t.JOB.CREATE.id = "+tUser.id).fetch();
				results = play.db.DB.executeQuery("select count(*) from t_result where T_RESULT_TIME between '" + lastDay + "' and '" + nowDay + "' and T_USER_ID="+tUser.id);
			}
		}
		else {
			try {
				Secure.login();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		int rajob = 0;
		int alarmnumlOne = 0;
		int alarmnumlTwo = 0;
		int alarmnumlthree = 0;
		int failnum = 0;
		int successnum = 0;
		int errornum = 0;
		int sum = 0;
		int percentage = 0;
		
		if(resultlist!=null && !"".equals(resultlist)){
			rajob = resultlist.size();
//			System.out.println("rajob="+rajob);
			for (TResult result : resultlist) {
				if ("OK".equals(result.T_RESULT_STATUS)) {
					successnum++;
					if ("1".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnumlOne++;
					} else if ("2".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnumlTwo++;
					} else if ("3".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnumlthree++;
					}
				} else {
					errornum++;
				}
			}
//			System.out.println("successnum="+successnum);
			if(rajob!=0)
			{
				double dou = (alarmnumlOne+alarmnumlTwo+alarmnumlthree+errornum)*100/rajob;
				percentage = (int)dou;
//				System.out.println("percentage="+percentage);
			}
			else {
				percentage=0;
			}
		}
		
		try{
			if(results!=null && !"".equals(results)){
				results.last();
				int row = results.getRow();
				sum = results.getInt(row);
				System.out.println("sum="+sum);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		list.add(rajob);//巡检任务总数
		list.add(alarmnumlOne);//严重告警个数
		list.add(failnum);//巡检失败数
		list.add(successnum);//巡检成功数
		list.add(sum);//发布给我的
		list.add(percentage);//巡检失败百分比
		SimpleDateFormat sdfyear = new SimpleDateFormat("yyyy");
		String year = sdfyear.format(d);
		String barname = year+"年月份严重告警统计";
		
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = format.format(d);
		//周起始时间
		calendar.setTime(d);
		calendar.add(Calendar.DAY_OF_YEAR, -6);
		Date lastdate = calendar.getTime();
		String lastWeek = sdf.format(lastdate);
//		System.out.println("lastWeek="+lastWeek);
		//月起始时间
		calendar.setTime(d);
		calendar.add(Calendar.DAY_OF_YEAR, -30);
		Date lastd = calendar.getTime();
		String lastMonth = sdf.format(lastd);
		StringBuffer sql = new StringBuffer();
		List<HashMap<String,String>> listmap = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = null;
		sql.append("SELECT id,T_NODE_TYPE_NAME from t_node_type where id IN(select tn.T_TYPE_ID from t_res_type2job tj,t_res_type2node tn where tj.T_TYPE_ID=tn.T_TYPE_ID)");
		ResultSet obj = play.db.DB.executeQuery(sql.toString());
		try {
			while(obj.next()){
				map = new HashMap<String,String>();
				map.put("name", obj.getString(2));
				StringBuffer hqlday = new StringBuffer();
				StringBuffer firstday = new StringBuffer();
				StringBuffer firstweek = new StringBuffer();
				StringBuffer firstmonth = new StringBuffer();
				//日   
				hqlday.append("select count(*) from t_result where T_TNODETYPE_ID="+obj.getLong(1)+" and T_RESULT_STATUS='ERROR' and T_RESULT_TIME like '"+nowTime+"%'");
				firstday.append("select count(*) from t_result where T_RESULT_ALARM_LEVEL='1' and T_TNODETYPE_ID="+obj.getLong(1)+" and T_RESULT_TIME like '"+nowTime+"%'");
				ResultSet reday = play.db.DB.executeQuery(hqlday.toString());
				ResultSet firstalarmday = play.db.DB.executeQuery(firstday.toString());
				firstalarmday.last();
				int severityday = firstalarmday.getInt(1);
				map.put("severityday", severityday+"");
				reday.last();
				int sumErrorDay = reday.getInt(1);
				map.put("sumErrorDay", sumErrorDay+"");
				//周 
				String percentweek =  errorWeek(obj.getLong(1),nowTime);
				firstweek.append("select count(*) from t_result where T_RESULT_ALARM_LEVEL='1' and T_TNODETYPE_ID="+obj.getLong(1)+" and date(T_RESULT_TIME) between date('"+lastWeek+"') and date('"+nowDay+"')");
//				System.out.println("hqlweek.toString() = " + hqlweek.toString());
				ResultSet firstalarmweek = play.db.DB.executeQuery(firstweek.toString());
				firstalarmweek.last();
				int severityweek = firstalarmweek.getInt(1);
//				System.out.println("severityweek"+severityweek);
				map.put("severityweek", severityweek+"");
				map.put("percentweek", percentweek);
				//月
				List<String> li = errorMonth(obj.getLong(1),nowTime);
//				System.out.println(li.get(0));
				firstmonth.append("select count(*) from t_result where T_TNODETYPE_ID="+obj.getLong(1)+" and T_RESULT_TIME between date('"+lastMonth+"') and date('"+nowDay+"')");
				ResultSet firstalarmmonth = play.db.DB.executeQuery(firstmonth.toString());
				firstalarmmonth.last();
				int severitymonth = firstalarmmonth.getInt(1);
				map.put("severitymonth", severitymonth+"");
				map.put("percentmonthup", li.get(0));
				map.put("percentmonthdown", li.get(1));
				listmap.add(map);
			}
//			System.out.println("listmap的长度为"+listmap.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		render("/DashBoard/index.html",list,listmap,displayName,barname);
	}

	//设备类型统计饼状图
	public static void queryPieData()
	{
		int x86 = 0;
		int aix = 0;
		int vmWare = 0;
		int hyperv = 0;
		int switchNode = 0;
		int controller = 0;
		List<TNode> tNodes = TNode.find("").fetch();
//		System.out.println("设备长度"+tNodes.size());
		if(tNodes!=null && tNodes.size()>0)
		{
			for(TNode tNode:tNodes)
			{
				if(tNode.T_NODE_DEVICETYPE!=null)
				{
					if(tNode.T_NODE_DEVICETYPE.equals("x86物理机"))
					{
						x86+=1;
					}
					else if(tNode.T_NODE_DEVICETYPE.equals("小型机"))
					{
						aix+=1;
					}
					else if(tNode.T_NODE_DEVICETYPE.equals("VMWare"))
					{
						vmWare+=1;
					}
					else if(tNode.T_NODE_DEVICETYPE.equals("Hyper-V"))
					{
						hyperv+=1;
					}
					else if(tNode.T_NODE_DEVICETYPE.equals("交换机"))
					{
						switchNode+=1;
					}
					else if(tNode.T_NODE_DEVICETYPE.equals("带外管理卡"))
					{
						controller+=1;
					}
				}
			}
		}
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		map.put("x86", x86);
		map.put("aix", aix);
		map.put("vmWare", vmWare);
		map.put("hyperv", hyperv);
		map.put("switchNode", switchNode);
		map.put("controller", controller);
		renderJSON(map);
	}
	
	//折线图
	public static void patrol() {
//		String username = session.get("username");
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String nowTime = sdf.format(new Date());
		Date date = new Date();
		// TUser user =
		// TUser.find("select t from TUser t where t.T_USER_NAME=admin").first();
		Map<String, Object> outmap = new TreeMap<String, Object>();
		HashMap<String, Integer> aixSuml1 = new HashMap<String, Integer>();
		HashMap<String, Integer> aixSuml2 = new HashMap<String, Integer>();
		HashMap<String, Integer> aixSuml3 = new HashMap<String, Integer>();
		HashMap<String, Integer> aixSuml4 = new HashMap<String, Integer>();
		List<TResult> resultlist = null;
		int jobnum = 0;
		String[] s = new String[7];
		Calendar calendar = Calendar.getInstance();
		for (int i = 6; i >= 0; i--) {
			calendar.setTime(date);
			calendar.add(Calendar.DATE, -i);
			Date d = calendar.getTime();
			String dayTime = sdf.format(d);
			String time = format.format(d);
			s[6 - i] = dayTime;
			int alarmnuml1 = 0;
			int alarmnuml2 = 0;
			int alarmnuml3 = 0;
			int alarmnuml4 = 0;
			// if (user != null) {
			resultlist = TResult.find(
					"select t from TResult t where date(t.T_RESULT_TIME) = date('"
							+ time + "')").fetch();
			// }
			for (TResult result : resultlist) {
				if(result.T_RESULT_ALARM_LEVEL!=null)
				{
					if ("1".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnuml1++;
					} else if ("2".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnuml2++;
					} else if ("3".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnuml3++;
					} else if ("4".equals(result.T_RESULT_ALARM_LEVEL)) {
						alarmnuml4++;
					}
				}
				List<TJob> tjob = TJob.find("select t from TJob t where t.id="+result.id).fetch();
				int j = tjob.size();
				jobnum += i;
			}
			aixSuml1.put(dayTime, alarmnuml1);
			aixSuml2.put(dayTime, alarmnuml2);
			aixSuml3.put(dayTime, alarmnuml3);
			aixSuml4.put(dayTime, alarmnuml4);
		}
		String aixDayData = "[\'" + s[0] + "\',\'" + s[1] + "\',\'" + s[2]
				+ "\',\'" + s[3] + "\',\'" + s[4] + "\',\'" + s[5] + "\',\'"
				+ s[6] + "\']";
//		System.out.println(aixDayData);
		outmap.put("aixDayData", aixDayData);
		String aixSumData = "[" + aixSuml1.get(s[0]) + "," + aixSuml1.get(s[1])
				+ "," + aixSuml1.get(s[2]) + "," + aixSuml1.get(s[3]) + ","
				+ aixSuml1.get(s[4]) + "," + aixSuml1.get(s[5]) + ","
				+ aixSuml1.get(s[6]) + "]";
		String backupSumData = "[" + aixSuml2.get(s[0]) + ","
				+ aixSuml2.get(s[1]) + "," + aixSuml2.get(s[2]) + ","
				+ aixSuml2.get(s[3]) + "," + aixSuml2.get(s[4]) + ","
				+ aixSuml2.get(s[5]) + "," + aixSuml2.get(s[6]) + "]";
		String storageSumData = "[" + aixSuml3.get(s[0]) + ","
				+ aixSuml3.get(s[1]) + "," + aixSuml3.get(s[2]) + ","
				+ aixSuml3.get(s[3]) + "," + aixSuml3.get(s[4]) + ","
				+ aixSuml3.get(s[5]) + "," + aixSuml3.get(s[6]) + "]";
		String switchSumData = "[" + aixSuml4.get(s[0]) + ","
				+ aixSuml4.get(s[1]) + "," + aixSuml4.get(s[2]) + ","
				+ aixSuml4.get(s[3]) + "," + aixSuml4.get(s[4]) + ","
				+ aixSuml4.get(s[5]) + "," + aixSuml4.get(s[6]) + "]";
//		System.out.println(aixSumData);
		outmap.put("aixSumData", aixSumData);
		outmap.put("backupSumData", backupSumData);
		outmap.put("storageSumData", storageSumData);
		outmap.put("switchSumData", switchSumData);
		renderJSON(outmap);
	}

	//失败率饼状图
	public static void health() {
//		String username = session.get("username");
//		TUser user = TUser.find(
//				"select t from TUser t where t.T_USER_NAME='" + username + "'")
//				.first();
//		Long id = user.id;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = new Date();
		String nowDay = sdf.format(d);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		calendar.add(Calendar.HOUR_OF_DAY, -24);
		Date date = calendar.getTime();
		String lastDay = sdf.format(date);
		List<TResult> tResultList = TResult.find(
				"select t from TResult t where date(t.T_RESULT_TIME) between date('"
						+ lastDay + "') and date('" + nowDay
						+ "')").fetch();
		int oklen = 0;
		int alllen = 0;
		int i = 0;
//		System.out.println(tResultList.size());
		if (tResultList != null && "".equals(tResultList)) {
			for (TResult result : tResultList) {
				if ("OK".equals(result.T_RESULT_ALARM_STATUS)) {
					oklen++;
				}
				alllen++;
			}
//			System.out.println(oklen+","+alllen);
			i = ((alllen-oklen) / alllen) * 100;
//			System.out.println(i);
		}
//		System.out.println(i);
		renderJSON(i);
	}

	public static void taskJSON() throws ParseException {
		String _start=params.get("start");
		String _end=params.get("end");
		JsonArray arr=new JsonArray();
		JsonObject obj;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String startTime = format.format(Tools.getDate(Long.parseLong(_start)));
		String endTime = format.format(Tools.getDate(Long.parseLong(_end)));
		Date date = Tools.getDate(Long.parseLong(_start));
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
		//巡检成功
		Map<String, Integer> sucMap = new HashMap<>();
		//巡检失败
		Map<String, Integer> errMap = new HashMap<>();
		//未巡检
		Map<String, Integer> unMap = new HashMap<>();
		List<TJob> tJobs = TJob.findAll();
		for (TJob job : tJobs) {
			//任务创建者
			if (job.CREATE == user && job.tNodeTypes.size() > 0) {
				String schedules = job.T_JOB_SCHEDULES;
				// 1.查询 startTime 到 当前时间 巡检结果 分析任务调度
				List<TResult> tResults = TResult.find("select t from TResult t where t.JOB.id = "+job.id+" and t.T_RESULT_ALARM_STATUS = 1 and t.T_RESULT_TIME between '"+startTime+"' and '"+endTime+"' ORDER BY t.T_RESULT_TIME asc").fetch();
				if (tResults != null && tResults.size() >0){
					Date tmpTime =  tResults.get(0).T_RESULT_TIME;
					Boolean flag = true;
					for (int i = 0; i < tResults.size(); i++) {
						if (tResults.get(i).T_RESULT_ALARM_LEVEL != null){
							if (!tResults.get(i).T_RESULT_ALARM_LEVEL.equals("0")){
								flag = false;
							}
						}
						if (tResults.get(i).T_RESULT_STATUS.equals("ERROR")){
							flag = false;
						}
						//默认50分钟内巡检结果为同一个任务
						if (tResults.get(i).T_RESULT_TIME.getTime() > tmpTime.getTime() + 50*60*1000 || i== tResults.size()-1){
							String afterTime = new SimpleDateFormat("yyyy-MM-dd").format(tResults.get(i).T_RESULT_TIME);
							if (flag){
								if (sucMap.get(afterTime)==null){
									sucMap.put(afterTime,1);
								}else {
									sucMap.put(afterTime,sucMap.get(afterTime)+1);
								}
							}else {
								if (errMap.get(afterTime)==null){
									errMap.put(afterTime,1);
								}else {
									errMap.put(afterTime,errMap.get(afterTime)+1);
								}
							}
							flag = true;
							tmpTime = tResults.get(i).T_RESULT_TIME;
						}
					}
				}

				//2.当前时间以后的 采集时间 未执行的任务 蓝色
				List<Date> results = CronDate.analyzeCron(schedules, Tools.getDate(Long.parseLong(_start)), Tools.getDate(Long.parseLong(_end)));
				String afterTime = "";
				int total = 0;
				for (Date d : results) {
					String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(d);
//					System.out.println("now "+nowTime +"after "+afterTime);
					if (nowTime.equals(afterTime)) {
						total += 1;
					} else {
						if (total > 0 && !afterTime.equals("")) {
							if (unMap.get(afterTime)==null){
								unMap.put(afterTime,total+1);
							}else {
								unMap.put(afterTime,unMap.get(afterTime)+total+1);
							}
						}
						total = 0;
					}
					afterTime = nowTime;
				}
				c.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		for (Map.Entry<String, Integer> entry : sucMap.entrySet()) {
			obj = new JsonObject();
			obj.addProperty("title","成功数：" + entry.getValue());
			obj.addProperty("start", entry.getKey());
			obj.addProperty("allDay", true);
			obj.addProperty("color", "green");
			arr.add(obj);
		}
		for (Map.Entry<String, Integer> entry : errMap.entrySet()) {
			obj = new JsonObject();
			obj.addProperty("title","失败数：" + entry.getValue());
			obj.addProperty("start", entry.getKey());
			obj.addProperty("allDay", true);
			obj.addProperty("color", "red");
			arr.add(obj);
		}
		for (Map.Entry<String, Integer> entry : unMap.entrySet()) {
			obj = new JsonObject();
			obj.addProperty("title", "等待数：" + entry.getValue());
			obj.addProperty("start", entry.getKey());
			obj.addProperty("allDay", true);
			obj.addProperty("color", "skyblue");
			arr.add(obj);
		}
		renderText(arr.toString());
	}
	//显示 任务调度详细
	public static void getJobDeatil() {
		//	String id = params.get("id");
		String time = params.get("start");
		String color = params.get("color");
		if (time != null &&!time.equals("")) {
			TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String startTime = format.format(Tools.getDate(Long.parseLong(time)));
			StringBuffer sb = new StringBuffer();
			sb.append("<div class='row'><div class='col-sm-12'><table class='table table-bordered'><tbody>");
			sb.append("<tr><td width='25%'>巡检开始时间：</td>");
			sb.append("<td width='25%'>任务名称：</td>");
			sb.append("<td width='25%'>巡检状态：</td>");
			sb.append("<td width='25%'>告警个数：</td></tr>");
			List<TJob> tJobs = TJob.findAll();
			for (TJob job : tJobs) {
				//任务创建者
				if (job.CREATE == user && job.tNodeTypes.size() > 0) {
					String schedules = job.T_JOB_SCHEDULES;
					if (color.equals("skyblue")){
						List<Date> results = null;
						try {
							results = CronDate.analyzeToday(schedules,startTime);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						for (Date d : results) {
							String nowTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(d);
							sb.append("<tr><td width='25%'>"+nowTime+"</td>");
							sb.append("<td width='25%'>"+job.T_JOB_NAME+"</td>");
							sb.append("<td width='25%'><img src='/new_public/img/alarm/alert_normal.png' width='18px' height='18px'/> 未执行</td>");
							sb.append("<td width='25%'>0</td></tr>");
						}
					}
					//查询当天巡检结果 分析任务调度
					List<TResult> tResults = TResult.find("select t from TResult t where t.JOB.id = " + job.id + " and t.T_RESULT_ALARM_STATUS = 1 and t.T_RESULT_TIME between '"+startTime+" 00:00:00' and '"+startTime+" 23:59:59' ORDER BY t.T_RESULT_TIME asc").fetch();
					if (tResults != null && tResults.size() > 0) {
						Date tmpTime = tResults.get(0).T_RESULT_TIME;
						int total = 0;
						for (int i = 0; i < tResults.size(); i++) {
//						String nowTime = new SimpleDateFormat("yyyy-MM-dd").format(tResults.get(i).T_RESULT_TIME);
							//统计告警个数
							if (tResults.get(i).T_RESULT_ALARM_LEVEL != null){
								if (!tResults.get(i).T_RESULT_ALARM_LEVEL.equals("0")){
									total = total + 1;
								}
							}else {
								if (tResults.get(i).T_RESULT_STATUS.equals("ERROR")){
									total = total + 1;
								}
							}
							//默认50分钟内巡检结果为同一个任务
							if (tResults.get(i).T_RESULT_TIME.getTime() > tmpTime.getTime() + 50*60*1000 || i== tResults.size()-1){
								String afterTime = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(tmpTime);
								if (color.equals("red") && total >0){
									sb.append("<tr><td width='25%'>"+afterTime+"</td>");
									sb.append("<td width='25%'>"+job.T_JOB_NAME+"</td>");
									sb.append("<td width='25%'><img src='/new_public/img/alarm/alert_critical.png' width='18px' height='18px'/> 异常</td>");
									sb.append("<td width='25%'>"+total+"</td></tr>");
								}
								if (color.equals("green") && total == 0){
									sb.append("<tr><td width='25%'>"+afterTime+"</td>");
									sb.append("<td width='25%'>"+job.T_JOB_NAME+"</td>");
									sb.append("<td width='25%'><img src='/new_public/img/alarm/alert_green.png' width='18px' height='18px'/> 正常</td>");
									sb.append("<td width='25%'>"+total+"</td></tr>");
								}
								tmpTime = tResults.get(i).T_RESULT_TIME;
								total = 0;
							}
						}
					}
				}
			}
			sb.append("</tbody></table></div></div>");
			renderText(sb.toString());
		}
		renderText("false");
	}
	
	public static void getOptionForce()
	{
		JsonArray arr=new JsonArray();
		JsonArray lineArr=new JsonArray();
		JsonObject lineObj;
		JsonObject obj = new JsonObject();
		obj.addProperty("category","0");
		obj.addProperty("name","服务器");
		obj.addProperty("value",5);
		arr.add(obj);
		List<TJob> tJobs = TJob.findAll();
		for(TJob tJob:tJobs )
		{
			if(tJob.tNodeTypes!=null && tJob.tNodeTypes.size()>0)
			{
				obj = new JsonObject();
				obj.addProperty("category","1");
				obj.addProperty("name",tJob.T_JOB_NAME);
				obj.addProperty("value",2);
				arr.add(obj);
				lineObj= new JsonObject();
				lineObj.addProperty("source",tJob.T_JOB_NAME);
				lineObj.addProperty("target","服务器");
				lineObj.addProperty("weight",1);
				lineArr.add(lineObj);
			}
		}
//		obj = new JsonObject();
//		obj.addProperty("category","1");
//		obj.addProperty("name","任务1");
//		obj.addProperty("value",2);
//		arr.add(obj);
//		lineObj= new JsonObject();
//		lineObj.addProperty("source","任务1");
//		lineObj.addProperty("target","服务器");
//		lineObj.addProperty("weight",1);
//		lineArr.add(lineObj);
		JsonObject forceObj = new JsonObject();
		forceObj.add("dataNodes",arr);
		forceObj.add("dataLinks",lineArr);
		renderJSON(forceObj);
	}
	
	//两天时间差(MM/dd/yyyy)
	public static int daysBetween(String smdate,String bdate) throws ParseException{  
        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    } 
	
	//两天时间差(yyyy-MM-dd)
	public static int daysBetween(Date smdate,Date bdate) throws ParseException    
    {    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    } 

	//柱状图
	public static void columnar() {
//		String username = session.get("username");
//		TUser user = TUser.find(
//				"select t from TUser t where t.T_USER_NAME='" + username + "'")
//				.first();
		Map<String, Object> outmap = new TreeMap<String, Object>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		String nowDay = sdf.format(d);
		Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		StringBuffer sql = new StringBuffer();
		StringBuffer sqlerror = new StringBuffer();
//		if (leapyear(year)) {
//			System.out.println("是闰年");
//		} else {
//			System.out.println("是平年");
//		}
		sql.append("SELECT '1月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-01-01') AND date('"+year+"-01-31')");
		sql.append(" union ");
		sql.append("SELECT '2月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-02-01') AND date('"+year+"-02-29')");
		sql.append(" union ");
		sql.append("SELECT '3月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-03-01') AND date('"+year+"-03-31')");
		sql.append(" union ");
		sql.append("SELECT '4月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-04-01') AND date('"+year+"-04-30')");
		sql.append(" union ");
		sql.append("SELECT '5月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-05-01') AND date('"+year+"-05-31')");
		sql.append(" union ");
		sql.append("SELECT '6月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-06-01') AND date('"+year+"-06-30')");
		sql.append(" union ");
		sql.append("SELECT '7月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-07-01') AND date('"+year+"-07-31')");
		sql.append(" union ");
		sql.append("SELECT '8月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-08-01') AND date('"+year+"-08-31')");
		sql.append(" union ");
		sql.append("SELECT '9月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-09-01') AND date('"+year+"-09-30')");
		sql.append(" union ");
		sql.append("SELECT '10月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-10-01') AND date('"+year+"-10-31')");
		sql.append(" union ");
		sql.append("SELECT '11月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-11-01') AND date('"+year+"-11-30')");
		sql.append(" union ");
		sql.append("SELECT '12月',COUNT(*) from T_RESULT  WHERE date(T_RESULT_TIME) BETWEEN date('"+year+"-12-01') AND date('"+year+"-12-31')");
		
		sqlerror.append("SELECT '1月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-01-01') AND date('"+year+"-01-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '2月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-02-01') AND date('"+year+"-02-29')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '3月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-03-01') AND date('"+year+"-03-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '4月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-04-01') AND date('"+year+"-04-30')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '5月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-05-01') AND date('"+year+"-05-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '6月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-06-01') AND date('"+year+"-06-30')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '7月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-07-01') AND date('"+year+"-07-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '8月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-08-01') AND date('"+year+"-08-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '9月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-09-01') AND date('"+year+"-09-30')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '10月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-10-01') AND date('"+year+"-10-31')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '11月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-11-01') AND date('"+year+"-11-30')");
		sqlerror.append(" union ");
		sqlerror.append("SELECT '12月',COUNT(*) from T_RESULT  WHERE T_RESULT_ALARM_LEVEL='1' AND date(T_RESULT_TIME) BETWEEN date('"+year+"-12-01') AND date('"+year+"-12-31')");

		ResultSet obj = play.db.DB.executeQuery(sql.toString());
		ResultSet objerror = play.db.DB.executeQuery(sqlerror.toString());
		// JPA.em().createQuery(sql.toString()).getResultList();
		String xstr = "[";
		String ystr = "[";
		String ystrerror = "[";
		try {
			while (obj.next()) {
				if (obj.isLast()) {
					xstr += "\'" + obj.getString(1) + "\']";
					ystr += "\'" + obj.getInt(2) + "\']";
				} else {
					xstr += "\'" + obj.getString(1) + "\',";
					ystr += "\'" + obj.getInt(2) + "\',";
				}
			}
			while (objerror.next()) {
				if (objerror.isLast()) {
					ystrerror += "\'" + objerror.getString(2) + "\']";
				} else {
					ystrerror += "\'" + objerror.getString(2) + "\',";
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		System.out.println(xstr);
//		System.out.println(ystr);
//		System.out.println(ystrerror);
		outmap.put("xstr", xstr);
		outmap.put("ystr", ystr);
		outmap.put("ystrerror", ystrerror);
		renderJSON(outmap);
	}

	// 判断平年闰年
	public static boolean leapyear(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}

	//漏斗图
	public static void funnel(){
		Map<String, Object> outmap = new TreeMap<String, Object>();
//		String username = session.get("username");
//		TUser user = TUser.find(
//				"select t from TUser t where t.T_USER_NAME='" + username + "'")
//				.first();
//		Long id = user.id;
		StringBuffer sql = new StringBuffer();
		sql.append("select count(*) as NUMBER,n.T_NODE_NAME from");
		sql.append(" TResult t,TNode n");
		sql.append(" where ");
		sql.append("t.T_RESULT_ALARM_LEVEL='1' and");
		sql.append(" t.NODE=n.id");
		sql.append(" group by ");
		sql.append("n.T_NODE_NAME");
		sql.append(" order by ");
		sql.append("NUMBER");
		sql.append(" desc ");
//		sql.append("limit 5");
//		ResultSet obj = play.db.DB.executeQuery(sql.toString());
		List<Object[]> listObj = JPA.em().createQuery(sql.toString()).getResultList();
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> firstMap = new HashMap<String, String>();
		firstMap.put("name", "无设备0");
		firstMap.put("value", "100");
		firstMap.put("hv", "0");
		list.add(firstMap);
		HashMap<String, String> secondMap = new HashMap<String, String>();
		secondMap.put("name", "无设备1");
		secondMap.put("value", "80");
		secondMap.put("hv", "0");
		list.add(secondMap);
		HashMap<String, String> thirdMap = new HashMap<String, String>();
		thirdMap.put("name", "无设备2");
		thirdMap.put("value", "60");
		thirdMap.put("hv", "0");
		list.add(thirdMap);
		HashMap<String, String> fourthMap = new HashMap<String, String>();
		fourthMap.put("name", "无设备3");
		fourthMap.put("value", "40");
		fourthMap.put("hv", "0");
		list.add(fourthMap);
		HashMap<String, String> fifthMap = new HashMap<String, String>();
		fifthMap.put("name", "无设备4");
		fifthMap.put("value", "20");
		fifthMap.put("hv", "0");
		list.add(fifthMap);
		String str="[\'无设备0\',\'无设备1\',\'无设备2\',\'无设备3\',\'无设备4\']";
		for(int i=0;i<listObj.size();i++)
		{
			if(i==0)
			{
				firstMap.put("name", (String) listObj.get(i)[1]);
				firstMap.put("value", "100");
				firstMap.put("hv", listObj.get(i)[0] + "");
				str=str.replaceAll("无设备0", (String) listObj.get(i)[1] );
			}
			else if(i==1)
			{
				secondMap.put("name", (String) listObj.get(i)[1]);
				secondMap.put("value", "80");
				secondMap.put("hv", listObj.get(i)[0] + "");
				str=str.replaceAll("无设备1", (String) listObj.get(i)[1] );
			}
			else if(i==2)
			{
				thirdMap.put("name", (String) listObj.get(i)[1]);
				thirdMap.put("value",  "60");
				secondMap.put("hv", listObj.get(i)[0] + "");
				str=str.replaceAll("无设备2", (String) listObj.get(i)[1] );
			}
			else if(i==3)
			{
				fourthMap.put("name", (String) listObj.get(i)[1]);
				fourthMap.put("value",  "40");
				fourthMap.put("hv", listObj.get(i)[0] + "");
				str=str.replaceAll("无设备3", (String) listObj.get(i)[1] );
			}
			else if(i==4)
			{
				fifthMap.put("name", (String) listObj.get(i)[1]);
				fifthMap.put("value",  "20");
				fifthMap.put("hv", listObj.get(i)[0] + "");
				str=str.replaceAll("无设备4", (String) listObj.get(i)[1] );
			}
		}
		outmap.put("str", str);
		outmap.put("dataList", list);
//		System.out.println(outmap);
		renderJSON(outmap);
	}
	
	//storage存储容量监控
	public static void capacity(){
		Map<String, Object> outmap = new TreeMap<String, Object>();
		int used = 0;
		int unused = 0;
		int sum = 0;
		StringBuffer sql = new StringBuffer();
		ResultSet rs = null;
		TParams tp = TParams.find("select t from TParams t where t.T_DRM_PARAMS_NAME='vCenter'").first();
		if(tp!=null && !"".equals(tp)){
			if("1".equals(tp.T_DRM_PARAMS_VALUE) && "1".equals(tp.T_DRM_PARAMS_ENABLE)){//判断是否启动vCenter巡检
				System.out.println("-----------------VCenter已经启动-----------------");
				sql.append("select sum(capacity) as total,sum(free_sapce) as leisure from t_res_datastore");
				try {
					rs = JdbcHelper.execute(sql.toString());
					while(rs.next()){
						sum = rs.getInt("total");
						unused = rs.getInt("leisure");
						used = sum - unused;
						outmap.put("used", used);
						outmap.put("unused", unused);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					if (rs != null) {
						try {
							rs.close();
							rs = null;
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}
				}
			}
		}else{
			System.out.println("-----------------没有配置VCenter-----------------");
		}
		renderJSON(outmap);
	}
	
	//不用了
	public static void device(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date d = new Date();
		String nowDay = sdf.format(d);
		StringBuffer sql = new StringBuffer();
		List<HashMap<String,String>> listmap = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = null;
		sql.append("SELECT id,T_NODE_TYPE_NAME from t_node_type where id IN(select tn.T_TYPE_ID from t_res_type2job tj,t_res_type2node tn where tj.T_TYPE_ID=tn.T_TYPE_ID)");
		ResultSet obj = play.db.DB.executeQuery(sql.toString());
		try {
			int sumpartol = 0;
			while(obj.next()){
				
				map = new HashMap<String,String>();
				sumpartol++;
				map.put("name", obj.getString(2));
				listmap.add(map);
				StringBuffer hql = new StringBuffer();
				hql.append("select count(*) from t_result where T_TNODETYPE_ID="+obj.getLong(1)+" and T_RESULT_TIME='"+nowDay+"'");
				ResultSet re = play.db.DB.executeQuery(hql.toString());
				re.last();
				int sumfinish = re.getRow();
				int percent = sumfinish/sumpartol*100;
				map.put("percent", percent+"");
				listmap.add(map);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		render("/DashBoard/index.html",listmap);
	}

	//周巡检失败个数(左下角展示)
	public static String errorWeek(Long id, String nowTime){
		String str = "";
		String strWeek = "";
		StringBuffer ql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = null;
		try {
			Date d = sdf.parse(nowTime);
			for(int i=6;i>0;i--){
				calendar = Calendar.getInstance();
				calendar.setTime(d);
				calendar.add(Calendar.DAY_OF_YEAR, -i);
				Date day = calendar.getTime();
				String dayTime = sdf.format(day);
				ql.append("select '"+(7-i)+"',count(*) from t_result where T_TNODETYPE_ID="+id+" and T_RESULT_STATUS='ERROR' and T_RESULT_TIME like'"+dayTime+"%'");
				ql.append(" union ");
			}
			ql.append("select '"+7+"',count(*) from t_result where T_TNODETYPE_ID="+id+" and T_RESULT_STATUS='ERROR' and T_RESULT_TIME like'"+nowTime+"%'");
//			System.out.println("ql="+ql);
			ResultSet res = play.db.DB.executeQuery(ql.toString());
			while(res.next()){
				str += res.getString(2)+",";
			}
//			System.out.println("str="+str);
			int s = str.length();
			strWeek = str.substring(0,s-1);
//			System.out.println("strWeek="+strWeek);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return strWeek;
	}
	
	//月巡检失败个数(左下角展示)
	public static List<String> errorMonth(Long id, String nowTime){
		List<String> li = new ArrayList<String>();
		List<Date> list = new ArrayList<Date>();
		String str1 = "";//上半月字符串
		String str2 = "";//下半月字符串
		String strMonthBefore = "";
		String strMonthEnd = "";
		StringBuffer ql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date day = sdf.parse(nowTime);
			list = getAllTheDateOftheMonth(day);
			for(Date date : list){
				String dayTime = sdf.format(date);
//				System.out.println("dayTime:"+dayTime);
				if(list.get(list.size()-1).equals(date)){
					ql.append("select '"+dayTime+"',count(*) from t_result where T_TNODETYPE_ID="+id+" and T_RESULT_STATUS='ERROR' and T_RESULT_TIME like'"+dayTime+"%'");
				}else{
					ql.append("select '"+dayTime+"',count(*) from t_result where T_TNODETYPE_ID="+id+" and T_RESULT_STATUS='ERROR' and T_RESULT_TIME like'"+dayTime+"%'");
					ql.append(" union ");
				}
			}
//			System.out.println("月="+ql);
			ResultSet res = play.db.DB.executeQuery(ql.toString());
			
				while(res.next()){
					int len = res.getRow();
					if(len>15){
						str2+=res.getString(2)+",";
					}else{
						str1 +=res.getString(2)+",";
					}
				}
			int s1 = str1.length();
			strMonthBefore = str1.substring(0,s1-1);
//			strMonthBefore += "]";
			int s2 = str2.length();
			strMonthEnd = str2.substring(0,s2-1);
//			strMonthEnd += "]";
//			System.out.println(strMonthBefore);
//			System.out.println(strMonthEnd);
			li.add(strMonthBefore);//上半月
			li.add(strMonthEnd);//下半月
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return li;
	}
	
	//求当前月份的所有日期
	private static List<Date> getAllTheDateOftheMonth(Date date) {
		List<Date> list = new ArrayList<Date>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.DATE, 1);

		int month = cal.get(Calendar.MONTH);
		while (cal.get(Calendar.MONTH) == month) {
			list.add(cal.getTime());
			cal.add(Calendar.DATE, 1);
		}
		return list;
	}
}

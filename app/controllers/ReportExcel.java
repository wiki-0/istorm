package controllers;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.db.jpa.JPA;
import util.CommonUtil;
import util.DataTableSource;
import util.excel.ExcelInput;
import models.TJob;
import models.TMenu;
import models.TNode;
import models.TNodeType;
import models.TResult;
import models.TUser;

public class ReportExcel extends CRUD {

	//给前台传入的默认时间
	public static void index() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		Date endDate = new Date();
		calendar.add(Calendar.DAY_OF_MONTH, -6);
		Date startDate = calendar.getTime();
		String startTime = format.format(startDate);
		String endTime = format.format(endDate);
		
		
		//获取设备分层和任务
		List<String> listjob = new ArrayList<String>();
		TreeMap<String, Object> outmap = null;
		String equipmentstr = "[";
		String sss = "[";
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT id,T_NODE_TYPE_NAME from t_node_type where id IN(select tn.T_TYPE_ID from t_res_type2job tj,t_res_type2node tn where tj.T_TYPE_ID=tn.T_TYPE_ID)");
		ResultSet obj = play.db.DB.executeQuery(sql.toString());
		try {
			while(obj.next()){
				outmap = new TreeMap<String, Object>();
				if(obj.isLast()){
					equipmentstr += "\'"+obj.getString(2)+"\']";
				}else{
					equipmentstr += "\'"+obj.getString(2)+"\',";
				}
				
				ResultSet res = play.db.DB.executeQuery("select t.id,t.T_JOB_NAME from t_job t,t_res_type2job tb where t.id=tb.T_JOB_ID and tb.T_TYPE_ID="+obj.getLong(1));
//				System.out.println(res);
					while(res.next()){
						sss += "\'"+res.getString(2)+"\',";
					}
					int s = sss.length();
					String facility = sss.substring(0,s-1);
//					System.out.println(str);
					facility += "]";
					System.out.println("facility="+facility);
//					outmap.put(obj.getString(2), facility);
					listjob.add(facility);
					System.out.println(listjob);
			}
//			outmap.put("equipmentstr", equipmentstr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
//		TreeMap<String, Object> outmap = equipment();
//		String equipmentstr = "[\'www\',\'baidu\',\'com\',\'cn\']";
//		System.out.println(equipmentstr);
//		List<TMenu> tMuneList = TMenu
//				.find("select t.T_RESULT_ALARM_LEVEL,t.T_RESULT_TIME from TMenu t where t.T_MENU_TYPE='report' ")
//				.fetch();
		render( startTime, endTime,equipmentstr,listjob);
	}

	//获取echarts图表的数据
	public static void chartdata() throws Exception {
		Map<String, Object> outmap = new TreeMap<String, Object>();
		String startTime = params.get("startTime");
		String endTime = params.get("endTime");
		String jobType = params.get("jobType");
		String job = params.get("job");
//		System.out.println(jobType);
		String username = session.get("username");
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?",
				username).first();
		outmap = firstpr(outmap,startTime,endTime,jobType,job);
		outmap = secondpr(outmap,startTime,endTime,jobType,job);
		outmap = thirdpr(outmap,startTime,endTime,jobType,job);
		outmap = fourthpr(outmap,startTime,endTime,jobType,job);
//		System.out.println(outmap.size());
		renderJSON(outmap);
	}
	
	//一级告警
	public static Map<String,Object> firstpr( Map<String,Object> outmap,String startTime,String endTime,String jobType, String job) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String username = session.get("username");
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?",
				username).first();
		String firstSumData = "[";
		String firstDayData = "[";
		StringBuffer sql = new StringBuffer();
		sql.append("select t from TResult t");
		sql.append(" where date(t.T_RESULT_TIME) between date('" + startTime
			+ " 00:00:00') and date('" + endTime + " 23:59:59') and t.T_RESULT_ALARM_LEVEL='1'");
		//判断设备类型和设备
		if(!jobType.equals("请选择设备")){
			if(job.equals("全部")){
				TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
				sql.append(" and t.TNODETYPE="+nodeType.id);
			}else{
				TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
				sql.append( " and t.NODE="+tJob.id);
			}
		}
		//判断用户权限
		if (!username.equals("admin")) {
			sql.append(" and t.USER=" + user.id);
		}
		List<TResult> firstTResult = TResult.find(sql.toString()).fetch();
		if (firstTResult != null && firstTResult.size() > 0) {
			for (TResult tResult : firstTResult) {
				String patrolDay = format
						.format(tResult.T_RESULT_TIME);
				Integer dayInteger = map.get(patrolDay);
				if (dayInteger != null) {
					map.put(patrolDay, ++dayInteger);
				} else {
					map.put(patrolDay, 1);
				}
			}
		}
		Date startDate = format.parse(startTime);
		Date endDate = format.parse(endTime);
		int days = daysOfTwo(startDate, endDate);
		for (int i = days; i >= 0; i--) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			Date date = calendar.getTime();
			String dayTime = format.format(date);
			Integer aixCount = map.get(dayTime);
			if (aixCount == null || aixCount == 0) {
				firstSumData += "0,";
				firstDayData += "\'" + dayTime + "\',";
			} else {
				firstSumData += aixCount + ",";
				firstDayData += "\'" + dayTime + "\',";
			}
		}
		firstSumData = firstSumData.substring(0,
				firstSumData.length() - 1) + "]";
		firstDayData = firstDayData.substring(0,
				firstDayData.length() - 1) + "]";
//		System.out.println("firstSumData = " + firstSumData);
//		System.out.println("firstDayData = " + firstDayData);
		outmap.put("firstSumData", firstSumData);
		outmap.put("firstDayData", firstDayData);
		return outmap;
	}

	//二级告警
	public static Map<String,Object> secondpr(Map<String,Object> outmap,String startTime,String endTime,String jobType, String job) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String username = session.get("username");
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?",
				username).first();
		String secondeSumData = "[";
		String secondeDayData = "[";
		StringBuffer sql = new StringBuffer();
		sql.append("select t from TResult t");
		sql.append(" where date(t.T_RESULT_TIME) between date('" + startTime
				+ " 00:00:00') and date('" + endTime + " 23:59:59') and t.T_RESULT_ALARM_LEVEL='2'");
		//判断设备类型和设备
		if(!jobType.equals("请选择设备")){
			if(job.equals("全部")){
				TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
				sql.append(" and t.TNODETYPE="+nodeType.id);
			}else{
				TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
				sql.append( "and t.NODE="+tJob.id);
			}
		}
		//判断用户权限
		if (!username.equals("admin")) {
			sql.append(" and t.USER=" + user.id);
		}
		
		List<TResult> firstTResult = TResult.find(sql.toString()).fetch();
		if (firstTResult != null && firstTResult.size() > 0) {
			for (TResult tResult : firstTResult) {
				String patrolDay = format
						.format(tResult.T_RESULT_TIME);
				Integer dayInteger = map.get(patrolDay);
				if (dayInteger != null) {
					map.put(patrolDay, ++dayInteger);
				} else {
					map.put(patrolDay, 1);
				}
			}
		}
		Date startDate = format.parse(startTime);
		Date endDate = format.parse(endTime);
		int days = daysOfTwo(startDate, endDate);
		for (int i = days; i >= 0; i--) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			Date date = calendar.getTime();
			String dayTime = format.format(date);
			Integer aixCount = map.get(dayTime);
			if (aixCount == null || aixCount == 0) {
				secondeSumData += "0,";
				secondeDayData += "\'" + dayTime + "\',";
			} else {
				secondeSumData += aixCount + ",";
				secondeDayData += "\'" + dayTime + "\',";
			}
		}
		secondeSumData = secondeSumData.substring(0,
				secondeSumData.length() - 1) + "]";
		secondeDayData = secondeDayData.substring(0,
				secondeDayData.length() - 1) + "]";
//		System.out.println("secondeSumData = " + secondeSumData);
//		System.out.println("secondeDayData = " + secondeDayData);
		outmap.put("secondeSumData", secondeSumData);
		outmap.put("secondeDayData", secondeDayData);
		return outmap;
	}

	//三级告警
	public static Map<String,Object> thirdpr(Map<String,Object> outmap,String startTime,String endTime,String jobType, String job) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String username = session.get("username");
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?",
				username).first();
		String thirdSumData = "[";
		String thirdDayData = "[";
		StringBuffer sql = new StringBuffer();
		sql.append("select t from TResult t");
		sql.append(" where date(t.T_RESULT_TIME) between date('" + startTime
				+ " 00:00:00') and date('" + endTime + " 23:59:59') and t.T_RESULT_ALARM_LEVEL='3'");
		//判断设备类型和设备
		if(!jobType.equals("请选择设备")){
			if(job.equals("全部")){
				TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
				sql.append(" and t.TNODETYPE="+nodeType.id);
			}else{
				TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
				sql.append( "and t.NODE="+tJob.id);
			}
		}
		//判断用户权限
		if (!username.equals("admin")) {
			sql.append(" and t.USER=" + user.id);
		}
		
		List<TResult> firstTResult = TResult.find(sql.toString()).fetch();
		if (firstTResult != null && firstTResult.size() > 0) {
			for (TResult tResult : firstTResult) {
				String patrolDay = format
						.format(tResult.T_RESULT_TIME);
				Integer dayInteger = map.get(patrolDay);
				if (dayInteger != null) {
					map.put(patrolDay, ++dayInteger);
				} else {
					map.put(patrolDay, 1);
				}
			}
		}
		Date startDate = format.parse(startTime);
		Date endDate = format.parse(endTime);
		int days = daysOfTwo(startDate, endDate);
		for (int i = days; i >= 0; i--) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			Date date = calendar.getTime();
			String dayTime = format.format(date);
			Integer aixCount = map.get(dayTime);
			if (aixCount == null || aixCount == 0) {
				thirdSumData += "0,";
				thirdDayData += "\'" + dayTime + "\',";
			} else {
				thirdSumData += aixCount + ",";
				thirdDayData += "\'" + dayTime + "\',";
			}
		}
		thirdSumData = thirdSumData.substring(0,
				thirdSumData.length() - 1) + "]";
		thirdDayData = thirdDayData.substring(0,
				thirdDayData.length() - 1) + "]";
//		System.out.println("thirdSumData = " + thirdSumData);
//		System.out.println("thirdDayData = " + thirdDayData);
		outmap.put("thirdSumData", thirdSumData);
		outmap.put("thirdDayData", thirdDayData);
		return outmap;
	}

	//四级告警
	public static Map<String,Object> fourthpr(Map<String,Object> outmap,String startTime,String endTime,String jobType, String job) throws Exception{
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String username = session.get("username");
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		TUser user = TUser.find("select t from TUser t where t.T_USER_NAME=?",
				username).first();
		String fourthSumData = "[";
		String fourthDayData = "[";
		StringBuffer sql = new StringBuffer();
		sql.append("select t from TResult t");
		sql.append(" where date(t.T_RESULT_TIME) between date('" + startTime
				+ " 00:00:00') and date('" + endTime + " 23:59:59') and t.T_RESULT_ALARM_LEVEL='4'");
		//判断设备类型和设备
		if(!jobType.equals("请选择设备")){
			if(job.equals("全部")){
				TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
				sql.append(" and t.TNODETYPE="+nodeType.id);
			}else{
				TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
				sql.append( "and t.NODE="+tJob.id);
			}
		}
		//判断用户权限	
		if (!username.equals("admin")) {
			sql.append(" and t.USER=" + user.id);
		}
		
		List<TResult> firstTResult = TResult.find(sql.toString()).fetch();
		if (firstTResult != null && firstTResult.size() > 0) {
			for (TResult tResult : firstTResult) {
				String patrolDay = format
						.format(tResult.T_RESULT_TIME);
				Integer dayInteger = map.get(patrolDay);
				if (dayInteger != null) {
					map.put(patrolDay, ++dayInteger);
				} else {
					map.put(patrolDay, 1);
				}
			}
		}
		Date startDate = format.parse(startTime);
		Date endDate = format.parse(endTime);
		int days = daysOfTwo(startDate, endDate);
		for (int i = days; i >= 0; i--) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate);
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			Date date = calendar.getTime();
			String dayTime = format.format(date);
			Integer aixCount = map.get(dayTime);
			if (aixCount == null || aixCount == 0) {
				fourthSumData += "0,";
				fourthDayData += "\'" + dayTime + "\',";
			} else {
				fourthSumData += aixCount + ",";
				fourthDayData += "\'" + dayTime + "\',";
			}
		}
		fourthSumData = fourthSumData.substring(0,
				fourthSumData.length() - 1) + "]";
		fourthDayData = fourthDayData.substring(0,
				fourthDayData.length() - 1) + "]";
//		System.out.println("fourthSumData = " + fourthSumData);
//		System.out.println("fourthDayData = " + fourthDayData);
		outmap.put("fourthSumData", fourthSumData);
		outmap.put("fourthDayData", fourthDayData);
		return outmap;
	}
	
	//获取数据列表展示
	public static void getResultList(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String username = session.get("username");
//		System.out.println(username);
		String startTime = params.get("startTime");
		String endTime = params.get("endTime");
		String jobType = params.get("jobType");
		String job = params.get("job");
//		System.out.println(startTime+","+endTime+","+jobType+","+job);
		JsonArray arr = new JsonArray();
		JsonObject obj = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select t from TResult t where date(t.T_RESULT_TIME) between date('"+startTime+" 00:00:00') and date('"+endTime+" 23:59:59') and t.T_RESULT_ALARM_STATUS='1'");
		if(!"admin".equals(username)){
			TUser tuser = TUser.find("from TUser tu where tu.T_USER_NAME='"+username+"'").first();
			sql.append(" and t.USER="+tuser.id);
		}
		if(!"请选择设备".equals(jobType)){
			if("全部".equals(job)){
				TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
				sql.append(" and t.TNODETYPE="+nodeType.id);
			}else{
				TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
				sql.append(" and t.NODE="+tJob.id);
			}
		}
//		System.out.println(sql);
		List<TResult> tResultList = TResult.find(sql.toString()).fetch();
		if(tResultList != null){
			for(TResult result : tResultList){
					obj = new JsonObject();
					obj.addProperty("T_NODE_TYPE_NAME", result.TNODETYPE.T_NODE_TYPE_NAME);
					obj.addProperty("T_JOB_NAME", result.JOB.T_JOB_NAME);
					obj.addProperty("T_NODE_NAME", result.NODE.T_NODE_NAME);
					obj.addProperty("T_SCRIPT_NAME", result.tScript.T_SCRIPT_NAME);
					if("OK".equals(result.T_RESULT_STATUS)){
						if("1".equals(result.T_RESULT_ALARM_LEVEL)){
							obj.addProperty("T_RESULT_ALARM_LEVEL", "严重告警");
						}else if("2".equals(result.T_RESULT_ALARM_LEVEL)){
							obj.addProperty("T_RESULT_ALARM_LEVEL", "主要告警");
						}else if("3".equals(result.T_RESULT_ALARM_LEVEL)){
							obj.addProperty("T_RESULT_ALARM_LEVEL", "次要告警");
						}else if("4".equals(result.T_RESULT_ALARM_LEVEL)){
							obj.addProperty("T_RESULT_ALARM_LEVEL", "提示告警");
						}else{
							obj.addProperty("T_RESULT_ALARM_LEVEL", "正常");
						}
					}else if("ERROR".equals(result.T_RESULT_STATUS)){
						obj.addProperty("T_RESULT_ALARM_LEVEL", "连接失败");
					}
					obj.addProperty("T_RESULT_TIME", sdf.format(result.T_RESULT_TIME));
					arr.add(obj);
				}
			}
		renderJSON(new DataTableSource(request, arr));
	}

	//生成Excel表格
	public static void toExcel() throws Exception{
		String username = session.get("username");
		String startTime = params.get("startTime");
		String endTime = params.get("endTime");
		String jobType = params.get("jobType");
		String job = params.get("job");
		
		String headLine = null;//表头
		String head = startTime+"到"+endTime;
		ExcelInput input = new ExcelInput();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String fileName = "" + sdf.format(new Date()) + "自动化巡检工具服务报告单.xls";// 表名
		response.setContentTypeIfNotSet("application/msexcel;charset=GBK");
		response.setHeader("Content-Disposition", "attachment;filename="
				+ new String(fileName.getBytes("GBK"), "iso-8859-1"));
		try{
			//创建工作簿
			WritableWorkbook wwb = Workbook.createWorkbook(response.out);
			String sheetName = "业务进程状态巡检";//小表名
			headLine = head + "日常巡检服务报告单";
			String[] columnNames = { "序号", "设备分层", "任务名称", "设备名称", "脚本名称", "告警等级", "创建时间"};
			List<String[]> list = new ArrayList<String[]>();
			
			
			
			StringBuffer sql = new StringBuffer();
			sql.append("select t from TResult t where t.T_RESULT_TIME between '"+startTime+" 00:00:00' and '"+endTime+" 23:59:59' and t.T_RESULT_ALARM_STATUS='1'");
			if(!"admin".equals(username)){
				TUser tuser = TUser.find("from TUser tu where tu.T_USER_NAME='"+username+"'").first();
				sql.append(" and t.USER="+tuser.id);
			}
			if(!"请选择设备".equals(jobType)){
				if("全部".equals(job)){
					TNodeType nodeType = TNodeType.find("from TNodeType tn where tn.T_NODE_TYPE_NAME='"+jobType+"'").first();
					sql.append(" and t.TNODETYPE="+nodeType.id);
				}else{
					TJob tJob = TJob.find("from TJob td where td.T_JOB_NAME='"+job+"'").first();
					sql.append(" and t.NODE="+tJob.id);
				}
			}
//			System.out.println("sql="+sql);
			List<TResult> tResultList = TResult.find(sql.toString()).fetch();
			String report = "";
			if(tResultList != null){
				for(TResult result : tResultList){
					if("OK".equals(result.T_RESULT_STATUS)){
						if("1".equals(result.T_RESULT_ALARM_LEVEL)){
							report = "严重告警";
						}else if("2".equals(result.T_RESULT_ALARM_LEVEL)){
							report = "主要告警";
						}else if("3".equals(result.T_RESULT_ALARM_LEVEL)){
							report = "次要告警";
						}else if("4".equals(result.T_RESULT_ALARM_LEVEL)){
							report = "提示告警";
						}else{
							report = "正常";
						}
					}else{
						report = "连接失败";
					}
					String[] str = {result.TNODETYPE.T_NODE_TYPE_NAME,result.JOB.T_JOB_NAME,result.NODE.T_NODE_NAME,result.tScript.T_SCRIPT_NAME,report,format.format(result.T_RESULT_TIME)};
					list.add(str);
					}
				}
			
			
			input.commonality(wwb, list, columnNames, headLine, sheetName, username);
			wwb.write();
			wwb.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//计算周
	public static String weekday(String startTime,String endTime) throws Exception{ 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date start = sdf.parse(startTime);
		Date end = sdf.parse(endTime);
		Calendar c = Calendar.getInstance();
		int a = (int) ((end.getTime()-start.getTime())/1000/24/60/60);
		c.setTime(start);
		String s = "[";
		for(int i=1;i<=a;i++){
			c.add(Calendar.DAY_OF_YEAR, +i);
			Date date = c.getTime();
			String str = sdf.format(date);
			if(i==a){
				s+="\'"+str+"\']";
			}else{
				s+="\'"+str+"\',";
			}
		}
//		System.out.println(s);
		return null;
	}
	
	//计算任意两天中的时间差(day是整数类型)
	public static int daysOfTwo(Date fDate, Date oDate) {

		Calendar aCalendar = Calendar.getInstance();

		aCalendar.setTime(fDate);

		int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);

		aCalendar.setTime(oDate);

		int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);

		return day2 - day1;

	}
	
	//计算当前月的起始时间
	public static String getYearMonthStartTime(String year, String month) {
		String startTime = "";
		if (month != null && month.length() == 1) {
			startTime = year + "-0" + month + "-01";
		} else if (month != null && month.length() == 2) {
			startTime = year + "-" + month + "-01";
		}
		return startTime;
	}
	
	//计算当前月的结束时间
	public static String getYearMonthEndTime(String year, String month) {
		String endTime = "";
		if (month != null && month.length() == 1) {
			if (month.equals("2")) {
				if (year != null && !"".equals(year)) {
					if (Integer.parseInt(year) % 4 == 0
							&& Integer.parseInt(year) % 100 != 0
							|| Integer.parseInt(year) % 400 == 0) {
						endTime = year + "-0" + month + "-29";
					} else {
						endTime = year + "-0" + month + "-28";
					}
				}
			} else if (month.equals("1") || month.equals("3")
					|| month.equals("5") || month.equals("7")
					|| month.equals("8")) {
				endTime = year + "-0" + month + "-31";
			}
		} else if (month != null && month.length() == 2) {
			if (month.equals("10") || month.equals("12")) {
				endTime = year + "-" + month + "-30";
			}

		}
		return endTime;
	}
	
}

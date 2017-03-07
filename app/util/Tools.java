package util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date; 
import java.util.List;
import java.util.TimeZone;

public class Tools {
	private static String _STR="";
	private static String _STR1="";
	private static Integer _Y=0;
	private static double _D=0d;
	public static Date getDate(){
		return new Date();
	}
	public static Date getDate(Long date){
		return new Date(date);
	}
	public static String getCurrentDate(){
		return currentTime("yyyy-MM-dd");
	}
	
	/**
	 * format:yyyy-MM-dd
	 * @return currentTime
	 */
	public static String dateformat1(){
		return currentTime("yyyy-MM-dd");
	}
	
	/**
	 * format:yyyy-MM-dd hh:mm:ss
	 * @return currentTime
	 */
	public static String dateformat2(){
		return currentTime("yyyy-MM-dd hh:mm:ss");
	}
	
	/**
	 * format:MM/dd/yyyy hh:mm:ss
	 * @return currentTime
	 */
	public static String dateformat3(){
		return currentTime("MM/dd/yyyy hh:mm:ss");
	}
	/**
	 * format:MM/dd/yyyy
	 * @return currentTime
	 */
	public static String dateformat4(){
		return currentTime("MM/dd/yyyy");
	}
	
	/**
	 * eg:Mon Apr 15 2013 to 2013-04-15
	 * @return
	 */
	public static String dateformat5(String date){
		String currentDate="";
		String[] months={"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		String[] nums={"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
		if(!"".equals(date) && null !=date){
			String _year=date.substring(11, 15);
			String _mon=date.substring(4, 7);
			String _day=date.substring(8, 10); 
			for (int i=0; i<months.length; i++) {
				if(months[i].equalsIgnoreCase(_mon))
					_mon=nums[i];
			}
			currentDate=_mon+"/"+_day+"/"+_year;
		}
		return currentDate;
	} 
	
	public static Long dateFormat6(String time){
		String temp="";
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		Date date=null; 
		if(!"".equals(time)){ 
			try { 
				date = sdf.parse(time); 
				DecimalFormat df = new DecimalFormat("#");
				temp=df.format(date.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} 
		
		return Tools.parseLong(temp);
	}
	public static String dateFormat7(String l){
		String date="";
		if(!"".equals(l)){
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			date=sdf.format(getDate(parseLong(l))).toString();
		}
		return date;
	}
	/**
	 * from yyyy-MM-dd HH:mm:ss parse to MM/dd/yyyy HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String dateParse(String time){
		String temp="";
		if(!"".equals(time)){
			temp=time.substring(5, 7)+"/"+time.substring(8, 10)+"/"+time.substring(0,4)+time.substring(10, 19);
		}
		return temp;
	}
	
	/**
	 * from MM/dd/yyyy HH:mm:ss parse to yyyy-MM-dd HH:mm:ss
	 * @param time
	 * @return
	 */
	public static String dateParse1(String time){
		String temp="";
		if(!"".equals(time)){
			temp=time.substring(6, 10)+"-"+time.substring(0, 2)+"-"+time.substring(3,5)+time.substring(10, 19);
		}
		return temp;
	}
	//获取当前时间
	public static String currentTime(String formate){  
		SimpleDateFormat sdf=new SimpleDateFormat(formate); 
		return sdf.format(new Date());
	} 
	
	/**
	 * 
	 * @description : 
	 * @author : divan
	 * @param param  eg: param=0 is current date;  //param>0  After a few days;// param<0 Before a few days;
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static String currentDate(int param){
		String result="";
		String param1="00:00:00";
		String param2="23:59:59";
		// startDate=2013-03-19 00:00:00
		// end Date= 2013-03-19 23:59:59
		Date date=new Date();
		int y=0, m=0, d=0;
		y=2013;
		m=date.getMonth()+1;
		d=date.getDate(); 
		if(param < 0){
			if(d+param>0){
				result=combinationDateTime(y, m, d+param, param1)+","+combinationDateTime(y, m, d+param, param2);
			}else if(m !=2 && (d-param<0)){ //非2月份
				if(m ==1){//1 月份
					result=combinationDateTime(y-1, 12, ((d+31)+param), param1)+","+combinationDateTime(y-1, 12, ((d+31)+param), param2);
				}else{
					result=combinationDateTime(y, (m-1), ((d+31)+param), param1)+","+combinationDateTime(y, (m-1), ((d+31)+param), param2);
				}
			}else{ //2月份 
				result=combinationDateTime(y, (m-1), ((d+28)+param), param1)+","+combinationDateTime(y, (m-1), ((d+28)+param), param2);
			}
		}else{
			result=combinationDateTime(y, m, d, param1)+","+combinationDateTime(y, m, d, param2);
		}
		return result;
	}
	public static String matchState(String[] states, String[] states1,  String currentState, String[] colors, String default1){
		String temp=default1;
		if(null != currentState){
			for(int i=0; i < states.length; i++){
				if(states[i].equalsIgnoreCase(currentState)){
					temp=colors[i];
					break;
				}
			}
			for(int i=0; i < states1.length; i++){
				if(states1[i].equalsIgnoreCase(currentState)){
					temp=colors[i];
					break;
				}
			}
		}  
		return temp;
	}
	
	public static String combinationDateTime(int year, int month, int day, String time){
		return year+"-"+month+"-"+day+" "+time;
	}
	 
	/**
	 * html中
	 * @param strDate (eg:2012-12-05T07:41:31GMTX+28800)
	 * @return
	 */
	public static String formatTime(String strDate){
		String result="";  
		if(!"".equals(strDate) && null != strDate){ 
			result=strDate.substring(0, 10)+"&nbsp;"+strDate.substring(11,19); 
		}
		return result;
	}
	/**
	 * java中
	 * @param strDate (eg:2012-12-05T07:41:31GMTX+28800)
	 * @return
	 */
	public static String formatTime1(String strDate){ 
		String result="";  
		if(!"".equals(strDate) && null != strDate){ 
			result=strDate.substring(0, 10)+" "+strDate.substring(11,19); 
		}
		return result;
	}
	
	/**
	 * 将GMT时间转换为北京时间
	 * @param gmtTime
	 * @return
	 */
	public static String convertTime(String gmtTime){
		String result="";
		if(!"".equals(gmtTime) && gmtTime!=null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			ParsePosition pos = new ParsePosition(0);
			Date date = sdf2.parse(gmtTime, pos);
			sdf.setTimeZone(TimeZone.getTimeZone("GMT+16"));
			result = sdf.format(date);
		}
		return result;
	}
	/**
	 * 用于转换解析所得GMT时间转换为CST
	 * 2013-09-11T17:00:00GMTX+28800
	 * @param strDate
	 * @return
	 */
	public static String formatTimetoCST(String strDate){
		String result="";  
		if(!"".equals(strDate) && null != strDate){ 
			result=strDate.substring(0, 10)+" "+strDate.substring(11,19); 
		}
		result = Tools.convertTime(result);
		return result;
	}
	
	public static String filterStr(String str){
		String temp="";
		if("".equals(str)  || "null".equals(str) || str==null){}
		else{temp=str;}
		return temp;
	}
	/**
	   * 将长时间格式字符串转换为时间 yyyy-MM-dd HH:mm:ss
	   * @param strDate
	   * @return Date
	   */
	public static Date strToDateLong(String strDate) {
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   ParsePosition pos = new ParsePosition(0);
	   Date strToDate = formatter.parse(strDate, pos);
	   return strToDate;
	}
	
	public static int parseInt(String value) {
		String val = value;
		if ((val == null) || (val.length() == 0)) {
			return 0;
		}

		val = val.trim();

		try {
			return Integer.parseInt(val);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}
	
	public static double parseDouble(String value){
		String val=value;
		if ((val == null) || (val.length() == 0)) {
			return 0;
		}

		val = val.trim();

		try {
			return Double.parseDouble(val);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}
	public static int parseInteger(String value){
		String val=value;
		if ((val == null) || (val.length() == 0)) {
			return 0;
		}

		val = val.trim();

		try {
			return Integer.parseInt(val);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	}
	
	public static Long parseLong(String value){
		String val=value;
		if ((val == null) || (val.length() == 0)) {
			return 0l;
		}

		val = val.trim();

		try {
			return Long.parseLong(val);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0l;
	}
	
	/**
	 * formate double with 2 points
	 * @param value
	 * @return
	 */
	public static double formatDouble(Double value){ 
		try {
			BigDecimal  b  =  new  BigDecimal(value); 
			return b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return 0;
	} 
	
	/**
	 * 
	 * @description : 把当前空间大小进行转换成最小单位，然后返回数据，
	 * @author : divan
	 * @param str (eg:312 G、0.0 M)
	 * @return 
	 */ 
	public static double parseGbMb(String str){
		
		String[] units={"T","G","M"};
		double result=0d;
		
		String V="";
		String originalUnit="";
		String originalValue="";
		if(!"".equals(str) && isContait(units, str) && !"null null".equals(str)){
			V=str;
			originalUnit=V.substring(V.length()-1, V.length());
			originalValue=V.substring(0, V.length()-1);
			
			if(units[0].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024*1024;
			}
			if(units[1].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024;
			}
			if(units[2].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue);
			}
		} 
		return result;
	}
	/**
	 * 
	 * @param str Capacity Size
	 * @param currentLevel 0:byte, 1=kb , 2=mb, 3=gb, 4=tb
	 * @para们 parseLevel 0:byte, 1=kb , 2=mb, 3=gb, 4=tb
	 * @return
	 */
	public static String formatCapacity(String str, int currentLevel, int parseLevel){
		_STR="";
		String[][] _STRS=new String[5][5];
		_STRS[0][0]="Byte";_STRS[0][1]="KB";_STRS[0][2]="MB";_STRS[0][3]="GB";_STRS[0][4]="TB";
		
		//首先进行过滤
		if(-1 != currentLevel){
			_Y=currentLevel;
		}else{
			if(!"".equals(str) && null != str){
				//初始化单位 				
				_STRS[1][0]="Byte";_STRS[1][1]="Kb";_STRS[1][2]="Mb";_STRS[1][3]="Gb";_STRS[1][4]="Tb";
				_STRS[2][0]="byte";_STRS[2][1]="kb";_STRS[2][2]="mb";_STRS[2][3]="gb";_STRS[2][4]="tb";
				_STRS[3][0]="B";_STRS[3][1]="K";_STRS[3][2]="M";_STRS[3][3]="G";_STRS[3][4]="T";
				_STRS[4][0]="b";_STRS[4][1]="k";_STRS[4][2]="m";_STRS[4][3]="g";_STRS[4][4]="t";
				
				//判断传入值，使用单位 
				for (int i = 0; i < _STRS.length; i++) {
					for (int j = 0; j < 5; j++) {
						_STR1=_STRS[i][j]; 
						if(str.indexOf(_STR1)!=-1){
							_Y=j; 
							if(i==0 || i==1 || i==2)str=str.substring(0, str.length()-2); 
							if(i==3 || i==4)str=str.substring(0, str.length()-1); 
							break;
						}
					}
				}
			}
		} 
		//转换最小单位
		if(!"".equals(_Y)){
			_D=0d;
			switch(_Y){
				case 0:
					_D=parseDouble(str);
					break;
				case 1:
					_D=parseDouble(str)*1024;
					break;
				case 2:
					_D=parseDouble(str)*1024*1024;
					break;
				case 3:
					_D=parseDouble(str)*1024*1024*1024;
					break;
				case 4:
					_D=parseDouble(str)*1024*1024*1024*1024;
					break;
			}
		} 
		//对 _LONG转换统一单位  
		switch(parseLevel){
			case 0: //byte
				BigDecimal  b1  =  new  BigDecimal(_D); 
				_STR=b1.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+_STRS[0][0];
				break;
			case 1:
				BigDecimal  b2  =  new  BigDecimal(_D/1024); 
				_STR=b2.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+_STRS[0][1];
				break;
			case 2:
				BigDecimal  b3  =  new  BigDecimal(_D/1024/1024); 
				_STR=b3.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+_STRS[0][2]; 
				break;
			case 3:
				BigDecimal  b4  =  new  BigDecimal(_D/1024/1024/1024); 
				_STR=b4.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+_STRS[0][3]; 
				break;
			case 4:
				BigDecimal  b5  =  new  BigDecimal(_D/1024/1024/1024/1024); 
				_STR=b5.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+_STRS[0][4]; 
				break; 
		} 
		return _STR;
	}
	
	/**
	 * @description 把当前数据转换成Mb， 然后返回
	 * @param str
	 * @return
	 */
	public static double parseGbMb2(String str){
		
		String[] units={"TB", "GB","MB"};
		double result=0d;
		
		String V="";
		String originalUnit="";
		String originalValue="";
		if(!"".equals(str) && isContait(units, str)){
			V=str;
			originalUnit=V.substring(V.length()-2, V.length());
			originalValue=V.substring(0, V.length()-2);
			
			if(units[0].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024*1024;
			}
			if(units[1].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024;
			}
			if(units[2].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue);
			}
		};  
		return result;
	}
	/**
	 * @description parse t,g,m to k
	 * @param str
	 * @return
	 */
	public static double parseCapacity(String str){
		
		String[] units={"T","G","M", "K", "Byte"};
		double result=0d;
		
		String V="";
		String originalUnit="";
		String originalValue="";
		if(!"".equals(str) && isContait(units, str) && !"null null".equals(str)){
			V=str;
			originalUnit=V.substring(V.length()-1, V.length());
			originalValue=V.substring(0, V.length()-1);
			
			if(units[0].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024*1024*1024*1024;
			}else if(units[1].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024*1024*1024;
			}else if(units[2].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024*1024;
			}else if(units[3].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue)*1024;
			}else if(units[4].equalsIgnoreCase(originalUnit)){
				result=parseDouble(originalValue);
			}
		} 
		return result;
	}
	public static String parseByteToTB(Double d){
		/**   
		 * 	  1kb=1000Byte
		 *    1MB=1024KB
		 *    1GB=1024MB
		 *    1TB=2014GB
		 */
		String[] units={"Byte","KB", "MB", "GB", "TB"};
		String result="";
		
		Double tempV=0d; 
		int i=0;
		if(d!= null && d>1000){i++;
			tempV=d/1024;
			while(tempV>1024){i++;
				tempV=tempV/1024;
			}
			
			result=formatDouble(tempV)+units[i];
		}else{
			result="0"+units[0];
		}
		return result;
	}
	
	/**
	 * @description parse double param from bm to tb
	 * @param capacity_mb
	 * @return
	 */
	public static String formatCapacity(double capacity_mb){
		
		String[] units={"MB","GB","TB"};
		String result="";
		
		double V=0d;
		int index=0;
		if(!"".equals(capacity_mb)){
			V=capacity_mb; 
			while (V>1024){index++;
				V=V/1024;
			};
			V=formatDouble(V);
			if(index==0){result=V+units[0];}
			if(index==1){result=V+units[1];}
			if(index==2){result=V+units[2];}
		};  
		return result;
	} 
	/**
	 * @description parse double param from bm to tb
	 * @param capacity_mb
	 * @return
	 */
	public static String formatCapacity(String capacity_mb){
		
		String[] units={"MB","GB","TB"};
		String result="";
		
		double V=0d;
		int index=0;
		if(!"".equals(capacity_mb)){
			V=parseDouble(capacity_mb); 
			while (V>1024){index++;
				V=V/1024;
			};
			V=formatDouble(V);
			if(index==0){result=V+units[0];}
			if(index==1){result=V+units[1];}
			if(index==2){result=V+units[2];}
		};  
		return result;
	} 
	
	public static boolean isContait(String[] strs, String v){
		boolean b=false;
		if(strs.length>0){
			for (String str : strs) {
				if(v.indexOf(str)>0)
					b=true;
			}
		}
		return b;
	}
	
	/**
	 *  format pct (eg: 0.2 is 0.2/divided)
	 * @param pct
	 * @param divided
	 * @return
	 */
	public static Double formatPCTMultiplyBy(double pct, double multiply){
		double d=0d;
		d=pct*multiply;
		return d;
	}
	/**
	 * 
	 * @param pct
	 * @param divided
	 * @return
	 */
	public static Double formatPCTDividedBy(double pct, double divided){
		double d=0d;
		if( 0 != divided){
			d=pct/divided;
		}
		return d;
	}
	
	/**
	 * format pct (eg:0.2 is 0.2)
	 * @param pct
	 * @return
	 */
	public static Double formatPCT(String pct){
		double d=0d; 
		try{
			d=parseDouble(pct);
		}catch(Exception e){
			e.printStackTrace();
		}
		return d;
	}
	/**
	 * format pct (eg:0.2 is 0.2)
	 * @param pct
	 * @return
	 */
	public static double formatPCT(double pct){
		double d=0d; 
		try{
			d=formatDouble(pct);
		}catch(Exception e){
			e.printStackTrace();
		}
		return d;
	}
}

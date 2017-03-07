package util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {
	
	/**
	 * 
	 * @param value
	 *            unit:KB
	 * @return
	 */
	public static String parserCapacity(long value) {
		if (value * 1.0 <= 0)
			value = 1;
		String[] unitArr = { "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB" };
		Double index = new Double("0");
		index = Math.floor(Math.log(value) / Math.log(1024));

		DecimalFormat df = new DecimalFormat("####.00");
		double size = value / Math.pow(1024, index.doubleValue());
		return df.format(size) + unitArr[index.intValue()];
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
			System.out.println("Parse Failure!");
		}

		return 0;
	}

    public static String parseColor(Integer ALARM_SEVERITY) {
		String color="";
		switch (ALARM_SEVERITY) {
		case 0:
			color= "green";
			break;
		case 1:
			color= "#660066";
			break;
		case 2:
			color= "#336699";
			break;
		case 3:
			color= "#ffcc33";
			break;
		case 4:
			color= "#ff9933";
			break;
		case 5:
			color= "#AA4643";
			break;
		case 6:
			color= "black";
			break;

		default:
			break;
		}
		return color;
	}

    public static String getSeverityName(Integer ALARM_SEVERITY) {
		String name="";
		if(ALARM_SEVERITY == null)
			return "";
		switch (ALARM_SEVERITY) {
		case 0:
			name= "Clear";
			break;
		case 1:
			name= "Information";
			break;
		case 2:
			name= "Warning";
			break;
		case 3:
			name= "Minor";
			break;
		case 4:
			name= "Major";
			break;
		case 5:
			name= "Critical";
			break;
		case 6:
			name= "Fatal";
			break;

		default:
			break;
		}
		return name;
	}

    public static String getSeverityCNName(Integer ALARM_SEVERITY) {
		String name="";
		if(ALARM_SEVERITY == null)
			return "";
		switch (ALARM_SEVERITY) {
		case 0:
			name= "清除";
			break;
		case 1:
			name= "未知";
			break;
		case 2:
			name= "参考";
			break;
		case 3:
			name= "无害";
			break;
		case 4:
			name= "警告";
			break;
		case 5:
			name= "紧急";
			break;
		case 6:
			name= "致命";
			break;

		default:
			break;
		}
		return name;
	}

    public static String getDateString(int type) {
		String pas = "";
		switch (type) {
		case 0:
			pas = "yyyy-MM-dd HH:mm:ss.S";
			break;
		case 1:
			pas = "yyyy-MM-dd";
			break;
		case 2:
			pas = "yyyy-MM-dd HH:mm:ss";
			break;
		case 3:
			pas = "yyyyMMddHHmmss";
			break;
		case 4:
			pas = "yyyyMMdd";
			break;
		case 5:
			pas = "yyyyMMddHHmm";
			break;
		case 6:
			pas = "yyyyMMddHH";
			break;
		case 7:
			pas = "yyyyMM";
			break;
		case 8:
			pas = "yyyy-MM-dd HH:mm";
			break;
		case 9:
			pas = "HH:mm";
			break;
		case 10:
			pas = "yyyy-MM-dd HH";
			break;
		case 11:
			pas = "yyyy-MM";
			break;
		case 12:
			pas = "yyyy,MM,dd,HH,mm,ss";
			break;
		case 13:
			pas = "MM/dd/yyyy HH:mm:ss";
			break;
        case 14:
			pas = "mm:ss";
			break;
        case 15:
			pas = "MM/dd/yyyy";
			break;
        case 16:
        	pas = "yyMMddHH";
        	break;
		default:
			pas = "yyyy-MM-dd";
			break;
		}
		return pas;
	}

    public static Date parseDate(String in, String pas) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pas);
		try {
			d = sdf.parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return d;
	}

	public static Date parseDate(String in, int type) {
		String pas = getDateString(type);
		return parseDate(in, pas);
	}

    public static String parseDateString(long timestamp,int type){
		String pas = "";
		Date d = new Date(timestamp);
		pas = parseString(d,type);
		return pas;
	}

	public static String parseString(Date date, int type) {
		String pas = getDateString(type);
		SimpleDateFormat sdf = new SimpleDateFormat(pas);
		return sdf.format(date);
	}

    public static String parseTSMDate(String date) {
        if(date.length() == 16){
            String a = "20";
            a += date.substring(1,3);
            a += "-";
            a += date.substring(3,5);
            a += "-";
            a += date.substring(5,7);
            a += " ";
            a += date.substring(7,9);
            a += ":";
            a += date.substring(9,11);
            a += ":";
            a += date.substring(11,13);
            return a;
        }
        return date;
    }

    public static Matcher matches(String regex,String text){
		if(text == null)
			return null;
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(text);
		if(m.find()){
			return m;
		}else{
			return null;
		}
	}

    public static void sortBy(final String fieldName,List<HashMap<String,String>> list){
        Collections.sort(list, new Comparator<HashMap<String, String>>() {
            public int compare(HashMap<String, String> obj1, HashMap<String, String> obj2) {
                char[] char1 = obj1.get(fieldName).toLowerCase().toCharArray();
                char[] char2 = obj2.get(fieldName).toLowerCase().toCharArray();
                int len = char1.length;
                if (char2.length <= char1.length)
                    len = char2.length;
                for (int i = 0; i < len; i++) {
                    if (char1[i] > char2[i])
                        return 1;
                    else if (char1[i] < char2[i])
                        return -1;
                }
                return 0;
            }
        });
    }

    public static String getReportType(String type){
    	String r_type = type;
    	if(type.equalsIgnoreCase("week")){
    		r_type = "周报";
    	}else if(type.equalsIgnoreCase("month")){
    		r_type = "月报";
    	}
    	return r_type;
    }
    
    public static String getVmFinishTimeDef(){
    	String dateStr ="";
    	try{
    	    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'+08:00'");// 2014-10-27T07:11:50-04:30
    		Calendar curr = Calendar.getInstance();
    		curr.set(Calendar.YEAR,curr.get(Calendar.YEAR)+1);
    		Date date=curr.getTime();
    		dateStr = df.format(date);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return dateStr;
    }
}

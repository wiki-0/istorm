package util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import play.Play;
import play.mvc.Http.Response;


public class CreateDoc {

    private Configuration configuration = null;

    public CreateDoc() {
        configuration = new Configuration();
        configuration.setDefaultEncoding("utf-8");
    }
 

    public void create(String fileName,String dayType,String aixSum,String backupSum,String storageSum,String switchSum,String startYear,String endYear,
    		String startMonth,String endMonth,String startDay,String endDay,Response response,Map<String,String> miniMap,String process,String x86ServerData,
    		String haGpfsData,String miniPerformance,String x86Performance,String miniComputerX86Data) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        
        map.put("aixImage", getImageStr("aix"+fileName));
        map.put("backupImage", getImageStr("backup"+fileName));
        map.put("storageImage", getImageStr("storage"+fileName));
        map.put("switchImage", getImageStr("switch"+fileName));
        if(dayType.equals("day"))
        {
        	map.put("day","日");
        }
        else if(dayType.equals("week"))
        {
        	map.put("day","周");
        }
        else if(dayType.equals("month"))
        {
        	map.put("day","月");
        }
        else if(dayType.equals("year"))
        {
        	map.put("day","年");
        }
        //设备数
        map.put("aixSum", aixSum);
        map.put("backupSum", backupSum);
        map.put("storageSum", storageSum);
        map.put("switchSum", switchSum);
        //开始时间年
        map.put("aixStartYear", startYear);
        map.put("backupStartYear", startYear);
        map.put("storageStartYear", startYear);
        map.put("switchStartYear", startYear);
        //结果时间年
        map.put("aixEndYear", endYear);
        map.put("backupEndYear", endYear);
        map.put("storageEndYear", endYear);
        map.put("switchEndYear", endYear);
        //开始时间月
        map.put("aixStartMonth", startMonth);
        map.put("backupStartMonth", startMonth);
        map.put("storageStartMonth", startMonth);
        map.put("switchStartMonth", startMonth);
        //结果时间月
        map.put("aixEndMonth", endMonth);
        map.put("backupEndMonth", endMonth);
        map.put("storageEndMonth", endMonth);
        map.put("switchEndMonth", endMonth);
        //开始时间日
        map.put("aixStartDay", startDay);
        map.put("backupStartDay", startDay);
        map.put("storageStartDay", startDay);
        map.put("switchStartDay", startDay);
        //结果时间日
        map.put("aixEndDay", endDay);
        map.put("backupEndDay", endDay);
        map.put("storageEndDay", endDay);
        map.put("switchEndDay", endDay);
        //小型机硬件巡检
        map.put("miniComputerData", miniMap.get("miniComputerData"));
        map.put("miniComputerTable", miniMap.get("miniComputerTable"));
        map.put("miniComputerx86Data", miniComputerX86Data);
        //业务进程状态
        map.put("Process", process);
        //x86服务器操作日志
        map.put("X86ServerData", x86ServerData);
        //hap&gpfs
        map.put("HaGpfsData", haGpfsData);
        //小型机性能
        map.put("MiniPerformance", miniPerformance);
        //x86服务器性能
        map.put("X86Performance", x86Performance);
//        configuration.setClassForTemplateLoading(this.getClass(), "/template");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setDirectoryForTemplateLoading(new File(Play.applicationPath + "/template"));
        Template t = configuration.getTemplate("template-report.ftl","UTF-8");
        String path = Play.applicationPath + "/doc/"+fileName+".doc";
        File outFile = new File(path);
        Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
        t.process(map, out);
        out.flush(); 
        out.close();
        OutputStream outputStream = null;
        outputStream = response.out;
        InputStream fis = new BufferedInputStream(new FileInputStream(path));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        response.setHeader("Content-Disposition",
                "attachment;filename=".concat(String.valueOf(URLEncoder.encode(fileName + ".doc", "UTF-8"))));
        OutputStream toClient = response.out;
        response.contentType ="application/octet-stream";
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
    }

    private String getImageStr(String fileName) {
        String imgFile = Play.applicationPath + "/pic/"+fileName+".jpeg";
        InputStream in = null;
        byte[] data = null;
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    public static void main(String[] args) throws Exception {
//        new CreateDoc().create();
    	Calendar calendar = Calendar.getInstance();
    	
    	String startTime = "1970-01-01 00:00:00";
    	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    	Date startDate = format.parse(startTime);
    	long timeLong = startDate.getTime() + 1441584988000l;
    	calendar.setTimeInMillis(timeLong);
    	Date date = calendar.getTime();
    	String endTime = format.format(date);
    	System.out.println("data = " + endTime);

    }
}
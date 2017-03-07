package controllers;

import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import models.*;

import org.jfree.chart.*;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import play.Play;
import play.mvc.Http.Response;
import sun.misc.BASE64Encoder;
import util.DataTableSource;
import util.WordGenerator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class ReportEs extends CRUD {

    public static void index() {
        render();
    }
    //变量未定义 出错页面
    public static void undefined(String text) {
        renderText(text.replace("is undefined", "变量没有定义,无法生成报告！"));
    }
    //上传文件保存
    public static void uploadFile(File file) throws Exception {

        if (file.getName().endsWith(".doc") || file.getName().endsWith(".xml")) {
            String upPath = Play.applicationPath + "/public/Template/";
            File afterfile = new File(upPath + file.getName());
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(afterfile);
            int bytesRead;
            byte[] buffer = new byte[8192];
            while ((bytesRead = fis.read(buffer, 0, 8192)) != -1) {
                fos.write(buffer, 0, bytesRead);// 传过来的文件写入文件
                // System.out.println(new String(buffer));
            }
            fos.flush();
            fos.close();
            if (file.getName().endsWith(".xml")) {
                ReportE xml = ReportE.find("byT_FILE_NAME",
                        file.getName().replaceAll(".xml", "")).first();
                if (xml == null) {
                    ReportE re = new ReportE();
                    re.T_FILE_XML = file.getName();
                    re.T_FILE_NAME = file.getName().replaceAll(".xml", "");
                    re.save();
                } else {
                    xml.T_FILE_XML = file.getName();
                    xml.save();
                }
            }
        }
        index();
    }

    /**
     * 生成报告
     * @throws IOException 
     */
    public static void createWord() throws IOException{
        ReportE cre = ReportE.findById(Long.parseLong(params.get("id")));
        String jpegType = params.get("jpegType");
        // 获取 xml路径和要生成的ftl路径
        String xmlPath = Play.applicationPath + "/public/Template/" + cre.T_FILE_XML;
        String ftlPath = xmlPath.replaceAll(".xml", ".ftl");
        File file = new File(xmlPath);
        file.renameTo(new File(ftlPath));
        //要生成报告的巡检结果
        Map<String, Object> map = new HashMap<>();
        //查询所有的报告配置 替换变量
        List<ReportD> reports = ReportD.findAll();
        //告警统计
        List<String> allList = new ArrayList();
        for (ReportD report : reports) {
            StringBuilder sql = new StringBuilder();
            sql.append("JOB.T_JOB_NAME is '" + report.T_Report_JOB + "'");
            sql.append(" and tScript.T_SCRIPT_NAME is '" + report.T_Report_SCRIPT + "'");
            sql.append(" and NODE.T_NODE_NAME is '" + report.T_Report_NODE + "'");
            sql.append(" and TNODETYPE.T_NODE_TYPE_NAME is '" + report.T_Report_NODETYPE + "'");
            sql.append("and T_RESULT_TIME BETWEEN '" + report.T_Report_STARTTIME + " 00:00:00' AND '" + report.T_Report_ENDTIME + " 23:59:59'");

            List<TResult> results = TResult.find(sql.toString()).fetch();
            StringBuilder list = new StringBuilder();
            int level = 0;
            int nuknown = 0;
            for (TResult result : results) {
                if (!result.T_RESULT_OUTPUT.equals("")) {
                    list.append("创建时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(result.T_RESULT_TIME));
                    list.append("返回结果：" + result.T_RESULT_OUTPUT);
                }
                if (result.T_RESULT_ALARM_LEVEL != null && !result.T_RESULT_ALARM_LEVEL.equals("")) {
                    if (result.T_RESULT_ALARM_LEVEL.equals("1")) {
                        level += 4;
                    } else if (result.T_RESULT_ALARM_LEVEL.equals("2")) {
                        level += 3;
                    } else if (result.T_RESULT_ALARM_LEVEL.equals("3")) {
                        level += 2;
                    } else if (result.T_RESULT_ALARM_LEVEL.equals("4")) {
                        level += 1;
                    }
                } else {
                    nuknown += 1;
                }
                allList.add(report.T_Report_VAR+","+level+","+nuknown);
            }
            if (list == null || list.equals("")) {
                list.append("没有数据");
            }
            map.put(report.T_Report_VAR, list);
        }
        //统计告警
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0;i<allList.size();i++){
            String[] list = allList.get(i).split(",");
            dataset.addValue(Long.parseLong(list[1]), "已解析", list[0]);
            dataset.addValue(Long.parseLong(list[2]), "未解析", list[0]);
        }
        //创建主题样式
        StandardChartTheme standardChartTheme = new StandardChartTheme("CN");
        //设置标题字体
        standardChartTheme.setExtraLargeFont(new Font("微软雅黑", Font.BOLD, 40));
        //设置图例的字体
        standardChartTheme.setRegularFont(new Font("微软雅黑", Font.PLAIN, 30));
        //设置轴向的字体
        standardChartTheme.setLargeFont(new Font("微软雅黑", Font.PLAIN, 30));
        //应用主题样式
        ChartFactory.setChartTheme(standardChartTheme);
        JFreeChart chart = null;
        if (jpegType.equals("bar")){
            chart = ChartFactory.createBarChart("告警统计图", "脚本名称", "告警等级统计", dataset, PlotOrientation.VERTICAL, true, true, false);
            chart.setTitle(new TextTitle("告警统计柱状图", new Font("宋体", Font.BOLD + Font.ITALIC, 30)));            
        }else if (jpegType.equals("line")){
            chart = ChartFactory.createLineChart3D("告警统计图 ", "脚本名称", "告警等级统计", dataset, PlotOrientation.VERTICAL, true, true, true);
            chart.setTitle(new TextTitle("告警统计折线图", new Font("宋体", Font.BOLD + Font.ITALIC, 30)));
        }else if (jpegType.equals("pie")){
            DefaultPieDataset dataset2 = new DefaultPieDataset();
            for(int i=0;i<allList.size();i++){
                String[] list = allList.get(i).split(",");
                dataset2.setValue(list[0]+"已解析",Long.parseLong(list[1]));
                dataset2.setValue(list[0]+"未解析",Long.parseLong(list[2]));
            }
            chart = ChartFactory.createPieChart("告警统计饼状图", dataset2, true, true, true);
        }

        LegendTitle legend = chart.getLegend(0);//设置Legend
        legend.setItemFont(new Font("宋体", Font.BOLD, 30));
        OutputStream os;
		try {
			String upPath = Play.applicationPath + "/public/Template/";
			os = new FileOutputStream(upPath+"company.jpeg");
			ChartUtilities.writeChartAsJPEG(os, chart, 1000, 800);
			os.close();
			map.put("image", getImageStr("company"));
		} catch (FileNotFoundException e) {
            //异常处理
			e.printStackTrace();
		}//图片是文件格式的，故要用到FileOutputStream用来输出。
		
        //参数： map 修改信息， ftlPath 文件路径， response 请求信息
        try {
			WordGenerator.createDoc(map, cre.T_FILE_XML.replaceAll(".xml", ".ftl"),cre.T_FILE_NAME, response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			 //还原为xml
	        File file2 = new File(ftlPath);
	        file2.renameTo(new File(xmlPath));
            //找到未定义的变量
            String text =e.toString();
            undefined(text);
		}
        //还原为xml
        File file2 = new File(ftlPath);
        file2.renameTo(new File(xmlPath));
    }
    //下载xml文件 修改
    public static void editFile() throws Exception {
        ReportE edi = ReportE.findById(Long.parseLong(params.get("id")));
        String doPath = Play.applicationPath + "/public/Template/" + edi.T_FILE_XML;
        File file = new File(doPath);
        InputStream fis = new BufferedInputStream(new FileInputStream(file));
        byte[] buffer = new byte[fis.available()];
        fis.read(buffer);
        fis.close();
        response.setHeader("Content-Disposition", "attachment;filename="
                .concat(String.valueOf(URLEncoder.encode("Template"
                        + (int) (Math.random() * 10000) + ".xml", "UTF-8"))));
        OutputStream toClient = response.out;
        response.contentType = "application/octet-stream";
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
    }
    //删除文件
    public static void deleteFile() {
        ReportE pep = ReportE.findById(Long.parseLong(params.get("id")));
        if (pep != null) {
            String delxmlPath = Play.applicationPath + "/public/Template";
            final String name = pep.T_FILE_NAME;
            File f = new File(delxmlPath);
            FileFilter ff = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    String s = pathname.getName().toLowerCase();
                    if (s.startsWith(name)) {
                        return true;
                    }
                    return false;
                }
            };
            File[] flist = f.listFiles(ff);
            if (flist == null) {
//				System.out.println("the flist is null");
                return;
            }
            for (File fs : flist) {
                fs.delete();
            }
        }
        pep.delete();
        renderText("true");

    }

    /**
     * 饼状图
     */
    public static void getPieDataset() throws Exception{
        DefaultPieDataset dpd = new DefaultPieDataset();
//        dpd.setValue(report.T_Report_VAR+"统计", level); // 告警等级统计
//        dpd.setValue(report.T_Report_VAR+"未解析", nuknown); // 未解析或未连接
        JFreeChart chart = ChartFactory.createPieChart("告警统计饼状图", dpd, true, true, false);
        chart.setTitle(new TextTitle("告警统计饼状图", new Font("宋体", Font.BOLD + Font.ITALIC, 50)));
        LegendTitle legend = chart.getLegend(0);//设置Legend
        legend.setItemFont(new Font("宋体", Font.BOLD, 50));
        PiePlot plot = (PiePlot) chart.getPlot();//设置Plot
        plot.setLabelFont(new Font("宋体", Font.BOLD, 50));
        OutputStream os = new FileOutputStream("company.jpeg");//图片是文件格式的，故要用到FileOutputStream用来输出。
        ChartUtilities.writeChartAsJPEG(os, chart, 1000, 800);
        os.close();
        return;
    }

    /**
     *  获得图片BASE64
     */
    private static String getImageStr(String fileName) {
        String upPath = Play.applicationPath + "/public/Template/";
        String imgFile = upPath + fileName + ".jpeg";
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

    /**
     * 报告生成列表
     */
    public static void getReportList() {

        StringBuilder sql = new StringBuilder();
        if (params.get("xmlName") != null && !(params.get("xmlName").equals(""))) {
            sql.append("T_FILE_XML like '%" + params.get("xmlName") + "%'");
        }
        List<ReportE> list = ReportE.find(sql.toString()).fetch();
        JsonArray arr = new JsonArray();
        JsonObject obj;
        TUser user = TUser.find("byT_USER_NAME", Security.connected()).first();
        if (list != null && list.size() > 0 && user != null) {
            for (ReportE report : list) {
                obj = new JsonObject();
                obj.addProperty("xmlName", report.T_FILE_XML);
                obj.addProperty("downLoad", "<a id='" + report.id + "dow' class='btn btn-primary btn-xs' href='/ReportEs/editFile?id=" + report.id + "'>下载模板</a>");
                obj.addProperty("jpegType", "<select id='"+report.id+"rel'><option value='line'>折线图</option><option value='bar'>柱状图</option><option value='pie'>饼状图</option></select>");
                obj.addProperty("createDoc", "<a id='" + report.id + "cre' class='btn btn-primary btn-xs' onclick='createReport(" + report.id + ")'>报告生成</a>");
                obj.addProperty("DELETE", "<button id='" + report.id + "del' class='btn btn-danger btn-xs' data-toggle='modal' data-target='#modal-sizes-1' onclick='deleteinfo(" + report.id + ")'>删除</button>");
                arr.add(obj);
            }
        }
        renderJSON(new DataTableSource(request, arr));
    }
}
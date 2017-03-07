package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class DocumentHandler {
	private Configuration configuration = null;
    private String templateFile;
    private String outputFilePath;
    private Map<String,Object> dataMap;
    
	public DocumentHandler(String templateFile,String outputFilePath,Map<String,Object> dataMap) {
		this.templateFile = templateFile;
		this.outputFilePath = outputFilePath;
		this.dataMap = dataMap;
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
	}

	public void createDoc() {
		//要填入模本的数据文件
		//设置模本装置方法和路径,FreeMarker支持多种模板装载方法。可以重servlet，classpath，数据库装载，
		//这里我们的模板是放在com.havenliu.document.template包下面
//		configuration.setClassForTemplateLoading(this.getClass(), Constants.WEB_PATH+"template"+File.separator);
//		configuration.setDirectoryForTemplateLoading(Constants.WEB_PATH+"template"+File.separator);
		
		configuration.setServletContextForTemplateLoading(Constants.sc, "\\template");
		Template t=null;
		try {
			//test.ftl为要装载的模板
			t = configuration.getTemplate(this.templateFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//输出文档路径及名称
		File outFile = new File(this.outputFilePath);
		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "utf-8"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		 
        try {
			t.process(this.dataMap, out);
		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

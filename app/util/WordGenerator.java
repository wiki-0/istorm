package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import play.Play;
import play.mvc.Http.Response;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class WordGenerator {
	private static Configuration configuration = null;
	private static Map<String, Template> allTemplates = null;

	private WordGenerator() {
		throw new AssertionError();
	}

	public static void createDoc(Map<?, ?> dataMap, String ftlName,String firstName ,Response response) throws Exception {
		configuration = new Configuration();
		configuration.setDefaultEncoding("utf-8");
		configuration.setClassForTemplateLoading(WordGenerator.class, "../../public/Template");
		allTemplates = new HashMap<>();
		try {
			allTemplates.put("test", configuration.getTemplate(ftlName));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		String name = Play.applicationPath + "/public/Template/" + firstName + ".doc";
		File f = new File(name);
		Template t = allTemplates.get("test");
		try {
			// 这个地方不能使用FileWriter因为需要指定编码类型否则生成的Word文档会因为有无法识别的编码而无法打开
			Writer w = new OutputStreamWriter(new FileOutputStream(f), "utf-8");
			t.process(dataMap, w);
			
			w.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		InputStream fis = new BufferedInputStream(new FileInputStream(name));
		byte[] buffer = new byte[fis.available()];
		fis.read(buffer);
		fis.close();
		response.setHeader("Content-Disposition",
				"attachment;filename=".concat(String.valueOf(URLEncoder.encode(new SimpleDateFormat("yyyyMMddhhmm").format(new Date())
 + ".doc", "UTF-8"))));
		OutputStream toClient = response.out;
		response.contentType ="application/octet-stream";
		toClient.write(buffer);
		toClient.flush();
		toClient.close();
	}

}

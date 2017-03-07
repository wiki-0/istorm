package util;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

public class JsonUtil {
	private static JsonFactory jf;
	private static ObjectMapper mapper;
	private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	static {
		mapper = new ObjectMapper();
//		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);//
		jf = new JsonFactory();
	}
	
	public static String obj2json(Object obj) {
		JsonGenerator jg = null;
		try {
			StringWriter out = new StringWriter();
			jg = jf.createJsonGenerator(out);
			mapper.writeValue(jg, obj);
			return out.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(jg!=null) jg.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static Object json2obj(String json,Class<?> clz) {
		try {
			return mapper.readValue(json,clz);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 把json串转化成对象
	 * @param jsonStr json
	 * @param typeToken 对象类型
	 * @return 目标对象
	 */
	public static <T> T jsonToObject(String jsonStr,TypeToken<T> typeToken) throws JsonSyntaxException {
		Type type = typeToken.getType();
		T result = null;
		try {
			
			result = gson.fromJson(jsonStr, type);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}

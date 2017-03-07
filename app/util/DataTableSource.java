package util;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.mvc.Http.Request;

public class DataTableSource{ 
    public Long iTotalRecords;
    public Long iTotalDisplayRecords; 
    public Long sEcho;
    public String[][] aaData;
    
	
	/**
	 * Create a new instance DataTableSource.
	 *
	 * @param request 服务请求
	 * @param list 返回列表
	 */
	public DataTableSource(Request request, List<?> list){
		String fields=request.params.get("Fields");
		String[] fieldsArray=null;
		if(fields!=null && !"".equals(fields)){
       	 	fieldsArray = fields.split(",");
        }
		iTotalRecords=(long) list.size();
		iTotalDisplayRecords=(long) list.size();
		this.sEcho=(long)0;
		aaData=new String[list.size()][fieldsArray.length];  
		String temp=null;
		try {
			for (int i=0; i<list.size(); i++) {
				Object obj=list.get(i);
				for (int j=0; j<fieldsArray.length; j++) { 
					temp=String.valueOf(obj.getClass().getField(fieldsArray[j].trim()).get(obj));
					if("".equals(temp) || "null".equals(temp) || "null null".equals(temp) || null == temp){temp="";}
					aaData[i][j]=temp;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) { 
			e.printStackTrace();
		} catch (IllegalAccessException e) { 
			e.printStackTrace();
		} catch (NoSuchFieldException e) { 
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Create a new instance DataTableSource.
	 *
	 * @param request 服务请求
	 * @param arr 返回JsonArray
	 */
	public DataTableSource(Request request, JsonArray arr){
		String fields=request.params.get("Fields");
		String[] fieldsArray=null;
		if(fields!=null && !"".equals(fields)){
       	 	fieldsArray = fields.split(",");
        }
		iTotalRecords=(long) arr.size();
		iTotalDisplayRecords=(long) arr.size();
		this.sEcho=(long)0;
		aaData=new String[arr.size()][fieldsArray.length];  
		String temp=null;
		JsonObject obj;
		try {
			for (int i=0; i<arr.size(); i++) {
				obj=(JsonObject)arr.get(i);
				for (int j=0; j<fieldsArray.length; j++) { 
					temp=String.valueOf(obj.get(fieldsArray[j].trim())); 
					if("".equals(temp) || "null".equals(temp) || "null null".equals(temp) || null == temp || "0".equals(temp) || "0.0".equals(temp)){
						temp="";
					}else{
						int index=temp.indexOf('"');
						if(index >-1){
							temp=temp.substring(1, temp.length()-1);
						} 
					}
					aaData[i][j]=temp;
				}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) { 
			e.printStackTrace();
		}
	}
}

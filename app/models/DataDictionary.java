package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;
/*
 * 数字字典
 */
@Entity
@Table(name="T_DataDictionary")
public class DataDictionary extends Model
{
	/** name */
	public String T_DD_NAME;
	/** type
	 *   1、下拉框
	 *   2、单选框
	 *   3、多选框
	 */
	public String T_DD_TYPE;
	/** key */
//	public String T_DD_KEY;
	/** value  */
	public String T_DD_VALUE;
	
	/** 父节点  */
	public long T_DD_PARENTID;
	/** 人员  */
	@ManyToOne
	@JoinColumn(name = "T_USER_ID")
	public TUser USER;
	
	/** 父节点  */
	public long T_DD_OPERATION;

	public static List examine(){
		List<HashMap<String,String>> departmentList = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> map = null;
		String departmentName = "";
		DataDictionary data = DataDictionary.find("select t from DataDictionary t where t.T_DD_NAME='部门'").first();
		Long departmentId = data.id;
		List<DataDictionary> list = DataDictionary.find("select t from DataDictionary t where t.T_DD_PARENTID='"+departmentId+"'").fetch();
		for(DataDictionary d : list){
			map = new HashMap<String,String>();
			departmentName = d.T_DD_VALUE;
			map.put("name", departmentName);
			departmentList.add(map);
		}
		return departmentList;
	}
}

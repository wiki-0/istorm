package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="T_DATA_DIC")
@AccessType("field")
public class TDataDic extends Model{
	@Required
	public String DIC_TYPE;//数据字典类型
	
	public String DIC_TYPE_DESC;//数据字典类型中文名称
	
	@Required
	public String VALUE;//数据字典值
	@Required
	public String DISPLAY_VALUE;//数据字典显示值
	
	public String DISPLAY_VALUE_DESC;//描述

	public String Cascade_Value; //字典父级名称
	
	
	public static TDataDic getDicByTypeAndValue(String dicType, String value) {
        return find("byDIC_TYPEAndVALUE", dicType, value).first();
    }
	
	
}

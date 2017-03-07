package models;


import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name="T_SCRIPT_VARIABLE")
@AccessType("field")
public class TScriptVariable extends Model  {
	
	/**
	 * 变量名称
	 */
	@Unique
	@Required
	public String T_SCRIPT_VARIABLE_NAME;
	
	/**
	 * 变量值
	 */
	@Required
	public String T_SCRIPT_VARIABLE_VALUE;
	
	
	/**
	 * 变量说明
	 */
	public String T_SCRIPT_VARIABLE_DESC;
	
	

}

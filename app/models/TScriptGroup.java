package models;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name = "T_SCRIPT_GROUP")
@AccessType("field")
public class TScriptGroup extends Model {

	/**
	 * 脚本分组名称
	 */
	@Unique
	@Required
	public String T_SCRIPT_GROUP_NAME;
	
	/**
	 * 脚本分组描述
	 */
	public String T_SCRIPT_GROUP_DESC;
	
	
}

package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name="T_MENU")
@AccessType("field")
public class TMenu extends Model{

	@Required
	public String T_MENU_TYPE;
	@Required
	public String T_MENU_NAME;
	@Required
	public String T_MENU_ACTION;
	
	public String T_MENU_VALUE;
	public String T_MENU_STYLE;
	public int T_MENU_ORDER;
	public String T_MENU_ENABLE;
	public Long T_MENU_PARENT;
	
}
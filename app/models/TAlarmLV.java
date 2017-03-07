package models;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "T_ALARM_LV")
@AccessType("field")
public class TAlarmLV extends Model {

	/**
	 * 脚本id
	 */
	@ManyToOne
	@JoinColumn(name = "T_SCRIPT_ID")
	public TScript tScript;

	/**
	 * 告警级别
	 */
	@Required
	public String T_ALARM_LEVEL;

	/**
	 * 阀值或关键字
	 */
	@Required
	public String T_ALARM_THRESHOLD;

	/**
	 * 颜色
	 */
	public String T_ALARM_COLOR;
	
	/**
	 * 关系
	 */
	public String T_ALARM_RELATION;

}

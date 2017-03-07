package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "T_SCRIPT")
@AccessType("field")
public class TScript extends Model {

	/**
	 * 脚本名称
	 */
	@Required
	public String T_SCRIPT_NAME;

	/**
	 * 脚本文件名
	 */
	@Required
	public String T_SCRIPT_FILENAME;

	/**
	 * 脚本内容
	 */
	@Lob
	public String T_SCRIPT_COMMAND;

	/**
	 * 脚本类型 值有：
	 * bat:bat脚本 
	 * vbs:vbs脚本
	 * ps1:powershell脚本
	 * sh:shell脚本 
	 * ommand:command
	 */
	@Required
	public String T_SCRIPT_TYPE;

	/**
	 * 创建人
	 */
	@Required
	public String T_SCRIPT_USER;

	/**
	 * 创建时间
	 */
	@Required
	public Date T_SCRIPT_DATE;

	/**
	 * 脚本分组id
	 */
	public int T_SCRIPT_GROUP_ID;

	/**
	 * 用户组id
	 */
	public int T_USER_GROUP_ID;

	@ManyToMany
	public Set<TJob> JOBS;

	/**
	 * 阀值或关键字判断 
	 * 0：阈值
	 * 1：关键字
	 */
	public String T_ALARM_THRESHOLD_TYPE;
	
	@OneToMany(mappedBy = "tScript", targetEntity = TAlarmLV.class,cascade={CascadeType.ALL},orphanRemoval=true)
	public Set<TAlarmLV> TAlarmLVs;

}

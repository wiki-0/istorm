package models;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.AccessType;
import com.google.gson.annotations.Expose;

import play.db.jpa.Model;

@Entity
@Table(name = "T_RESULT")
@AccessType("field")
public class TResult extends Model {

	/** 巡检时间 */
	@Expose
	public Date T_RESULT_TIME;

	/** 巡检结果信息 */
	@Lob
	@Expose
	public String T_RESULT_OUTPUT;

	/**
	 * 巡检结果状态 <br>
	 * OK <br>
	 * ERROR <br>
	 */

	public String T_RESULT_STATUS;
	/**
	 * 告警级别
	 * 0：无
	 * 1：严重
	 * 2：主要
	 * 3：次要
	 * 4：提示
	 */
	public String T_RESULT_ALARM_LEVEL;

	/**
	 * 是否解析 <br>
	 * 1 解析 <br>
	 * 0 未解析
	 */
	public String T_RESULT_ALARM_STATUS;


	/**
	 * 是否阅读 <br>
	 * 1 已读 <br>
	 * 0 未读
	 */
	public String T_RESULT_ISREAD = "0";

	/** JOB表外键 */

	@ManyToOne
	@JoinColumn(name = "T_JOB_ID")
	public TJob JOB;

	/** TSCRIPT表外键 **/
	@ManyToOne
	@JoinColumn(name = "T_SCRIPT_ID")
	public TScript tScript;

	/** Node表外键 **/

	@ManyToOne
	@JoinColumn(name = "T_NODE_ID")
	public TNode NODE;
	/** 设备分层表外键 **/

	@ManyToOne
	@JoinColumn(name = "T_TNODETYPE_ID")
	public TNodeType TNODETYPE;

	/** User表外键 **/

	@ManyToOne
	@JoinColumn(name = "T_USER_ID")
	public TUser USER;

}

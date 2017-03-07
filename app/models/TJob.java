package models;

import java.util.*;

import javax.persistence.*;
import org.hibernate.annotations.AccessType;
import com.google.gson.annotations.Expose;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "T_JOB")
@AccessType("field")
public class TJob extends Model {

	/**
	 * 巡检任务名称
	 */
	@Required
	public String T_JOB_NAME;
	/**
	 * 巡检任务描述
	 */
	public String T_JOB_DESC;
	/**
	 * 采集时间频率
	 */
	public String T_JOB_SCHEDULES;
	/**
	 * 创建时间
	 */
	public Date T_JOB_CREATETIME;
	/**
	 * 是否报警
	 */
	public Boolean T_ALARM;
	/**
	 * 是否被下属层级集成
	 */
	public Boolean T_INTERGRATION;
	/**
	 * 是否发布
	 */
	public Boolean T_RELEASE = false;
	/**
	 * 巡检脚本id
	 */
	@Expose
	@ManyToMany(targetEntity = TScript.class, fetch = FetchType.EAGER)
	@JoinTable(name = "T_RES_JOB2SCRIPT", joinColumns = @JoinColumn(name = "T_JOB_ID"), inverseJoinColumns = @JoinColumn(name = "T_SCRIPT_ID"))
	public List<TScript> SCRIPT = new LinkedList<>();

	/**
	 * 巡检结果
	 */
	@OneToMany(mappedBy = "JOB", targetEntity = TResult.class)
	public Set<TResult> tResults;
    /**
     * 设备分层
     */
    @ManyToMany(mappedBy="tJobs",targetEntity = TNodeType.class)
    public Set<TNodeType> tNodeTypes = new HashSet<>();

	/** 报告发送人 */
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "T_JOB_DISTRIBUTE")
	public TUserGroup DISTRIBUTE;

	/** 查看角色 */
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "T_JOB_ROLE_SHOW")
	public TUserGroup SHOW;

	/**修改角色*/
	@OneToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "T_JOB_ROLE_MODIFY")
	public TUserGroup MODIFY;

	/** 查看角色 */
	@OneToOne(targetEntity = TUser.class, fetch = FetchType.EAGER ,cascade = CascadeType.MERGE)
	@JoinColumn(name = "T_JOB_USER_ID")
	public TUser CREATE;
}

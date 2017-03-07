package models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.AccessType;

import com.google.gson.annotations.Expose;

import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

/**
 * 设备分层表
 * 
 * @author twx
 *
 */
@Entity
@Table(name = "T_NODE_TYPE")
@AccessType("field")
public class TNodeType extends Model {

	public long pId; // 父节点

	@Required
	public String T_NODE_TYPE_NAME;// 设备分组名称

	public String T_NODE_TYPE_DESC;// 设备描述
	/**
	 * 负责人
	 */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id")
	public TUser T_NODE_TYPE_USER;

	/**
	 * 设备
	 */
	
	@Expose
    @ManyToMany(targetEntity = TNode.class, fetch = FetchType.EAGER)
	@JoinTable(name = "t_res_type2node",joinColumns = @JoinColumn(name = "T_TYPE_ID"), inverseJoinColumns = @JoinColumn(name = "T_NODE_ID"))
	public Set<TNode> tNodes =  new HashSet<>();
	
	/**
	 * 任务
	 */
	
	@Expose
    @ManyToMany(targetEntity = TJob.class, fetch = FetchType.EAGER)
    @JoinTable(name = "t_res_type2job", joinColumns = @JoinColumn(name = "T_TYPE_ID"), inverseJoinColumns = @JoinColumn(name = "T_JOB_ID"))
	public Set<TJob> tJobs = new HashSet<>();

	/**
	 * 巡检结果
	 */
	@OneToMany(mappedBy = "TNODETYPE", targetEntity = TResult.class)
	public Set<TResult> tResults;

	@Required
	public String ROOM_POSITION;// 机房位置

	public String tBusinessType;// 业务系统分类

	public boolean isExtend;// 是否继承

}

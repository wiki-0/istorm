package models;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.AccessType;

import com.google.gson.annotations.Expose;

import play.data.validation.Password;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.db.jpa.Model;

@Entity
@Table(name = "T_NODE")
@AccessType("field")
public class TNode extends Model {

	/** 设备名称 */
	@Unique
	@Required
	public String T_NODE_NAME;
	
	/** IP地址 */
	@Unique
	@Required
	public String T_NODE_IP;
	/** 账号 */
	@Required
	public String T_NODE_ACCOUNT;
	/** 密码 */
	@Required
	@Password
	public String T_NODE_PWD;
	/** ssh或telnet登入方式 */
	public String T_NODE_LOGINTYPE;
	/** 业务系统*/
	public String T_NODE_SYSTEM;
	/** 设备负责人 */
	public String T_NODE_CONTACTS;
	/** 操作系统 */
	public String T_NODE_OS;
	/** 厂商 */
	public String T_NODE_VENDOR;
	/** 机房 */
	public String T_NODE_ROOM;
	/** 设备类型 */
	public String T_NODE_DEVICETYPE;
	/** 型号 */
	public String T_NODE_MODEL;
	/** 序列号 */
	public String T_NODE_SN;
	/** 部门 */
	public String T_NODE_DEPARTMENT;
	/** SFTP或FTP上传脚本方式 */
	public String T_NODE_UPLOADTYPE;
	/** 上传脚本保存目录 */
	public String T_NODE_LOCALPATH;

	
	 /**
     * 设备分层
     */
	@ManyToMany(mappedBy="tNodes",targetEntity = TNodeType.class)
	public Set<TNodeType> tNodeTypes = new HashSet<>();
	/**
	 * 巡检结果
	 */
	@OneToMany(mappedBy = "NODE", targetEntity = TResult.class)
	public Set<TResult> tResults;
}

package models;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import models.deadbolt.Role;
import models.deadbolt.RoleHolder;

import org.hibernate.annotations.AccessType;
import play.data.validation.Email;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;
import javax.persistence.OneToMany;

@Entity
@Table(name = "T_USER", uniqueConstraints = { @UniqueConstraint(columnNames = { "T_USER_NAME", "T_USER_PERMISSION" }) })
@AccessType("field")
public class TUser extends Model implements RoleHolder {

	/** 用户账号 */
	@Required
	public String T_USER_NAME;
	/** 密码 */
	@Password
	public String T_USER_PASSWORD;
	/** 显示名称 */
	public String T_USER_DISPLAY_NAME;
	/** 部门 */
	public String T_USER_DEPARTMENT;
	/** 手机 */
	public String T_USER_TELPHONE;
	/** 邮箱 */
	@Email
	public String T_USER_MAIL;
	/** 权限 */
	@Required
	public String T_USER_PERMISSION;

	@OneToMany(mappedBy = "USER", targetEntity = TResult.class)
	public Set<TResult> RESULT;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "T_NODE_TYPE_USER")
	public List<TNodeType> tNodeType;


	
	public static TUser connect(String username, String password) {
		return find("byT_USER_NAMEAndT_USER_PASSWORD", username, password).first();
	}

	public static TUser getByName(String username) {
		return find("byT_USER_NAME", username).first();
	}

	public List<? extends Role> getRoles() {
		return Arrays.asList(new TRole(T_USER_PERMISSION));
	}

	public static void updOrInsertAD(List<TUser> userList) {
		if (userList != null && userList.size() > 0) {
			for (TUser tUser : userList) {
				TUser oldUser = TUser.getByName(tUser.T_USER_NAME);
				if (oldUser != null) {
					oldUser.T_USER_NAME = tUser.T_USER_NAME;
					oldUser.T_USER_DISPLAY_NAME = tUser.T_USER_DISPLAY_NAME;
					oldUser.T_USER_DEPARTMENT = tUser.T_USER_DEPARTMENT;
					oldUser.T_USER_MAIL = tUser.T_USER_MAIL;
					oldUser.save();
				} else {
					tUser.save();
				}
			}

		}

	}
}

package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Access;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.db.jpa.Model;
@Entity
@Table(name="T_USER_GROUP")
@AccessType("field")

public class TUserGroup extends Model{
	/** 组名称 */
	public String T_GROUP_NAME;
	

}

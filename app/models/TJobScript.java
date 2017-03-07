package models;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import com.google.gson.annotations.Expose;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="T_RES_JOB2SCRIPT")
@AccessType("field")
public class TJobScript extends Model {
	
	/** 任务名*/

	public long T_JOB_ID;
	/** 主机
     */
	public long T_SCRIPT_ID;
	
}

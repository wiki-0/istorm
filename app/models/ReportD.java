package models;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "T_ReportD", uniqueConstraints = { @UniqueConstraint(columnNames = { "T_Report_VAR" }) })
public class ReportD extends Model {
	
	/** 报表替换变量*/
	@Required
	public String T_Report_VAR;
	/** 报表任务  */
	public String T_Report_JOB ;
	/** 报表分层  */
	public String T_Report_NODETYPE ;
	/** 报表设备  */
	public String T_Report_NODE ;	
	/** 报表脚本 */
	public String T_Report_SCRIPT ;	
	/** 开始时时间  */
	public String T_Report_STARTTIME ;
	/** 结束时时间  */
	public String T_Report_ENDTIME ;

}

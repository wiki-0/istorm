package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="T_ReportE")
public class ReportE extends Model {
	
	/** DOC报表模板*/
	@Required
	public String T_FILE_DOC;
	/** XML报表模板*/
	@Required
	public String T_FILE_XML;	
    
    public String T_FILE_NAME;

}

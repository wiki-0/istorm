package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="T_PARAMS")
@AccessType("field")
public class TParams extends Model
{
	public String T_DRM_PARAMS_DESC;//描述
	public String T_DRM_PARAMS_NAME;//key
	public String T_DRM_PARAMS_VALUE;//value
	public String T_DRM_PARAMS_ENABLE;//0能用，1不能用

}

package models;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import play.db.jpa.Model;

@Entity
@Table(name="T_RELEVANCY_GROUP")
@AccessType("field")
public class TRelevancyUserGroup extends Model{
	
	public String USER_ID;
	
	public String USER_GROUP_ID;
}

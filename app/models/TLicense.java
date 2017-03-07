package models;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.AccessType;

import controllers.CRUD.Exclude;
import controllers.CRUD.Hidden;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name = "T_LICENSE")
@AccessType("field")
public class TLicense extends Model {

	/**
	 * 验证状态
	 * 0:拒绝
	 * 1:同意
	 */
	@Hidden
	public String T_LICENSE_STATUS = "0";

	/**
	 * mac地址
	 */
	public String T_LICENSE_MAC;

	/**
	 * 密钥对应用户名
	 */
	public String T_LICENSE_USER_NAME;

	/**
	 * 密钥文件名字
	 */
	public String T_LICENSE_FILE_NAME;

	/**
	 * 密钥内容
	 */
	@Lob
	public String T_LICENSE_CONTENT;

}

package models;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.AccessType;

import play.data.validation.Required;
import play.db.jpa.Model;

//虚拟机实体对象
@Entity
@Table(name="T_RES_VM",uniqueConstraints={@UniqueConstraint(columnNames={"IP","WORK_ORDER_ID","VS_ID"})})
@AccessType("field")
public class TResVM extends Model {
	//虚机名称	
	@Required
	public String NAME;
	//系统信息	
	public String SYS_INFO;
	//操作系统	
	public String OS;
    //IP地址	
	@Required
	public String IP;
	//安全等保	String
	public String SECURITY_LEVEL;
	//服务器角色	String
	public String SERVER_ROLE;
	//虚机配置	
	public Integer CPU_NUMBER;
	
	public Integer MEMORY_SIZE;
	//功能域	
	public String DMZ;
	//集群名称	
	public String CLUSTER_NAME;
	//集群IP	
	public String CLUSTER_IP;
	//宿主机	
	public String PARENT_HOST;
	//虚拟机状态	
	public String STATUS;
	//系统盘大小	(GB)
	public Integer ROOT_VG_SIZE;
	//数据盘大小	(GB)
	public Integer DATA_VG_SIZE;	
	
	//虚拟机磁盘
	public Double Disk_Total;
	
	//更新时间	
	@Required
	public String UPDATE_TIME;
	
	//创建时间	
	@Required
	public String CREATE_TIME;
	
	//挂起时间
	public String PENDING_TIME;
	
	//工单编号
	@Required
	public Long WORK_ORDER_ID;
	
	//虚拟机ID
	public String VS_ID;
	/**
	 * 设备ID
	 */
	public String DEVICE_ID;
	
	//物理cpu
	public Integer VSPCPU;  
	
	public String VM_UUID;
	
	public Integer ELEMENT_ID;
	
	@Required
	public String DataStore;
	//内存使用率
	@Required
	public String MemoryUSERate;
	
	//CPU使用率
	@Required
	public String CpuUSERate;
	
	//磁盘利用率
	@Required
	public Double DiskUSERate;
	
	//Rootvg利用率
	public String RootVgRate;
	
    public Date EXPIRE_TIME; //虚拟机到期时间
   
    //@Transient
	//public final String htmlAlert = "";
    
    @Transient
    public String expireTimeStr;
	
	//DataVg 利用率
	@Transient
	public String DataVgRate;
	
	@Transient
	public String vm_Time;
	
	//虚拟机用户名称
	@Transient
	public String v_UserName;
	//虚拟机密码
	@Transient
	public String v_PassWord;
	
	//虚拟机新密码
	@Transient
	public String New_PassWord;
	//新的用户名
	@Transient
	public String New_UserName;
	
	public static List<TResVM> getVMByName(String vmName){
		List<TResVM> objects = null;
		try{
		   objects = TResVM.find("select t from TResVM t where t.NAME = '"+vmName+"' AND t.STATUS <> 'delete' ").fetch();
		}catch(Exception e){
		   e.printStackTrace();	
		}
		return objects;
	}
}

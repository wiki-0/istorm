package util;

import javax.servlet.ServletContext;

public class Constants {
	
	public static String TEMPLATE_PATH ="";
	public static String WEEKLY_REPORT_PATH = "";
	public static String ORACLE = "ORACLE";
	public static String MYSQL = "MYSQL";
	public static String DB2 = "DB2";
	public static String REPORT_TYPE_NORMAL = "report";
	public static String REPORT_TYPE_BUSINESS = "business";
    public static String KPI_SUB_TYPE_BASE = "base";
    public static String KPI_SUB_TYPE_EXTEND = "extend";
    public static String KPI_QUERY_INTERVAL_QUARTER = "quarter";
    public static String KPI_QUERY_INTERVAL_HOUR = "hour";
    public static String KPI_QUERY_INTERVAL_DAY = "day";
    
    public static String KPI_TYPE_IOPS = "iops";
    public static String KPI_TYPE_KBPS = "kbps";
    public static String KPI_TYPE_HITRATIO = "hitratio";
    public static String KPI_TYPE_RESPONSETIME = "responsetime";
    
    public static String BUSINESS_TOP_IOPS_N = "bs_021001";
    public static String BUSINESS_TOP_KBPS_N = "bs_021002";
    public static String BUSINESS_TOP_HITRATIO_N = "bs_021003";
    public static String BUSINESS_TOP_RESPONSETIME_N = "bs_021004";
    
    public static String DEVICE_TYPE_VOLUME = "volume";
    public static String DEVICE_TYPE_HOST = "Host";
    public static String DEVICE_TYPE_SWITCH = "Switch";
    public static String DEVICE_TYPE_SUBSYSTEM = "Subsystem";
    public static String WEB_PATH = "";
    public static String TAG_CONFIG_PATH = "";
    
    public static String USER_NAME = "username";
    
    
    public static ServletContext sc = null;
    
    /*
     * Export Report Label
     */
    public static String LABEL_TIME_RANGE = "time_range";
    public static String LABEL_CONFIG_HOST = "HostTable";
    public static String LABEL_CONFIG_SWTICH = "SwitchTable";
    public static String LABEL_CONFIG_STORAGE = "StorageTable";
    public static String LABEL_EVENT_DEVICE = "event_device";
    public static String LABEL_EVENT_STATUS = "event_severity";
    public static String LABEL_EVENT_TABLE = "EventTable";
    public static String LABEL_STORAGE_PRF_IOPS = "storage_prf_iops";
    public static String LABEL_STORAGE_PRF_RIOPS = "storage_prf_riops";
    public static String LABEL_STORAGE_PRF_WIOPS = "storage_prf_wiops";
    public static String LABEL_STORAGE_PRF_KBPS = "storage_prf_kbps";
    public static String LABEL_STORAGE_PRF_RKBPS = "storage_prf_rkbps";
    public static String LABEL_STORAGE_PRF_WKBPS = "storage_prf_wkbps";
    public static String LABEL_STORAGE_PRF_HITRATIO = "storage_prf_hitratio";
    public static String LABEL_STORAGE_PRF_RHITRATIO = "storage_prf_rhitratio";
    public static String LABEL_STORAGE_PRF_WHITRATIO = "storage_prf_whitratio";
    public static String LABEL_STORAGE_PRF_RIOPS_RATE = "storage_prf_riops_rate";
    public static String LABEL_STORAGE_PRF_WIOPS_RATE = "storage_prf_wiops_rate";
    public static String LABEL_STORAGE_PRF_SEQ_IOPS = "storage_prf_seq_iops";
    public static String LABEL_STORAGE_PRF_RAN_IOPS = "storage_prf_random_iops";
    public static String LABEL_SWITCH_PRF_PCKTS = "switch_prf_pckts";    
    public static String LABEL_SWITCH_PRF_DATARATE = "switch_prf_data_rate";   
    public static String LABEL_TOP_PORT_DISCARD_C3 = "top_port_discard_c3";
    public static String LABEL_TOP_PORT_ENC_IN = "top_port_enc_in";
    public static String LABEL_TOP_PORT_LINK_FAILURES = "top_port_linkfailures";
    public static String LABEL_TOP_VOLUME_IOPS = "top_volume_iops";
    public static String LABEL_TOP_VOLUME_KBPS = "top_volume_kbps";
    public static String LABEL_TOP_VOLUME_RESPONSE_TIME = "top_volume_response_time";
    
    public static String LABEL_TOP_N_CPU_HOST = "CpuTable";
    public static String LABEL_TOP_N_MEMORY_HOST = "MemoryTable";
    
    
    public static Float THREHOLD_VALUE = null;
    
    
}
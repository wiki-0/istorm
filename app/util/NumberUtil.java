package util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtil {
	
	
	/**
	 * 百分比转换成小数
	 * @author ShenPS
	 * @param @param percentage
	 * @param @return
	 * return Double
	 * @throws 
	 * @date 2015年12月3日 下午2:10:00
	 */
	public static Double percentageToDouble(String percentage){	
		NumberFormat nf=NumberFormat.getPercentInstance();
		Number m = null;
		try {
			m = nf.parse(percentage);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return m.doubleValue();		
	}
	/**
	 * 计算百分比
	 *
	 * @author ShenPS
	 * @param @param x
	 * @param @param total
	 * @param @return
	 * return String
	 * @throws 
	 * @date 2015年12月2日 上午11:33:40
	 */
	public static String getPercent(int x,int total){  
		   String result="";//接受百分比的值  
		   double x_double=x*1.0;
		   double tempresult=x_double/total;  
		   DecimalFormat df1 = new DecimalFormat("0%");    //##.00%   百分比格式，后面不足2位的用0补齐  		       
		   result= df1.format(tempresult);    
		   return result;  
		}

}

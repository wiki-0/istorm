package util;

import java.io.File;
import java.io.FileWriter;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mysql.jdbc.Connection;

import models.TJob;
import models.TNode;
import models.TResult;
import play.Logger;
import play.Play;
import play.db.jpa.JPA;
import util.jdbc.JDBCParser;
import util.quartz.QuartzManager;
import util.quartz.QuartzParser;

public class CommonUtil {
	/**
	 * @Title: createShell
	 * @Description: create shell file
	 * @param @param
	 *            filepath file path
	 * @param @param
	 *            text shell text
	 * @param @throws
	 *            Exception
	 */
	public static void createShell(String filepath, String text) throws Exception {
		File file = new File(filepath);
		File file2 = new File(file.getParent());
		file2.mkdirs();
		file.createNewFile(); // create file
		FileWriter fw = new FileWriter(file);
		fw.write(text);
		fw.close();
	}

	/** 判断一个数字是否在一个数组中 */
	public static boolean isComprise(long value, Long[] array) {
		// 现在开始对 数据是否存在进行判断
		for (long i : array) {
			if (i == value) {
				return true;
			}
		}
		return false;
	}
}

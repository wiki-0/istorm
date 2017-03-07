package jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import models.TAlarmLV;
import models.TResult;
import models.TScript;
import play.jobs.Job;
import play.jobs.On;

@On("0 */30 * * * ?")
public class ParseResults extends Job {

	public void doJob() {
		List<TResult> list = TResult.find("T_RESULT_ALARM_STATUS= '0' and T_RESULT_STATUS='OK'").fetch();
		System.out.println(list.isEmpty());
		List<TResult> listError = TResult.find("T_RESULT_ALARM_STATUS= '0' and T_RESULT_STATUS='ERROR'").fetch();
		System.out.println(list.isEmpty());
		for (TResult tResult : listError) {
			tResult.T_RESULT_ALARM_STATUS = "1";
			tResult.save();
		}

		for (TResult result : list) {
			TScript script = result.tScript;

			if (script.T_ALARM_THRESHOLD_TYPE.equals("") || script.T_ALARM_THRESHOLD_TYPE.isEmpty()
					|| script.T_ALARM_THRESHOLD_TYPE == null) {
				result.T_RESULT_ALARM_STATUS = "1";
				result.save();
			} else if (script.T_ALARM_THRESHOLD_TYPE.equals("1") || script.T_ALARM_THRESHOLD_TYPE.equals("0")) {
				List<TAlarmLV> tAlarmLVs = TAlarmLV.find("tScript.id", script.id).fetch();
				String resultLv = null;
				for (TAlarmLV tAlarmLV : tAlarmLVs) {
					System.out.println(tAlarmLV.T_ALARM_LEVEL);
					String fazhi = tAlarmLV.T_ALARM_THRESHOLD;
					String[] parseFaZhi = fazhi.split(",");
					// 关键字
					if (script.T_ALARM_THRESHOLD_TYPE.equals("1")) {
						if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("like")) {
							if (result.T_RESULT_OUTPUT.contains(fazhi)) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("and")) {
							boolean flag = true;
							for (String string : parseFaZhi) {
								if (result.T_RESULT_OUTPUT.contains(string.trim())) {
									flag = true;
								} else {
									flag = false;
									break;
								}
							}
							if (flag) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("or")) {
							boolean flag = true;
							for (String string : parseFaZhi) {
								if (result.T_RESULT_OUTPUT.contains(string.trim())) {
									flag = true;
									break;
								} else {
									flag = false;
								}
							}
							if (flag) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("not")) {
							boolean flag = true;
							for (String string : parseFaZhi) {
								if (!result.T_RESULT_OUTPUT.contains(string.trim())) {
									flag = true;
								} else {
									flag = false;
									break;
								}
							}
							if (flag) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						}
					} else if (script.T_ALARM_THRESHOLD_TYPE.equals("0")) {// 阈值
						float parseFloat = Float.parseFloat(fazhi);
						float max = extractMaxDigital(result.T_RESULT_OUTPUT);
						System.out.println("解析最大值" + max);
						if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase(">")) {
							if (max > parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase(">=")) {
							if (max >= parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("<")) {
							if (max < parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("<=")) {
							if (max <= parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("=")) {
							if (max == parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						} else if (tAlarmLV.T_ALARM_RELATION.equalsIgnoreCase("!=")) {
							if (max != parseFloat) {
								resultLv = tAlarmLV.T_ALARM_LEVEL;
								break;
							}
						}
					}
				}
				if (resultLv == null) {
					result.T_RESULT_ALARM_LEVEL = "0";
				} else if (resultLv.equals("严重")) {
					result.T_RESULT_ALARM_LEVEL = "1";
				} else if (resultLv.equals("主要")) {
					result.T_RESULT_ALARM_LEVEL = "2";
				} else if (resultLv.equals("次要")) {
					result.T_RESULT_ALARM_LEVEL = "3";
				} else if (resultLv.equals("提示")) {
					result.T_RESULT_ALARM_LEVEL = "4";
				} else {
					result.T_RESULT_ALARM_LEVEL = "0";
				}
				result.T_RESULT_ALARM_STATUS = "1";
				result.save();
			}
		}
	}

	public static float extractMaxDigital(String out) {
		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\d+\\.?\\d+");
		Matcher matcher = pattern.matcher(out);
		while (matcher.find()) {
			list.add(matcher.group());
		}
		float max = (float) 0.0;
		for (String str : list) {
			float f1 = Float.parseFloat(str);
			if (f1 > max)
				max = f1;
		}
		return max;
	}
}

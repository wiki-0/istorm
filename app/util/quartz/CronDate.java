package util.quartz;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.quartz.CronTrigger;

/**
 *  Cron 表达式解析
 *  
 */
public class CronDate  extends CronTrigger{

    // trigger
    private final static String TRIGGER_PREVIEW_SCHEDULE = "triggerPreviewSchedule";
    protected final static String GROUP_PREVIEW_SCHEDULE = "groupPreviewSchedule";

    public CronDate() {
        // TODO Auto-generated constructor stub
    }
    protected List<Date> getNextSchedules(final String cron ,String startTime) throws ParseException {
        final List<Date> results = new ArrayList<Date>();
        final CronTrigger cronTrigger = new CronTrigger(TRIGGER_PREVIEW_SCHEDULE, GROUP_PREVIEW_SCHEDULE, cron);
        DateFormat fmt =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date staTime = fmt.parse(startTime+" 00:00:00");
        Date endTime = fmt.parse(startTime+" 23:59:59");
        while (staTime.getTime()<=endTime.getTime()){
            staTime = cronTrigger.getFireTimeAfter(staTime);
            if (staTime.getTime()<=staTime.getTime()&&staTime.getTime()<=endTime.getTime()){
                results.add(staTime);
            }
        }
        return Collections.unmodifiableList(results);
    }
    protected List<Date> getNext2Schedules(final String cron ,Date firedate ,Date enddate) throws ParseException {
        final List<Date> results = new ArrayList<Date>();
        final CronTrigger cronTrigger = new CronTrigger(TRIGGER_PREVIEW_SCHEDULE, GROUP_PREVIEW_SCHEDULE, cron);
        while (firedate.getTime()<=enddate.getTime()){
            firedate = cronTrigger.getFireTimeAfter(firedate);
            results.add(firedate);
        }
        return Collections.unmodifiableList(results);
    }
    //获得2个时间段之间的运行时间集合
    public static List<Date> analyzeCron (String default_cron,Date startTime ,Date endTime) throws ParseException {
        final CronDate tsd = new CronDate();
        final List<Date> dateList = tsd.getNext2Schedules(default_cron,startTime,endTime);
        final List<Date> results = new ArrayList<Date>();
        for (final Date da : dateList) {
            results.add(da);
        }
        return results;
    }
    //获得当天的运行集合
    public static List<Date> analyzeToday (String default_cron,String startTime) throws ParseException {
        final CronDate tsd = new CronDate();
        final List<Date> dateList = tsd.getNextSchedules(default_cron,startTime);
        final List<Date> results = new ArrayList<Date>();
        for (final Date da : dateList) {
            results.add(da);
        }
        return results;
    }
}
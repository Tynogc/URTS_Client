package utility;

public class AlarmValue {
	
	public int id;
	public int menuID;
	
	public String name = "";
	
	public String time1 = "";
	public String time2 = "";
	
	public int alarmStatus = ALL_OK;
	public static final int ALL_OK = 0;
	public static final int ALL_WARN = 213;
	public static final int ALL_ALARM = 1366;
	public static final int ALL_ALARM_AKUT = 4829;
	
	private AlarmValue connected;
	
	public void resolve(){
		alarmStatus = ALL_OK;
		name = "";
		time1 = "";
		time2 = "";
		id = 0;
		menuID = 0;
		AlarmValue a = connected;
		connected = null;
		if(a!=null)
			a.resolve();
	}
	
	public void setSuperAlarm(AlarmValue a){
		if(a.alarmStatus > alarmStatus){
			connected = a;
			alarmStatus = a.alarmStatus;
			id = a.id;
			name = a.name;
			time1 = a.time1;
			time2 = a.time2;
		}
	}
	
	public void setTime(){
		long t = System.currentTimeMillis();
		time1 = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date (t));
		time2 = new java.text.SimpleDateFormat("dd.MM.yy").format(new java.util.Date (t));
	}
	
}

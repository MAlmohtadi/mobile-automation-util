package jo.aspire.mobile.automationUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
        public static boolean isDebugLogginEnabled(){
            return true;
        }

       	public static void log(Object o) {
       		System.out.println(o.toString());
       	}

       	public static void debug(Object o)
       	{
       		if(isDebugLogginEnabled())
       		{
       			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
       			Date date = new Date();
       			System.out.println();
       			
       			String classAndMethod = "Class="+ Thread.currentThread().getStackTrace()[2].getClassName().toString() + " Method=" + Thread.currentThread().getStackTrace()[2].getMethodName().toString() + " Line=" + Thread.currentThread().getStackTrace()[2].getLineNumber() +" Message=";
       			System.out.println("DEBUG ("+ dateFormat.format(date) +"): " + classAndMethod + o);
       		}
       	}
}

package jo.aspire.web.automationUtil;

import java.util.HashMap;

public class StateHelper {
       private static ThreadLocal<HashMap<String, Object>> CrossStepState = new ThreadLocal<HashMap<String, Object>>();
       private static HashMap<String, Object> CrossStoryState = new HashMap<String, Object>();

       // Save key with Object Value
       public static void setStepState(String key, Object value) {
              CrossStepState.get().put(key, value);

       }
       
       public static Object getStepState(String key) {
              return CrossStepState.get().get(key);
       }

       public static void clearStepState() {
           CrossStepState.get().clear();

       }
       
       
       public static boolean checkStepStateContainsKey (String keyState)
       {
    	  
    	   return CrossStepState.get().containsKey(keyState);
       }
       
      
       
       // Save key with Object Value
       public static void setStoryState(String key,
                     Object LocationObject) {
    	   CrossStoryState.put(key, LocationObject);
       }
       
       public static Object getStoryState(String key) {
              return CrossStoryState.get(key);
       }


       public static boolean checkStoryStateContainsKey (String keyState)
       {
    	  
    	   return CrossStoryState.containsKey(keyState);
       }
             
}


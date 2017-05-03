import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Map;

/**
 * Tools Class
 * Created by gawainx on 2017/5/2.
 */
public class GAUtils {
    public static String JsonBuilder(Map<String,String> src){
        //Build Json
        JSONObject jsonObj = new JSONObject();
        if(src!=null){
            try{
                for(Map.Entry<String,String> iter:src.entrySet()){
                    jsonObj.put(iter.getKey(),iter.getValue());
                }
            }catch(JSONException e){
                return null;
            }
        }
        return jsonObj.toString();
    }

    static String JSONParser(String src, String field){
        //Parse Json String
        try{
            JSONTokener jsonTokener = new JSONTokener(src);
            JSONObject tmp = (JSONObject)jsonTokener.nextValue();
            return tmp.getString(field);
        }catch(JSONException e){
            return null;
        }
    }
}

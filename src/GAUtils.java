import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.HashMap;
import java.util.Map;

/**
 * Tools Class
 * Created by gawainx on 2017/5/2.
 */
class GAUtils {
    static String JsonBuilder(Map<String, String> src){
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
    static Map<String,String> JsonArrayParser(String jsonSrc){
        if(jsonSrc == null) return null;
        Map<String,String> ans = new HashMap<>();
        try {
            JSONArray jsonArr = new JSONArray(jsonSrc);
            for (int i = 0; i < jsonArr.length(); i++){
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                String key = jsonObj.getString("ID");
                String val = jsonObj.getString("IP");
                ans.put(key,val);
            }
        }catch (JSONException e){
            return null;
        }
        return ans;
    }
}

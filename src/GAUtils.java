import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.*;

/**
 * Tools Class
 * Created by gawainx on 2017/5/2.
 */
class GAUtils {
    /*TODO:Modify,id-IP instead name-IP*/
    static Map<String,String> routerTable = new HashMap<>();

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
    static Map<String,String> JsonArrayParser(String jsonSrc,String keys,String vals){
        /*
          Router JsonParser
         */
        if(jsonSrc == null) return null;
        Map<String,String> ans = new HashMap<>();
        try {
            JSONArray jsonArr = new JSONArray(jsonSrc);
            for (int i = 0; i < jsonArr.length(); i++){
                JSONObject jsonObj = jsonArr.getJSONObject(i);
                String key = jsonObj.getString(keys);
                String val = jsonObj.getString(vals);
                ans.put(key,val);
            }
        }catch (JSONException e){
            return null;
        }
        return ans;
    }

    static Map<String,String> JsonArrayParser(String jsonSrc, String key){
        //return key-json map
        Map<String,String> ans = new HashMap<>();
        if((jsonSrc == null) || (key == null)){
            return null;
        }
        try{
            JSONArray jsonArray = new JSONArray(jsonSrc);
            for(int i = 0; i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String val = jsonObject.getString(key);
                ans.put(val,jsonObject.toString());
            }
            return ans;
        }catch (JSONException e){
            return null;
        }
    }
    static String FormatJson(String jsonSrc){
        //Format Json String to get nature output
        StringBuilder ans = new StringBuilder();
        for(int i=0;i<jsonSrc.length();i++){
            char c = jsonSrc.charAt(i);
            if(c != '}'){
                ans.append(c);
                if((c == '{') || (c == ',')){
                    ans.append("\n\t");
                }
            }else{
                ans.append("\n");
                ans.append(c);
            }
        }
        return ans.toString();
    }
    static List<String> SplitJsonArr(String jsonSrc){
        List<String> ans = new ArrayList<>();
        if(jsonSrc == null) return null;
        try{
            JSONArray jsonArray = new JSONArray(jsonSrc);
            for(int i = 0; i < jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ans.add(jsonObject.toString());
            }
            return ans;
        }catch (JSONException e){
            return null;
        }
    }
}

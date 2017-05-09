import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gawainx on 2017/5/4.
 */
public class JsonArrayTest {
    public static void jsonArrayTest() throws JSONException {
        JSONArray jsonarray = new JSONArray("[{'name':'xiazdong','age':20},{'name':'xzdong','age':15}]");
        for (int i = 0; i < jsonarray.length(); i++) {
            JSONObject jsonobj = jsonarray.getJSONObject(i);
            String name = jsonobj.getString("name");
            int age = jsonobj.getInt("age");
            System.out.println("name = " + name + ",age = " + age);
        }
    }
    public static void jsonObjectAndArrayTest() throws JSONException {
        String jsonstring = "{'name':'xiazdong','age':20,'book':['book1','book2']}";
        JSONObject jsonobj = new JSONObject(jsonstring);

        String name = jsonobj.getString("name");
        System.out.println("name" + ":" + name);

        int age = jsonobj.getInt("age");
        System.out.println("age" + ":" + age);

        JSONArray jsonarray = jsonobj.getJSONArray("book");
        for (int i = 0; i < jsonarray.length(); i++) {
            String book = jsonarray.getString(i);
            System.out.println("book" + i + ":" + book);
        }
    }
    static void buildJSONArray() throws JSONException {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("name","lll");
        jsonObj.put("phone","12349398242");
        JSONArray ja = new JSONArray();
        ja.put(jsonObj);
        JSONObject obj2 = new JSONObject();
        obj2.put("idd","lsa");
        obj2.put("phone","1982108392");
        ja.put(obj2);
        System.out.println(ja.toString());
    }
    public static void main(String[] args) throws JSONException {
        jsonArrayTest();
        jsonObjectAndArrayTest();
        buildJSONArray();
    }
}

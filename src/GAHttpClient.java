import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Created by gawainx on 2017/4/24.
 * Http Client
 */
public class GAHttpClient {
    private String targetURL;
    GAHttpClient(String url){
        this.targetURL = url;
    }
    public String handleGetResponseBody() throws IOException {
        //Send GET Request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(targetURL);
        request.setHeader("User-Agent","IOT Device");
        CloseableHttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity());
    }
    public String handlePutResponseBody(String data) throws IOException{
        //Send Put Request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut(targetURL);
        request.setHeader("User-Agent","IOT device/Communicate Module");
        request.setEntity(new StringEntity(data));
        CloseableHttpResponse response = httpClient.execute(request);
        return EntityUtils.toString(response.getEntity());
    }
    public String handleDeleteRequest(String data){
        /* TODO : How to delete data from Registry */
        return "DONE";
    }
    public static String JSONParser(String src,String field){
        //Parse Json String
        try{
            JSONTokener jsonTokener = new JSONTokener(src);
            JSONObject tmp = (JSONObject)jsonTokener.nextValue();
            return tmp.getString(field);
        }catch(JSONException e){
            return "NIL";
        }
    }
}

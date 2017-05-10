import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by gawainx on 2017/4/24.
 * Http Client
 */
class GAHttpClient {
    private String targetURL;
    GAHttpClient(String url){
        this.targetURL = url;
    }
    String handleGetResponseBody() throws IOException {
        //Send GET Request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(targetURL);
        request.setHeader("User-Agent","IOT Device");
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 200){return EntityUtils.toString(response.getEntity());}
        else{
            //404
            return null;
        }
    }
    String handleGetResponseBody(String data) throws IOException {
        //send GET request with custom data
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(targetURL);
        request.setHeader("getIDs",data);
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 200){
            return EntityUtils.toString(response.getEntity());
        }else{
            return null;
        }
    }
    String handlePutResponseBody(String data) throws IOException{
        //Send Put Request
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut(targetURL);
        request.setHeader("User-Agent","IOT device/Communicate Module");
        request.setEntity(new StringEntity(data));
        CloseableHttpResponse response = httpClient.execute(request);
        if(response.getStatusLine().getStatusCode() == 201){
            return EntityUtils.toString(response.getEntity());
        }else{
            return "failed";
        }
//        return EntityUtils.toString(response.getEntity());
//        return response.getStatusLine().toString();
    }
    int handlePostRequest(String data) throws IOException{
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut(targetURL);
        request.setHeader("User-Agent","IOT device/Communicate Module");
        request.setEntity(new StringEntity(data));
        CloseableHttpResponse response = httpClient.execute(request);
        return response.getStatusLine().getStatusCode();
    }
    int handleDeleteRequest(String name) throws IOException{
        /* TODO : How to delete data from Registry */
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete(targetURL+name);
        request.setHeader("User-Agent","IOT device/Communicate Module");
        CloseableHttpResponse response = httpClient.execute(request);
        return response.getStatusLine().getStatusCode();
    }

}

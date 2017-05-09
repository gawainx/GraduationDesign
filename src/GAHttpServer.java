import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by gawainx on 2017/4/24.
 * HTTP Server For REST
 */
public class GAHttpServer implements Runnable{
    static private String statusJSON;//return device status info
    public static void setStatusJSON(String json){
        statusJSON = json;
    }
    public static String getStatusJSON(){
        return statusJSON;
    }

    @Override
    public void run() {
        try{
            String deviceName = "local";
            if(statusJSON!=null){
                deviceName = GAUtils.JSONParser(statusJSON,"Name");
            }
            InetSocketAddress address = new InetSocketAddress(8080);
            HttpServer server = HttpServer.create(address,0);
            server.createContext("/"+deviceName,new RootHandler());
            server.createContext("/deviceLogic",new ForwardHandler());
            server.createContext("/apis",new InterfaceHandler());
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
class RootHandler implements HttpHandler{
    //Root Path Handler
    @Override
    public void handle(HttpExchange httpExchange) throws IOException{
        String request = httpExchange.getRequestMethod();
        if(request.equalsIgnoreCase("GET")){
            //handle get method
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type","text/plain");
            httpExchange.sendResponseHeaders(200,0);

            OutputStream responseBody = httpExchange.getResponseBody();
            responseBody.write(GAHttpServer.getStatusJSON().getBytes());
            responseBody.close();
        }else{
            httpExchange.sendResponseHeaders(400,0);
        }
    }
}
class ForwardHandler implements HttpHandler{
    //handle forward request
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String request = httpExchange.getRequestMethod().toUpperCase();
        switch (request){
            case ("GET"):{
                //Handle GET method
                break;
            }case("PUT"):{
                //handle PUT Request method
                //forward data to logic module
                Headers responseHeader = httpExchange.getResponseHeaders();
                responseHeader.set("Content-Type","text/plain");
                httpExchange.sendResponseHeaders(201,0);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(httpExchange.getRequestBody()));
                StringBuffer tmp = new StringBuffer();// any need to consider about thread safety ?
                String line;
                while((line = in.readLine())!=null){
                    if(line.length() == 0) break;
                    tmp.append(line);
                }
                UpdateClient uc = new UpdateClient("Logic",4096);//update Data
                int ucRes = uc.send(tmp.toString());
                OutputStream responseBody = httpExchange.getResponseBody();
                String res;
                if(ucRes == 1){
                    res = "success\n\n";
                }else{
                    res = "failed\n\n";
                }
                responseBody.write(res.getBytes());
                responseBody.close();
                break;
            }
        }

    }
}
class InterfaceHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String requestMethod = httpExchange.getRequestMethod();
        if(requestMethod.equalsIgnoreCase("PUT")
                ||requestMethod.equalsIgnoreCase("DELETE")){
            Headers responseHeaders = httpExchange.getResponseHeaders();
            responseHeaders.set("Content-Type","text/plain");
            httpExchange.sendResponseHeaders(200,0);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpExchange.getRequestBody()));
            StringBuffer tmp = new StringBuffer();// any need to consider about thread safety ?
            if(requestMethod.equalsIgnoreCase("PUT")){
                tmp.append("PUT\n");
            }else{
                tmp.append("DELETE\n");
            }
            String line;
            while((line = in.readLine())!=null){
                if(line.length() == 0) break;
                tmp.append(line);
            }
            tmp.append("\n\n");
            UpdateClient uc = new UpdateClient();
            uc.send(tmp.toString());
        }
    }
}
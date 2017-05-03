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
            server.createContext("/info",new RootHandler());
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
        }else if(request.equalsIgnoreCase("PUT")){
            //pass
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
                httpExchange.sendResponseHeaders(200,0);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(httpExchange.getRequestBody()));
                StringBuilder tmp = new StringBuilder();// any need to consider about thread safety ?
                String line;
                while((line = in.readLine())!=null){
                    if(line.length() == 0) break;
                    tmp.append(line);
                }
                OutputStream responseBody = httpExchange.getResponseBody();
                String res = "{\"status\":\"OK\"}";
                responseBody.write(res.getBytes());
                responseBody.close();
                //TODO:improve PUT method, write info to response body
//                System.out.println(tmp.toString());
                UpdateClient uc = new UpdateClient("Logic",4096);//update Data
                uc.send(tmp.toString());
                break;
            }
        }

    }
}
class InterfaceHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

    }
}
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gawainx on 2017/4/24.
 * Receive data from Logic Container and Forward
 */
/*TODO: Registry Device Logic*/

public class ForwardServer implements Runnable{
    private int port;
    /*TODO:Modify,id-IP instead name-IP*/
    private static Map<String,String> routerTable = new HashMap<>();

    static void setRouterTable(String key, String value){
        routerTable.put(key,value);
    }
    ForwardServer(int p){
        this.port = p;
    }
    ForwardServer(){
        this.port = 3776;
    }
    @Override
    public void run() {
        try{
            ServerSocket ss = new ServerSocket(this.port);
            for(;;){
                Socket c =ss.accept();
                ConnectionHandler chndlr = new ConnectionHandler(c);
                new Thread(chndlr).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private class ConnectionHandler implements Runnable{
        private Socket sock;

        ConnectionHandler(Socket sock){
            this.sock = sock;
        }

        @Override
        public void run() {

            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream())
            );
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(sock.getOutputStream())
                )
            ){
                StringBuilder mes = new StringBuilder();
                String line;
                while((line = in.readLine())!=null){
                    if(line.length() == 0) break;
                    mes.append(line);// TODO:any need to handle /n ?
                }
                String dataJson = mes.toString();

                String targetName = GAUtils.JSONParser(dataJson,"targetName");
                String targetDeviceIP = null;
                    /*TODO: Can add Cache Map to storage id-IP Tables here. Need to modify. Name-id-IP*/
                if(targetName.equalsIgnoreCase("Registry")){
                    //report local status to registry
                    //TODO Send info to Registry
                    routerTable.clear();
                    String tmp = MakeStatus(dataJson);
                    if(tmp!=null){
                        GAHttpServer.setStatusJSON(tmp);
                    }
                    targetDeviceIP = routerTable.get("Registry");
                    GAHttpClient upload = new GAHttpClient(targetDeviceIP);
                    upload.handlePostRequest(dataJson);
                }
                else{
                    if(routerTable.containsKey(targetName)){
                        targetDeviceIP = routerTable.get(targetName);
                    }else{
                        GAHttpClient reqIP = new GAHttpClient(
                                "http://api.devices.com:8080/devices"+targetName);//need to modify the ip address
                        targetDeviceIP = GAUtils.JSONParser(
                                reqIP.handleGetResponseBody(),"URI"
                        );
                        routerTable.put(targetName,targetDeviceIP);
                    }
                    GAHttpClient forward = new GAHttpClient(targetDeviceIP);
                    forward.handlePutResponseBody(dataJson);
                }
                out.println("success\n\n");//return operation result
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    private String MakeStatus(String jsonSrc){
        //Make device Status Json
        if(jsonSrc!=null){
            try{
                Map<String,String> tmp = new HashMap<>();
                JSONObject jsonObject = new JSONObject(jsonSrc);
                Iterator iterator = jsonObject.keys();
                while(iterator.hasNext()){
                    String key = (String) iterator.next();
                    if(!key.equalsIgnoreCase("targetName")){
                        String value = jsonObject.getString(key);
                        tmp.put(key,value);
                    }
                }
                return GAUtils.JsonBuilder(tmp);
            }catch(JSONException e){
                return null;
            }
        }else{
            return null;
        }
    }
}

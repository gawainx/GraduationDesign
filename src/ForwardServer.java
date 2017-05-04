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
                String targetID = GAUtils.JSONParser(dataJson,"targetID");
                //targetName and targetID can't be null at the same time
                String targetDeviceIP = null;
                    /*TODO: Can add Cache Map to storage id-IP Tables here. Need to modify. Name-id-IP*/
                if(targetID!=null){
                    if(targetID.equalsIgnoreCase("Registry")){
                        //report device status and update local status
                        GAHttpServer.setStatusJSON(MakeStatus(dataJson));
                        targetDeviceIP = "http://api.mei99.com:6133";
                        GAHttpClient infoUploader = new GAHttpClient(targetDeviceIP);
                        infoUploader.handlePostRequest(dataJson);
                    }else if(routerTable.containsKey(targetID)){
                        targetDeviceIP = routerTable.get(targetID);
                        GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);
                        String forwardRes = forwarder.handlePutResponseBody(dataJson);
                        out.print(forwardRes+"\n\n");//handle transfer result
                    }else{
                        // not exist IP in local
                        GAHttpClient findIPById = new GAHttpClient("http://api.mei99.com:6133/device/ID"+targetID);
                        String resultJson = findIPById.handleGetResponseBody();
                        if(resultJson == null){
                            out.print("404\n\n");
                            out.flush();
                        }else{
                            targetDeviceIP = GAUtils.JSONParser(resultJson,"IP");
                            GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);
                            String forwardRes = forwarder.handlePutResponseBody(dataJson);
                            out.print(forwardRes+"\n\n");//handle transfer result
                        }
                    }
                }else{
                    //targetID is null, use targetName to find IP and forward data
                    GAHttpClient findIPByName = new GAHttpClient("http://api.mei99.com:6133/devices/Name/"+targetName);
                    String resJson = findIPByName.handleGetResponseBody();
                    Map<String,String> resMap = GAUtils.JsonArrayParser(resJson);
                    if(resMap!=null){
                        for(Map.Entry<String,String> iter:resMap.entrySet()){
                            targetDeviceIP = iter.getValue();
                            GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);
                            String forwardRes = forwarder.handlePutResponseBody(dataJson);
                            out.print(forwardRes+"\n\n");//handle transfer result
                        }
                        routerTable.putAll(resMap);
                    }
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    static String MakeStatus(String jsonSrc){
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

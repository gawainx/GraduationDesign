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

    private static String registryURI;

    public static String getRegistryURI() {
        return registryURI;
    }

    public static void setRegistryURI(String registryURI) {
        ForwardServer.registryURI = registryURI;
    }

    static void setRouterTable(String key, String value){
        GAUtils.routerTable.put(key,value);
    }
    ForwardServer(int p){
        this.port = p;
        registryURI = "http://turing.mei99.com:6133";
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
                mes.append("\n\n");
                String dataJson = mes.toString();

                String targetName = GAUtils.JSONParser(dataJson,"targetName");
                String targetID = GAUtils.JSONParser(dataJson,"targetID");
                //targetName and targetID can't be null at the same time
                String targetDeviceIP = null;
                if(targetID != null){
                    if(targetID.equalsIgnoreCase("Registry")){
                        //report device status and update local status
//                        GAHttpServer.setStatusJSON(MakeStatus(dataJson));
//                        GAHttpServer.setStatusJSON(dataJson);
//                        System.out.println("dataJson:"+dataJson);
                        String statusJson = MakeStatus(dataJson);
//                        System.out.println("statusJson in Forward:"+statusJson);
                        GAHttpServer.statusJSON = statusJson;
                        String id = GAUtils.JSONParser(dataJson,"id");
                        targetDeviceIP = registryURI+"/devices/ID/"+id;
                        GAHttpClient infoUploader = new GAHttpClient(targetDeviceIP);
                        infoUploader.handlePostRequest(dataJson+"\n\n");
                    }else if(GAUtils.routerTable.containsKey(targetID)){
                        targetDeviceIP = GAUtils.routerTable.get(targetID);
                        GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);
                        String forwardRes = forwarder.handlePutResponseBody(dataJson);
                        out.print(forwardRes+"\n\n");//handle transfer result
                    }else{
                        // not exist IP in local
                        GAHttpClient findIPById = new GAHttpClient(registryURI+"/devices/ID/"+targetID);
                        String resultJson = findIPById.handleGetResponseBody();
                        if(resultJson == null){
                            out.print("404\n\n");
                            out.flush();
                        }else{
                            targetDeviceIP = GAUtils.JSONParser(resultJson,"ip");
                            GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);
                            String forwardRes = forwarder.handlePutResponseBody(dataJson);
                            out.print(forwardRes+"\n\n");//handle transfer result
                        }
                    }
                }else{
                    //targetID is null, use targetName to find IP and forward data
                    GAHttpClient findIPByName = new GAHttpClient(registryURI+"/devices/Name/"+targetName);
                    String resJson = findIPByName.handleGetResponseBody();
                    Map<String,String> resMap = GAUtils.JsonArrayParser(resJson,"id","ip");//ID-IP map
                    if(resMap!=null){
                        for(Map.Entry<String,String> iter:resMap.entrySet()){

                            targetDeviceIP = iter.getValue();//val->IP
                            GAHttpClient forwarder = new GAHttpClient(targetDeviceIP);

                            String forwardRes = forwarder.handlePutResponseBody(dataJson);
                            out.print(forwardRes+"\n\n");//handle transfer result
                        }
                        GAUtils.routerTable.putAll(resMap);
                    }else{
                        //no matchable devices
                        out.print("404\n\n");
                    }
                }
            }
            catch (IOException e){
                System.out.println("error");
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
                    if((key.equalsIgnoreCase("targetName"))||
                            (key.equalsIgnoreCase("targetID"))){
                    }else{
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

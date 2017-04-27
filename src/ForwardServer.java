import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
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

    public static void setRouterTable(String key,String value){
        routerTable.put(key,value);
    }
    public ForwardServer(int p){
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
                try(BufferedReader in = new BufferedReader(
                        new InputStreamReader(c.getInputStream())
                );
                    PrintWriter out = new PrintWriter(
                            new OutputStreamWriter(c.getOutputStream())
                    )
                ){
                    StringBuilder mes = new StringBuilder();
                    String line;
                    while((line = in.readLine())!=null){
                        if(line.length() == 0) break;
                        mes.append(line);// TODO:any need to handle /n ?
                    }
                    String dataJson = mes.toString();
                    String targetName = GAHttpClient.JSONParser(dataJson,"targetName");
                    String targetDeviceIP = null;
                    /*TODO: Can add Cache Map to storage id-IP Tables here*/
                    if(targetName.equalsIgnoreCase("Local")){
                        //initialize the device info
                        GAHttpServer.setStatusJSON(dataJson);
                    }else{
                        if(routerTable.containsKey(targetName)){
                            targetDeviceIP = routerTable.get(targetName);
                        }else{
                            GAHttpClient reqIP = new GAHttpClient(
                                    "http://api.devices.com:8080/"+targetName);//need to modify the ip address
                            targetDeviceIP = GAHttpClient.JSONParser(
                                    reqIP.handleGetResponseBody(),"URI"
                            );
                            routerTable.put(targetName,targetDeviceIP);
                        }
                        GAHttpClient forward = new GAHttpClient(targetDeviceIP);
                        forward.handlePutResponseBody(dataJson);
                    }
                    out.write("success"+'\n');
                    out.flush();
                    System.out.println("Finish");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

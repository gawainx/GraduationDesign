import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
/**
 * Created by gawainx on 2017/5/5.
 * deal with forward to multi devices at one time
 */
public class MultiForwardServer implements Runnable {
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRegistryURI() {
        return RegistryURI;
    }

    public void setRegistryURI(String registryURI) {
        RegistryURI = registryURI;
    }

    //deal with forward data to more than one devices at one request
    private int port;
    private String RegistryURI;
    MultiForwardServer(){
        this.port = 8890;
        RegistryURI = "http://mei99.com";
    }
    MultiForwardServer(int port , String registryURI){
        this.port = port;
        this.RegistryURI = registryURI;
    }
    @Override
    public void run() {
        try{
            ServerSocket ss = new ServerSocket(this.port);
            for(;;){
                Socket c =ss.accept();
                MultiConnectionHandler mchndlr = new MultiConnectionHandler(c);
                new Thread(mchndlr).start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private class MultiConnectionHandler implements Runnable{
        private Socket sock;
        MultiConnectionHandler(Socket sock){this.sock = sock;}
        @Override

        public void run() {
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream())
            );
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(sock.getOutputStream())
                )
            ){
                StringBuffer mes = new StringBuffer();
                String line;
                while((line = in.readLine())!=null){
                    if(line.length() == 0) break;
                    mes.append(line);
                }
                String dataJsonArr = mes.toString();
                Map<String,String> forwardMap = GAUtils.JsonArrayParser(dataJsonArr,"targetID");
                //new design
                //must be targetID
                /*data type: [{},{},{}]
                 forwardMap : id-JsonData type
                 */
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                for(String k : forwardMap.keySet()){
                    jsonArray.put(k);
                }
                jsonObject.put("ids",jsonArray.toString());
                GAHttpClient uriGetter = new GAHttpClient(RegistryURI);
                String jsonSet = uriGetter.handleGetResponseBody(jsonObject.toString());//jsonSet will be id-IP format json data
                if(jsonSet != null){
                    JSONObject resSet = new JSONObject(jsonSet);
                    Iterator iterator = resSet.keys();
                    while(iterator.hasNext()){
                        String id = (String) iterator.next();
                        String targetUri = resSet.getString(id);
                        GAHttpClient forwarder = new GAHttpClient(targetUri);
                        String forwardResult = forwarder.handlePutResponseBody(forwardMap.get(id));
                        out.print(forwardResult+"\n\n");
                    }
                }else{
                    //json set is null
                    out.print("failed\n\n");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

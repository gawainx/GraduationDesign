import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gawainx on 2017/4/24.
 * Forward Data
 */
public class UpdateClient{
    private String hostName;
    private int port;
    UpdateClient(String hostName,int port){
        this.hostName = hostName;
        this.port = port;
    }
    UpdateClient(){
        this.port = 4096;
        this.hostName = "Logic";
    }
    public int send(String Content){
        //Forwarding data
        try(Socket sock = new Socket(hostName,port);
            PrintWriter to = new PrintWriter(
                    new OutputStreamWriter(sock.getOutputStream()))
        ){
            to.print(Content+"\n\n");
            to.flush();
            return 1;
        }catch(Exception e){
            return -1;
        }
    }
}

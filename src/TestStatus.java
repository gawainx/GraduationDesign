import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by gawainx on 2017/4/26.
 * Test Status Json
 */
public class TestStatus {
    public static void main(String[] args){
        System.out.println("Test Start");
        String statusJson = "{\"mode\":\"Morning\",\"id\":\"0x0098ff\",\"targetName\":\"Local\"}";
        String hostName = "comModule";
        int port = 8974;
        try(Socket sock = new Socket(hostName,port);
            BufferedReader from = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            PrintWriter to = new PrintWriter(
                    new OutputStreamWriter(sock.getOutputStream()))
        ){
            System.out.println("TRY");
            to.print(statusJson+"\n\n");
            to.flush();
            String line;
            while((line = from.readLine())!= null){
                System.out.println(line);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

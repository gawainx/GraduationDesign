import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by gawainx on 2017/5/11.
 * deal with update data to devices
 */
public class comDevices implements Runnable {
    private int port;
    comDevices(int p){
        this.port = p;
    }

    @Override
    public void run() {
        try{
            ServerSocket ss = new ServerSocket(this.port);
            Socket c = ss.accept();
            try(BufferedReader in = new BufferedReader(
                    new InputStreamReader(c.getInputStream())
            );
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(c.getOutputStream())
                )
            ){
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = in.readLine())!=null){
                    if(line.length() == 0) break;
                    stringBuffer.append(line);
                }
                int res = new UpdateClient("10.108.92.23",8895)
                        .send(stringBuffer.toString());
                if(res == 1){
                    out.print("Flash Success"+"\n\n");
                }
                else{
                    out.print("failed");
                }
            }
        }catch (IOException e){
            System.out.println("Errors occur when update data to device.");
        }
    }
}

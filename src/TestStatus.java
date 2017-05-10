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
        System.out.println(ForwardServer.MakeStatus("{\"targetName\":\"Registry\",\"targetID\":\"Registry\",\"name\":\"light_bedroom\",\"id\":\"000004\",\"type\":\"light\",\"status\":\"off\"}"));
    }
}

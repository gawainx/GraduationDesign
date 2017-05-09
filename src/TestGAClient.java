import java.io.IOException;

/**
 * Created by gawainx on 2017/5/5.
 * test GAClient method
 */
public class TestGAClient {
    public static void main(String[] args) throws IOException{
        GAHttpClient tget = new GAHttpClient("http://192.168.43.50:8080/devices/ID/000007");
        System.out.println(tget.handlePutResponseBody("{\"state\":\"on\",\"type\":\"613\",\"id\":\"000007\",\"name\":\"mei99\"}"));
    }
}

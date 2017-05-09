import java.io.IOException;
import java.util.Map;

/**
 * Created by gawainx on 2017/5/8.
 */
public class TestJsonArray {
    public static void main(String[] args) throws IOException {
        GAHttpClient gaHttpClient = new GAHttpClient("http://10.108.94.12:8080/devices/name/curtain");
        String jsonArr = gaHttpClient.handleGetResponseBody();
        System.out.println(jsonArr);
        Map<String,String> nameIDiP = GAUtils.JsonArrayParser(jsonArr,"id","ip");
        Map<String,String> idData = GAUtils.JsonArrayParser(jsonArr,"id");
        for(Map.Entry<String,String> iter:nameIDiP.entrySet()){
            System.out.println("key:\t"+iter.getKey());
            System.out.println("Val:\t"+iter.getValue());
        }
        for(Map.Entry<String,String> iter:idData.entrySet()){
            System.out.println("key:\t"+iter.getKey());
            System.out.println("Json:\t"+iter.getValue());
        }

    }
}

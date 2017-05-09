/**
 * Created by gawainx on 2017/5/4.
 */
public class TestJsonBuilder
{
    public static void main(String[] args){
        System.out.println(ForwardServer.MakeStatus("{\"targetName\":\"Local\",\"name\":\"Camera\",\"id\":\"00001\",\"type\":\"switch\",\"status\":\"on\"}"));

        System.out.println(GAUtils.FormatJson("{\"sadsada\":\"iopqw\",\"sad\":\"yuo\"}"));
        String json = "{\"targetName\":\"Local\",\"name\":\"Camera\",\"id\":\"00001\",\"type\":\"switch\",\"status\":\"on\"}";
        System.out.println(GAUtils.JSONParser(json,"loc"));
    }
}

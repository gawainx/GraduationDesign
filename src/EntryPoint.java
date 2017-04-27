/**
 * Created by gawainx on 2017/4/25.
 */
public class EntryPoint {
    public static void main(String[] args){
        ForwardServer f2fs = new ForwardServer(8974);
        ForwardServer.setRouterTable("Registry","http://turing.mei99:6133");
        new Thread(f2fs).start();//Listening for forwardServer
        GAHttpServer gaHttpServer = new GAHttpServer();
        new Thread(gaHttpServer).start();//Listen for HttpServer
    }
}

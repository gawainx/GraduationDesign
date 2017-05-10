/**
 * EntryPoint.Main Func
 * Created by gawainx on 2017/4/25.
 */
public class EntryPoint {
    public static void main(String[] args){
        ForwardServer f2fs = new ForwardServer(8974);
        ForwardServer.setRegistryURI("http://10.108.94.12:8080");
        new Thread(f2fs).start();//Listening for forwardServer
        GAHttpServer gaHttpServer = new GAHttpServer();
        new Thread(gaHttpServer).start();//Listen for HttpServer
    }
}

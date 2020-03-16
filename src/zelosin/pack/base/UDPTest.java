package zelosin.pack.base;

import zelosin.pack.Services.UDP.EchoClient;
import zelosin.pack.Services.UDP.EchoServer;

public class UDPTest {
    static EchoClient client;

    public static void setup(){
        new EchoServer().start();
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
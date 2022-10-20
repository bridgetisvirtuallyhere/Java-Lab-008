/*
@author Bridget Acosta
@date 10/19/2022
 */
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import java.util.concurrent.TimeUnit;

public class LEDClient {
    private ZContext zctx;
    private ZMQ.Socket zsocket;
    private Gson gson;
    private String connStr;
    private final String topic = "GPIO";

    private static final int[] OFF = {0, 0, 0};

    public LEDClient(String protocol, String host, int port) {
        zctx = new ZContext();
        zsocket = zctx.createSocket(SocketType.PUB);
        this.connStr = String.format("%s://%s:%d", protocol, "*", port);
        zsocket.bind(connStr);
        this.gson = new Gson();
    }

    public void send(int[] color) throws InterruptedException {
        JsonArray ja = gson.toJsonTree(color).getAsJsonArray();
        String message = topic + " " + ja.toString();
        System.out.println(message);
        zsocket.send(message);
    }
    /*
    Alters the colors
    Rotates through multiple colors, etc.
     */
    public void thatsTheSoundOfThePolice(int[] blue, int[]red, int times, int miliseconds) throws  InterruptedException{
        for(int i=0; i<times; i++) {
            send(blue);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
            send(red);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        }
    }


    /*public void red(int[] red, int times, int miliseconds) throws  InterruptedException{
        for(int i=0; i<times; i++) {
            send(red);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
            send(LEDClient.OFF);
            TimeUnit.MILLISECONDS.sleep(miliseconds);
        }
    }*/
public static void police(){

}
    public void close() throws InterruptedException {
        TimeUnit.SECONDS.sleep(2); // Allow the socket a chance to flush.
        this.zsocket.close();
        this.zctx.close();
    }

    public static void main(String[] args) {
        LEDClient ledClient = new LEDClient("tcp", "192.168.86.250", 5001);
        try {
            int[] blue = {0, 0, 255};
            int[] red = {255, 0, 0};
            //Alters the timing
            ledClient.thatsTheSoundOfThePolice(blue, red, 5, 1000);
            ledClient.thatsTheSoundOfThePolice(blue, red, 5, 500);
            ledClient.thatsTheSoundOfThePolice(blue, red, 3, 250);
            ledClient.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
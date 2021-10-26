import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Future;

public class AsynchronousServer {
    private static AsynchronousServerSocketChannel server;

    public static void main(String[] args) {

        try {
            server = AsynchronousServerSocketChannel.open();
            server.bind(new InetSocketAddress("127.0.0.1", 4555));
        }catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            Future<AsynchronousSocketChannel> acceptFuture = server.accept();
        }
    }
}

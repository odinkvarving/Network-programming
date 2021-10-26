import java.net.Socket;

public class AsynchronousClientThread extends Thread {
    public Socket socket;
    public String clientName;

    public AsynchronousClientThread(Socket socket, String clientName) {
        this.socket = socket;
        this.clientName = clientName;
    }

    public void run() {
        clientChannel = acceptResult.get();
        if ((clientChannel != null) && (clientChannel.isOpen())) {
            while (true) {
                ByteBuffer buffer = ByteBuffer.allocate(32);
                Future<Integer> readResult  = clientChannel.read(buffer);

                // perform other computations

                readResult.get();

                buffer.flip();
                Future<Integer> writeResult = clientChannel.write(buffer);

                // perform other computations

                writeResult.get();
                buffer.clear();
            }
            clientChannel.close();
            serverChannel.close();
        }
    }
}


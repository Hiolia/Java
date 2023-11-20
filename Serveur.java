import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur {

    private ServerSocket serverSocket;

    public Serveur(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void demarrageServeur() throws IOException {
        while (!this.serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            GestionClient gc = new GestionClient(socket);
            Thread thread = new Thread(gc);
            thread.start();
        }

    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(5000);
        Serveur serveur = new Serveur(serverSocket);
        serveur.demarrageServeur();
    }
}
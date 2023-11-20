import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class GestionClient implements Runnable {

    public static ArrayList<GestionClient> liste_gc = new ArrayList<>();
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private String pseudo;

    public GestionClient(Socket socket) throws IOException {
        this.socket = socket;
        this.din = new DataInputStream(this.socket.getInputStream());
        this.dout = new DataOutputStream(this.socket.getOutputStream());
        this.pseudo = this.din.readUTF();
        this.liste_gc.add(this);
        broadcastMessageServeur("SERVEUR: " + pseudo + " est entré dans le chat!");
    }



    @Override
    public void run() {
        while (true) {
            try {
                String messageRecu = this.din.readUTF();
                if (messageRecu.equalsIgnoreCase("EXIT")) {
                    suppClient();
                } else {
                    broadcastMessage(messageRecu);
                }
            } catch (IOException e) {
                break;
            }
        }
    }

    public void broadcastMessage(String envoi) {
        for (GestionClient gc : this.liste_gc) {
            try {
                if (!gc.pseudo.equals(this.pseudo)) {
                    gc.dout.writeUTF(this.pseudo + ": " + envoi);
                    gc.dout.flush();
                }
            } catch (IOException e) {

            }
        }
    }

    public void broadcastMessageServeur(String envoi) {
        for (GestionClient gc : this.liste_gc) {
            try {
                if (!gc.pseudo.equals(this.pseudo)) {
                    gc.dout.writeUTF(envoi);
                    gc.dout.flush();
                }
            } catch (IOException e) {

            }
        }
    }



    public void suppClient() throws IOException {
        this.liste_gc.remove(this);
        broadcastMessageServeur("SERVEUR: " + this.pseudo + " a quitté le chat!");
        this.din.close();
        this.dout.close();
        this.socket.close();
    }

}
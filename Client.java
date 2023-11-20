import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements KeyListener, WindowListener {

    private JFrame jFrame;
    private JLabel jLabel;
    private JTextField jTextField;
    private Socket socket;
    private DataInputStream din;
    private DataOutputStream dout;
    private String username;
    private ArrayList<String> listeMessage = new ArrayList<>();

    public Client(Socket socket, String username) {

        this.jFrame = new JFrame(username);
        this.jTextField = new JTextField();
        this.jTextField.setSize(200, 200);
        this.jTextField.setOpaque(false);
        this.jLabel = new JLabel("Bienvenue ! Veuillez rentrez votre pseudo");
        this.jFrame.setLayout(new BorderLayout());
        this.jFrame.add(this.jTextField, BorderLayout.SOUTH);
        this.jFrame.add(this.jLabel, BorderLayout.NORTH);
        this.jFrame.setSize(500, 500);
        this.jFrame.setVisible(true);
        this.jTextField.addKeyListener(this);
        this.jFrame.addWindowListener(this);

        this.username = username;
        try {
            this.socket = socket;
            this.din = new DataInputStream(this.socket.getInputStream());
            this.dout = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
        }
    }


    public void attenteMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (socket.isConnected()) {
                    try {
                        String messageGroupe = din.readUTF();
                        listeMessage.add(messageGroupe);
                        StringBuilder message = new StringBuilder();
                        for (String str : listeMessage) {
                            message.append("<html>" + str + "<br/><html/>");
                        }
                        jLabel.setText(message.toString());
                    } catch (IOException e) {
                        closeEverything();
                    }
                }
            }
        }).start();
    }

    public void closeEverything() {
        try {
            if (this.din != null) {
                this.din.close();
            }
            if (this.dout != null) {
                this.dout.close();
            }
            if (this.socket != null) {
                this.socket.close();
            }
            System.exit(0);
        } catch (IOException e) {

        }
    }

    public void initialisationPseudo() {
        this.username = this.jTextField.getText();
        this.jFrame.setTitle(this.username);
        this.jLabel.setText("Votre pseudo est: " + this.username);
        try {
            this.dout.writeUTF(this.username);
            this.dout.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.listeMessage.add("Votre pseudo est: " + this.username);
    }

    public void fermeture() {
        try {
            this.dout.writeUTF("EXIT");
            this.dout.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        closeEverything();
    }

    public void envoieMessage(String message) {
        try {
            this.dout.writeUTF(message);
            this.dout.flush();
            this.listeMessage.add(message);
            StringBuilder stringBuilder = new StringBuilder();
            for (String m : this.listeMessage) {
                stringBuilder.append("<html>" + m + "<br/><html/>");
            }
            this.jLabel.setText(stringBuilder.toString());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client(new Socket("localhost", 5000), "Client");
        client.attenteMessage();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == 10) {
            if (this.jLabel.getText().equals("Bienvenue ! Veuillez rentrez votre pseudo")) {
                initialisationPseudo();
            } else {
                String message = this.jTextField.getText();
                if (message.equals("EXIT")) {
                    fermeture();
                } else {
                    envoieMessage(message);
                }
            }
            this.jTextField.setText("");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            this.dout.writeUTF("EXIT");
            this.dout.flush();
            closeEverything();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatappclient;

import app.Messages;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static chatappclient.Client.sInput;
import app.HomaPageChatApp;
import java.awt.ComponentOrientation;

/**
 *
 * @author MERT
 */
class ListenClient extends Thread {

    public void run() {
        while (Client.socket.isConnected()) {
            try {

                Messages receiver = (Messages) (sInput.readObject());
                switch (receiver.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = receiver.content.toString(); // İsim mesajı gönderimi
                        HomaPageChatApp.playGame.jList3.applyComponentOrientation(ComponentOrientation.UNKNOWN);
                               
                        HomaPageChatApp.playGame.timer.start();

                        break;
                    case Disconnect:
                        break;
                    case Text:
                        // HomaPageChatApp.playGame.jTextField18.setText(receiver.content.toString());
                        break;

                    case Selected:
                        // HomaPageChatApp.playGame.RivalSelection = (int) receiver.content;
                        break;

                    case Bitis:
                        break;
                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.StopClient();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.StopClient();
                break;
            }
        }

    }
}

public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;
    public static ListenClient listenMe;

    public static void DisplayClient(String msg) {
        System.out.println(msg);
    }

    public static void SendMessageClient(Messages msg) {
        try {
            Client.sOutput.writeObject(msg);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void StartClient(String ip, int port) {
        try {
            Client.socket = new Socket(ip, port);

            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new ListenClient();
            Client.listenMe.start();

            Messages msg = new Messages(Messages.Message_Type.Name);
            msg.content = HomaPageChatApp.playGame.txt_name.getText();
            Client.SendMessageClient(msg);

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void StopClient() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}

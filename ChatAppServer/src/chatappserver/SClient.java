/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatappserver;

import app.Message;
import static app.Message.Message_Type.Selected;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;

/**
 *
 * @author INSECT
 */
public class SClient {

    int id;
    public String name = "";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    Listen listenThread;
    PairingThread pairThread;

    SClient rival;

    public boolean paired = false;
    public boolean paired2 = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.listenThread = new Listen(this);
        this.pairThread = new PairingThread(this);

    }

    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class Listen extends Thread {

        SClient TheClient;

        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {

            while (TheClient.soket.isConnected()) {
                try {
                    Message received = (Message) (TheClient.sInput.readObject());

                    switch (received.type) {
                        case Name:
                            TheClient.name = received.content.toString();
                            // isim verisini gönderdikten sonra eşleştirme işlemine başla
                            TheClient.pairThread.start();
                            break;
                        case Disconnect:
                            break;
                        case Text:
                            Server.Send(TheClient.rival, received);
                            break;
                        case Selected:
                            Server.Send(TheClient.rival, received);

                            break;
                        case Bitis:
                            break;

                    }

                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    Server.Clients.remove(TheClient);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }

    class PairingThread extends Thread {

        SClient TheClient;
        public ArrayList<SClient> crival;

        PairingThread(SClient TheClient) {
            this.TheClient = TheClient;
            this.crival = new ArrayList<>();
        }

        public void run() {
            while (TheClient.soket.isConnected() && TheClient.paired == false) {
                try {

                    //client eğer eşleşmemişse gir
                    if (!TheClient.paired) {
                        //  List<SClient> crival = new ArrayList<>();

                        while (TheClient.soket.isConnected()) {
                            //liste içerisinde eş arıyor
                            for (SClient clnt : Server.Clients) {
                                  System.out.println("TheClient rival name " + TheClient.rival.name);
                                if (TheClient != clnt && clnt.rival == null) {
                                    //eşleşme sağlandı ve gerekli işaretlemeler yapıldı
                                    for (int i = 0; i < crival.size(); i++) {

                                        crival.add(clnt);
                                        //     crival.paired = true;
                                        crival.add(TheClient);
                                        TheClient.rival = crival.set(id, clnt);
                                        TheClient.paired = true;

                                    }

                                    break;
                                }
                            }
                            sleep(1000);
                        }

                        Message msg1 = new Message(Message.Message_Type.RivalConnected);
                        msg1.content = TheClient.name;
                        Server.Send(TheClient.rival, msg1);
                        System.out.println("TheClient name 22222 " + TheClient.name);

                        Message msg2 = new Message(Message.Message_Type.RivalConnected);
                        msg2.content = TheClient.rival.name;
                        System.out.println("TheClient rival name " + TheClient.rival.name);
                        Server.Send(TheClient, msg2);
                    }
                    //                   Server.pairTwo.release(3);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}

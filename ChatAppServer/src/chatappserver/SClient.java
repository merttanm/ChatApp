/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatappserver;

import app.Messages;
import static app.Messages.Message_Type.Selected;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author MERT
 */

public class SClient {

    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    dinleme listenThread;
    EşleştirmeThread pairThread;
    SClient rival;

    public boolean paired = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.listenThread = new dinleme(this);
        this.pairThread = new EşleştirmeThread(this);

    }


    public void Send(Messages message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    class dinleme extends Thread {

        SClient TheClient;


        dinleme(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {

            while (TheClient.soket.isConnected()) {
                try {

                    Messages received = (Messages) (TheClient.sInput.readObject());

                    switch (received.type) {
                        case Name:
                            TheClient.name = received.content.toString();
                            TheClient.pairThread.start();
                            break;
                        case Disconnect:
                            break;
                        case Text:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                        case Textt:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text3:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text4:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text5:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text6:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text7:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text8:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text9:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text10:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text11:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text12:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text13:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text14:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text15:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text16:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text17:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                            case Text18:
                            Server.SendMessageServer(TheClient.rival, received);
                            break;
                        case Selected:
                            Server.SendMessageServer(TheClient.rival, received);
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

    class EşleştirmeThread extends Thread {

        SClient TheClient;

        EşleştirmeThread(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            while (TheClient.soket.isConnected() && TheClient.paired == false) {
                try {
                    Server.pairTwo.acquire(1);

                    if (!TheClient.paired) {
                        SClient crival = null;
                       
                        while (crival == null && TheClient.soket.isConnected()) {
                            

                            for (SClient clnt : Server.Clients) {
                                if (TheClient != clnt && clnt.rival == null) {
                                    crival = clnt;
                                    crival.paired = true;
                                    crival.rival = TheClient;
                                    TheClient.rival = crival;
                                    TheClient.paired = true;
                                    break;
                                }
                            }
                           
                            sleep(1000);
                        }
                        
                        Messages msg1 = new Messages(Messages.Message_Type.RivalConnected);
                        msg1.content = TheClient.name;
                        Server.SendMessageServer(TheClient.rival, msg1);

                        Messages msg2 = new Messages(Messages.Message_Type.RivalConnected);
                        msg2.content = TheClient.rival.name;
                        Server.SendMessageServer(TheClient, msg2);
                    }
                   
                    Server.pairTwo.release(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(EşleştirmeThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}

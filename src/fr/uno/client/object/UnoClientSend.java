package fr.uno.client.object;

import fr.uno.common.game.Message;
import fr.uno.common.game.MessageType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Classe de gestion d'envoi de message par le client
 */
public class UnoClientSend implements Runnable {
    private final transient Logger log = Logger.getLogger(UnoClientSend.class);

    private Socket sockSocket;
    private ObjectOutputStream oOut;

    /**
     * M�thode de construction du gestionnaire de r�ception de requ�tes
     *
     * @param sockSocket Socket d'envoi
     * @param oOut       OutputStream de sortie
     */
    UnoClientSend(Socket sockSocket, ObjectOutputStream oOut) {
        super();
        this.sockSocket = sockSocket;
        this.oOut = oOut;
    }

    public void run() {
        try (Scanner sc = new Scanner(System.in)) {
            /*
            while (true) {
                // TODO Le chat de la partie, lancer en Thread lors du d�part
                System.out.print("Votre message >> ");
                String m = sc.nextLine();
                Message mess = new Message(MessageType.CHAT, "UnoClient", m);
                try {
                    oOut.writeObject(mess);
                    oOut.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            */
        }
    }

    /**
     * M�thode d'envoie de message
     *
     * @param mess Objet envoy�
     */
    public void sendMessage(Object mess) {
        try {
            this.oOut.writeObject(mess);
            this.oOut.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public Socket getSockSocket() {
        return sockSocket;
    }

    public void setSockSocket(Socket sockSocket) {
        this.sockSocket = sockSocket;
    }

    public ObjectOutputStream getoOut() {
        return oOut;
    }

    public void setoOut(ObjectOutputStream oOut) {
        this.oOut = oOut;
    }
}

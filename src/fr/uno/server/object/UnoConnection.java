package fr.uno.server.object;


import fr.uno.common.game.MessageType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe d'objet de connexion
 *
 * @author BestShuffle
 */
public class UnoConnection implements Runnable {

    private final transient Logger log = Logger.getLogger(UnoConnection.class);

    private UnoServer server;
    private ServerSocket serverSocket;

    /**
     * Construit une connexion
     *
     * @param server Serveur
     * @throws IOException
     */
    UnoConnection(UnoServer server) throws IOException {
        super();
        this.server = server;
        this.serverSocket = new ServerSocket(server.getPort());
        log.info("Serveur démarré sur le port " + server.getPort());
        log.info("En attente de " + server.getNbWantedPlayers() + " joueurs...");
    }

    @Override
    public void run() {
        while (true) {
            try {
                Socket sockNewClient = serverSocket.accept();
                UnoConnectedClient newClient = new UnoConnectedClient(server, sockNewClient);
                server.addClient(newClient);
                log.info("Nouvelle connexion, id = " + newClient.getId());
                new Thread(newClient).start();
            } catch (IOException ignored) {
            }
        }
    }
}

package fr.uno.server;

import fr.uno.client.service.ServiceConfig;
import fr.uno.server.object.UnoServer;
import org.apache.log4j.Logger;

/**
 * @author BestShuffle
 */
public class UnoServerMain {
    private static final transient Logger log = Logger.getLogger(UnoServerMain.class);

    /**
     * Starting a new server
     *
     * @param args Arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            printUsage();
        } else {
            // Chargement de la config
            ServiceConfig.getInstance().loadConfig();

            Integer port = new Integer(args[0]);
            Integer nbPlayersWanted = new Integer(args[1]);
            new UnoServer(port, nbPlayersWanted);
        }
    }

    private static void printUsage() {
        log.error("java server.UnoServer <port> <nbPlayersWanted>");
        log.error("\t<port>: server's port");
        log.error("\t<nbPlayersWanted>: how many players per game");
    }
}
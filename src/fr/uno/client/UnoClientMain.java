package fr.uno.client;

import fr.uno.client.graphic.display.MainDisplay;
import fr.uno.client.service.ServiceClient;
import fr.uno.client.service.ServiceConfig;
import fr.uno.client.service.ServiceWindow;

import java.io.IOException;

/**
 * Classe principale
 */
public class UnoClientMain {

    /**
     * Méthode principale
     *
     * @param args Arguments de la VM (d'entrée)
     */
    public static void main(String[] args) {
        // Chargement de la config
        ServiceConfig.getInstance().loadConfig();
        // Affichage de la page de connexion
        ServiceWindow.getInstance().showConnect();
    }
}

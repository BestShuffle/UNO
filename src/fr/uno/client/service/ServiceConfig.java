package fr.uno.client.service;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Classe de gestion de configuration faite en Singleton
 */
public class ServiceConfig {
    private static volatile ServiceConfig instance;
    private final transient Logger log = Logger.getLogger(ServiceConfig.class);
    private Properties prop;

    private ServiceConfig() {
        super();
    }

    /**
     * Méthode de récupération de l'instance unique
     *
     * @return Instance uniques
     */
    public static ServiceConfig getInstance() {
        // Méthode pour éviter le synchronized qui est coûteux
        if (ServiceConfig.instance == null) {
            // Anti multi instanciation par multi thread
            synchronized (ServiceConfig.class) {
                if (ServiceConfig.instance == null) {
                    ServiceConfig.instance = new ServiceConfig();
                }
            }
        }
        return ServiceConfig.instance;
    }

    /**
     *
     * Méthode de chargement de la configuration
     */
    public void loadConfig() {
        prop = new Properties();
        try {
            FileInputStream input = new FileInputStream(new File(ServiceConfig.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent() + "/config.properties");
            prop.load(input);
        } catch (FileNotFoundException e) {
            log.error("Fichier de configuration introuvable.");
            e.printStackTrace();
        } catch (URISyntaxException | IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Méthode de récupération de la valeur d'un élément de la configuration à partir de sa clé
     *
     * @param key Clé
     * @return Valeur
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
}

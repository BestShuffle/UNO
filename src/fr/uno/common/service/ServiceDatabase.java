package fr.uno.common.service;

import fr.uno.client.UnoClientUtils;
import fr.uno.client.service.ServiceConfig;
import fr.uno.client.service.ServiceWindow;
import org.apache.log4j.Logger;

import java.sql.*;

/**
 * Classe de gestion de base de données
 */
public class ServiceDatabase {
    private static final transient Logger log = Logger.getLogger(ServiceDatabase.class);
    private static Connection conn;

    // Instance unique
    private static volatile ServiceDatabase instance;

    private ServiceDatabase() {
        super();
    }

    /**
     * Méthode de récupération de l'instance unique
     *
     * @return Instance unique
     */
    public static ServiceDatabase getInstance() {
        // Méthode pour éviter le synchronized qui est coûteux
        if (ServiceDatabase.instance == null) {
            // Anti multi instanciation par multi thread
            synchronized (ServiceDatabase.class) {
                if (ServiceDatabase.instance == null) {
                    ServiceDatabase.instance = new ServiceDatabase();
                }
            }
        }
        return ServiceDatabase.instance;
    }

    /**
     * Méthode de connexion à la base de données
     */
    public void connectDb() {
        try {
            Class.forName("org.postgresql.Driver");
            log.info("Driver PSQL O.K.");
            String url = ServiceConfig.getInstance().getProperty("bdUrl");
            String user = ServiceConfig.getInstance().getProperty("bdUser");
            String passwd = ServiceConfig.getInstance().getProperty("bdPasswd");
            if (url != null && user != null && passwd != null) {
                conn = DriverManager.getConnection(url, user, passwd);
                log.info("Connexion à la base réussie");
            } else {
                log.error("La configuration est mal remplie.");
                System.exit(0);
            }
        } catch (ClassNotFoundException e) {
            log.error("Driver PSQL introuvable.");
            e.printStackTrace();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Méthode de connexion d'un utilisateur à la base de données
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return S'il est bien connecté
     */
    public boolean checkLogPlayer(String username, String password) throws SQLException {
        boolean isLogged = false;
        if (isPlayerLoginOk(username, password)) {
            isLogged = true;
        }
        return isLogged;
    }

    /**
     * Méthode connectant un utilisateur
     *
     * @param username Nom d'utilisateur
     * @throws SQLException Erreur SQL
     */
    public void connectPlayer(String username) throws SQLException {
        try {
            log.info("Connexion du joueur " + username + " en base de données.");
            execute("UPDATE uno.player SET connected = true WHERE username = '" + username + "'");
        } catch (SQLException e) {
            log.error("ERREUR SQL : " + e.getMessage());
            throw e;
        }
    }

    /**
     * Méthode déconnectant un utilisateur
     *
     * @param username Nom d'utilisateur
     */
    public void disconnectPlayer(String username) {
        try {
            log.info("Déconnexion du joueur " + username + " en base de données.");
            execute("UPDATE uno.player SET connected = false WHERE username = '" + username + "'");
        } catch (SQLException e) {
            log.error("ERREUR SQL : " + e.getMessage());
        }
    }

    /**
     * Méthode retournant si un utilisateur est connecté
     *
     * @param username Nom d'utilisateur
     * @return L'utilisateur est connecté ?
     * @throws SQLException Erreur SQL
     */
    public boolean isPlayerConnected(String username) throws SQLException {
        ResultSet rs = executeQuery("SELECT connected FROM uno.player WHERE username = '" + username + "'");
        boolean isConnected = false;
        try {
            if (rs != null && rs.next()) {
                isConnected = rs.getBoolean("connected");
            }
        } catch (SQLException e) {
            log.error("ERREUR SQL : " + e.getMessage());
            throw e;
        } finally {
            closeQuietly(rs);
        }
        return isConnected;
    }

    /**
     * Méthode de vérification si les logins d'un utilisateur sont bons
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @return Logins utilisateurs sont bons ?
     * @throws SQLException Erreur SQL
     */
    private boolean isPlayerLoginOk(String username, String password) throws SQLException {
        ResultSet rs = executeQuery("SELECT username FROM uno.player WHERE username = '" + username + "' and password = '" + password + "'");
        boolean isOk = false;
        try {
            if (rs != null && rs.next()) {
                isOk = true;
            }
        } catch (SQLException e) {
            log.error("ERREUR SQL : " + e.getMessage());
            throw e;
        } finally {
            closeQuietly(rs);
        }
        return isOk;
    }

    /**
     * Méthode enregistrant un utilisateur en base de données
     *
     * @param username Nom d'utilisateur
     * @param password Mot de passe
     * @param email    Email
     * @throws SQLException Erreur SQL
     */
    public void registerUser(String username, String password, String email) throws SQLException {
        try {
            String sRegisterUserRequest = "INSERT INTO uno.player(username,password,mail,connected) " +
                    "VALUES ('" + username + "','" + password + "','" + email + "',false);";
            execute(sRegisterUserRequest);
        } catch (SQLException e) {
            throw e;
        }
    }

    /**
     * Méthode de création de statement
     *
     * @return Statement
     */
    private Statement createStatement() {
        try {
            return conn.createStatement();
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * Méthode d'envoi de requête vers la base de données
     *
     * @param request Requête à envoyer
     * @return La requête a bien été exécutée ?
     * @throws SQLException Erreur d'exécution
     */
    public boolean execute(String request) throws SQLException {
        return execute(null, request);
    }

    /**
     * Méthode d'envoi de requête vers la base de données
     *
     * @param request Requête à envoyer
     * @return Résultat(s) de la requête
     * @throws SQLException Erreur d'exécution
     */
    private boolean execute(Statement st, String request) throws SQLException {
        checkDbIsConnected();
        if (st == null) {
            st = createStatement();
        }
        try {
            if (st != null) {
                return st.execute(request);
            }
        } catch (SQLException e) {
            log.info("ERREUR Execute : " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Méthode d'envoi de requête vers la base de données avec retour de résultat
     *
     * @param request Requête à envoyer
     * @return Résultat(s) de la requête
     * @throws SQLException Erreur d'exécution
     */
    public ResultSet executeQuery(String request) throws SQLException {
        return executeQuery(null, request);
    }


    /**
     * Méthode d'envoi de requête vers la base de données avec retour de résultat
     *
     * @param request Requête à envoyer
     * @return Résultat(s) de la requête
     * @throws SQLException Erreur d'exécution
     */
    private ResultSet executeQuery(Statement st, String request) throws SQLException {
        checkDbIsConnected();
        if (st == null) {
            st = createStatement();
        }
        try {
            if (st != null) {
                return st.executeQuery(request);
            }
        } catch (SQLException e) {
            log.error("ERREUR ExecuteQuery : " + e.getMessage());
            throw e;
        }
        return null;
    }

    /**
     * Méthode vérifiant si le service est connecté à la base de données
     */
    private void checkDbIsConnected() {
        if (conn == null) {
            connectDb();
        }
    }

    /**
     * Méthode de fermeture d'un ResultSet avec gestion d'erreur
     *
     * @param rs ResultSet à fermer
     */
    private void closeQuietly(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }
}
package fr.uno.common.game;

import com.google.gson.*;
import fr.uno.server.object.UnoConnectedClient;
import org.apache.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Moteur de l'application
 */
public class Engine {
    private final transient static Logger log = Logger.getLogger(Engine.class);

    /**
     * Crée une pile de carte non mélangée
     *
     * @return Liste des cartes non mélangée
     */
    public static List<UnoCard> loadCards() {
        List<UnoCard> lCardsLoaded = new ArrayList<>();
        String sCards = createCardJson();
        JsonObject jsonObject = new JsonParser().parse(sCards).getAsJsonObject();
        JsonArray jsArrayCards = jsonObject.getAsJsonArray("cards");
        for (int i = 0; i < jsArrayCards.size(); i++) {
            String cardName = jsArrayCards.get(i).getAsJsonObject().get("name").getAsString();
            UnoCardColor cardColor = UnoCardColor.valueOf(jsArrayCards.get(i).getAsJsonObject().get("color").getAsString());
            lCardsLoaded.add(new UnoCard(cardColor, cardName));
        }
        return lCardsLoaded;
    }

    /**
     * Méthode pour créer la liste de carte non mélangée sous la forme d'une chaine de caractère au format JSON
     *
     * @return liste de carte non mélangée
     */
    public static String createCardJson() {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        boolean bResult;
        File fFilesFolders;
        JsonArray jaCards = new JsonArray(UnoCardColor.values().length);
        JsonObject jsonObject = new JsonObject();
        for (UnoCardColor color : UnoCardColor.values()) {
            // Tout sauf le dossier other
            if (!color.equals(UnoCardColor.OTHER)) {
                try {
                    fFilesFolders = new File("./images/" + color);
                    bResult = fFilesFolders.exists();
                    color = (bResult) ? UnoCardColor.valueOf(fFilesFolders.getName().toUpperCase()) : null;
                    UnoCardColor finalColor = color;

                    Files.find(Paths.get("./images/" + color), Integer.MAX_VALUE,
                            (filePath, fileAttr) -> fileAttr.isRegularFile()).forEach((path) -> {
                        String lastFolderInPath = path.getName(path.getNameCount() - 1).toString();
                        String name = lastFolderInPath.substring(0, lastFolderInPath.indexOf("."));
                        jaCards.add(new Gson().toJsonTree(new UnoCard(finalColor, name)));
                        jsonObject.add("cards", jaCards);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return gson.toJson(jsonObject);
    }

    // TODO Surement améliorable
    public static void distributeCardsToClient(UnoConnectedClient client) {
        List<UnoCard> listMixedList = client.getServer().getMixedCards();
        List<UnoCard> listPlayerCards = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            UnoCard card = listMixedList.get(i);
            boolean isSpecialCard = false;
            for (UnoCardSpecialEffect effect : UnoCardSpecialEffect.values()) {
                if (card.getName().startsWith(effect.toString())) {
                    isSpecialCard = true;
                    listPlayerCards.add(new UnoCardSpecial(card.getColor(), card.getName(), effect));
                }
            }
            if (!isSpecialCard) {
                listPlayerCards.add(new UnoCard(card.getColor(), card.getName()));
            }
            listMixedList.remove(card);
        }
        client.setCards(listPlayerCards);
        client.getServer().setMixedCards(listMixedList);
    }
}

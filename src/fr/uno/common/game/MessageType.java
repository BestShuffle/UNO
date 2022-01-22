package fr.uno.common.game;

/**
 * Enumération contenant les types de message de l'application
 */
public enum MessageType {

    CHAT("chat"),
    INFO("info"),
    REQUEST_USERNAME("request_username"),
    GIVE_CARDS("give_cards"),
    REQUEST_PICK_CARD("request_pick_card"),
    PICK_CARD("pick_card"),
    REQUEST_PLAY_CARD("request_play_card"),
    REQUEST_CHANGE_COLOR("request_change_color"),
    PLAY_CARD("play_card"),
    REQUEST_PLAY_CARD_SPECIAL("request_play_card_special"),
    REMOVE_CARD("remove_card"),
    GAME_START("game_start"),
    GAME_FINISHED("game_finished"),
    GET_CARDS_NUMBER("get_cards_number"),
    HAS_NOT_TO_PLAY("has_not_to_play"),
    HAS_TO_PLAY("has_to_play"),
    REFUSE_CONNECT("refuse_connect"),
    REQUEST_CHECK_LOGIN("request_check_login"),
    REQUEST_LOGIN("request_login"),
    REQUEST_LOGOUT("request_logout"),
    BAD_LOGIN("bad_login"),
    ALREADY_CONNECTED("already_connected"),
    REQUEST_REGISTER("request_register"),
    REGISTERED("registered"),
    REQUEST_PLAY("request_play");

    private String type;

    MessageType(String type) {
        this.type = type;
    }

    public String toString() {
        return type;
    }
}

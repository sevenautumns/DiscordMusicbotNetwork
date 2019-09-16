package de.autumnal.discordmusicbotnetwork.message.enums;

public enum Emoji {
    A_ONE_EMOJI ("1\u20E3"),
    B_TWO_EMOJI ("2\u20E3"),
    C_THREE_EMOJI ("3\u20E3"),
    D_FOUR_EMOJI ("4\u20E3"),
    E_FIVE_EMOJI ("5\u20E3"),
    F_SIX_EMOJI ("6\u20E3"),
    G_SEVEN_EMOJI ("7\u20E3"),
    H_EIGHT_EMOJI ("8\u20E3"),
    I_NINE_EMOJI ("9\u20E3"),
    J_TEN_EMOJI ("\uD83D\uDD1F"),
    ZERO_EMOJI ("0\u20E3"),
    PLAY_EMOJI ("\u25B6"),
    PAUSE_EMOJI ("\u23F8"),
    PLAY_PAUSE_EMOJI ("\u23EF"),
    STOP_EMOJI ("\u23F9"),
    FAST_FORWARD_EMOJI ("\u23ED"),
    FAST_BACKWARD_EMOJI ("\u23EE"),
    FORWARD_EMOJI ("\u23E9"),
    BACKWARD_EMOJI ("\u23EA"),
    LOOP_ALL_EMOJI ("\uD83D\uDD01"),
    LOOP_ONE_EMOJI ("\uD83D\uDD02"),
    RANDOM_EMOJI ("\uD83D\uDD00"),
    RED_X_EMOJI ("\u274C"),
    GREEN_DINGBATS ("\u2705"),
    HEAVY_PLUS ("\u2795"),
    HEAVY_MINUS ("\u2796"),
    RIGHT_ARROW ("\u27A1"),
    LEFT_ARROW ("\u2B05"),
    LEFT_ARROW_WITH_HOOK ("\u21A9"),
    GREEN_X_EMOJI ("\u274E"),
    EJECT_EMOJI ("\u23CF"),
    RED_CIRCLE ("\u2B55"),
    CIRCLE_ARROWS ("\uD83D\uDD04"),
    I_EMOJI ("\u2139"),
    INBOX ("\ud83d\udce5"),
    OUTBOX ("\ud83d\udce4"),
    GERMAN_FLAG ("\ud83c\udde9\ud83c\uddea"),
    UNITED_KINGDOM_FLAG ("\ud83c\uddec\ud83c\udde7");

    public final String unicode;

    Emoji(String unicode){
        this.unicode = unicode;
    }

    public static final Emoji getEmojiByUnicode(String unicode){
        for (Emoji e: Emoji.values()) {
            if(e.unicode.equals(unicode)) return e;
        }
        return null;
    }
}


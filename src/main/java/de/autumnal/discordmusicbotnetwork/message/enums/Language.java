package de.autumnal.discordmusicbotnetwork.message.enums;

public enum Language {
    ENGLISCH,
    GERMAN;

    public static Language getLanguageFromString(String language){
        language = language.toUpperCase();
        try {
            return Language.valueOf(language);
        }catch (Exception e){
            return Language.ENGLISCH;
        }
    }
}

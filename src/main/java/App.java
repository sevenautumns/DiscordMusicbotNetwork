import bot.BotManager;
import message.MessageManager;

public class App {
    public static void main(String[] args){
        BotManager.getInstance();
        MessageManager.getInstance();
    }

    /**
     * Hallo hier ist @Sven, der Ersteller der MusikBots.
     * Ich wollte euch hiermit ein bisschen die Funktionsweise der Bots vorstellen, die ihr mithilfe dieses Kanals nutzen könnt.
     * Ein Musikbot wird immer diese Nachricht (unter meiner hier) bereitstellen, mit der ihr die meisten Funktionen der Bots nutzen könnt.
     * Diese sind :track_previous: und :track_next: um zum vorherigen Lied bzw. nächsten Lied zu wechseln und :play_pause: um die aktuelle Wiedergabe zu Pausieren/Starten.
     * Auch gibt es verschiedene Abspielmodi, diese sind einmal "Normal" (Kein Zeichen), dass die Warteschlange an Liedern einfach abgearbeitet wird und zwei besondere Loop Modi. Diese sind einmal :repeat_one:, hier wird die ganze Zeit das aktuelle Lied wiederholt und :repeat:, hier werden alle Lieder in der Warteschlange die ganze Zeit wiederholt. Um einen der beiden Loop Modi wieder zu entfernen drückt einfach nochmal den entsprechenden Knopf.
     * Welcher Modi bei eurem Bot aktiv ist, seht ihr dann an dem Zeichen, neben dem Namen eures Bottes in der Liste unter mir.
     * Zuletzt gibt es die Beiden "Befehle" :inbox_tray: und :outbox_tray: die eurem Sprachkanal einen Bot beitreten bzw. verlassen lässt.
     * Um nun Musik einzufügen müsst ihr einfach in diesem Kanal eine Nachricht schreiben.
     * Sollte eure Nachricht ein Youtube-Link, oder irgendein Link zu einem Lied, sein, wird dieses Lied sofort abgespielt, alternativ könnt ihr aber auch eine Anfrage schreiben wie z.B. "The Pretender - Foo Fighters", dann wird eine neue Nachricht in diesem Kanal, von einem Bot, geschrieben, die die ersten 10 Youtube Suchergebnisse enthält, hier könnt ihr dann einfach mit den Zahlen :one:, :two:, ... auswählen, welches der Lieder ihr haben wollt.
     * Sollte kein Bot in eurem Sprachkanal sein, wenn ihr einen Link oder eine Anfrage in diesem Kanal schreibt, tritt euch automatisch ein Bot bei.
     *
     * Bei weiteren Fragen oder Fehlern die auch auffallen, schreibt mir einfach eine PrivatNachricht.
     */
}

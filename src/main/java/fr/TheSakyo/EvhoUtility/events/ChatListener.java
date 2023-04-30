package fr.TheSakyo.EvhoUtility.events;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import fr.TheSakyo.EvhoUtility.UtilityMain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/*****************************************************************/
/* PARTIE ÉVÈNEMENT POUR ACTUALISER LE FORMAT DU GRADE DU JOUEUR */
/*****************************************************************/
public class ChatListener implements Listener {

	/* Récupère la class "Main" + un évènement compatible LuckPerms */
    UtilityMain main;
    public ChatListener(UtilityMain pluginMain) { this.main = pluginMain; }
    /* Récupère la class "Main" + un évènement compatible LuckPerms */


  /***********************************************************/
  /* PARTIE ÉVÈNEMENT LORSQU'UN JOUEUR ÉCRIT DANS LE TCHAT  */
  /*********************************************************/

     // *** Évènement lorsque le parle dans le tchat *** //
     @EventHandler
	 public void onAsyncChat(AsyncChatEvent e) {

        List<UUID> playersNotified = new ArrayList<>();

        String message = ColorUtils.format(CustomMethod.ComponentToString(e.message()));
        String[] parts = message.split(" ");

        for(String part: parts) {

            if(part.startsWith("@")) {

                Player target = Bukkit.getServer().getPlayerExact(part.replace("@", ""));

                if(target != null && !playersNotified.contains(target.getUniqueId())) {

                    playersNotified.add(target.getUniqueId());
                    target.playSound(target.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                }
            }
        }
	 }
    // *** Évènement lorsque le parle dans le tchat *** //

    /***********************************************************/
    /* PARTIE ÉVÈNEMENT LORSQU'UN JOUEUR ÉCRIT DANS LE TCHAT  */
    /*********************************************************/
}


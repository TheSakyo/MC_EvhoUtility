package fr.TheSakyo.EvhoUtility.events;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ConcurrentModificationException;

public class NPCListener implements Listener {

    /* Récupére la class "Main" */
	private UtilityMain main;
	public NPCListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupére la class "Main" */


    /***********************************************************/
	/* EVENEMENENT QUAND LE JOUEUR SE DÉPLACE OU SE TÉLÉPORTE */
	/*********************************************************/
    @EventHandler
    public void onMove(PlayerMoveEvent e) {

        Player p = e.getPlayer();

        Location locFrom = e.getFrom();
        Location locTo = e.getTo();

        Chunk chunkFrom = locFrom.getChunk();
        Chunk chunkTo = locTo.getChunk();

                    /* -------------------------------------------------------------------- */

        // ⬇️ On Vérfie si le chunk où se situe le Joueur n'est pas celu où il était précédement ⬇️ //
        if(!chunkTo.equals(chunkFrom)) {

            // ⬇️ On recharge les 'NPC' pour le Joueur en question selon le chunk où il se situe ⬇️ //
            loadNPCOnChunk(p, chunkTo);
            loadNPCOnChunk(p, chunkFrom);
            // ⬆️ On recharge les 'NPC' pour le Joueur en question selon le chunk où il se situe ⬆️ //
        }
        // ⬆️ On Vérfie si le chunk où se situe le Joueur n'est pas celu où il était précédement ⬆️ //
    }

                            /* ----------------------------------------------------------------------------------------------- */
                            /* ----------------------------------------------------------------------------------------------- */

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {

        Player p = e.getPlayer();

        Location locFrom = e.getFrom();
        Location locTo = e.getTo();

        Chunk chunk = locTo.getChunk();

                    /* -------------------------------------------------------------------- */

        loadNPCOnChunk(p, locTo.getChunk()); // On recharge les 'NPC' pour le Joueur en question selon le chunk où il se situe

    }
    /***********************************************************/
    /* EVENEMENENT QUAND LE JOUEUR SE DÉPLACE OU SE TÉLÉPORTE */
    /*********************************************************/


    /* ------------------------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------------------------- */
    /* ------------------------------------------------------------------------------------------------------------------------------- */


    /**
     *
     * Essait de recharger les NPCs étant dans les {@link Chunk} demandé
     * pour un {@link Player joueur}
     *
     * @param player Le {@link Player joueur} qui subira les modifications.
     * @param chunk Le {@link Chunk} en question.
     */
    private void loadNPCOnChunk(Player player, Chunk chunk) {

        /*try {

            main.NPCS.values().forEach(NPC -> {

                if(chunk.contains(NPC.getLocation().getBlock().getBlockData())) {

                    // Recharge le 'NPC' pour le Joueur, s'il n'est pas défini comme détruit
                    if(!NPC.isDestroyed()) NPC.reloadNPC(player, NPC.getLocation(), main.console);
                }
            });

        } catch(ConcurrentModificationException ignored) {}*/
    }
}

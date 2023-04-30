package fr.TheSakyo.EvhoUtility.events;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.ZoneManager;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.kyori.adventure.title.Title;
import net.minecraft.ChatFormatting;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class ZoneListener implements Listener {

    /* Récupère la class "Main" */
	private final UtilityMain main;
	public ZoneListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */



    /********************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR BOUGE DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /******************************************************************************/
    @EventHandler
    public void onMoveInZone(PlayerMoveEvent e) {

        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();


        // Récupère les différentes localisations et Bloc cible //
        Location playerLoc = p.getLocation();
        Block fromBlock = from.getBlock();
        Block toBlock = to.getBlock();
        // Récupère les différentes localisations et Bloc cible //

                             /* ------------------------------------------------------------------------------------- */

        Set<String> keysZoneSectionCfg = ConfigFile.getConfigurationSection(main.zoneConfig, "ZONE").getKeys(false);

        if(!keysZoneSectionCfg.isEmpty()) {

            for(String key : keysZoneSectionCfg) {

                if(ZoneManager.hasRegion(key)) {

                    Location firstPosition = ZoneManager.getFirstLocationPos(key);
                    Location secondPosition = ZoneManager.getSecondLocationPos(key);

                    /* -------------------------------------------- */

                    // Définit une nouvelle localisation propre pour la localisation précédente du joueur, si le block sous le joueur n'était pas de l'air. //
                    if(toBlock.getRelative(BlockFace.DOWN).getType() != Material.AIR && !CustomMethod.inRegionCuboid(to, firstPosition, secondPosition)) {

                        // On vérifie d'abord, les régions où se situe le joueur, pour ensuite définir une nouvelle localisation propre pour la localisation précédente du joueur //
                        if((key.equalsIgnoreCase("default_" + p.getWorld()) && CustomMethod.inRegionCuboid(to, firstPosition, secondPosition))
                           || (!key.equalsIgnoreCase("default_" + p.getWorld()) && !CustomMethod.inRegionCuboid(to, firstPosition, secondPosition))) {

                            // Récupère la dernière localisation du Joueur.
                            Location lastLocation = new Location(toBlock.getWorld(), toBlock.getX(), toBlock.getY(), toBlock.getZ(), playerLoc.getYaw(), playerLoc.getPitch());

                            // Ajoute la localisation précédente du Joueur s'il en a pas
                            ZoneManager.previousLocation.putIfAbsent(p.getUniqueId(), lastLocation);

                            // Remplace la localisation précédente du Joueur
                            ZoneManager.previousLocation.replace(p.getUniqueId(), lastLocation);

                        }
                        // On vérifie d'abord, les régions où se situe le joueur, pour ensuite définir une nouvelle localisation propre pour la localisation précédente du joueur //
                    }
                    // Définit une nouvelle localisation propre avec la variable 'previousLocalisation', si le block sous le joueur n'était pas de l'air. //

                                                     /* ------------------------------------------------------- */

                    // Vérifie la région sur laquelle effectuer l'évènement //
                    List<Boolean> checkRegionAndPerm;
                    if(key.equalsIgnoreCase("default_" + p.getWorld().getName())) checkRegionAndPerm = inNotRegionZone_playerHasPerm(p, from, to, key);
                    else checkRegionAndPerm = inRegionZone_playerHasPerm(p, from, to, key);
                    // Vérifie la région sur laquelle effectuer l'évènement //

                    if(checkRegionAndPerm != null && !checkRegionAndPerm.isEmpty()) {

                        if(checkRegionAndPerm.size() == 2 && checkRegionAndPerm.get(0) && !checkRegionAndPerm.get(1)) {

                            if(CustomMethod.hasByPassPerm(p)) { return; }

                            e.setCancelled(true); // Annule l'évènement

                            // Aprés un tick, on décale le Joueur pour l'empêcher de rejoindre la Zone //
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, () -> {

                                e.setCancelled(false); // Réactive l'évènement

                                /* Si le Joueur essaie de Sortir d'une Zone Globale ou Entrer dans une zone auquel il n'a pas accès par le bas,
                                   on pousse donc le joueur en récupérant le vecteur de la localisation précédente avec une multiplication -1 */
                                if(from.getBlockY() < to.getBlockY()) p.setVelocity(from.toVector().multiply(-1));

                                /* Sinon, Si le Joueur essaie de Sortir d'une Zone Globale ou Entrer dans une zone auquel il n'a pas accès par le haut,
                                   on pousse donc le joueur en récupérant le vecteur de la localisation précédente,
                                   ou on le téléporte si la nouvelle localisation contient de l'air */
                                else if(from.getBlockY() > to.getBlockY()) {

                                    // Si la nouvelle localisation contient de l'air, on téléporte le joueur à la localisation précédente
                                    if(toBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR) p.teleport(ZoneManager.previousLocation.get(p.getUniqueId()));

                                    // Sinon, on le pousse en récupérant le vecteur de la localisation précédente.
                                    else p.setVelocity(from.toVector());
                                }

                                // Sinon, on le pousse à la localisation précédente à partir de la nouvelle avec une normalisation et une multiplication -2
                                else p.setVelocity(to.toVector().subtract(ZoneManager.previousLocation.get(p.getUniqueId()).toVector()).normalize().multiply(-2));

                                p.playSound(playerLoc, Sound.ENTITY_GHAST_SHOOT, 1f, 1f);
                                p.spawnParticle(Particle.CLOUD, playerLoc, 16);

                                Title title;
                                if(key.equalsIgnoreCase("default_" + p.getWorld().getName())) title = Title.title(CustomMethod.StringToComponent(" "), CustomMethod.StringToComponent(ChatFormatting.RED + "TU NE SORTIRA PAS !"));
                                else title = Title.title(CustomMethod.StringToComponent(" "), CustomMethod.StringToComponent(ChatFormatting.RED + "Vous n'avez pas la permission pour entré dans cette région !"));

                                p.showTitle(title);
                            }, 1);
                            // Aprés un tick, on décale le Joueur pour l'empêcher de rejoindre la Zone //
                        }
                    }
                }
            }
        }
    }
    /********************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR BOUGE DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /******************************************************************************/


    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR JETTE UN ITEM DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /***************************************************************************************/
	@EventHandler
	public void onDropInZone(PlayerDropItemEvent e) {

		Player p = e.getPlayer();
        Location getItemDropLocation = e.getItemDrop().getLocation();

        checkZonePermission(e, p, getItemDropLocation);
	}
    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR JETTE UN ITEM DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /***************************************************************************************/


    /*******************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR RÉCUPÈRE UN ITEM DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /******************************************************************************************/
	@EventHandler
	public void onPickUpInZone(EntityPickupItemEvent e) {

        Location getItemLocation = e.getItem().getLocation();
        if(e.getEntity() instanceof Player p) checkZonePermission(e, p, getItemLocation);
	}
    /******************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR RÉCUPÈRE UN ITEM DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /******************************************************************************************/



    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR CASSE UN BLOC DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /**************************************************************************************/
	@EventHandler
	public void onBreakInZone(BlockBreakEvent e) {

		Player p = e.getPlayer();
        Location getBlockLocation = e.getBlock().getLocation();

        checkZonePermission(e, p, getBlockLocation);
	}
    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR CASSE UN BLOC DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /**************************************************************************************/


    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR PLACE UN BLOC DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /**************************************************************************************/
	@EventHandler
	public void onPlaceInZone(BlockPlaceEvent e) {

		Player p = e.getPlayer();
        Location getBlockPlacedLocation = e.getBlockPlaced().getLocation();

        checkZonePermission(e, p, getBlockPlacedLocation);
	}
    /***************************************************************************************/
    /* ~~~ ÉVÈNEMENT QUAND LE JOUEUR PLACE UN BLOC DANS UNE ZONE DU SERVEUR (EVHOZONE) ~~~ */
    /**************************************************************************************/



    /*******************************************************************************************************/
    /* ~~~ MÉTHODE VÉRIFIANT SI LA LOCATION DEFINITE EST DANS LA REGION DE LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /* ~~~ ET VERIFIES ÉGALEMENT SI LE JOUEUR A LA PERMISSION D'ENTRÉ DANS LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /******************************************************************************************************/
    private List<Boolean> inRegionZone_playerHasPerm(Player p, Location locFrom, Location locTo, String keyName) {

        Location locFromToCheck = null;
        List<Boolean> returnedList = new ArrayList<>();

        Location firstPosition = ZoneManager.getFirstLocationPos(keyName);
        Location secondPosition = ZoneManager.getSecondLocationPos(keyName);

                /* ------------------------------------------------------------------- */

        if(CustomMethod.inRegionCuboid(locTo, firstPosition, secondPosition)) {

                            /* -------------------------------------------------- */

            if(locFrom != null) {

                if(CustomMethod.inRegionCuboid(locFrom, firstPosition, secondPosition)) {

                    if((!CustomMethod.hasByPassPerm(p)) && !(ZoneManager.hasPerm(keyName, p))) {

                        if(((locFrom.getBlockY() != locTo.getBlockY()) && (locFrom.getBlockY() < locTo.getBlockY())) ||
                            (locFrom.getBlockX() != locTo.getBlockX()) || (locFrom.getBlockZ() != locTo.getBlockZ())) {

                                Title title = Title.title(CustomMethod.StringToComponent(" "), CustomMethod.StringToComponent(ChatFormatting.RED + "Vous ne pouvez pas être dans cette région !"));
                                p.showTitle(title);

                                p.teleport(p.getWorld().getSpawnLocation());
                                return List.of();
                        }
                    }

                } else {

                    returnedList.add(true);
                    if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
                    else { returnedList.add(false); }
                }

            } else {

                returnedList.add(true);
                if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
                else { returnedList.add(false); }
            }

                            /* -------------------------------------------------- */

        } else {

            returnedList.add(false);
            if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
            else { returnedList.add(false); }
        }
                /* ------------------------------------------------------------------- */

        return returnedList;
    }
    /*******************************************************************************************************/
    /* ~~~ MÉTHODE VÉRIFIANT SI LA LOCATION DEFINITE EST DANS LA REGION DE LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /* ~~~ ET VERIFIES ÉGALEMENT SI LE JOUEUR A LA PERMISSION D'ENTRÉ DANS LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /******************************************************************************************************/

  /* ---------------------------------------------------------------------------------------------------------------------------------*/
  /* ---------------------------------------------------------------------------------------------------------------------------------*/

    /*************************************************************************************************************/
    /* ~~~ MÉTHODE VÉRIFIANT SI LA LOCATION DEFINITE N'EST pas DANS LA REGION DE LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /* ~~~  ET VERIFIES ÉGALEMENT SI LE JOUEUR A LA PERMISSION DE SORTIR DANS LA ZONE DU SERVEUR DÉFINIT    ~~~ */
    /************************************************************************************************************/
    private List<Boolean> inNotRegionZone_playerHasPerm(Player p, Location locFrom, Location locTo, String keyName) {

        Location locFromToCheck = null;
        List<Boolean> returnedList = new ArrayList<>();

        Location firstPosition = ZoneManager.getFirstLocationPos(keyName);
        Location secondPosition = ZoneManager.getSecondLocationPos(keyName);

                /* ------------------------------------------------------------------- */

        if(!CustomMethod.inRegionCuboid(locTo, firstPosition, secondPosition)) {

                            /* -------------------------------------------------- */

            if(locFrom != null) {

                if(!CustomMethod.inRegionCuboid(locFrom, firstPosition, secondPosition)) {

                    if((!CustomMethod.hasByPassPerm(p)) && !(ZoneManager.hasPerm(keyName, p))) {

                        if(((locFrom.getBlockY() != locTo.getBlockY()) && (locFrom.getBlockY() < locTo.getBlockY())) ||
                            (locFrom.getBlockX() != locTo.getBlockX()) || (locFrom.getBlockZ() != locTo.getBlockZ())) {

                                Title title = Title.title(CustomMethod.StringToComponent(" "), CustomMethod.StringToComponent(ChatFormatting.RED + "Vous ne pouvez pas sortir de la région !"));
                                p.showTitle(title);

                                p.teleport(p.getWorld().getSpawnLocation());
                                return List.of();
                        }
                    }

                } else {

                    returnedList.add(true);
                    if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
                    else { returnedList.add(false); }
                }

            } else {

                returnedList.add(true);
                if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
                else { returnedList.add(false); }
            }

                            /* -------------------------------------------------- */

        } else {

            returnedList.add(false);
            if(ZoneManager.hasPerm(keyName, p)) { returnedList.add(true); }
            else { returnedList.add(false); }
        }
                /* ------------------------------------------------------------------- */

        return returnedList;
    }
    /*************************************************************************************************************/
    /* ~~~ MÉTHODE VÉRIFIANT SI LA LOCATION DEFINITE N'EST pas DANS LA REGION DE LA ZONE DU SERVEUR DÉFINIT ~~~ */
    /* ~~~  ET VERIFIES ÉGALEMENT SI LE JOUEUR A LA PERMISSION DE SORTIR DANS LA ZONE DU SERVEUR DÉFINIT    ~~~ */
    /************************************************************************************************************/


    /******************************************************************************************************/
    /* ~~~  MÉTHODE UTILE POUR LES ÉVÈNEMENTS (VÉRIFICATION DES PERMISSIONS POUR ACCÉDER À UNE ZONE)  ~~~ */
    /*****************************************************************************************************/
    private void checkZonePermission(Cancellable cancellableEvent, Player player, Location location) {

        Set<String> keysZoneSectionCfg = ConfigFile.getConfigurationSection(main.zoneConfig, "ZONE").getKeys(false);

        if(!keysZoneSectionCfg.isEmpty()) {

            for(String key : keysZoneSectionCfg) {

                if(ZoneManager.hasRegion(key)) {

                    // Vérifie la région sur laquelle effectuer l'évènement //
                    List<Boolean> checkRegionAndPerm;
                    if(key.equalsIgnoreCase("default_" + player.getWorld().getName())) checkRegionAndPerm =
                            inNotRegionZone_playerHasPerm(player, null, location, key);
                    else checkRegionAndPerm = inRegionZone_playerHasPerm(player, null, location, key);
                    // Vérifie la région sur laquelle effectuer l'évènement //

                    if((checkRegionAndPerm != null && !checkRegionAndPerm.isEmpty()) && checkRegionAndPerm.size() == 2) {

                        if(checkRegionAndPerm.get(0) && !checkRegionAndPerm.get(1)) {

                            if(CustomMethod.hasByPassPerm(player)) { cancellableEvent.setCancelled(false); return; }
                            cancellableEvent.setCancelled(true);

                        } else cancellableEvent.setCancelled(false);
                    }
                }
            }
        }
    }
    /******************************************************************************************************/
    /* ~~~  MÉTHODE UTILE POUR LES ÉVÈNEMENTS (VÉRIFICATION DES PERMISSIONS POUR ACCÉDER À UNE ZONE)  ~~~ */
    /*****************************************************************************************************/
}

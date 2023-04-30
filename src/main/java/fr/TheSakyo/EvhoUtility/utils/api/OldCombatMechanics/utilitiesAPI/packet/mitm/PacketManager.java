package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

/**
 * Gère tous les 'PacketListeners' et tout le reste.
 * Ce gestionnaire supprime les écouteurs lorsque le joueur quitte. Vous n'avez pas besoin de garder la trace de cela !
 */
public class PacketManager implements Listener {

    private static PacketManager instance;

    private final Map<UUID, PacketInjector> injectorMap = new HashMap<>();

    /**
     * Instancie un nouveau 'PacketManager'
     *
     * @param plugin Une instance du plugin ({@link fr.TheSakyo.EvhoUtility.UtilityMain})
     */
    private PacketManager(UtilityMain plugin) {

        Bukkit.getPluginManager().registerEvents(this, plugin);
        OldCM.addDisableListener(() -> { removeAll(); instance = null; });
    }

    /**
     * Retourne l'instance du gestionnaire
     *
     * @return Une instance du PacketManager
     */
    public static synchronized PacketManager getInstance() {

        if(instance == null) { instance = new PacketManager(UtilityMain.getInstance()); }
        return instance;
    }

    /**
     * Ajoute un écouteur de paquets
     *
     * @param listener Le {@link PacketListener} à ajouter
     * @param player   Le Joueur à écouter
     *
     * @throws NullPointerException Si un paramètre est nul
     */
    @SuppressWarnings("unused")
    public void addListener(PacketListener listener, Player player) {

        Objects.requireNonNull(listener, "L'Auditeur ne peut pas être nul");
        Objects.requireNonNull(player, "Le Joueur ne peut pas être nul");

        // Aucune modification pendant les vérifications, sinon le résultat peut être faux ! (il change en fonction de l'État actuel)
        synchronized(injectorMap) {

            if(injectorMap.containsKey(player.getUniqueId())) { injectorMap.get(player.getUniqueId()).addPacketListener(listener); }
            else {

                try {

                    PacketInjector injector = new PacketInjector(player);
                    injector.addPacketListener(listener);
                    injectorMap.put(player.getUniqueId(), injector);

                } catch(Exception e) { Messenger.debug("Erreur d'attachement de l'écouteur de paquets !", e); }
            }
        }
    }

    /**
     * Supprime l'écouteur d'un Joueur
     *
     * @param listener Le Joueur à supprimer
     * @param player Le Joueur pour lequel il faut le supprimait
     *
     * @throws NullPointerException Si l'un des paramètres est nul.
     */
    @SuppressWarnings("unused")
    public void removeListener(PacketListener listener, Player player) {

        Objects.requireNonNull(listener, "L'Auditeur ne peut pas être nul");
        Objects.requireNonNull(player, "Le Joueur ne peut pas être nul");

        // Aucune modification pendant les vérifications, sinon le résultat peut être faux ! (il change en fonction de l'État actuel)
        synchronized(injectorMap) {

            if(!injectorMap.containsKey(player.getUniqueId())) { return; }

            PacketInjector injector = injectorMap.get(player.getUniqueId());
            injector.removePacketListener(listener);

            if(injector.getListenerAmount() < 1) {

                injector.detach();
                injectorMap.remove(player.getUniqueId());
            }
        }
    }

    /**
     * Supprime <b>tous</b> les écouteurs d'un Joueur.
     *
     * @param uuid L'{@link UUID} du Joueur pour lequel il faut supprimer tous les écouteurs.
     *
     * @throws NullPointerException Si l'UUID est nul
     */
    @SuppressWarnings("WeakerAccess")
    public void removeAllListeners(UUID uuid) {

        Objects.requireNonNull(uuid, "l'uuid ne peut être nul");

        // Aucune modification pendant les vérifications, sinon le résultat peut être faux ! (il change en fonction de l'État actuel)
        synchronized(injectorMap) {

            if(injectorMap.containsKey(uuid)) {

                injectorMap.get(uuid).detach();
                injectorMap.remove(uuid);
            }
        }
    }

    /**
     * <i>Supprime <b>tous</b> les écouteurs</i>.
     * À utiliser avec précaution ou pas du tout.
     */
    private void removeAll() {

        synchronized(injectorMap) {

            // Clone pour éviter les modifications simultanées
            Set<UUID> keys = new HashSet<>(injectorMap.keySet());
            keys.forEach(this::removeAllListeners);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent event) { removeAllListeners(event.getPlayer().getUniqueId()); }
}

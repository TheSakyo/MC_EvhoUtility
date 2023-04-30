package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.PacketHelper;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketAdapter;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketEvent;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketManager;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.team.TeamPacket;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams.CollisionRule;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams.TeamAction;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.teams.TeamUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Disables player collisions.
 */
public class ModulePlayerCollisions extends Module {

    private final CollisionPacketListener collisionPacketListener;
    private final Map<UUID, TeamPacket> playerTeamMap;

    public ModulePlayerCollisions(UtilityMain plugin) {

        super(plugin, "disable-player-collisions");

        // Injecte tous les joueurs au démarrage, afin que le plugin fonctionne correctement après un rechargement.
        collisionPacketListener = new CollisionPacketListener();
        playerTeamMap = new WeakHashMap<>();

         // Dissoudre nos équipes OCM dans onDisable pour qu'elles puissent être réutilisées.
        OldCM.addDisableListener(() -> {

            synchronized(playerTeamMap) {

                for(Map.Entry<UUID, TeamPacket> entry : playerTeamMap.entrySet()) {

                    if(TeamUtils.isOcmTeam(entry.getValue())) { TeamUtils.disband(entry.getValue().getName(), Bukkit.getServer().getPlayer(entry.getKey())); }
                }
            }
        });

        OldCM.addEnableListener(() -> {

            for(Player player : Bukkit.getOnlinePlayers()) { PacketManager.getInstance().addListener(collisionPacketListener, player); }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent e) {

        // Toujours attacher le 'listener', il vérifie en interne
        PacketManager.getInstance().addListener(collisionPacketListener, e.getPlayer());
        createOrUpdateTeam(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChangeWorld(PlayerChangedWorldEvent e) {
        createOrUpdateTeam(e.getPlayer());
    }

     /**
     * Crée une équipe ou la met à jour en envoyant le bon paquet au Joueur.
     *
     * @param player Le Joueur à qui l'envoyer
     */
    private void createOrUpdateTeam(Player player) {

        CollisionRule collisionRule = isEnabled(player.getWorld()) ? CollisionRule.NEVER : CollisionRule.ALWAYS;

        synchronized(playerTeamMap) {
            if(playerTeamMap.containsKey(player.getUniqueId())) {

                TeamPacket teamPacket = playerTeamMap.get(player.getUniqueId());
                teamPacket = teamPacket.withAction(TeamAction.UPDATE);
                teamPacket = teamPacket.withCollisionRule(collisionRule);

                playerTeamMap.put(player.getUniqueId(), teamPacket);
                teamPacket.send(player);

            } else {

                debug("Fausse équipe de collision créée pour vous.", player);
                createAndSendNewTeam(player, collisionRule);
            }
        }
    }

    /**
     * Crée un nouveau {@link TeamPacket}, le stocke dans la carte de cache et l'envoie au joueur.
     *
     * @param player Le Joueur à qui l'envoyer
     * @param collisionRule {@link CollisionRule} à utiliser
     */
    private void createAndSendNewTeam(Player player, CollisionRule collisionRule) {

        synchronized(playerTeamMap) {

            TeamPacket newTeamPacket = TeamUtils.craftTeamCreatePacket(player, collisionRule);
            playerTeamMap.put(player.getUniqueId(), newTeamPacket);
            newTeamPacket.send(player);
        }
    }


    @Override
    public void reload() {

        synchronized(playerTeamMap) {

            for(Player player : Bukkit.getOnlinePlayers()) { createOrUpdateTeam(player); }
        }
    }

    private class CollisionPacketListener extends PacketAdapter {

        private final Class<?> targetClass = PacketHelper.getPacketClass(PacketHelper.PacketType.PlayOut, "ScoreboardTeam");

        @Override
        public void onPacketSend(PacketEvent packetEvent) {
            if(packetEvent.getPacket().getPacketClass() != targetClass) { return; }

            synchronized(playerTeamMap) { handlePacket(packetEvent); }
        }

        private void handlePacket(PacketEvent packetEvent) {

            Object nmsPacket = packetEvent.getPacket().getNmsPacket();
            TeamPacket incomingTeamPacket = TeamPacket.from(nmsPacket);

            CollisionRule collisionRule = isEnabled(packetEvent.getPlayer().getWorld()) ? CollisionRule.NEVER : CollisionRule.ALWAYS;


            if(interestingForPlayer(incomingTeamPacket, packetEvent.getPlayer())) { updateToPacket(packetEvent.getPlayer(), incomingTeamPacket); }

             // Toujours mis à jour, ne réagit que lorsqu'il est activé
            if(!isEnabled(packetEvent.getPlayer().getWorld())) { return; }

            Messenger.debug("[%s-%s] La règle de collision est fixée à %s pour l'action %s dans le monde. %s.", incomingTeamPacket.getName(),
                    Optional.ofNullable(playerTeamMap.get(packetEvent.getPlayer().getUniqueId())).map(TeamPacket::getName), collisionRule, incomingTeamPacket.getAction(),
                    packetEvent.getPlayer().getWorld().getName());

            incomingTeamPacket = incomingTeamPacket.withCollisionRule(collisionRule);
            packetEvent.setPacket(PacketHelper.wrap(incomingTeamPacket.getNmsPacket()));

            // Réinstaurer s'il a été dissous pour avoir la règle correcte
            if(!playerTeamMap.containsKey(packetEvent.getPlayer().getUniqueId())) { createAndSendNewTeam(packetEvent.getPlayer(), collisionRule); }
        }

        private boolean interestingForPlayer(TeamPacket packet, Player player) {

            if(TeamUtils.targetsPlayer(packet, player)) { return true; }

            TeamPacket storedTeam = playerTeamMap.get(player.getUniqueId());
            return storedTeam != null && storedTeam.getName().equals(packet.getName());
        }

        /**
         * Met à jour le {@link TeamPacket} donné dans le paquet SGN et le supprime du cache, s'il a été dissous.
         */
        private void updateToPacket(Player player, TeamPacket incomingPacket) {

            Optional<TeamPacket> current = Optional.ofNullable(playerTeamMap.get(player.getUniqueId()));

             // Seulement, nous dissolvons ces équipes et nous n'avons pas besoin de créer une nouvelle équipe en réponse.
            // Donc ignore simplement ces paquets de dissolution. La carte des équipes de joueurs a déjà été mise à jour lorsque le paquet de dissolution a été envoyé
            if(incomingPacket.getAction() == TeamAction.DISBAND && TeamUtils.isOcmTeam(incomingPacket)) return;


            boolean currentIsOcmTeam = current.isPresent() && TeamUtils.isOcmTeam(current.get());

            // Nous avons déjà une équipe de OCM !
            if(incomingPacket.getAction() == TeamAction.DISBAND && currentIsOcmTeam) return;

            // Nous avons une nouvelle équipe (c'est-à-dire pas une mise à jour).
            if(current.isEmpty() || !incomingPacket.getName().equals(current.get().getName())) {

                // L'Ancienne équipe est la nôtre → Dissolution
                if(currentIsOcmTeam) TeamUtils.disband(current.get().getName(), player);
                current = Optional.of(incomingPacket);
            }

            Optional<TeamPacket> newPacket = current.get().adjustedTo(incomingPacket, player);

            if(newPacket.isPresent()) playerTeamMap.put(player.getUniqueId(), newPacket.get());
            else {

                playerTeamMap.remove(player.getUniqueId());
                debug("Votre équipe a été dissoute.", player);
            }
        }
    }
}

package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Config;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketAdapter;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketEvent;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketManager;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.sound.SoundPacket;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A module to disable the new attack sounds.
 */
public class ModuleAttackSounds extends Module {

    private final SoundListener soundListener;

    private final Set<String> blockedSounds;

    public ModuleAttackSounds(UtilityMain plugin) {

        super(plugin, "disable-attack-sounds");

        this.soundListener = new SoundListener();
        this.blockedSounds = new HashSet<>(getBlockedSounds());

        // Injecte tous les joueurs au démarrage, afin que le plugin fonctionne correctement après un rechargement.
        OldCM.addEnableListener(() -> {

            for(Player player : Bukkit.getOnlinePlayers()) { PacketManager.getInstance().addListener(soundListener, player); }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent e) {

        // Toujours attacher le 'listener' (il vérifie en interne)
        PacketManager.getInstance().addListener(soundListener, e.getPlayer());
    }

    @Override
    public void reload() {

        blockedSounds.clear();
        blockedSounds.addAll(getBlockedSounds());
    }

    private Collection<String> getBlockedSounds() { return this.module().getStringList("blocked-sounds"); }

    /**
     * Disables attack sounds.
     */
    private class SoundListener extends PacketAdapter {

        private boolean disabledDueToError;

        @Override
        public void onPacketSend(PacketEvent packetEvent) {

            if(disabledDueToError || !isEnabled(packetEvent.getPlayer().getWorld())) return;

            try {

                SoundPacket.from(packetEvent.getPacket()).filter(it -> blockedSounds.contains(it.getSoundName())).ifPresent(packet -> {

                    packetEvent.setCancelled(true);
                    if(Config.debugEnabled()) { debug("Son bloqué : " + packet.getSoundName(), packetEvent.getPlayer()); }

                });

            } catch(Exception | ExceptionInInitializerError e) {

                disabledDueToError = true;
                Messenger.warn(e, "Erreur de détection des paquets de son !");
            }
        }
    }
}

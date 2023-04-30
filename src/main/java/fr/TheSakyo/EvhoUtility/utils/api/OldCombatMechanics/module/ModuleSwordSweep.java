package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.ToolDamage;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketAdapter;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketEvent;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.mitm.PacketManager;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.packet.particle.ParticlePacket;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Un module pour désactiver l'attaque par balayage.
 */
public class ModuleSwordSweep extends Module {

    private final List<Location> sweepLocations = new ArrayList<>();
    private final ParticleListener particleListener;
    private EntityDamageEvent.DamageCause sweepDamageCause;
    private BukkitRunnable task;

    public ModuleSwordSweep(UtilityMain plugin) {

        super(plugin, "disable-sword-sweep");

        this.particleListener = new ParticleListener();

        // Sera disponible à partir d'une version 1.11.
        try { sweepDamageCause = EntityDamageEvent.DamageCause.valueOf("ENTITY_SWEEP_ATTACK"); }
        catch(IllegalArgumentException e) { sweepDamageCause = null; }

        // Injecte tous les joueurs au démarrage, afin que le plugin fonctionne correctement après un rechargement.
        OldCM.addEnableListener(() -> {

            for(Player player : Bukkit.getOnlinePlayers()) { PacketManager.getInstance().addListener(particleListener, player); }
        });
    }

    @Override
    public void reload() {

        // Nous n'avons rien mis en place en premier lieu.
        if(sweepDamageCause != null) return;
        if(task != null) task.cancel();

        task = new BukkitRunnable() {

            @Override
            public void run() { sweepLocations.clear(); }
        };
        task.runTaskTimer(plugin, 0, 1);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerLogin(PlayerJoinEvent e) {

        // Toujours attacher le listener, il vérifie en interne
        PacketManager.getInstance().addListener(particleListener, e.getPlayer());
    }

    //Changé de HIGHEST à LOWEST pour supporter le plugin DamageIndicator
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamaged(EntityDamageByEntityEvent e) {

        Entity damager = e.getDamager();
        World world = damager.getWorld();

        if(!isEnabled(world)) return;

        if(!(damager instanceof Player)) return;

        if(sweepDamageCause != null) {

            if(e.getCause() == sweepDamageCause) {

                e.setCancelled(true);
                debug("Balayage annulé", damager);
            }

            // Attaque par balayage détectée ou non, nous n'avons pas besoin de nous rabattre sur l'implémentation de la supposition.
            return;
        }

        Player attacker = (Player) e.getDamager();
        ItemStack weapon = attacker.getInventory().getItemInMainHand();

        if(isHoldingSword(weapon.getType())) onSwordAttack(e, attacker, weapon);
    }

    private void onSwordAttack(EntityDamageByEntityEvent e, Player attacker, ItemStack weapon) {

        //Désactive le balayage de l'épée
        Location attackerLocation = attacker.getLocation();

        int level = 0;

        //Dans un try catch pour les serveurs qui n'ont pas été mis à jour
        try { level = weapon.getEnchantmentLevel(Enchantment.SWEEPING_EDGE); }
        catch(NoSuchFieldError ignored) {}

        float damage = ToolDamage.getDamage(weapon.getType()) * level / (level + 1) + 1;

        if(e.getDamage() == damage) {

            // Possiblement une attaque par balayage d'épée.
            if(sweepLocations.contains(attackerLocation)) {

                debug("Annulation du balayage...", attacker);
                e.setCancelled(true);
            }

        } else { sweepLocations.add(attackerLocation); }
    }

    private boolean isHoldingSword(Material mat) { return mat.toString().endsWith("_SWORD"); }

    /**
     * Cache les particules de balayage.
     */
    private class ParticleListener extends PacketAdapter {

        private boolean disabledDueToError;

        @Override
        public void onPacketSend(PacketEvent packetEvent) {

            if(disabledDueToError || !isEnabled(packetEvent.getPlayer().getWorld())) { return; }

            try {

                ParticlePacket.from(packetEvent.getPacket()).filter(it -> it.getParticleName().toUpperCase(Locale.ROOT).contains("SWEEP")).ifPresent(e -> packetEvent.setCancelled(true));

            } catch(Exception | ExceptionInInitializerError e) {

                disabledDueToError = true;
                Messenger.warn(e, "Erreur de détection des paquets de balayage. Veuillez le signaler avec l'exception suivante" +
                        "L'annulation du balayage devrait toujours fonctionner, mais des particules pourraient apparaître.");
            }
        }
    }
}

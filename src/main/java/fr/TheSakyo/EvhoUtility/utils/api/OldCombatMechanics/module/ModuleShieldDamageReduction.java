package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Permet de personnaliser les pourcentages de réduction des dégâts du bouclier.
 */
public class ModuleShieldDamageReduction extends Module {

    private int genericDamageReductionAmount, genericDamageReductionPercentage, projectileDamageReductionAmount, projectileDamageReductionPercentage;
    private final Map<UUID, List<ItemStack>> fullyBlocked = new WeakHashMap<>();

    public ModuleShieldDamageReduction(UtilityMain plugin) {

        super(plugin, "shield-damage-reduction");
        reload();
    }

    @Override
    public void reload() {

        genericDamageReductionAmount = this.module().getInt("generalDamageReductionAmount", 1);
        genericDamageReductionPercentage = this.module().getInt("generalDamageReductionPercentage", 50);
        projectileDamageReductionAmount = this.module().getInt("projectileDamageReductionAmount", 1);
        projectileDamageReductionPercentage = this.module().getInt("projectileDamageReductionPercentage", 50);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDamage(PlayerItemDamageEvent e) {

        final Player player = e.getPlayer();
        if(!isEnabled(player.getWorld())) return;
        final UUID uuid = player.getUniqueId();
        final ItemStack item = e.getItem();

        if (fullyBlocked.containsKey(uuid)) {

            final List<ItemStack> armour = fullyBlocked.get(uuid);

            // 'ItemStack.equals()' vérifie le matériau, la durabilité et la quantité pour s'assurer que rien n'a changé entre-temps.
            // Nous vérifions toutes les pièces de cette manière, au cas où ils porteraient deux casques ou quelque chose d'étrange.
            final List<ItemStack> matchedPieces = armour.stream().filter(piece -> piece.equals(item)).toList();
            armour.removeAll(matchedPieces);

            debug("Ignore les dommages causés par la durabilité de l'armure en raison d'un blocage complet", player);
            if(!matchedPieces.isEmpty()) e.setCancelled(true);

        }
    }

    @Deprecated
    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(EntityDamageByEntityEvent e) {

        Entity entity = e.getEntity();

        if(!(entity instanceof final Player player)) return;
        if(!isEnabled(player.getWorld())) return;
        if(!shieldBlockedDamage(e)) return;

        // Au lieu de réduire les dégâts à 33%, appliquer la réduction de configuration.
        final double damageReduction = getDamageReduction(e.getDamage(), e.getCause());

        // Assurez-vous également que la réduction des dommages n'entraîne pas de dommages négatifs.
        e.setDamage(DamageModifier.BLOCKING, 0);

        if(e.getFinalDamage() >= damageReduction) e.setDamage(DamageModifier.BLOCKING, -damageReduction);

        debug("Dégâts réduits de: " + e.getDamage(DamageModifier.BLOCKING) + " à " + e.getFinalDamage(), player);

        final UUID uuid = player.getUniqueId();

        if(e.getFinalDamage() <= 0) { // S'assure que l'armure n'est pas endommagée si elle est entièrement bloquée.

            final List<ItemStack> armour = Arrays.stream(player.getInventory().getArmorContents()).filter(Objects::nonNull).collect(Collectors.toList());
            fullyBlocked.put(uuid, armour);

            new BukkitRunnable() {

                @Override
                public void run() {

                    fullyBlocked.remove(uuid);
                    debug("Retiré de l'ensemble entièrement bloqué !", player);
                }

            }.runTaskLater(plugin, 1);
        }
    }

    private double getDamageReduction(double fullDamage, DamageCause damageCause) {

        // Code NMS 1.8, où f est le dommage subi : f = (1.0F + f) * 0.5F;

        // Réduit par le montant
        fullDamage -= damageCause == DamageCause.PROJECTILE ? projectileDamageReductionAmount : genericDamageReductionAmount;

        // Réduit par le pourcentage
        fullDamage *= (damageCause == DamageCause.PROJECTILE ? projectileDamageReductionPercentage : genericDamageReductionPercentage) / 100F;

        // Ne pas réduire de plus que les dommages réels causés.
        if(fullDamage < 0) fullDamage = 0;
        return fullDamage;
    }

    @Deprecated
    private boolean shieldBlockedDamage(EntityDamageByEntityEvent e) {

         // Ne réduisent les dommages que s'ils ont frappés de plein fouet, c'est-à-dire que le bouclier a bloqué une partie des dommages.
        return e.getDamage() > 0 && e.getDamage(DamageModifier.BLOCKING) < 0;
    }
}

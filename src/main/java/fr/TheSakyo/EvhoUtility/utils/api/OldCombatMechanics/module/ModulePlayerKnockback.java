package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Retourne la formule du knockback à 1.8.
 * Désactive également la résistance au knockback des netherites.
 */
public class ModulePlayerKnockback extends Module {

    private double knockbackHorizontal;
    private double knockbackVertical;
    private double knockbackVerticalLimit;
    private double knockbackExtraHorizontal;
    private double knockbackExtraVertical;
    private boolean netheriteKnockbackResistance;

    private final Map<UUID, Vector> playerKnockbackHashMap = new WeakHashMap<>();

    public ModulePlayerKnockback(UtilityMain plugin) {

        super(plugin, "old-player-knockback");
        reload();
    }

    @Override
    public void reload() {

        knockbackHorizontal = this.module().getDouble("knockback-horizontal", 0.4);
        knockbackVertical = this.module().getDouble("knockback-vertical", 0.4);
        knockbackVerticalLimit = this.module().getDouble("knockback-vertical-limit", 0.4);
        knockbackExtraHorizontal = this.module().getDouble("knockback-extra-horizontal", 0.5);
        knockbackExtraVertical = this.module().getDouble("knockback-extra-vertical", 0.1);
        netheriteKnockbackResistance = this.module().getBoolean("enable-knockback-resistance", false) && Reflector.versionIsNewerOrEqualAs(1, 16, 0);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) { playerKnockbackHashMap.remove(e.getPlayer().getUniqueId()); }

    // Vanilla fait son propre knockback, donc nous devons le définir à nouveau.
    // Priorité = la plus basse, car nous ignorons la vélocité existante, ce qui pourrait casser d'autres plugins.
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerVelocityEvent(PlayerVelocityEvent event) {

        final UUID uuid = event.getPlayer().getUniqueId();
        if(!playerKnockbackHashMap.containsKey(uuid)) return;

        event.setVelocity(playerKnockbackHashMap.get(uuid));
        playerKnockbackHashMap.remove(uuid);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {

        // Désactiver netherite kb, l'attribut de résistance au knockback fait que l'évènement velocity n'est pas appelé.
        final Entity entity = event.getEntity();
        if(!(entity instanceof Player) || netheriteKnockbackResistance) return;

        final AttributeInstance attribute = ((Player) entity).getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        attribute.getModifiers().forEach(attribute::removeModifier);
    }

    // Priorité de surveillance parce que nous ne modifions rien ici, mais appliquons sur l'événement de changement de vélocité
    @Deprecated
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {

        final Entity damager = event.getDamager();
        if(!(damager instanceof final LivingEntity attacker)) return;
        if(!isEnabled(attacker.getWorld())) return;

        final Entity entityDamager = event.getEntity();
        if(!(entityDamager instanceof final Player victim)) return;

        if(event.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
        if(event.getDamage(EntityDamageEvent.DamageModifier.BLOCKING) > 0) return;

        // Déterminer la direction du knockback de base
        Location attackerLocation = attacker.getLocation();
        Location victimLocation = victim.getLocation();

        double d0 = attackerLocation.getX() - victimLocation.getX();
        double d1;

        for(d1 = attackerLocation.getZ() - victimLocation.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) { d0 = (Math.random() - Math.random()) * 0.01D; }

        final double magnitude = Math.sqrt(d0 * d0 + d1 * d1);

        // Obtenir le knockback du joueur avant que la friction ne soit appliquée.
        final Vector playerVelocity = victim.getVelocity();

        // Appliquer la friction, puis ajouter le knockback de base
        playerVelocity.setX((playerVelocity.getX() / 2) - (d0 / magnitude * knockbackHorizontal));
        playerVelocity.setY((playerVelocity.getY() / 2) + knockbackVertical);
        playerVelocity.setZ((playerVelocity.getZ() / 2) - (d1 / magnitude * knockbackHorizontal));

        // Calculer le bonus de knockback pour le sprint ou les niveaux d'enchantement de knockback.
        final EntityEquipment equipment = attacker.getEquipment();
        if(equipment != null) {
            final ItemStack heldItem = equipment.getItemInMainHand().getType() == Material.AIR ? equipment.getItemInOffHand() : equipment.getItemInMainHand();

            int bonusKnockback = heldItem.getEnchantmentLevel(Enchantment.KNOCKBACK);
            if(attacker instanceof Player && ((Player) attacker).isSprinting()) ++bonusKnockback;

            if(playerVelocity.getY() > knockbackVerticalLimit) playerVelocity.setY(knockbackVerticalLimit);

            if(bonusKnockback > 0) {  // Appliquer le bonus de knockback

                playerVelocity.add(new Vector((-Math.sin(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) * (float)bonusKnockback * knockbackExtraHorizontal),
                        knockbackExtraVertical, Math.cos(attacker.getLocation().getYaw() * 3.1415927F / 180.0F) * (float)bonusKnockback * knockbackExtraHorizontal));
            }
        }

        if(netheriteKnockbackResistance) {

             // Permet à la netherite d'affecter le knockback horizontal. Chaque pièce d'armure offre une résistance de 10%.
            final double resistance = 1 - victim.getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE).getValue();
            playerVelocity.multiply(new Vector(resistance, 1, resistance));
        }

        final UUID victimId = victim.getUniqueId();

        // Le Knockback est envoyé immédiatement en 1.8+, il n'y a aucune raison d'envoyer des paquets manuellement.
        playerKnockbackHashMap.put(victimId, playerVelocity);

        // Parfois 'PlayerVelocityEvent' ne se déclenche pas, supprimez les données pour ne pas affecter les événements ultérieurs si cela se produit.
        Bukkit.getScheduler().runTaskLater(plugin, () -> playerKnockbackHashMap.remove(victimId), 1);
    }
}
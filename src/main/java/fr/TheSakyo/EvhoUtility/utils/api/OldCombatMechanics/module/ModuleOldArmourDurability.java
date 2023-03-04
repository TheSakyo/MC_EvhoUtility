package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ModuleOldArmourDurability extends Module {

    private final Map<UUID, List<ItemStack>> explosionDamaged = new WeakHashMap<>();

    public ModuleOldArmourDurability(UtilityMain plugin) {
        super(plugin, "old-armour-durability");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemDamage(PlayerItemDamageEvent e) {

        Player player = e.getPlayer();

        if(!isEnabled(player.getWorld())) return;

        final ItemStack item = e.getItem();
        final Material itemType = item.getType();

        // Vérifiez si c'est une pièce d'armure qu'ils portent actuellement.
        if(Arrays.stream(player.getInventory().getArmorContents()).noneMatch(armourPiece -> armourPiece != null &&
                        armourPiece.getType() == itemType && armourPiece.getType() != Material.ELYTRA /* Ignore l'élytre, car il n'apporte aucune protection de toute façon. */)) return;

        final UUID uuid = player.getUniqueId();
        if(explosionDamaged.containsKey(uuid)) {

            final List<ItemStack> armour = explosionDamaged.get(uuid);

            // 'ItemStack.equals()' vérifie le matériau, la durabilité et la quantité pour s'assurer que rien n'a changé entre-temps.
            // Nous vérifions toutes les pièces de cette manière, au cas où ils porteraient deux casques ou quelque chose d'étrange.
            final List<ItemStack> matchedPieces = armour.stream().filter(piece -> piece.equals(item)).toList();

            armour.removeAll(matchedPieces);
            debug("Ignorance de l'objet correspond à l'explosion...", player);

            if(!matchedPieces.isEmpty()) return;
        }

        int reduction = this.module().getInt("reduction");

         // 60 + (40 / (niveau + 1) ) % de chances que la durabilité soit réduite (pour chaque point de durabilité).
        final int damageChance = 60 + (40 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1));

        final Random random = new Random();
        final int randomInt = random.nextInt(100); // entre 0 (inclus) et 100 (exclus)
        if(randomInt >= damageChance) reduction = 0;

        debug("Item endommagé: " + itemType + " Dégâts: " + e.getDamage() + " Changé en: " + reduction, player);
        e.setDamage(reduction);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerExplosionDamage(EntityDamageEvent e) {

        if(e.isCancelled()) return;
        if(e.getEntityType() != EntityType.PLAYER) return;

        final EntityDamageEvent.DamageCause cause = e.getCause();
        if(cause != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && cause != EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) return;

        final Player player = (Player) e.getEntity();
        final UUID uuid = player.getUniqueId();

        final List<ItemStack> armour = Arrays.stream(player.getInventory().getArmorContents()).filter(Objects::nonNull).collect(Collectors.toList());
        explosionDamaged.put(uuid, armour);

        BukkitRunnable runnable = new BukkitRunnable() {

            @Override
            public void run() {

                explosionDamaged.remove(uuid);
                debug("Suppression de l'ensemble des explosions !", player);
            }
        };

        // Ce délai semble suffisant pour que les événements de durabilité se déclenchent.
        runnable.runTaskLater(plugin, 1);
        debug("Explosion détectée !", player);
    }
}

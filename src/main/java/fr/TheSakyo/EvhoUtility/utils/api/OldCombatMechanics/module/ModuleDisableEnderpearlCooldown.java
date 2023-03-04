package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;

/**
 * Permet de lancer des enderpearls aussi souvent que vous le souhaitez, pas seulement après un cooldown.
 */
public class ModuleDisableEnderpearlCooldown extends Module {

   /**
     * Contient les joueurs qui ont lancé une perle ender. Comme le gestionnaire appelle launchProjectile, qui appelle également ProjectileLaunchEvent,
     * nous devons ignorer l'appel à cet événement.
     */
    private final Set<UUID> ignoredPlayers = new HashSet<>();
    private Map<UUID, Long> lastLaunched;
    private int cooldown;
    private String message;

    public ModuleDisableEnderpearlCooldown(UtilityMain plugin) {

        super(plugin, "disable-enderpearl-cooldown");
        reload();
    }

    public void reload() {

        cooldown = this.module().getInt("cooldown");
        if(cooldown > 0) {

            if(lastLaunched == null) lastLaunched = new WeakHashMap<>();

        } else lastLaunched = null;
        message = this.module().getBoolean("showMessage") ? this.module().getString("message") : null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerShoot(ProjectileLaunchEvent e) {

        if(e.isCancelled()) return; // Pour la compatibilité avec d'autres plugins

        final Projectile projectile = e.getEntity();
        final ProjectileSource shooter = projectile.getShooter();

         if(!(projectile instanceof EnderPearl)) return;
        if(!(shooter instanceof final Player player)) return;

        if(!isEnabled(player.getWorld())) return;

        final UUID uuid = player.getUniqueId();

        if(ignoredPlayers.contains(uuid)) return;

        e.setCancelled(true);

         // Vérifiez si le cooldown a déjà expiré.
        if(lastLaunched != null) {

            final long currentTime = System.currentTimeMillis() / 1000;

            if(lastLaunched.containsKey(uuid)) {

                final long elapsedSeconds = currentTime - lastLaunched.get(uuid);

                if(elapsedSeconds < cooldown) {

                    if(message != null) Messenger.sendNormalMessage(player,message,cooldown - elapsedSeconds);
                    return;
                }
            }

            lastLaunched.put(uuid, currentTime);
        }

        // Assure que nous ignorons l'événement déclenché par 'launchProjectile'.
        ignoredPlayers.add(uuid);
        ignoredPlayers.remove(uuid);

         final EnderPearl pearl = player.launchProjectile(EnderPearl.class);
        pearl.setVelocity(player.getEyeLocation().getDirection().multiply(2));

        if(player.getGameMode() == GameMode.CREATIVE) return;

        final ItemStack enderpearlItemStack;
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack mainHand = playerInventory.getItemInMainHand();
        final ItemStack offHand = playerInventory.getItemInOffHand();

        if(isEnderPearl(mainHand)) enderpearlItemStack = mainHand;
        else if(isEnderPearl(offHand)) enderpearlItemStack = offHand;
        else return;

        enderpearlItemStack.setAmount(enderpearlItemStack.getAmount() - 1);
    }

    private boolean isEnderPearl(ItemStack itemStack) { return itemStack != null && itemStack.getType() == Material.ENDER_PEARL; }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) { if(lastLaunched != null) lastLaunched.remove(e.getPlayer().getUniqueId()); }
}

package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.MathHelper;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Établit des règles de régénération de santé personnalisées.
 * Valeurs par défaut basées sur la version 1.8 de <a href="https://minecraft.gamepedia.com/Hunger?oldid=948685">https://minecraft.gamepedia.com/Hunger?oldid=948685</a>.
 */
public class ModulePlayerRegen extends Module {

    private final Map<UUID, Long> healTimes = new WeakHashMap<>();

    public ModulePlayerRegen(UtilityMain plugin) { super(plugin, "old-player-regen"); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onRegen(EntityRegainHealthEvent e) {

        if(e.isCancelled()) return; // Au cas où un autre plugin aurait annulé l'événement.

        if(e.getEntityType() != EntityType.PLAYER || e.getRegainReason() != EntityRegainHealthEvent.RegainReason.SATIATED) return;

        final Player p = (Player)e.getEntity();

        if(!isEnabled(p.getWorld())) return;
        final UUID playerId = p.getUniqueId();

        // Nous annulons le regen, mais la saturation et l'épuisement doivent être ajustés séparément.
        // L'Épuisement est modifié au prochain tick, et la saturation au tick suivant (si épuisement > 4).
        e.setCancelled(true);

        // Obtient les valeurs d'épuisement et de saturation avant que la guérison ne les modifie
        final float previousExhaustion = p.getExhaustion();
        final float previousSaturation = p.getSaturation();

        // Vérifie qu'il s'est écoulé au moins x secondes depuis la dernière guérison.
        final long currentTime = System.currentTimeMillis();
        final boolean hasLastHealTime = healTimes.containsKey(playerId);
        final long lastHealTime = healTimes.computeIfAbsent(playerId, id -> currentTime);

        debug("Épuisement: " + previousExhaustion + " Saturation: " + previousSaturation + " Temps: " + (currentTime - lastHealTime), p);

        // Si nous sautons cette guérison, nous devons corriger l'épuisement dans le tic suivant.
        if (hasLastHealTime && currentTime - lastHealTime <= this.module().getLong("interval")) {

            Bukkit.getScheduler().runTaskLater(plugin, () -> p.setExhaustion(previousExhaustion), 1L);
            return;
        }

        final double maxHealth = p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        final double playerHealth = p.getHealth();

        if (playerHealth < maxHealth) {

            p.setHealth(MathHelper.clamp(playerHealth + this.module().getInt("amount"), 0.0, maxHealth));
            healTimes.put(playerId, currentTime);
        }

        // Calcule la nouvelle valeur d'épuisement, qui doit être comprise entre 0 et 4. Si elle est supérieure, elle réduira la saturation dans le tick suivant.
        final float exhaustionToApply = (float)this.module().getDouble("exhaustion");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            // Nous le faisons dans le tick suivant parce que bukkit n'arrête pas le changement d'épuisement lors de l'annulation de l'événement.
            p.setExhaustion(previousExhaustion + exhaustionToApply);
            debug("Épuisement avant: " + previousExhaustion + " Maintenant: " + p.getExhaustion() + " Saturation maintenant: " + previousSaturation, p);

        }, 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) { healTimes.remove(e.getPlayer().getUniqueId()); }
}

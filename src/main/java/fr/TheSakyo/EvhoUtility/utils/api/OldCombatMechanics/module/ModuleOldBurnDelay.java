package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

/**
 * Ramener l'ancien comportement de retardement de la combustion du feu
 */
public class ModuleOldBurnDelay extends Module {

    private int fireTicks;

    public ModuleOldBurnDelay(UtilityMain plugin) {

        super(plugin, "old-burn-delay");
        reload();
    }

    @Override
    public void reload() { fireTicks = this.module().getInt("fire-ticks"); }

    @EventHandler
    public void onFireTick(EntityDamageEvent e) {

        final Entity entity = e.getEntity();

        if(!isEnabled(entity.getWorld())) return;

        if(e.getCause() == EntityDamageEvent.DamageCause.FIRE) {

            entity.setFireTicks(fireTicks);
            debug("Met le feu aux tiques pour " + fireTicks, entity);
        }
    }
}

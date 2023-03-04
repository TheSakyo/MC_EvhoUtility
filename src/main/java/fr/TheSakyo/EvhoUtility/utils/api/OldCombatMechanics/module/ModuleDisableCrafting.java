package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.ConfigUtils;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Rend les matériaux spécifiés inaptes à l'artisanat.
 */
public class ModuleDisableCrafting extends Module {

    private List<Material> denied;
    private String message;

    public ModuleDisableCrafting(UtilityMain plugin) {

        super(plugin, "disable-crafting");
        reload();
    }

    @Override
    public void reload() {

        denied = ConfigUtils.loadMaterialList(this.module(), "denied");
        message = this.module().getBoolean("showMessage") ? this.module().getString("message") : null;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrepareItemCraft(PrepareItemCraftEvent e){

        final List<HumanEntity> viewers = e.getViewers();
        if(viewers.size() < 1) return;

        final World world = viewers.get(0).getWorld();
        if(!isEnabled(world)) return;

        final CraftingInventory inv = e.getInventory();
        final ItemStack result = inv.getResult();

        if(result != null && denied.contains(result.getType())) {

            inv.setResult(null);
            if(message != null) viewers.forEach(viewer -> Messenger.sendNormalMessage(viewer,message));
        }
    }
}
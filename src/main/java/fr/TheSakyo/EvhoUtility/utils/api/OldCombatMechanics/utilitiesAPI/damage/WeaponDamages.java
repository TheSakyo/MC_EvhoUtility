package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM_ConfigHandler;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.ConfigUtils;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class WeaponDamages {

    private static Map<String, Double> damages;

    private static UtilityMain plugin;

    public static void initialise(UtilityMain plugin) {

        WeaponDamages.plugin = plugin;
        reload();
    }

    private static void reload() {

        ConfigurationSection section = new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml").getConfigurationSection("old-tool-damage.damages");
        damages = ConfigUtils.loadDoubleMap(section);
    }

    public static double getDamage(Material mat) {

        //Remplacer les noms des matériaux de la 1.14 par ceux utilisés dans le fichier config.yml
        String name = mat.name().replace("GOLDEN", "GOLD").replace("WOODEN", "WOOD").replace("SHOVEL", "SPADE");
        return damages.getOrDefault(name, -1.0);
    }

    public static Map<Material, Double> getMaterialDamages() {

        Map<Material, Double> materialMap = new HashMap<>();

        damages.forEach((name, damage) -> {

            String newName = name.replace("GOLD", "GOLDEN").replace("WOOD", "WOODEN").replace("SPADE", "SHOVEL");
            materialMap.put(Material.valueOf(newName), damage);
        });

        return materialMap;
    }
}

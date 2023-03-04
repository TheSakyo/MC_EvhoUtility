package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.GenericPotionDurations;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions.PotionDurations;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionType;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Divers utilitaires pour faciliter le travail avec {@link org.bukkit.configuration.Configuration Configurations}.
 *
 * @see org.bukkit.configuration.file.YamlConfiguration
 * @see org.bukkit.configuration.ConfigurationSection
 */
public class ConfigUtils {

    /**
     * Charge en toute sécurité tous les doubles d'une section de configuration, en lisant à la fois les valeurs doubles et entières.
     *
     * @param section La section à partir de laquelle charger les doubles.
     * @return La carte des doubles.
     */
    public static Map<String, Double> loadDoubleMap(ConfigurationSection section) {

        Objects.requireNonNull(section, "La section ne peut pas être nulle !");

        return section.getKeys(false).stream()
                .filter(((Predicate<String>) section::isDouble).or(section::isInt))
                .collect(Collectors.toMap(key -> key, section::getDouble));
    }

    /**
     * Charge la liste des {@link Material Materials} avec la clé donnée à partir d'une section de configuration.
     * Ignore en toute sécurité les matériaux non correspondants.
     *
     * @param section La section à partir de laquelle charger la liste de matériaux.
     * @param key La clé de la liste de matériaux.
     * @return La liste de matériaux chargée ou une liste vide s'il n'y a pas de liste à la clé donnée.
     */
    public static List<Material> loadMaterialList(ConfigurationSection section, String key) {

        Objects.requireNonNull(section, "La section ne peut pas être nulle !");
        Objects.requireNonNull(key, "La clé ne peut pas être nulle !");

        if(!section.isList(key)) { return new ArrayList<>(); }

        return section.getStringList(key).stream().map(Material::matchMaterial).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * Récupère les valeurs de durée des potions depuis la configuration
     *
     * @param section La section à partir de laquelle charger les valeurs de durée.
     *
     * @return Un HashMap de PotionType et de PotionDurations.
     */
    public static HashMap<PotionType, PotionDurations> loadPotionDurationsList(ConfigurationSection section) {

        Objects.requireNonNull(section, "La section ne peut pas être nulle !");
        HashMap<PotionType, PotionDurations> durationsHashMap = new HashMap<>();
        ConfigurationSection durationsSection = section.getConfigurationSection("potion-durations");

            for(String potionName : durationsSection.getKeys(false)) {

                ConfigurationSection potionSection = durationsSection.getConfigurationSection(potionName);
                ConfigurationSection drinkable = potionSection.getConfigurationSection("drinkable");
                ConfigurationSection splash = potionSection.getConfigurationSection("splash");

                potionName = potionName.toUpperCase(Locale.ROOT);

                try {

                    PotionType potionType = PotionType.valueOf(potionName);
                    durationsHashMap.put(potionType, new PotionDurations(getGenericDurations(drinkable), getGenericDurations(splash)));

                } catch(IllegalArgumentException e) { Messenger.debug("Saut du chargement de la potion " + potionName); }
            }

        return durationsHashMap;
    }

    private static GenericPotionDurations getGenericDurations(ConfigurationSection section) {

        return new GenericPotionDurations(section.getInt("base"), section.getInt("II"), section.getInt("extended"));
    }
}

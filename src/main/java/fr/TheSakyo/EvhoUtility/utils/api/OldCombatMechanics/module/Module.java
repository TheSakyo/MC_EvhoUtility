package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.OldCM_ConfigHandler;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Config;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Locale;

/**
 * Un module fournissant une fonctionnalité spécifique, par exemple la restauration de l'effet de choc de la canne à pêche.
 */
public abstract class Module implements Listener {

    protected UtilityMain plugin;

    private final String configName;
    private final String moduleName;

    /**
     * Crée un nouveau module.
     *
     * @param plugin L'Instance du plugin
     * @param configName Le Nom du Module dans la Configuration
     */
    protected Module(UtilityMain plugin, String configName) {

        this.plugin = plugin;
        this.configName = configName;
        this.moduleName = getClass().getSimpleName();
    }

    /**
     * Vérifie si le module est activé dans le monde donné.
     *
     * @param world Le monde a vérifié. Null pour vérifier si le module est désactivé dans le monde.
     * @return Vrai si le module est activé dans ce monde.
     */
    public boolean isEnabled(World world){ return Config.moduleEnabled(configName, world); }

    /**
     * Vérifie si ce module est globalement activé/désactivé.
     *
     * @retourne Vrai si ce module est globalement activé.
     */
    public boolean isEnabled(){ return isEnabled(null); }

    /**
     * Vérifie si un paramètre donné de ce module est activé.
     *
     * @param name le nom du paramètre
     * @return Vrai si le paramètre portant ce nom est activé. Retourne false si le paramètre n'existe pas.
     */
    public boolean isSettingEnabled(String name) { return new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml").getBoolean(configName + "." + name, false); }

    /**
     * Retourne la section de configuration pour ce module.
     *
     * @return La section de configuration pour ce module
     */
    public ConfigurationSection module() { return new OldCM_ConfigHandler(plugin).getConfig("/OldCombatCfg.yml").getConfigurationSection(configName); }

    /**
     * Appelé lorsque le plugin est rechargé. Doit relire toutes les clés de configuration pertinentes et les autres ressources qui pourraient avoir
     * changé.
     */
    public void reload() { /* Intentionnellement laissé vide ! À utiliser par les modules individuels. */ }

    /**
     * Sort un message de débogage.
     *
     * @param text Lee textes du message
     */
    protected void debug(String text) { Messenger.debug("[" + moduleName + "] " + text); }

    /**
     * Envoie un message de débogage à l'expéditeur de la commande donnée.
     *
     * @param text Le texte du message
     * @param sender L'Éxpéditeur à qui l'envoyer
     */
    protected void debug(String text, CommandSender sender) {

        if(Config.debugEnabled()){ Messenger.send(sender, "&8&l[&fDEBUG&8&l][&f" + moduleName + "&8&l]&7 " + text); }
    }

    @Override
    public String toString() {

        return Arrays.stream(configName.split("-")).map(word -> Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase(Locale.ROOT))
                .reduce((a, b) -> a + " " + b).orElse(configName);
    }

    /**
     * Récupère le nom du module, tel qu'il est extrait du nom de la classe.
     *
     * @return Le nom du module, par exemple ModuleDisableAttackCooldown.
     */
    public String getModuleName(){ return moduleName; }
}

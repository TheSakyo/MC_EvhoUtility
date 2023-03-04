package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics;

import com.destroystokyo.paper.Metrics;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.hooks.api.Hook;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module.Module;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Config;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.EntityDamageByEntityListener;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module.*;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventException;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OldCM {

    /* ----------------------------------------------------------------- */

    private static UtilityMain plugin = UtilityMain.getInstance();
    private static final Logger logger = Logger.getAnonymousLogger();
    private static final List<Runnable> disableListeners = new ArrayList<>();
    private static final List<Runnable> enableListeners = new ArrayList<>();
    private static final List<Hook> hooks = new ArrayList<>();

    // Variable pour le Nom du Plugin //
	public static String prefix = ChatColor.WHITE + "[" + ChatColor.RED + "Old" + ChatColor.DARK_GREEN + "Combat" + ChatColor.WHITE + "]" + " ";
	// Variable pour le Nom du Plugin //


    /* ----------------------------------------------------------------- */


    public OldCM() { super(); }

    public static void onEnable() {

        // Configuration du fichier config.yml
        new OldCM_ConfigHandler(plugin).setupConfig();

        // Initialisation de l'utilitaire ModuleLoader
        ModuleLoader.initialise(plugin);

        // Enregistre tous les modules
        registerModules();

        // Initialise tous les crochets
        hooks.forEach(hook -> hook.init(plugin));

        // Initialise l'utilitaire Messenger
        Messenger.initialise(plugin);

        // Initialise l'utilitaire de configuration
        Config.initialise(plugin);

        // Paper Metrics
        Metrics metrics = new Metrics(plugin.getServer().getName(), "16758", false, logger);

        // Diagramme à barres simple
        metrics.addCustomChart(new Metrics.SimpleBarChart("enabled_modules", () -> ModuleLoader.getModules().stream().filter(Module::isEnabled).collect(Collectors.toMap(Module::toString, module -> 1))));

        // Graphique à secteurs des modules activés/désactivés pour chaque module.
        ModuleLoader.getModules().forEach(module -> metrics.addCustomChart(new Metrics.SimplePie(module.getModuleName() + "_pie", () -> module.isEnabled() ? "enabled" : "disabled")));

        enableListeners.forEach(Runnable::run);

        // Gère correctement le chargement/déchargement de 'Plugman'.
        List<RegisteredListener> joinListeners = Arrays.stream(PlayerJoinEvent.getHandlerList().getRegisteredListeners()).filter(registeredListener -> registeredListener.getPlugin().equals(plugin)).toList();

        Bukkit.getOnlinePlayers().forEach(player -> {

            PlayerJoinEvent event = new PlayerJoinEvent(player, CustomMethod.StringToComponent(""));

            // Trompez tous les modules en leur faisant croire que le joueur vient de se joindre au jeu si le plugin a été chargé avec Plugman.
            // De cette façon, les vitesses d'attaque, les modifications d'objets, etc. seront appliquées immédiatement au lieu de l'être après une nouvelle connexion.
            joinListeners.forEach(registeredListener -> {

                try { registeredListener.callEvent(event); }
                catch(EventException e) { e.printStackTrace(); }
            });
        });

        //Message disant que l'api 'OldCombat' est activé
        plugin.console.sendMessage(plugin.prefix + ChatColor.GREEN + " - " + prefix + ChatColor.GREEN + "API Enabled");
    }


    public static void onDisable() {

        disableListeners.forEach(Runnable::run);

        // Gère correctement le chargement/déchargement de 'Plugman'.
        List<RegisteredListener> quitListeners = Arrays.stream(PlayerQuitEvent.getHandlerList().getRegisteredListeners()).filter(registeredListener -> registeredListener.getPlugin().equals(plugin)).toList();

        // Faites croire à tous les modules que le joueur vient de quitter le jeu si le plugin a été déchargé avec Plugman.
        // De cette façon, les vitesses d'attaque, les modifications d'objets, etc. seront restaurées immédiatement au lieu de l'être après une déconnexion.
        Bukkit.getOnlinePlayers().forEach(player -> {

            PlayerQuitEvent event = new PlayerQuitEvent(player, CustomMethod.StringToComponent(""), PlayerQuitEvent.QuitReason.KICKED);

            quitListeners.forEach(registeredListener -> {

                try { registeredListener.callEvent(event); }
                catch (EventException e) { e.printStackTrace(); }

            });
        });

        //Message disant que l'api 'OldCombat' est activé
        plugin.console.sendMessage(plugin.prefix + ChatColor.GREEN + " - " + prefix + ChatColor.RED + "API Disabled");
    }

    private static void registerModules() {

        // Écouteurs de modules
        ModuleLoader.addModule(new ModuleAttackCooldown(plugin));
        ModuleLoader.addModule(new ModulePlayerCollisions(plugin));

        //Les auditeurs enregistrés après avec la même priorité semblent être appelés plus tard

        //Ces quatre-là écoutent OldEntityDamageByEntityEvent :
        ModuleLoader.addModule(new ModuleOldToolDamage(plugin));
        ModuleLoader.addModule(new ModuleSwordSweep(plugin));
        ModuleLoader.addModule(new ModuleOldPotionEffects(plugin));
        ModuleLoader.addModule(new ModuleOldCriticalHits(plugin));

        //Les blocs suivants sont tous sur la plus basse priorité, ils seront donc appelés dans l'ordre suivant :
        //Ordre des dégâts : base → effets des potions -> coup critique -> enchantements -> blocage -> effets de l'armure.
        //'EntityDamageByEntityListener' appelle 'OldEntityDamageByEntityEvent', voir les modules ci-dessus.
        ModuleLoader.addModule(new EntityDamageByEntityListener(plugin));

        //Après 'ModuleSwordBlocking' pour calculer le blocage
        ModuleLoader.addModule(new ModuleShieldDamageReduction(plugin));

        //Ensuite,'OldArmourStrength' recalcule la défense d'armure en conséquence.
        ModuleLoader.addModule(new ModuleOldArmourStrength(plugin));

        ModuleLoader.addModule(new ModuleSwordBlocking(plugin));
        ModuleLoader.addModule(new ModuleOldArmourDurability(plugin));

        ModuleLoader.addModule(new ModuleFishingKnockback(plugin));
        ModuleLoader.addModule(new ModulePlayerKnockback(plugin));
        ModuleLoader.addModule(new ModulePlayerRegen(plugin));

        ModuleLoader.addModule(new ModuleDisableCrafting(plugin));
        ModuleLoader.addModule(new ModuleDisableProjectileRandomness(plugin));
        ModuleLoader.addModule(new ModuleDisableBowBoost(plugin));
        ModuleLoader.addModule(new ModuleProjectileKnockback(plugin));
        ModuleLoader.addModule(new ModuleDisableEnderpearlCooldown(plugin));
        ModuleLoader.addModule(new ModuleChorusFruit(plugin));

        ModuleLoader.addModule(new ModuleAttackSounds(plugin));
        ModuleLoader.addModule(new ModuleOldBurnDelay(plugin));
        ModuleLoader.addModule(new ModuleAttackFrequency(plugin));
        ModuleLoader.addModule(new ModuleFishingRodVelocity(plugin));
    }

    public static void upgradeConfig() { new OldCM_ConfigHandler(plugin).upgradeConfig(); }

    public static boolean doesConfigExist() { return new OldCM_ConfigHandler(plugin).doesConfigExist(); }

    /**
     * Enregistre un 'runnable' à exécuter lorsque le plugin est désactivé.
     *
     * @param action Un {@link Runnable} à exécuter lorsque le plugin est désactivé.
     */
    public static void addDisableListener(Runnable action) { disableListeners.add(action); }

    /**
     * Enregistre un 'runnable' à exécuter lorsque le plugin est activé.
     *
     * @param action Un {@link Runnable} à exécuter lorsque le plugin est activé.
     */
    public static void addEnableListener(Runnable action) { enableListeners.add(action); }
}

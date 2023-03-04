package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * Désactive le temps de recharge de l'attaque.
 */
public class ModuleAttackCooldown extends Module {

    public ModuleAttackCooldown(UtilityMain plugin) { super(plugin, "disable-attack-cooldown"); }

    @Override
    public void reload() { Bukkit.getOnlinePlayers().forEach(this::adjustAttackSpeed); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerJoinEvent e) { adjustAttackSpeed(e.getPlayer()); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldChange(PlayerChangedWorldEvent e) { adjustAttackSpeed(e.getPlayer()); }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        // Ceci est ici pour faciliter la désinstallation du plugin.
        setAttackSpeed(player, PVPMode.NEW_PVP.getBaseAttackSpeed());
    }

    /**
     * Ajuste la vitesse d'attaque à la valeur par défaut ou configurée, selon que le module est activé ou non.
     *
     * @param player Le Joueur pour lequel il faut le régler.
     */
    private void adjustAttackSpeed(Player player) {

        World world = player.getWorld();

        double attackSpeed = isEnabled(world) ? this.module().getDouble("generic-attack-speed") : PVPMode.NEW_PVP.getBaseAttackSpeed();

        setAttackSpeed(player, attackSpeed);
    }

    /**
     * Définit la vitesse d'attaque à la valeur donnée.
     *
     * @param player Le Joueur pour lequel le réglage doit être effectué.
     * @param attackSpeed La vitesse d'attaque à définir.
     */
    public static void setAttackSpeed(Player player, double attackSpeed) {

        AttributeInstance attribute = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED);
        if(attribute == null) { return; }

        double baseValue = attribute.getBaseValue();

        if(baseValue != attackSpeed) {

            Messenger.debug(String.format("Réglage de la vitesse d'attaque du joueur %s à %.2f (était : %.2f)", player.getName(), attackSpeed, baseValue));

            attribute.setBaseValue(attackSpeed);
            player.saveData();
        }
    }

    public static void setAttackSpeed(Player player, PVPMode mode) { setAttackSpeed(player,mode.getBaseAttackSpeed()); }



            /* ------------------------------------------------------------------------------------------------------------------------------------------ */

    /**
     * Les différents modes pvp pour les versions 1.8 ou plus récentes.
     */
    public enum PVPMode {

        // 16 est suffisant pour le désactiver
        OLD_PVP("1.8", 16),
        NEW_PVP("1.9+", 4);

        private final String name;
        private final double baseAttackSpeed;

        PVPMode(String name, double baseAttackSpeed) {

            this.name = name;
            this.baseAttackSpeed = baseAttackSpeed;
        }

        /**
         * Renvoie la version du mode PvP.
         *
         * @retrun La Version du mode PvP
         */
        public String getName() { return name; }

        /**
         * La valeur de base de {@link Attribute#GENERIC_ATTACK_SPEED}.
         * La valeur peut être une approximation, si l'attribut n'existe pas dans le mode PVP.
         *
         * @return La Valeur de base
         */
        public double getBaseAttackSpeed() { return baseAttackSpeed; }

        /**
         * Renvoie le mode PVP du joueur, par défaut {@link #OLD_PVP}.
         *
         * @param player Le Joueur pour lequel il faut récupérer le mode PvP
         * @return Le Mode PVP du joueur
         */
        public static PVPMode getModeForPlayer(Player player) {

            Objects.requireNonNull(player, "Le joueur ne peut pas être nul !");

            double baseAttackSpeed = player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).getBaseValue();
            return getByBaseAttackSpeed(baseAttackSpeed).orElse(PVPMode.OLD_PVP);
        }

        private static Optional<PVPMode> getByBaseAttackSpeed(double speed) { return Arrays.stream(values()).filter(pvpMode -> pvpMode.getBaseAttackSpeed() == speed).findFirst(); }
    }
}

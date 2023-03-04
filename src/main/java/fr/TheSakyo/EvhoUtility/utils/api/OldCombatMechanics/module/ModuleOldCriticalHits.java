package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.module;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage.OldEntityDamageByEntityEvent;
import org.bukkit.event.EventHandler;

import java.util.Random;

public class ModuleOldCriticalHits extends Module {

    public ModuleOldCriticalHits(UtilityMain plugin) {

        super(plugin, "old-critical-hits");
        reload();
    }

    private boolean isMultiplierRandom, allowSprinting, roundDown;
    private double multiplier, addend;
    private Random random;

    @Override
    public void reload() {

        random = new Random();

        isMultiplierRandom = this.module().getBoolean("is-multiplier-random", true);
        allowSprinting = this.module().getBoolean("allowSprinting", true);
        roundDown = this.module().getBoolean("roundDown", true);
        multiplier = this.module().getDouble("multiplier", 1.5);
        addend = this.module().getDouble("addend", 1);
    }

    @EventHandler
    public void onDamage(OldEntityDamageByEntityEvent e) {

        if(!isEnabled(e.getDamagee().getWorld())) return;

         // En 1.9, un coup critique nécessite que le joueur ne sprinte pas.
        if(e.was1_8Crit() && (allowSprinting || !e.wasSprinting())) {

            // Recalcule selon les règles 1.8 : https://minecraft.fandom.com/wiki/Damage?oldid=706258#Critical_hits
            // C'est-à-dire que l'attaque inflige une quantité aléatoire de dégâts supplémentaires, jusqu'à 50% de plus (arrondi à l'inférieur) plus un cœur.
            // Code Bukkit 1.8_r3 : i += this.random.nextInt(i / 2 + 2) ;
            // Nous générons plutôt un multiplicateur aléatoire entre 1 et 1,5 (ou configuré par l'utilisateur).
            double actualMultiplier = isMultiplierRandom ? (1 + random.nextDouble() * (multiplier - 1)) : multiplier;
            e.setCriticalMultiplier(actualMultiplier);
            e.setCriticalAddend(addend);
            e.setRoundCritDamage(roundDown);
        }
    }
}

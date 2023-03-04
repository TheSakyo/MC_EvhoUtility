package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.potions;

/**
 * Used to hold information on duration of base, II & extended versions of potion.
 * <p>
 * All durations are in seconds.
 */
public class GenericPotionDurations {

    private final int base, II, extended;

    public GenericPotionDurations(int base, int II, int extended) {

        this.base = base;
        this.II = II;
        this.extended = extended;
    }

    /**
     * Renvoie la durée de base de la potion en secondes.
     *
     * @return La durée de la potion de base en secondes.
     */
    public int getBaseTime() { return base; }

    /**
     * Renvoie la durée en secondes de la potion amplifiée.
     *
     * @return La durée en secondes de la potion amplifiée.
     */
    public int getIITime() { return II; }

    /**
     * Renvoie la durée en secondes de la potion étendue.
     *
     * @return La durée en secondes de la potion étendue.
     */
    public int getExtendedTime() { return extended; }
}

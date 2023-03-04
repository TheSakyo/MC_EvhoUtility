package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.materials;

import org.bukkit.inventory.ItemStack;

/**
 * Un article dans différentes versions.
 */
public interface VersionedMaterial {

    /**
     * Crée une nouvelle pile d'éléments.
     *
     * @return La pile d'éléments créée
     */
    ItemStack newInstance();

    /**
     * Indique si la pile d'éléments est constituée de ce matériau.
     *
     * @param other La pile d'éléments
     *
     * @return Vrai si la pile d'éléments est de ce matériau.
     */
    boolean isSame(ItemStack other);
}

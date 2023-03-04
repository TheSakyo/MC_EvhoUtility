package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.materials;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * Un matériau qui essaie chaque nom dans une liste donnée jusqu'à ce qu'il trouve un matériau fonctionnel.
 */
public class NameListVersionedMaterial implements VersionedMaterial {

    private Material finalMaterial;

    private NameListVersionedMaterial(Material finalMaterial) { this.finalMaterial = finalMaterial; }

    @Override
    public ItemStack newInstance() { return new ItemStack(finalMaterial); }

    @Override
    public boolean isSame(ItemStack other) { return other.getType() == finalMaterial; }

   /**
     * Renvoie un nouveau {@link VersionedMaterial} qui choisit le premier matériau fonctionnel dans une liste de noms.
     *
     * @param names Les noms des matériaux
     *
     * @return Le matériau versionné
     *
     * @throws IllegalArgumentException Si aucun matériau n'était valide
     */
    public static VersionedMaterial ofNames(String... names) {

        for(String name : names) {

            Material material = Material.matchMaterial(name);

            if(material != null) { return new NameListVersionedMaterial(material); }

            material = Material.matchMaterial(name, true);
            if(material != null) { return new NameListVersionedMaterial(material); }
        }

        throw new IllegalArgumentException("N'ayant pas trouvé de matériel de travail, Essait : " + String.join(",", names) + ".");
    }
}

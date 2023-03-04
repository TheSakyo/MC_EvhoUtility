package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.materials;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection.Reflector;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.function.Supplier;

/**
 * Un matériel qui a une version pour avant 1.13 et après 1.13.
 */
public class DualVersionedMaterial implements VersionedMaterial {

    private Supplier<ItemStack> oldItem;
    private Supplier<ItemStack> newItem;

    /**
     * Crée un nouveau matériau à double version.
     *
     * @param oldItem Le fournisseur d'items de l'ancienne version.
     * @param newItem Le fournisseur de l'item de la nouvelle version.
     */
    public DualVersionedMaterial(Supplier<ItemStack> oldItem, Supplier<ItemStack> newItem) {

        ItemStack item = oldItem.get();
        Damageable meta = (Damageable)item.getItemMeta();

        if(item.getType() == Material.matchMaterial("INK_SACK")) {

            meta.setDamage(Short.valueOf((short)4).intValue());
            this.oldItem = () -> item;

        } else if(item.getType() == Material.GOLDEN_APPLE) {

            meta.setDamage(Short.valueOf((short)1).intValue());
            this.oldItem = () -> item;

        } else { this.oldItem = oldItem; }

        this.newItem = newItem;
    }

    @Override
    public ItemStack newInstance() { return getItemSupplier().get(); }

    @Override
    public boolean isSame(ItemStack other) {

        ItemStack baseInstance = newInstance();

        Damageable baseInstanceItemMeta = (Damageable)baseInstance.getItemMeta();
        Damageable otherItemMeta = (Damageable)other.getItemMeta();

        // les 'items' ne diffèrent pas plus que ces deux éléments.
        return baseInstance.getType() == other.getType() && baseInstanceItemMeta.getDamage() == otherItemMeta.getDamage();
    }

    private Supplier<ItemStack> getItemSupplier() { return Reflector.versionIsNewerOrEqualAs(1, 13, 0) ? newItem : oldItem; }

    @Override
    public String toString() { return "DualVersionedMaterial{" + "picked=" + (getItemSupplier() == newItem ? "new" : "old") + ", item=" + newInstance() + '}'; }

    /**
     * Renvoie un nouveau {@link DualVersionedMaterial} basé sur les noms des matériaux.
     *
     * @param nameOld L'Ancien nom
     * @param nameNew Le Nouveau nom
     *
     * @return un matériel à double version utilisant les noms fournis
     */
    public static VersionedMaterial ofMaterialNames(String nameOld, String nameNew) { return new DualVersionedMaterial(fromMaterial(nameOld), fromMaterial(nameNew)); }

    private static Supplier<ItemStack> fromMaterial(String name) { return () -> new ItemStack(Material.matchMaterial(name)); }
}

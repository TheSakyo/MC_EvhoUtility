package fr.TheSakyo.EvhoUtility.utils.custom.enums;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Récupère une liste de Matériaux
 */
public enum MaterialsUtils {

    ALL_AIR(Material.AIR, Material.CAVE_AIR, Material.VOID_AIR),
    ALL_GLASS(Material.GLASS_BOTTLE, Material.GLASS, Material.GLASS_PANE, Material.TINTED_GLASS, Material.SPYGLASS),
    ALL_STAINED_GLASS(Material.WHITE_STAINED_GLASS, Material.ORANGE_STAINED_GLASS, Material.MAGENTA_STAINED_GLASS, Material.LIGHT_BLUE_STAINED_GLASS,
                      Material.YELLOW_STAINED_GLASS, Material.LIME_STAINED_GLASS, Material.PINK_STAINED_GLASS, Material.GRAY_STAINED_GLASS,
                      Material.LIGHT_GRAY_STAINED_GLASS, Material.CYAN_STAINED_GLASS, Material.PURPLE_STAINED_GLASS, Material.BLUE_STAINED_GLASS,
                      Material.BROWN_STAINED_GLASS, Material.GREEN_STAINED_GLASS, Material.RED_STAINED_GLASS, Material.BLACK_STAINED_GLASS,
                      Material.WHITE_STAINED_GLASS_PANE, Material.ORANGE_STAINED_GLASS_PANE, Material.MAGENTA_STAINED_GLASS_PANE, Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                      Material.YELLOW_STAINED_GLASS_PANE, Material.LIME_STAINED_GLASS_PANE, Material.PINK_STAINED_GLASS_PANE, Material.GRAY_STAINED_GLASS_PANE,
                      Material.LIGHT_GRAY_STAINED_GLASS_PANE, Material.CYAN_STAINED_GLASS_PANE, Material.PURPLE_STAINED_GLASS_PANE, Material.BLUE_STAINED_GLASS_PANE,
                      Material.BROWN_STAINED_GLASS_PANE, Material.GREEN_STAINED_GLASS_PANE, Material.RED_STAINED_GLASS_PANE, Material.BLACK_STAINED_GLASS_PANE);

    private final Collection<Material> materials;
    MaterialsUtils(Material... materials) { this.materials = List.of(materials); }

    /**
     * Renvoie une collection de matériaux de l'énumération {@link MaterialsUtils} actuelle.
     *
     * @return Une collection de matériaux.
     */
    public Collection<Material> getMaterials() { return materials; }

                /* ------------------------------------------------------------------ */

    /**
     * récupère une Liste d'objets {@link MaterialsUtils} pour renvoyer une liste d'objets {@link Material}
     *
     * @return Une collection de matériaux.
     */
    public static Collection<Material> getMaterials(MaterialsUtils... materials) {

        List<Material> materialList = new ArrayList<Material>();
        for(MaterialsUtils material : materials) materialList.addAll(material.getMaterials());

        return materialList;
    }
}

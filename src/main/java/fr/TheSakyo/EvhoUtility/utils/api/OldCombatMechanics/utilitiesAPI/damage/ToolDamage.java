package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.damage;

import org.bukkit.Material;

/**
 * Valeurs par défaut des dommages causés par les outils Minecraft 1.9
 */
public enum ToolDamage {

    // valeurs communes
    STONE_SWORD(5), STONE_SHOVEL(3.5F), STONE_PICKAXE(3), STONE_AXE(9), STONE_HOE(1),
    IRON_SWORD(6), IRON_SHOVEL(4.5F), IRON_PICKAXE(4), IRON_AXE(9), IRON_HOE(1),
    DIAMOND_SWORD(7), DIAMOND_SHOVEL(5.5F), DIAMOND_PICKAXE(5), DIAMOND_AXE(9), DIAMOND_HOE(1),

    // valeurs pré-1.13
    STONE_SPADE(3.5F), IRON_SPADE(4.5F), DIAMOND_SPADE(5.5F),
    WOOD_SWORD(4), WOOD_SPADE(2.5F), WOOD_PICKAXE(2), WOOD_AXE(7), WOOD_HOE(1),
    GOLD_SWORD(4), GOLD_SPADE(2.5F), GOLD_PICKAXE(2), GOLD_AXE(7), GOLD_HOE(1),

    // valeurs post-1.13
    WOODEN_SWORD(4), WOODEN_SHOVEL(2.5F), WOODEN_PICKAXE(2), WOODEN_AXE(7), WOODEN_HOE(1),
    GOLDEN_SWORD(4), GOLDEN_SHOVEL(2.5F), GOLDEN_PICKAXE(2), GOLDEN_AXE(7), GOLDEN_HOE(1),
    NETHERITE_SWORD(8), NETHERITE_SHOVEL(6.5F), NETHERITE_PICKAXE(6), NETHERITE_AXE(10), NETHERITE_HOE(1);

    private final float damage;

    ToolDamage(float damage) { this.damage = damage; }

    public static float getDamage(String mat) { return valueOf(mat).damage; }

    public static float getDamage(Material mat) { return getDamage(mat.toString()); }

    public float getDamage() { return damage; }
}
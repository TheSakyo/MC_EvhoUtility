package fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.utilitiesAPI.reflection;

import fr.TheSakyo.EvhoUtility.utils.api.OldCombatMechanics.example.NbtFactory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemData {

    public static void mark(ItemStack is, String marker) { if(!hasMark(is, marker)) { NbtFactory.fromItemTag(is).put("[OCM]" + marker, (byte) 1); } }
    public static void unmark(ItemStack is, String marker) { if(hasMark(is, marker)) { NbtFactory.fromItemTag(is).remove("[OCM]" + marker); } }
    public static boolean hasMark(ItemStack is, String marker) { return is != null && is.getType() != Material.AIR && NbtFactory.fromItemTag(is).get("[OCM]" + marker) != null; }

}

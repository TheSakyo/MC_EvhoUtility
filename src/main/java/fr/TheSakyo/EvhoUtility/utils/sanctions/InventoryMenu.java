package fr.TheSakyo.EvhoUtility.utils.sanctions;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class InventoryMenu {
	
	
	/************************/
	/* INVENTAIRE MENU KICK */ 
	/************************/
	
  	public void InventoryKick(Player p) {
  		
		Inventory invK = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(ChatFormatting.RED + "Menu-Kick"));
		
		p.openInventory(invK);
		
        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { invK.setItem(26, Close); }
		
		ItemStack Kick = new ItemStack(Material.LEVER, 1);
		ItemMeta CustomK = Kick.getItemMeta();
		CustomK.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Kick"));
		Kick.setItemMeta(CustomK);
		invK.setItem(11, Kick);
		
		
		ItemStack KickS = new ItemStack(Material.LEVER, 1);
		ItemMeta CustomKS = KickS.getItemMeta();
		CustomKS .displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Kick En Silence"));
		KickS.setItemMeta(CustomKS);
		invK.setItem(15, KickS);
  	}
  	
  	/************************/
	/* INVENTAIRE MENU KICK */ 
	/************************/
  	
  	
  	
  	/************************/
	/* INVENTAIRE MENU MUTE */ 
	/************************/
  	
  	public void InventoryMute(Player p) {
  		
		Inventory invM  = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(ChatFormatting.RED + "Menu-Mute"));
		
		p.openInventory(invM);
		
        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { invM.setItem(26, Close); }
		
		ItemStack Mute = new ItemStack(Material.DISPENSER, 1);
		ItemMeta CustomM = Mute.getItemMeta();
		CustomM.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Mute"));
		Mute.setItemMeta(CustomM);
		invM.setItem(10, Mute);					
		
		ItemStack MuteS = new ItemStack(Material.DISPENSER, 1);
		ItemMeta CustomMS = MuteS.getItemMeta();
		CustomMS.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Mute En Silence"));
		MuteS.setItemMeta(CustomMS);
		invM.setItem(12, MuteS);
		
		ItemStack TempMute = new ItemStack(Material.DROPPER, 1);
		ItemMeta CustomTM = TempMute.getItemMeta();
		CustomTM.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempMute"));
		TempMute.setItemMeta(CustomTM);
		invM.setItem(14, TempMute);
		
		ItemStack TempMuteS = new ItemStack(Material.DROPPER, 1);
		ItemMeta CustomTMS = TempMuteS.getItemMeta();
		CustomTMS.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempMute En Silence"));
		TempMuteS.setItemMeta(CustomTMS);
		invM.setItem(16, TempMuteS);
		
  	}
  	
  	/************************/
	/* INVENTAIRE MENU MUTE */ 
	/************************/
  	
  	
  	
  	
  	/***********************/
	/* INVENTAIRE MENU BAN */ 
	/***********************/
  	
  	public void InventoryBan(Player p) {
  		
		
		Inventory invB  = Bukkit.getServer().createInventory(null, 36, CustomMethod.StringToComponent(ChatFormatting.RED + "Menu-BAN"));
		
		p.openInventory(invB);
		
        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { invB.setItem(35, Close); }
		
		ItemStack Ban = new ItemStack(Material.TNT, 1);
		ItemMeta CustomB = Ban.getItemMeta();
		CustomB.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban"));
		Ban.setItemMeta(CustomB);
		invB.setItem(1, Ban);					
		
		ItemStack BanS = new ItemStack(Material.TNT, 1);
		ItemMeta CustomBS = BanS.getItemMeta();
		CustomBS.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban En Silence"));
		BanS.setItemMeta(CustomBS);
		invB.setItem(3, BanS);
		
		ItemStack TempBan = new ItemStack(Material.OBSERVER, 1);
		ItemMeta CustomTB = TempBan.getItemMeta();
		CustomTB.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempBan"));
		TempBan.setItemMeta(CustomTB);
		invB.setItem(5, TempBan);
		
		ItemStack TempBanS = new ItemStack(Material.OBSERVER, 1);
		ItemMeta CustomTBS = TempBanS.getItemMeta();
		CustomTBS.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempBan En Silence"));
		TempBanS.setItemMeta(CustomTBS);
		invB.setItem(7, TempBanS);
		
		ItemStack BanIP = new ItemStack(Material.BEDROCK, 1);
		ItemMeta CustomBI = BanIP.getItemMeta();
		CustomBI.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban-IP"));
		BanIP.setItemMeta(CustomBI);
		invB.setItem(21, BanIP);
		
		ItemStack BanIPS = new ItemStack(Material.BEDROCK, 1);
		ItemMeta CustomBIS = BanIPS.getItemMeta();
		CustomBIS.displayName(CustomMethod.StringToComponent(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban-IP En Silence"));
		BanIPS.setItemMeta(CustomBIS);
		invB.setItem(23, BanIPS);

  	}
  	
  	/***********************/
	/* INVENTAIRE MENU BAN */ 
	/***********************/
  	
  	
  	
  	/**************************/
	/* INVENTAIRE MENU AUTRES */ 
	/**************************/
  	
  	public void InventoryOther(Player p) {
  		
		
		Inventory invA  = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(ChatFormatting.YELLOW + "Menu-Autres"));
		
		p.openInventory(invA);
		
        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { invA.setItem(26, Close); }
		
		ItemStack freeze = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta CustomF = freeze.getItemMeta();
		CustomF.displayName(CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Freeze"));
		freeze.setItemMeta(CustomF);
		invA.setItem(10, freeze);
		
		ItemStack inv = new ItemStack(Material.CHEST, 1);
		ItemMeta CustomI = inv.getItemMeta();
		CustomI.displayName(CustomMethod.StringToComponent(ChatFormatting.BLUE.toString() + ChatFormatting.BOLD.toString() + "Inventaire Du Joueur"));
		inv.setItemMeta(CustomI);
		invA.setItem(13, inv);
		
		ItemStack ender = new ItemStack(Material.ENDER_CHEST, 1);
		ItemMeta CustomE = ender.getItemMeta();
		CustomE.displayName(CustomMethod.StringToComponent(ChatFormatting.DARK_PURPLE.toString() + ChatFormatting.BOLD.toString() + "EnderChest Du Joueur"));
		ender.setItemMeta(CustomE);
		invA.setItem(14, ender);
		
		ItemStack van = new ItemStack(Material.FEATHER, 1);
		ItemMeta CustomV = van.getItemMeta();
		CustomV.displayName(CustomMethod.StringToComponent(ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Mode Vanish"));
		van.setItemMeta(CustomV);
		invA.setItem(16, van);
  		
  	}
  	
  	/**************************/
	/* INVENTAIRE MENU AUTRES */ 
	/**************************/
}

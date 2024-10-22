package fr.TheSakyo.EvhoUtility.utils.sanctions;

import java.util.ArrayList;
import java.util.List;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class SanctionUtils implements Listener {
	
		/* Récupère la class "Main" */
		private final UtilityMain main;

	    public SanctionUtils(UtilityMain pluginMain) { this.main = pluginMain; }
		/* Récupère la class "Main" */

	    
	    // Variables Utiles //

	  	InventoryMenu inventoryMenu = new InventoryMenu();
	   
	  	public static Inventory sanctionInv = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(ChatFormatting.DARK_RED + "Sanction Evhonia"));
	  	
	   
	  	public static String prefix = ChatFormatting.WHITE + "[" + ChatFormatting.GOLD + "Evho" + ChatFormatting.RED + "Sanction" + ChatFormatting.WHITE + "]" + " ";
	  	
	  	
	  	public static ArrayList<String> InvNameReason = new ArrayList<>();
	   
	  	public static ArrayList<String> InvNameTime = new ArrayList<>();
	   
	  	public static ArrayList<String> InvSoloName = new ArrayList<>();
	   
	  	public static ArrayList<String> InvAllName = new ArrayList<>();
	   
	  	public 	static ArrayList<String> GetPlayer = new ArrayList<>();
	   
	  	public static ArrayList<String> GetTime = new ArrayList<>();

	  	public static ArrayList<ItemStack> itemsP = new ArrayList<>();
	   	
	   
	  	public static ArrayList<ItemStack> itemsCloseQuit = new ArrayList<>();

	  	public static ArrayList<ItemStack> itemsCloseReturn = new ArrayList<>();
	   
	  	// Variables Utiles //
	  	
	  	
	  	
	  	
	  	/*********************************/
		/* INVENTAIRE LISTES DES JOUEURS */ 
		/*********************************/

		public void InventoryPlayers(Player p, String string) {

			if(!itemsP.isEmpty()) { itemsP.clear(); }

	  	    for(Player online : Bukkit.getServer().getOnlinePlayers()) {	
	  	    	
				ItemStack item = Skin.PlayerHead(null, p, online.getName());
				itemsP.add(item);
	  	    }
			  new ScrollInventory(itemsP, ChatFormatting.DARK_GRAY + "Liste " + string, p);
  		} 
	  	
	  	/*********************************/
		/* INVENTAIRE LISTES DES JOUEURS */ 
		/*********************************/
	  	
	  	
	  	
	  	/************************************/
		/* MÉTHODE POUR DES BOUTONS QUITTER */
		/************************************/
	  	
	  	public static void SetItemForOne() {
	  		
			ItemStack closeQuit = new ItemStack(Material.BARRIER, 1);
			ItemMeta CustomCQ = closeQuit.getItemMeta();
			CustomCQ.displayName(CustomMethod.StringToComponent(ChatFormatting.DARK_RED.toString() + ChatFormatting.BOLD.toString() + "Quitter"));
			CustomCQ.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.RED + "Quittez le menu !")));
			closeQuit.setItemMeta(CustomCQ);
			
			ItemStack closeReturn = new ItemStack(Material.BARRIER, 1);
			ItemMeta CustomCR = closeReturn.getItemMeta();
			CustomCR.displayName(CustomMethod.StringToComponent(ChatFormatting.DARK_RED.toString() + ChatFormatting.BOLD.toString() + "Retour"));
			CustomCR.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.RED + "Retour au menu précédent !")));
			closeReturn.setItemMeta(CustomCR);
			
			if(!itemsCloseQuit.contains(closeQuit)) itemsCloseQuit.add(closeQuit);
			
			if(!itemsCloseReturn.contains(closeReturn)) itemsCloseReturn.add(closeReturn);
			
	  	}
	  	
	  	/************************************/
		/* MÉTHODE POUR DES BOUTONS QUITTER */
		/************************************/
	  	
	  	
	  	
	  	/********************/
		/* INVENTAIRE STAFF */ 
		/********************/
	  	
		public static void InventoryStaff() {
			
			ItemStack Kick = new ItemStack(Material.REDSTONE_TORCH, 1);
			ItemMeta CustomK = Kick.getItemMeta();
			CustomK.displayName(CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Kick"));
			CustomK.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.WHITE + "Menu de gestion Kick !")));
			Kick.setItemMeta(CustomK);
			
			sanctionInv.setItem(12, Kick);
			
			ItemStack Mute = new ItemStack(Material.BELL, 1);
			ItemMeta CustomM = Mute.getItemMeta();
			CustomM.displayName(CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Mute"));
			CustomM.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.WHITE + "Menu de gestion Mute !")));
			Mute.setItemMeta(CustomM);
			
			sanctionInv.setItem(10, Mute);
			
			ItemStack Ban = new ItemStack(Material.COMMAND_BLOCK, 1);
			ItemMeta CustomB = Ban.getItemMeta();
			CustomB.displayName(CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu BAN"));
			CustomB.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.WHITE + "Menu de gestion Ban !")));
			Ban.setItemMeta(CustomB);
			
			sanctionInv.setItem(14, Ban);
			
			ItemStack Autres = new ItemStack(Material.SUNFLOWER, 1);
			ItemMeta CustomBN = Autres.getItemMeta();
			CustomBN.displayName(CustomMethod.StringToComponent(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Autres"));
			CustomBN.lore(List.of(CustomMethod.StringToComponent(ChatFormatting.WHITE + "Menu de gestion Autres !")));
			Autres.setItemMeta(CustomBN);
			
			sanctionInv.setItem(16, Autres);
			
	        for(ItemStack Close : itemsCloseQuit) { sanctionInv.setItem(26, Close); }

		}
		
		/********************/
		/* INVENTAIRE STAFF */ 
		/********************/
		
		
		
		/********************************************************************/
		/* CRÉER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS (AUTRES) */
		/********************************************************************/
		
		public String StringMenuPlayer(String string) {
			
			if(!InvAllName.contains(string)) InvAllName.add(string);
			return string;

		}
		
		/********************************************************************/
		/* CR�ER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS (AUTRES) */ 
		/********************************************************************/
		
		
		
		/*********************************************************************************/
		/* CRÉER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS QUI SERONT SANCTIONNÉ */
		/********************************************************************************/
		
		public String StringMenuPlayerReason(String string) {
			
			if(!InvNameReason.contains(string)) InvNameReason.add(string);
			if(!InvAllName.contains(string)) InvAllName.add(string);
			return string;

		}
		
		/*********************************************************************************/
		/* CRÉER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS QUI SERONT SANCTIONNÉ */
		/********************************************************************************/
		
		
		
		/***********************************************************************************************/
		/* CRÉER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS QUI SERONT SANCTIONNÉ AVEC UN TEMPS */
		/***********************************************************************************************/
		
		public String StringMenuPlayerTime(String string) {
			
			if(!InvNameTime.contains(string)) InvNameTime.add(string);
			if(!InvAllName.contains(string)) InvAllName.add(string);
			return string;

		}
		
		/***********************************************************************************************/
		/* CRÉER LE TITRE DE L'INVENTAIRE DE LA LISTES DES JOUEURS QUI SERONT SANCTIONNÉ AVEC UN TEMPS */
		/***********************************************************************************************/
		
		
		
		
		/***********************************************************************/
		/* ÉVÈNEMENT QUI OUVRE UN INVENTAIRE SANCTION EN FERMANT UN INVENTAIRE */ 
		/***********************************************************************/
		
		public static void OpenSanctionInventoryOnClose(InventoryClickEvent e) {
			
			Player p = (Player) e.getWhoClicked();
			ItemStack current = e.getCurrentItem();
			
			if(current == null) return;	
			
			if(current.getType() == Material.BARRIER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(ChatFormatting.DARK_RED.toString() + ChatFormatting.BOLD.toString() + "Retour")) {
				
				p.closeInventory();
				p.openInventory(sanctionInv);
			}
		}
		
		/***********************************************************************/
		/* ÉVÈNEMENT QUI OUVRE UN INVENTAIRE SANCTION EN FERMANT UN INVENTAIRE */ 
		/***********************************************************************/
		
		
		
		/************************************************/
		/* ÉVÈNEMENT QUAND ON CLIQUE DANS UN INVENTAIRE */ 
		/************************************************/
		
		@EventHandler
		public void onClick(InventoryClickEvent e) {
			
			Player p = (Player) e.getWhoClicked();
			ItemStack current = e.getCurrentItem();
			
			String title = CustomMethod.ComponentToString(e.getView().title());
			String getItemDisplayName = null;

			if(current == null) return;

			if(current.getItemMeta() != null && current.getItemMeta().displayName() != null) {

				getItemDisplayName = CustomMethod.ComponentToString(current.getItemMeta().displayName());
			}

			if(title.equalsIgnoreCase(ChatFormatting.DARK_RED + "Sanction Evhonia")) {
				
				e.setCancelled(true);	
				p.closeInventory();
				
				InvSoloName.clear();
	    	   	
	    	   	InvSoloName.add(title);
				
				if(current.getType() == Material.REDSTONE_TORCH && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Kick")) {
					
					inventoryMenu.InventoryKick(p);
				} 
				
				if(current.getType() == Material.BELL && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Mute")) {
					
					inventoryMenu.InventoryMute(p);
					
				}
				
				if(current.getType() == Material.COMMAND_BLOCK && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu BAN")) {
					
					inventoryMenu.InventoryBan(p);
				}
				
				if(current.getType() == Material.SUNFLOWER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Menu Autres")) {
					
					inventoryMenu.InventoryOther(p);
				}
			}
			
			else if(title.equalsIgnoreCase(ChatFormatting.RED + "Menu-Kick")) {
				
				e.setCancelled(true);
				p.closeInventory();
				
				InvSoloName.clear();
	    	   	
	    	   	InvSoloName.add(title);
				
				OpenSanctionInventoryOnClose(e);
				
				if(current.getType() == Material.LEVER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Kick")) {
						
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Kick]"));
				}
				
				if(current.getType() == Material.LEVER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Kick En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Kick-S]"));
				}
				
			}
			
			else if(title.equalsIgnoreCase(ChatFormatting.RED + "Menu-Mute")) {
				
				e.setCancelled(true);
				p.closeInventory();
				
				OpenSanctionInventoryOnClose(e);
				
				if(current.getType() == Material.DROPPER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempMute")) {

					this.InventoryPlayers(p, StringMenuPlayerTime(ChatFormatting.DARK_GRAY + "Joueurs [TempMute]"));
				}
				
				if(current.getType() == Material.DROPPER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempMute En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerTime(ChatFormatting.DARK_GRAY + "Joueurs [TempMute-S]"));
				}
				
				
				if(current.getType() == Material.DISPENSER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Mute")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Mute]"));
				}
				
				if(current.getType() == Material.DISPENSER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Mute En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Mute-S]"));					
				}
				
			}
			
			else if(title.equalsIgnoreCase(ChatFormatting.RED + "Menu-BAN")) {
				
				e.setCancelled(true);
				p.closeInventory();
				
				InvSoloName.clear();

	    	   	InvSoloName.add(title);
				
				OpenSanctionInventoryOnClose(e);
				
				if(current.getType() == Material.OBSERVER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempBan")) {

					this.InventoryPlayers(p, StringMenuPlayerTime(ChatFormatting.DARK_GRAY + "Joueurs [TempBan]"));
				}
				
				if(current.getType() == Material.OBSERVER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "TempBan En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerTime(ChatFormatting.DARK_GRAY + "Joueurs [TempBan-S]"));
				}
				
				if(current.getType() == Material.TNT && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Ban]"));
				}
				
				if(current.getType() == Material.TNT && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Ban-S]"));					
				}
				
				if(current.getType() == Material.BEDROCK && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban-IP")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Ban-IP]"));
				}
				
				if(current.getType() == Material.BEDROCK && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + "Ban-IP En Silence")) {
					
					this.InventoryPlayers(p, StringMenuPlayerReason(ChatFormatting.DARK_GRAY + "Joueurs [Ban-IP-S]"));					
				}

			}
			
			else if(title.equalsIgnoreCase(ChatFormatting.YELLOW + "Menu-Autres")) {
				
				e.setCancelled(true);
				p.closeInventory();
				
				InvSoloName.clear();
	    	   	
	    	   	InvSoloName.add(title);
	    	   	
				OpenSanctionInventoryOnClose(e);

				if(current.getType() == Material.BLAZE_ROD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Freeze")) {

					this.InventoryPlayers(p, StringMenuPlayer(ChatFormatting.DARK_GRAY + "Joueurs [Freeze]"));
					
				} 
				
				if(current.getType() == Material.CHEST && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.BLUE.toString() + ChatFormatting.BOLD.toString() + "Inventaire Du Joueur")) {

					this.InventoryPlayers(p, StringMenuPlayer(ChatFormatting.DARK_GRAY + "Joueurs [Inventaire]"));
				} 
				
				if(current.getType() == Material.ENDER_CHEST && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.DARK_PURPLE.toString() + ChatFormatting.BOLD.toString() + "EnderChest Du Joueur")) {

					this.InventoryPlayers(p, StringMenuPlayer(ChatFormatting.DARK_GRAY + "Joueurs [EnderChest]"));
				}
				
				if(current.getType() == Material.FEATHER && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + "Mode Vanish")) {
	
					Inventory playerVanish = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(ChatFormatting.DARK_GRAY + "Mode-Vanish"));
					
					ItemStack VanishOn = new ItemStack(Material.POTION, 1);
					ItemMeta CustomVON = VanishOn.getItemMeta();
					CustomVON.displayName(CustomMethod.StringToComponent(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Vanish ON"));
					VanishOn.setItemMeta(CustomVON);
					
					playerVanish.setItem(12, VanishOn);
					
					ItemStack VanishOff = new ItemStack(Material.POTION, 1);
					ItemMeta CustomVOFF = VanishOff.getItemMeta();
					CustomVOFF.displayName(CustomMethod.StringToComponent(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Vanish OFF"));
					VanishOff.setItemMeta(CustomVOFF);
					
					playerVanish.setItem(14, VanishOff);
					
	   	 	        for (ItemStack Close : itemsCloseQuit) {

	   	 	        	playerVanish.setItem(26, Close);
	   	 	        }
					
					p.openInventory(playerVanish);
				} 
			
			}
			
			else if(title.equalsIgnoreCase(ChatFormatting.DARK_GRAY + "Mode-Vanish")) {
				
				e.setCancelled(true);
				p.closeInventory();
				
	    	   	InvSoloName.clear();
	    	   	
	    	   	InvSoloName.add(title);
	    	   	
	    	   	OpenSanctionInventoryOnClose(e);
	    	   	
	    	   	if(current.getType() == Material.POTION && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Vanish ON")) {
	    	   		
	    	   		new AnvilCreation(main).CreateSearchVanishOnAnvilGui(p);
	    	   	}
	    	   	
	    	   	if(current.getType() == Material.POTION && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && getItemDisplayName.equalsIgnoreCase(ChatFormatting.GREEN.toString() + ChatFormatting.BOLD.toString() + "Vanish OFF")) {
	    	   		
	    	   		new AnvilCreation(main).CreateSearchVanishOffAnvilGui(p);
	    	   	}
	    	   	
			}
			
		}
		
		/************************************************/
		/* ÉVÈNEMENT QUAND ON CLIQUE DANS UN INVENTAIRE */ 
		/************************************************/

}

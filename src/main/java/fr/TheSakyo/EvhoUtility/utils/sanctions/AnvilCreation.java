package fr.TheSakyo.EvhoUtility.utils.sanctions;

import java.util.List;
import java.util.UUID;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.AnvilGui.AnvilUtils;
import org.bukkit.ChatColor;

public class AnvilCreation implements Listener {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
    public AnvilCreation(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
    
    
    // Variable pour l'enclume customisée //
	AnvilUtils.Builder builder = (new AnvilUtils.Builder());
	
	AnvilUtils AU;
	// Variable pour l'enclume customisée //
	
	
	
	/**************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE */
	/**************************************/
	

	public void CreateSearchAnvilGui(Player p) {
		
			ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
			SkullMeta metaH = (SkullMeta)head.getItemMeta();
			metaH.displayName(CustomMethod.StringToComponent("Pseudo"));
			head.setItemMeta(metaH); 
			
			
			builder.onComplete((player, text) -> {
		    	
		    	ItemStack item = AnvilUtils.Response.getItemOutput();
		    	
		    	for(Player players : Bukkit.getServer().getOnlinePlayers()) {

		    	 if(!text.equalsIgnoreCase(players.getName())) {

					 String displayName = ChatColor.RED + ChatColor.stripColor(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
				 	 item.getItemMeta().displayName(CustomMethod.StringToComponent(displayName));
					 return AnvilUtils.Response.text("Joueurs Introuvable !");
				 }
	        	}
		    	
		    	for(String InvNameP : SanctionUtils.InvSoloName) {
   					
	   				Inventory playerSolo = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvNameP));
	   				
	   				ItemStack PlayerS = Skin.PlayerHead(null, p, CustomMethod.ComponentToString(item.getItemMeta().displayName()));
	   				playerSolo.setItem(13, PlayerS);
	   				
	   	 	        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerSolo.setItem(26, Close); }

	   	 	        p.openInventory(playerSolo);
	   	 	        
   				}
		    	
		    	return AnvilUtils.Response.close(); 
		    })                                         
		    .text("Écrivez le pseudo du joueur")
		    .itemLeft(head)
		    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(head.getItemMeta().displayName())))
		    .title(ChatColor.AQUA + "Recherche du joueur")                               
		    .plugin(UtilityMain.getInstance())                                   
		    .open(p);                                           
    }
	
	/**************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE */
	/**************************************/
	
	
	
	/************************************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE DES JOUEURS A VANISH  */
	/***********************************************************/

	public void CreateSearchVanishOnAnvilGui(Player p) {
		
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta metaH = (SkullMeta)head.getItemMeta();
		metaH.displayName(CustomMethod.StringToComponent("Aucun Joueur"));
		head.setItemMeta(metaH); 
		
		
		builder.onComplete((player, text) -> {
	    	
	    	ItemStack item = AnvilUtils.Response.getItemOutput();
	    	
	    	for(Player players : Bukkit.getServer().getOnlinePlayers()) {

	    	  if(!text.equalsIgnoreCase(players.getName())) {

				  String displayName = ChatColor.RED + ChatColor.stripColor(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
				  item.getItemMeta().displayName(CustomMethod.StringToComponent(displayName));
				  return AnvilUtils.Response.text("Joueurs Introuvable !");
			  }
        	}
	    	
	    	for(String InvNameP : SanctionUtils.InvSoloName) {
					
   				Inventory playerV = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvNameP + " On"));
   				
   				ItemStack PlayerSV = Skin.PlayerHead(null, p, CustomMethod.ComponentToString(item.getItemMeta().displayName()));
   				playerV.setItem(13, PlayerSV);
   				
   	 	        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerV.setItem(26, Close); }
   	 	        
   	 	        p.openInventory(playerV);
   	 	        
	    	}
	    	
	    	return AnvilUtils.Response.close(); 
	    })                                          
	    .text("Écrivez le pseudo du joueur")
	    .itemLeft(head)
	    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(head.getItemMeta().displayName())))
	    .title(ChatColor.AQUA + "Joueur V-ON")                               
	    .plugin(UtilityMain.getInstance())                                   
	    .open(p);  
		
	}
	
	/************************************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE DES JOUEURS A VANISH  */
	/***********************************************************/
	
	
	
	/*************************************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE DES JOUEURS A DÉVANISH */
	/*************************************************************/

	public void CreateSearchVanishOffAnvilGui(Player p) {
		
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta metaH = (SkullMeta)head.getItemMeta();
		metaH.displayName(CustomMethod.StringToComponent("Aucun Joueur"));
		head.setItemMeta(metaH); 
		
		
		builder.onComplete((player, text) -> {
	    	
	    	ItemStack item = AnvilUtils.Response.getItemOutput();
	    	
	    	for(Player players : Bukkit.getServer().getOnlinePlayers()) {

	    	  if(!text.equalsIgnoreCase(players.getName())) {

				  String displayName = ChatColor.RED + ChatColor.stripColor(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
				  item.getItemMeta().displayName(CustomMethod.StringToComponent(displayName));
				  return AnvilUtils.Response.text("Joueurs Introuvable !");
			  }
        	}
	    	
	    	for(String InvNameP : SanctionUtils.InvSoloName) {
					
   				Inventory playerV = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvNameP + " Off"));
   				
   				ItemStack PlayerSV = Skin.PlayerHead(null, p, CustomMethod.ComponentToString(item.getItemMeta().displayName()));
   				playerV.setItem(13, PlayerSV);
   				
   	 	        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerV.setItem(26, Close); }

   	 	        p.openInventory(playerV);
			}
	    	
	    	return AnvilUtils.Response.close(); 
	    })                                         
	    .text("Écrivez le pseudo du joueur")
	    .itemLeft(head)
	    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(head.getItemMeta().displayName())))
	    .title(ChatColor.AQUA + "Joueur V-OFF")                               
	    .plugin(UtilityMain.getInstance())                                   
	    .open(p);    
	}
	
	/*************************************************************/
	/* CRÉATION DE L'ENCLUME DE RECHERCHE DES JOUEURS A DÉVANISH */
	/*************************************************************/
	
	
	
	
	/**************************************************/
	/* CRÉATION DE L'ENCLUME DE RAISON A UNE SANCTION */
	/**************************************************/
	public void CreateReasonAnvilGui(Player p) {
		
		ItemStack paper = new ItemStack(Material.PAPER, 1);
		ItemMeta metaP = paper.getItemMeta();
		metaP.displayName(CustomMethod.StringToComponent("Aucune Raison"));
		paper.setItemMeta(metaP); 
		
		
		builder.onComplete((player, text) -> {
			
			ItemStack item = AnvilUtils.Response.getItemOutput();
			
			if(text == null || text != null && text.isEmpty() || CustomMethod.ComponentToString(item.getItemMeta().displayName()) == null
			  || CustomMethod.ComponentToString(item.getItemMeta().displayName()) != null && CustomMethod.ComponentToString(item.getItemMeta().displayName()).isEmpty()
			  || CustomMethod.ComponentToString(item.getItemMeta().displayName()).length() == 1) {

				String displayName = ChatColor.RED + ChatColor.stripColor(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
				item.getItemMeta().displayName(CustomMethod.StringToComponent(displayName));
				return AnvilUtils.Response.text("Entrez une raison valide !");
			}
			
			
			for(String InvNameP : SanctionUtils.InvSoloName) {
					
   				Inventory playerReason = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvNameP + " Sanction"));
   				
   				ItemStack PlayerR = new ItemStack(Material.PLAYER_HEAD, 1);
   				SkullMeta CustomPR = (SkullMeta)PlayerR.getItemMeta();
   										
   				CustomPR.lore(List.of(item.getItemMeta().displayName()));
   				
   				for(String ItemNameP : SanctionUtils.GetPlayer) {

					try {

						PlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
						CustomPR.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(PlayerEntity.getUUIDByPlayerName(ItemNameP, null)));

												/* ------------------------------ */
						try {

							Skin skin = Skin.get(ItemNameP);
							profile.getProperties().add(new ProfileProperty("textures", skin.getValue(), skin.getSignature()));
							CustomPR.setPlayerProfile(profile);

						} catch(IllegalStateException | NullPointerException ignored) {}
												/* ------------------------------ */
					} catch(Exception ignored) {}

	   			   CustomPR.displayName(CustomMethod.StringToComponent(ChatColor.YELLOW + "Tête de " + ChatColor.GOLD + ItemNameP));
   				}
   				
   				PlayerR.setItemMeta(CustomPR);
   				
   				playerReason.setItem(13, PlayerR);
   				
   	 	        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerReason.setItem(26, Close); }
   	 	        
   	 	        p.openInventory(playerReason);
   	 	        
			}
			
			return AnvilUtils.Response.close(); 
		})                                       
	    .text("Écrivez la raison de la sanction")
	    .itemLeft(paper)
	    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(paper.getItemMeta().displayName())))
	    .title(ChatColor.AQUA + "Raison de la sanction")                               
	    .plugin(UtilityMain.getInstance())                                   
	    .open(p);  
	}
	
	/**************************************************/
	/* CRÉATION DE L'ENCLUME DE RAISON A UNE SANCTION */
	/**************************************************/
	
	
	
	/*************************************************************/
	/* CRÉATION DE L'ENCLUME DE RAISON AVEC TEMPS A UNE SANCTION */
	/*************************************************************/

	public void CreateReasonTimeAnvilGui(Player p) {
		
		ItemStack PlayerT = new ItemStack(Material.PAPER, 1);
		ItemMeta CustomPT = PlayerT.getItemMeta();
		CustomPT.displayName(CustomMethod.StringToComponent("Aucune Raison"));
		PlayerT.setItemMeta(CustomPT);
		
		
		builder.onComplete((player, text) -> {

			ItemStack item = AnvilUtils.Response.getItemOutput();
			
			if(text == null || text != null && text.isEmpty() || CustomMethod.ComponentToString(item.getItemMeta().displayName()) == null
			   || CustomMethod.ComponentToString(item.getItemMeta().displayName()) != null && CustomMethod.ComponentToString(item.getItemMeta().displayName()).isEmpty()
			   || CustomMethod.ComponentToString(item.getItemMeta().displayName()).length() == 1) {

				String displayName = ChatColor.RED + ChatColor.stripColor(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
				item.getItemMeta().displayName(CustomMethod.StringToComponent(displayName));
				return AnvilUtils.Response.text("Entrez une raison valide !");
			}

			for(String InvNameP : SanctionUtils.InvSoloName) {
				
				Inventory playerReasonT = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvNameP + " Sanction"));
				
				ItemStack PlayerRT = new ItemStack(Material.PLAYER_HEAD, 1);
				SkullMeta CustomPRT = (SkullMeta)PlayerRT.getItemMeta();
				
				for(String Time : SanctionUtils.GetTime) { CustomPRT.lore(List.of(CustomMethod.StringToComponent(Time), item.getItemMeta().displayName())); }
				
				for(String ItemNameP : SanctionUtils.GetPlayer) {

					try {

						PlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
						CustomPRT.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(PlayerEntity.getUUIDByPlayerName(ItemNameP, null)));

												/* ------------------------------ */
						try {

							Skin skin = Skin.get(ItemNameP);
							profile.getProperties().add(new ProfileProperty("textures", skin.getValue(), skin.getSignature()));
							CustomPRT.setPlayerProfile(profile);

						} catch(IllegalStateException | NullPointerException ignored) {}
												/* ------------------------------ */
					} catch(Exception ignored) {}

	   			   CustomPRT.displayName(CustomMethod.StringToComponent(ChatColor.YELLOW + "Tête de " + ChatColor.GOLD + ItemNameP));
				}
				
				PlayerRT.setItemMeta(CustomPRT);
				
				playerReasonT.setItem(13, PlayerRT);
				
		        for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerReasonT.setItem(26, Close); }
		        
		        p.openInventory(playerReasonT);
	        
			}
			
			return AnvilUtils.Response.close(); 
		})                                         
	    .text("Écrivez la raison de la sanction")
	    .itemLeft(PlayerT)
	    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(PlayerT.getItemMeta().displayName())))
	    .title(ChatColor.AQUA + "Ainsi que la raison")                               
	    .plugin(UtilityMain.getInstance())                                   
	    .open(p);
	}
	
	/*************************************************************/
	/* CRÉATION DE L'ENCLUME DE RAISON AVEC TEMPS A UNE SANCTION */
	/*************************************************************/
	
	
	
	
	/*************************************************/
	/* CRÉATION DE L'ENCLUME DE TEMPS A UNE SANCTION */
	/*************************************************/

	public void CreateTimeAnvilGui(Player p) {
		
		ItemStack clock = new ItemStack(Material.CLOCK, 1);
		ItemMeta metaC = clock.getItemMeta();
		metaC.displayName(CustomMethod.StringToComponent("0 'second' ou 'minute' ou 'hour' ou 'day'"));
		clock.setItemMeta(metaC); 
		
		
		builder.onComplete((player, text) -> {
	    	
	    	ItemStack item = AnvilUtils.Response.getItemOutput();
	    		
    		SanctionUtils.GetTime.clear();
			
			if(!SanctionUtils.GetTime.contains(item)) SanctionUtils.GetTime.add(CustomMethod.ComponentToString(item.getItemMeta().displayName()));
   			
   			new AnvilCreation(main).CreateReasonTimeAnvilGui(p);
    		
    		return AnvilUtils.Response.close();
	    })                                         
	    .text("Écrivez le temps de la sanction")
	    .itemLeft(clock)
	    .onLeftInputClick(player -> player.sendMessage(CustomMethod.ComponentToString(clock.getItemMeta().displayName())))
	    .title(ChatColor.AQUA + "Temps de la sanction")                               
	    .plugin(UtilityMain.getInstance())                                   
	    .open(p);
	}
	
	/*************************************************/
	/* CRÉATION DE L'ENCLUME DE TEMPS A UNE SANCTION */
	/*************************************************/
	
	
	
	
	/*********************************************************/
	/* ÉVÈNEMENT QUAND ON QUITTE SUR UN INVENTAIRE D'ENCLUME */
	/*********************************************************/
	
	@SuppressWarnings("unused")
	@EventHandler
	public void AnvilClose(InventoryCloseEvent e) {
		
		Player p = (Player) e.getPlayer();

		String title = CustomMethod.ComponentToString(e.getView().title());
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Recherche du joueur")){
				 
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Joueur V-ON")){
			 
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Joueur V-OFF")){
			 
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Raison de la sanction")) {
			
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Ainsi que la raison")) {
			
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
		
		if(title.equalsIgnoreCase(ChatColor.AQUA + "Temps de la sanction")) {
			
		  e.getInventory().setItem(0, new ItemStack(Material.AIR));
		}
	}
	
	/*********************************************************/
	/* ÉVÈNEMENT QUAND ON QUITTE SUR UN INVENTAIRE D'ENCLUME */
	/*********************************************************/
}
	


package fr.TheSakyo.EvhoUtility.utils.sanctions;

import java.util.*;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ScrollInventory implements Listener {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
    public ScrollInventory(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


    
    // Variables Utiles //

    public ArrayList<Inventory> pages = new ArrayList<Inventory>();
    public UUID id;
    public int currpage = 0;
    public static HashMap<UUID, ScrollInventory> users = new HashMap<UUID, ScrollInventory>();
    
    // Variables Utiles //
    
    
    
    /********************************************/
	/* CRÉER UN INVENTAIRE QU'ONT PEUT SCROLLER */
	/********************************************/
    
	public ScrollInventory(ArrayList<ItemStack> items, String name, Player p) {
		
	    this.id = UUID.randomUUID();
	    
	    users.put(p.getUniqueId(), this);
	
	    Inventory page = getBlankPage(name, p);
	
	    for(int i = 0; i < items.size(); i++) {
	    	
	    	if(page.contains(items.get(i))) return;
	
	        if(page.firstEmpty() == -1) {

				if(!pages.contains(page)) { pages.remove(page); }
				pages.add(page);

	            page = getBlankPage(name, p);
	            page.addItem(items.get(i));
	        
	        } else { page.addItem(items.get(i)); }
	    }
		if(!pages.contains(page)) { pages.remove(page); }
		pages.add(page);

	    p.openInventory(pages.get(currpage));
	}
	
	/********************************************/
	/* CRÉER UN INVENTAIRE QU'ONT PEUT SCROLLER */
	/********************************************/

	
   // Création des boutons 'pages suivantes', 'pages précédentes' et 'Rechercher' //
	
   public final static String nextPageName = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Suivant";
   public final static String previousPageName = ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "Précédent";
   
   public final static String SearchAnvilGuiName = ChatColor.RED.toString() + ChatColor.BOLD.toString() + "Rechercher";
   
   // Cr�ation des boutons 'pages suivantes', 'pages précédentes' et 'Rechercher' //

   
   
    /************************************/
	/* RECUP�RE LA PAGE DE L'INVENTAIRE */ 
	/************************************/
   
    private Inventory getBlankPage(String name, Player p) {
    	
        Inventory page = Bukkit.getServer().createInventory(null, 54, CustomMethod.StringToComponent(name));
        
        ScrollInventory inv = ScrollInventory.users.get(p.getUniqueId());
        
		ItemStack close = new ItemStack(Material.BARRIER, 1);
		ItemMeta CustomC = close.getItemMeta();
		CustomC.displayName(CustomMethod.StringToComponent(ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Retour"));
		CustomC.lore(List.of(CustomMethod.StringToComponent(ChatColor.RED + "Retour au menu [Sanction Evhonia] !")));
		close.setItemMeta(CustomC);
        
        ItemStack nul = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE, 1);
        ItemMeta metaN = nul.getItemMeta();
        metaN.displayName(CustomMethod.StringToComponent(" "));
        nul.setItemMeta(metaN);
        
        if(inv.currpage > 0) {

			if(inv.currpage == pages.size()-1) {

				ItemStack prevpage = new ItemStack(Material.ARROW, 1);
				ItemMeta metaPP = prevpage.getItemMeta();
				metaPP.displayName(CustomMethod.StringToComponent(previousPageName));
				prevpage.setItemMeta(metaPP);

				page.setItem(48, prevpage);

			} else {

		        ItemStack nextpage =  new ItemStack(Material.ARROW, 1);
				ItemMeta metaNP = nextpage.getItemMeta();
				metaNP.displayName(CustomMethod.StringToComponent(nextPageName));
				nextpage.setItemMeta(metaNP);

				ItemStack prevpage = new ItemStack(Material.ARROW, 1);
				ItemMeta metaPP = prevpage.getItemMeta();
				metaPP.displayName(CustomMethod.StringToComponent(previousPageName));
				prevpage.setItemMeta(metaPP);

				page.setItem(50, nextpage);
				page.setItem(48, prevpage);
			}

		} else {
        	
	        ItemStack nextpage =  new ItemStack(Material.ARROW, 1);
	        ItemMeta metaNP = nextpage.getItemMeta();
	        metaNP.displayName(CustomMethod.StringToComponent(nextPageName));
	        nextpage.setItemMeta(metaNP);
	        
	        page.setItem(50, nextpage);
	        
	        page.setItem(48, nul);
        }
        
        ItemStack search = new ItemStack(Material.ANVIL, 1);
        ItemMeta metaA = search.getItemMeta();
        metaA.displayName(CustomMethod.StringToComponent(SearchAnvilGuiName));
        search.setItemMeta(metaA);
        
        String lorebook = ChatColor.GOLD + "Vous êtes à la page [" + ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + inv.currpage + ChatColor.GOLD + "]";
        
        ItemStack book = new ItemStack(Material.BOOK, 1);
        ItemMeta metaB = book.getItemMeta();
        metaB.displayName(CustomMethod.StringToComponent(ChatColor.AQUA.toString() + ChatColor.BOLD.toString() + "Information"));
        metaB.lore(List.of(CustomMethod.StringToComponent(lorebook)));
        book.setItemMeta(metaB);
        
        // ITEM EN HAUT EN EN BAS DU MENU (DECORATION ET QUELQUES FONCTIONS) //
        
        page.setItem(0, nul);
        page.setItem(1, nul);
        page.setItem(2, nul);
        page.setItem(3, nul);
        page.setItem(4, search);
        page.setItem(5, nul);
        page.setItem(6, nul);
        page.setItem(7, nul);
        page.setItem(8, nul);
        
        page.setItem(45, nul);
        page.setItem(46, nul);
        page.setItem(47, nul);
        page.setItem(49, book);
        page.setItem(51, nul);
        page.setItem(52, nul);	

        page.setItem(53, close);
        
        // ITEM EN HAUT EN EN BAS DU MENU (DECORATION ET QUELQUES FONCTIONS) //
        
        return page;
    }
    
    /************************************/
	/* RECUPÈRE LA PAGE DE L'INVENTAIRE */
	/************************************/

    
    
    /************************************************************************/
	/* ÉVÈNEMENT QUAND ON CLIQUE DANS L'INVENTAIRE DE LA LISTES DES JOUEURS */
	/************************************************************************/

	@EventHandler
    public static void onClickWithInventoryPlayer(InventoryClickEvent e) {
    	
		Player p = (Player)e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		
        if(!ScrollInventory.users.containsKey(p.getUniqueId())) return;
        ScrollInventory inv = ScrollInventory.users.get(p.getUniqueId());
		
		if(current == null) return;	
		
		for(String InvPlayers : SanctionUtils.InvAllName) {

			 if(CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(InvPlayers) || CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(ChatColor.DARK_GRAY + "Liste " + InvPlayers)) {
				 
				e.setCancelled(true);	
				
				SanctionUtils.OpenSanctionInventoryOnClose(e);
				
				SanctionUtils.InvSoloName.clear();
	    	   	
				SanctionUtils.InvSoloName.add(CustomMethod.ComponentToString(e.getView().title()));
				
				
				if(current.getType() == Material.ARROW && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(ScrollInventory.nextPageName)) {
					
		            if(inv.currpage >= inv.pages.size() -1) { return; }
		            
		            else {
		            	
		            	if(CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(ChatColor.DARK_GRAY + "Liste " + InvPlayers)) p.closeInventory();
						
		                inv.currpage += 1;
		                p.openInventory(inv.pages.get(inv.currpage));
		            }
		            
				} else if(current.getType() == Material.ARROW && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(ScrollInventory.previousPageName)) {
					
	                if(inv.currpage > 0) {
	                	
	                	if(CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(ChatColor.DARK_GRAY + "Liste " + InvPlayers)) p.closeInventory();

	                    inv.currpage -= 1;
	                    p.openInventory(inv.pages.get(inv.currpage));
	                    
	                } else { return; }
		                
		       } else if(current.getType() == Material.ANVIL && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(ScrollInventory.SearchAnvilGuiName)) {
		    	    
		    	    if(CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(ChatColor.DARK_GRAY + "Liste " + InvPlayers)) p.closeInventory();
		    	    
		    	   	new AnvilCreation(UtilityMain.getInstance()).CreateSearchAnvilGui(p); //Créer une Enclume de recherche customisée
                } 
				
				for(Player online : Bukkit.getServer().getOnlinePlayers()) {

					String prefixPlayerHead = ChatColor.YELLOW + "Tête de " + ChatColor.GOLD;
					if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {
						
						if(CustomMethod.ComponentToString(e.getView().title()).equalsIgnoreCase(ChatColor.DARK_GRAY + "Liste " + InvPlayers)) {
							
							p.closeInventory();

							Inventory playerList = Bukkit.getServer().createInventory(null, 27, CustomMethod.StringToComponent(InvPlayers));
			   				
			   				ItemStack PlayerL = Skin.PlayerHead(null, p, CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));
			   				playerList.setItem(13, PlayerL);
			   				
			   				for(ItemStack Close : SanctionUtils.itemsCloseReturn) { playerList.setItem(26, Close); }
			   				
			   				p.openInventory(playerList);
						} 
					}

				}
		       
            }
			
		}
		 
	 }
    
    /************************************************************************/
	/* ÉVÈNEMENT QUAND ON CLIQUE DANS L'INVENTAIRE DE LA LISTES DES JOUEURS */
	/************************************************************************/

}

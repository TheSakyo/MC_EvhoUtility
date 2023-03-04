package fr.TheSakyo.EvhoUtility.events;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.utils.sanctions.AnvilCreation;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.sanctions.SanctionUtils;
import fr.TheSakyo.EvhoUtility.utils.sanctions.ScrollInventory;

public class InteractMenuStaffListener implements Listener {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public InteractMenuStaffListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */


	String prefixPlayerHead = ChatColor.YELLOW + "Tête de " + ChatColor.GOLD;


	/*************************************************************************/
	/* ACTIONS SELON LES DIFFÉRENTS CLIQUE EFFECTUER AU NIVEAU DU MENU STAFF */
	/*************************************************************************/

	@EventHandler
	public void onClickSanction(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());
		
		if(current == null) return;
		
		for(String InvNameS : SanctionUtils.InvNameReason) {

			if(title.equalsIgnoreCase(InvNameS)) {

				e.setCancelled(true);
				p.closeInventory();

				SanctionUtils.OpenSanctionInventoryOnClose(e);
				ScrollInventory.onClickWithInventoryPlayer(e);

				for(Player online : Bukkit.getServer().getOnlinePlayers()) {

					if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

						SanctionUtils.GetPlayer.clear();

						if(!SanctionUtils.GetPlayer.contains(CustomMethod.ComponentToString(current.getItemMeta().displayName()))) SanctionUtils.GetPlayer.add(CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));
						new AnvilCreation(main).CreateReasonAnvilGui(p); //Créer une Enclume de raison customisée
					}
				}
			}
		}
	}

	@EventHandler
	public void onClickSanctionTime(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());
		
		if(current == null) return;
		
		for(String InvNameST : SanctionUtils.InvNameTime) {
			
			if(title.equalsIgnoreCase(InvNameST)) {

				e.setCancelled(true);
				p.closeInventory();

				SanctionUtils.OpenSanctionInventoryOnClose(e);
				ScrollInventory.onClickWithInventoryPlayer(e);

				for(Player online : Bukkit.getServer().getOnlinePlayers()) {

					if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

						SanctionUtils.GetPlayer.clear();

						if(!SanctionUtils.GetPlayer.contains(CustomMethod.ComponentToString(current.getItemMeta().displayName()))) SanctionUtils.GetPlayer.add(CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));
						new AnvilCreation(main).CreateTimeAnvilGui(p); //Créer une Enclume de temps customisée
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClickVanish(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());
		
		if(current == null) return;	
		
		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Mode-Vanish On")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd vanish " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " on");
				}
			}
		}

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Mode-Vanish Off")) {

			e.setCancelled(true);
			p.closeInventory();

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd vanish " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " off");
				}
			}
		}
	}
	
	@EventHandler
	public void onClickFreeze(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Freeze]")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd freeze " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));
				}
			}
		}
	}

	@EventHandler
	public void onClickInvsee(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Inventaire]")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

					Bukkit.getServer().dispatchCommand(p, "invsee " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));
				}
			}
		}
	}


	@EventHandler
	public void onClickEnderChest(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();
		Inventory clickedInventory = e.getClickedInventory();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [EnderChest]")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName())) {

					Bukkit.getServer().dispatchCommand(p, "enderchest " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, ""));

				}
			}
		}
										/* --------------------------------------------------------------------- */

		// Bonus : On vérifie si le joueur à les permision de bypass ou la permission spécifique pour déplacer l'inventaire d'un autre joueur que lui //
		if(clickedInventory.getType() == InventoryType.PLAYER && clickedInventory != p.getInventory()) {

				// Si le Joueur n'a ni la permission de bypass, ni la permission spécifique pour déplacer l'inventaire, on annule l'évènement
				if(!CustomMethod.hasByPassPerm(p) && !p.hasPermission("evhoutility.invsee.move")) e.setCancelled(true);
		}
		// Bonus : On vérifie si le joueur à les permision de bypass ou la permission spécifique pour déplacer l'inventaire d'un autre joueur que lui //
	}

	@EventHandler
	public void onClickKick(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Kick] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd kick " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickKickSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Kick-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd kicksilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickTempMute(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [TempMute] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd tempmute " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(1)) + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));

				}
			}
		}
	}

	@EventHandler
	public void onClickTempMuteSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [TempMute-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd tempmutesilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(1)) + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}


	@EventHandler
	public void onClickMute(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Mute] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd mute " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickMuteSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Mute-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd mutesilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickBan(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Ban] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd ban " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickBanSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Ban-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd bansilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickTempBan(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [TempBan] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd tempban " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(1)) + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickTempBanSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [TempBan-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd tempbansilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(1)) + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));
				}
			}
		}
	}

	@EventHandler
	public void onClickBanIP(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Ban-IP] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd banip " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));

				}
			}
		}
	}

	@EventHandler
	public void onClickBanIPSilence(InventoryClickEvent e) {

		Player p = (Player) e.getWhoClicked();
		ItemStack current = e.getCurrentItem();

		String title = CustomMethod.ComponentToString(e.getView().title());

		if(current == null) return;

		if(title.equalsIgnoreCase(ChatColor.DARK_GRAY + "Joueurs [Ban-IP-S] Sanction")) {

			e.setCancelled(true);
			p.closeInventory();

			SanctionUtils.OpenSanctionInventoryOnClose(e);
			ScrollInventory.onClickWithInventoryPlayer(e);

			for(Player online : Bukkit.getServer().getOnlinePlayers()) {

				if(current.getType() == Material.PLAYER_HEAD && current.hasItemMeta() && current.getItemMeta().hasDisplayName() && CustomMethod.ComponentToString(current.getItemMeta().displayName()).equalsIgnoreCase(prefixPlayerHead + online.getName()) && current.getItemMeta().hasLore()) {

					Bukkit.getServer().dispatchCommand(p, "proxycmd banipsilent " + CustomMethod.ComponentToString(current.getItemMeta().displayName()).replace(prefixPlayerHead, "") + " " + CustomMethod.ComponentToString(current.getItemMeta().lore().get(0)));

				}
			}
		}
	}

	/*************************************************************************/
	/* ACTIONS SELON LES DIFFÉRENTS CLIQUE EFFECTUER AU NIVEAU DU MENU STAFF */
	/*************************************************************************/
}

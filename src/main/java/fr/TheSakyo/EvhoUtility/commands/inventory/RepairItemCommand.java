package fr.TheSakyo.EvhoUtility.commands.inventory;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class RepairItemCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public RepairItemCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/****************************************************************/
	/* PARTIE COMMANDE POUR RÉPARER UN ITEM DANS LA MAIN DU JOUEUR  */
	/****************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.repair")) {

				if(args.length == 0) {
					
					ItemStack HandItem = p.getInventory().getItemInMainHand();
					
					//Info : Le Trident est détecté comme un item d'air
					if(HandItem.getType() == Material.AIR && HandItem.getType() != Material.TRIDENT) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne possédez aucun item(s) dans votre main principale");
						
					} else {
						
						if(HandItem.getAmount() >= 2) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Rien n'a été réparé, vous ne pouvez réparé seulement qu'un seul item a la fois situant dans votre main principale");
							return true;
						}
						
						ItemMeta it = HandItem.getItemMeta();
						
						if(((Damageable) it).hasDamage()) {

							((Damageable) it).setDamage(0);
							HandItem.setItemMeta(it);
							
							p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez réparé l'item dans votre main principale");
							
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Item dans votre main principale n'est pas cassé");
					}

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.repair.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								ItemStack HandItem = target.getInventory().getItemInMainHand();
								
								//Info : Le Trident est détecté comme un item d'air
								if(HandItem.getType() == Material.AIR && HandItem.getType() != Material.TRIDENT) {
									
									p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède aucun item(s) dans sa main principale");
									
								} else {
									
									if(HandItem.getAmount() >= 2) {
										
										p.sendMessage(main.prefix + ChatFormatting.RED + "Rien n'a été réparé, vous ne pouvez réparé seulement qu'un seul item a la fois situant dans la main principale de " + ChatFormatting.GOLD + target.getName());
										return true;
									}
									
									ItemMeta it = HandItem.getItemMeta();
									
									if(((Damageable) it).hasDamage()) {

										((Damageable) it).setDamage(0);
										HandItem.setItemMeta(it);
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous venez de réparé l'item dans la main principale de " + ChatFormatting.GOLD + target.getName());
										target.sendMessage(main.prefix + ChatFormatting.GRAY + "Votre item dans votre main principale a été réparé par " + ChatFormatting.GOLD + p.getName());
										
									} else p.sendMessage(main.prefix + ChatFormatting.RED + "L'Item dans votre main principale n'est pas cassé");
								}

							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /repair sans arguments");
						
					} else {
						
						if(p.hasPermission("evhoutility.repair.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /repair sans arguments");
					}
					
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous réparez votre item en main principal, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					ItemStack HandItem = target.getInventory().getItemInMainHand();
					
					//Info : Le Trident est détecté comme un item d'air
					if(HandItem.getType() == Material.AIR && HandItem.getType() != Material.TRIDENT) {
						
						sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède aucun item(s) dans sa main principale");
						
					} else {
						
						if(HandItem.getAmount() >= 2) {
							
							sender.sendMessage(main.prefix + ChatFormatting.RED + "Rien n'a été réparé, vous ne pouvez réparé seulement qu'un seul item a la fois situant dans la main principale de " + ChatFormatting.GOLD + target.getName());
							return true;
						}
						
						ItemMeta it = HandItem.getItemMeta();
						
						if(((Damageable) it).hasDamage()) {

							((Damageable) it).setDamage(0);
							HandItem.setItemMeta(it);
							
							sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous venez de réparé l'item dans la main principale de " + ChatFormatting.GOLD + target.getName());
							target.sendMessage(main.prefix + ChatFormatting.GRAY + "Votre item dans votre main principale a été réparé par " + ChatFormatting.GOLD + "La Console");
							
						} else sender.sendMessage(main.prefix + ChatFormatting.RED + "L'Item dans votre main principale n'est pas cassé");
					}
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");

		}
		
		return false;
	}
	
	/****************************************************************/
	/* PARTIE COMMANDE POUR RÉPARER UN ITEM DANS LA MAIN DU JOUEUR  */
	/****************************************************************/

}

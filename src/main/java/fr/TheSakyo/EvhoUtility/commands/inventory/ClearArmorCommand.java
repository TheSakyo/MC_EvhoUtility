package fr.TheSakyo.EvhoUtility.commands.inventory;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ClearArmorCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ClearArmorCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	/*************************************************************/
	/* PARTIE COMMANDE POUR SUPPRIMER L'ARMURE ENTIÈRE DU JOUEUR */
    /*************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.clear")) {
				
				if(args.length == 0) {
					
					for(ItemStack item : p.getInventory().getArmorContents()) {
						
                        if(item != null) {
                        	
                        	// Remplace les armures de l'exécuteur par de l'air //
                        	
    				    	p.getInventory().setHelmet(new ItemStack(Material.AIR));
    				    	p.getInventory().setChestplate(new ItemStack(Material.AIR));
    				    	p.getInventory().setLeggings(new ItemStack(Material.AIR));
    						p.getInventory().setBoots(new ItemStack(Material.AIR));
    						
    						// Remplace les armures l'exécuteur par de l'air //
    						
    						p.sendMessage(main.prefix + ChatFormatting.GRAY + "Votre armure a été supprimer");
                            return true;
                        }
                    }
				    p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne possédez pas d'armure");

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.clear.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								for(ItemStack item : target.getInventory().getArmorContents()) {
									
			                        if(item != null) {
			                        	
			                        	// Remplace les armures du joueur précisé par de l'air //
			                        	
			                        	target.getInventory().setHelmet(new ItemStack(Material.AIR));
			                        	target.getInventory().setChestplate(new ItemStack(Material.AIR));
			                        	target.getInventory().setLeggings(new ItemStack(Material.AIR));
			                        	target.getInventory().setBoots(new ItemStack(Material.AIR));
			    						
			    						// Remplace les armures du joueur précisé par de l'air //
			    						
			    						p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "L'Armure de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été supprimer");
			                            target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre armure a été supprimer par " + ChatFormatting.GOLD + p.getName());
			    						return true;
			                        }
			                    }
								p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède pas d'armure");
							
							} else {
								
								p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
							}
						
						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /cleararmor sans arguments"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.clear.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /cleararmor sans arguments");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour supprimer votre armure, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					for(ItemStack item : target.getInventory().getArmorContents()) {
						
                        if(item != null) {
                        	
                        	// Remplace les armures du joueur précisé par de l'air //
                        	
                        	target.getInventory().setHelmet(new ItemStack(Material.AIR));
                        	target.getInventory().setChestplate(new ItemStack(Material.AIR));
                        	target.getInventory().setLeggings(new ItemStack(Material.AIR));
                        	target.getInventory().setBoots(new ItemStack(Material.AIR));
    						
    						// Remplace les armures du joueur précisé par de l'air //
    						
    						sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "L'Armure de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été supprimer");
                            target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Votre armure a été supprimer par " + ChatFormatting.GOLD + "La Console");
    						return true;
                        }
                    }
					sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède pas d'armure");
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
		}
		
		return false;
	}
	
	/*************************************************************/
	/* PARTIE COMMANDE POUR SUPPRIMER L'ARMURE ENTIÈRE DU JOUEUR */
    /*************************************************************/

}

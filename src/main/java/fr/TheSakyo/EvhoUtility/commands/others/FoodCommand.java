package fr.TheSakyo.EvhoUtility.commands.others;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class FoodCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public FoodCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
   /**********************************************************/
   /* 		  PARTIE COMMANDE POUR RASSASIER LE JOUEUR       */
   /* INFO : Ne fonctionne pas en mode spéctateur et créatif */
   /**********************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.food")) {
				
				if(args.length == 0) {
					
					if(p.getGameMode() == GameMode.CREATIVE) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas vous rassasiez en mode de jeux Créatif");
						
					} else if(p.getGameMode() == GameMode.SPECTATOR) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas vous rassasiez en mode de jeux Spéctateur");
						
					} else {
						
						if(p.getFoodLevel() == 20) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas vous rassasiez car vous n'avez pas faim");
							
						} else {
								
							p.setFoodLevel(20);
							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes désormais rassasier");
							
							if(p.getHealth() != p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
								
								p.sendMessage(ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous pouvez vous soigner avec /heal");
							}
						}
					}

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.food.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								if(target.getGameMode() == GameMode.CREATIVE) {
									
									p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier en mode de jeux Créatif");
									
								} else if(target.getGameMode() == GameMode.SPECTATOR) {
									
									p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier en mode de jeux Spéctateur");
									
								} else {
									
									if(target.getFoodLevel() == 20) {
										
										p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier car il n'a pas faim");
										
									} else {
											
										target.setFoodLevel(20);
										
										p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " est désormais rassasier");
										target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes désormais rassasier par " + ChatFormatting.GOLD + p.getName());
									}
								}
							
							} else {
								
								p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
							}
						
						} else { p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /food sans arguments"); }
						
					} else {
						
						if(p.hasPermission("evhoutility.food.other")) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /food sans arguments");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous rassasiez, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target.getGameMode() == GameMode.CREATIVE) {
						
						sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier en mode de jeux Créatif");
						
					} else if(target.getGameMode() == GameMode.SPECTATOR) {
						
						sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier en mode de jeux Spéctateur");
						
					} else {
						
						if(target.getFoodLevel() == 20) {
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être rassasier car il n'a pas faim");
							
						} else {
								
							target.setFoodLevel(20);
							
							sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " est désormais rassasier");
							target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes désormais rassasier par " + ChatFormatting.GOLD + "La Console");
						}
					}
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
		}
		
		return false;
	}
	
   /**********************************************************/
   /* 		  PARTIE COMMANDE POUR RASSASIER LE JOUEUR       */
   /* INFO : Ne fonctionne pas en mode spéctateur et créatif */
   /**********************************************************/
}

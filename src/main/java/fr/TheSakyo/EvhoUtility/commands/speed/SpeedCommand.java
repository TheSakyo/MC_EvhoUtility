package fr.TheSakyo.EvhoUtility.commands.speed;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;

public class SpeedCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public SpeedCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	// Code Couleur pour les messages au niveau du tchat //
	String GI = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString();
	// Code Couleur pour les messages au niveau du tchat //

	
	
  /*********************************************/
  /* PARTIE COMMANDE POUR LA VITESSE DE MARCHE */ 
  /*********************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.speed")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /speed <0.1 à 1> ou <reset>");

				} else {
					
					if(args.length == 1) {
						
						if(CustomMethod.isFloat(args[0])) {
							
							if(Float.parseFloat(args[0]) > 1) {

								p.performCommand("speed");
								return true;
							}

							/******************************/

							p.setWalkSpeed(Float.parseFloat(args[0]));
							p.sendMessage(main.prefix + GI + "Votre vitesse de marche a été définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
							
						} else if(args[0].equalsIgnoreCase("reset")) {

							p.setWalkSpeed(0.2f);
							p.sendMessage(main.prefix + GI + "Votre vitesse de marche a été mise par défaut");
							
						} else p.performCommand("speed");
					
					} else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.speed.other")) {
							
							if(CustomMethod.isFloat(args[0])) {
								
								if(Float.parseFloat(args[0]) >= 2) {

									p.performCommand("speed");
									return true;
								}

								/******************************/

								if(Bukkit.getServer().getPlayer(args[1]) != null) {
									
									Player target = Bukkit.getServer().getPlayer(args[1]);

									/***************************/

									if(main.freezeP.contains(target.getUniqueId())) {
										
									   if(target.getWalkSpeed() == 0.0f) {
											
										 p.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas changer la vitesse de marche d'un joueur freeze !");
										 return true;
									   }
									}

									/***************************/

									target.setWalkSpeed(Float.parseFloat(args[0]));
									target.sendMessage(main.prefix + GI + "Votre vitesse de marche a été définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]) + GI + " par " + ChatFormatting.GOLD + p.getName());

									/***************************/

									p.sendMessage(main.prefix + GI + "Vitesse de marche pour " + ChatFormatting.GOLD + target.getName() + GI + " définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
								
								} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
								
							} else if(args[0].equalsIgnoreCase("reset")) {
								
								if(Bukkit.getServer().getPlayer(args[1]) != null) {
									
									Player target = Bukkit.getServer().getPlayer(args[1]);

									/***************************/

									if(main.freezeP.contains(target.getUniqueId())) {
										
									   if(target.getWalkSpeed() == 0.0f) {
											
										 p.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas changer la vitesse de marche d'un joueur freeze !");
										 return true;
									   }
									}

									/***************************/

									target.setWalkSpeed(0.2f);
									target.sendMessage(main.prefix + GI + "Votre vitesse de marche a été mise par défaut par " + ChatFormatting.GOLD + p.getName());
									
									p.sendMessage(main.prefix + GI + "Vitesse de marche pour " + ChatFormatting.GOLD + target.getName() + GI + " définit par défaut");
								
								} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

							} else p.performCommand("speed");
						
						} else p.performCommand("speed");
						
					} else {
						
						if(p.hasPermission("evhoutility.speed.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						else p.performCommand("speed");
					}
					
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /speed <0.1 à 1> <player> ou <reset> <player>");
			else {
				
				if(args.length == 1) Bukkit.getServer().dispatchCommand(sender, "speed");
				else if(args.length == 2) {
						
					if(CustomMethod.isFloat(args[0])) {
						
						if(Float.parseFloat(args[0]) >= 2) {

							Bukkit.getServer().dispatchCommand(sender, "speed");
							return true;
						}

						/******************************/

						if(Bukkit.getServer().getPlayer(args[1]) != null) {
							
							Player target = Bukkit.getServer().getPlayer(args[1]);

							/***************************/

							if(main.freezeP.contains(target.getUniqueId())) {
								
							   if(target.getWalkSpeed() == 0.0f) {
									
								 sender.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas changer la vitesse de marche d'un joueur freeze !");
								 return true;
							   }
							}

							/***************************/

							target.setWalkSpeed(Float.parseFloat(args[0]));
							target.sendMessage(main.prefix + GI + "Votre vitesse de marche a été définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]) + GI + " par " + ChatFormatting.GOLD + "La Console");
							
							sender.sendMessage(main.prefix + GI + "Vitesse de marche pour " + ChatFormatting.GOLD + target.getName() + GI + " définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
						
						} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
					} else if(args[0].equalsIgnoreCase("reset")) {
						
						if(Bukkit.getServer().getPlayer(args[1]) != null) {
							
							Player target = Bukkit.getServer().getPlayer(args[1]);

							/***************************/

							if(main.freezeP.contains(target.getUniqueId())) {
								
							   if(target.getWalkSpeed() == 0.0f) {
									
								 sender.sendMessage(ChatFormatting.RED + "Vous ne pouvez pas changer la vitesse de marche d'un joueur freeze !");
								 return true;
							   }
							}

							/***************************/

							target.setWalkSpeed(0.2f);
							target.sendMessage(main.prefix + GI + "Votre vitesse de marche a été mise par défaut par " + ChatFormatting.GOLD + "La Console");
							
							sender.sendMessage(main.prefix + GI + "Vitesse de marche pour " + ChatFormatting.GOLD + target.getName() + GI + " définit par défaut");
						
						} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

					} else Bukkit.getServer().dispatchCommand(sender, "speed");
					
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
			}
		}
		
		return false;
	}
	
	/*********************************************/
	/* PARTIE COMMANDE POUR LA VITESSE DE MARCHE */ 
	/*********************************************/

}

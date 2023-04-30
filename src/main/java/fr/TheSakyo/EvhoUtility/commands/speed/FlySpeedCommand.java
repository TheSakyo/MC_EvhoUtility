package fr.TheSakyo.EvhoUtility.commands.speed;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;

public class FlySpeedCommand implements CommandExecutor {


	/* Récupère la class "Main" */
	private final UtilityMain main;
	public FlySpeedCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	String GI = ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString();
	
	
	
   /******************************************/
   /* PARTIE COMMANDE POUR LA VITESSE DE VOL */ 
   /******************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.flyspeed")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /flyspeed <0.1 à 1> ou <reset>");

				} else {
					
					if(args.length == 1) {
						
						if(CustomMethod.isFloat(args[0])) {
							
							if(Float.parseFloat(args[0]) > 1) {

								p.performCommand("flyspeed");
								return true;
							}

							/******************************/

							p.setFlySpeed(Float.parseFloat(args[0]));
							p.sendMessage(main.prefix + GI + "Votre vitesse de vol a été définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
							
						} else if(args[0].equalsIgnoreCase("reset")) {

							p.setFlySpeed(0.1f);
							p.sendMessage(main.prefix + GI + "Votre vitesse de vol a été mise par défaut");
							
						} else p.performCommand("flyspeed");
					
					} else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.flyspeed.other")) {
							
							if(CustomMethod.isFloat(args[0])) {
								
								if(Float.parseFloat(args[0]) >= 2) {

									p.performCommand("flyspeed");
									return true;
								}

								/******************************/

								if(Bukkit.getServer().getPlayer(args[1]) != null) {
									
									Player target = Bukkit.getServer().getPlayer(args[1]);

									/***************************/

									target.setFlySpeed(Float.parseFloat(args[0]));
									target.sendMessage(main.prefix + GI + "Votre vitesse de vol a été définit é " + ChatFormatting.GREEN + Float.parseFloat(args[0]) + GI + " par " + ChatFormatting.GOLD + p.getName());
									
									p.sendMessage(main.prefix + GI + "Vitesse de vol pour " + ChatFormatting.GOLD + target.getName() + GI + " définit é " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
								
								} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
								
							} else if(args[0].equalsIgnoreCase("reset")) {
								
								if(Bukkit.getServer().getPlayer(args[1]) != null) {
									
									Player target = Bukkit.getServer().getPlayer(args[1]);

									/***************************/

									target.setFlySpeed(0.1f);
									target.sendMessage(main.prefix + GI + "Votre vitesse de vol a été mise par défaut par " + ChatFormatting.GOLD + p.getName());
									
									p.sendMessage(main.prefix + GI + "Vitesse de vol pour " + ChatFormatting.GOLD + target.getName() + GI + " définit par défaut");
								
								} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

							} else p.performCommand("flyspeed");
						
						} else p.performCommand("flyspeed");
						
					} else {
						
						if(p.hasPermission("evhoutility.flyspeed.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
						else p.performCommand("flyspeed");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /flyspeed <0.1 à 1> <player> ou <reset> <player>");

			} else {
				
				if(args.length == 1) { Bukkit.getServer().dispatchCommand(sender, "flyspeed"); }
				else if(args.length == 2) {
						
					if(CustomMethod.isFloat(args[0])) {
						
						if(Float.parseFloat(args[0]) >= 2) {

							Bukkit.getServer().dispatchCommand(sender, "flyspeed");
							return true;
						}

						/******************************/

						if(Bukkit.getServer().getPlayer(args[1]) != null) {
							
							Player target = Bukkit.getServer().getPlayer(args[1]);

							/***************************/

							target.setFlySpeed(Float.parseFloat(args[0]));
							target.sendMessage(main.prefix + GI + "Votre vitesse de vol a été définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]) + GI + " par " + ChatFormatting.GOLD + "La Console");

							sender.sendMessage(main.prefix + GI + "Vitesse de vol pour " + ChatFormatting.GOLD + target.getName() + GI + " définit à " + ChatFormatting.GREEN + Float.parseFloat(args[0]));
						
						} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
					} else if(args[0].equalsIgnoreCase("reset")) {
						
						if(Bukkit.getServer().getPlayer(args[1]) != null) {
							
							Player target = Bukkit.getServer().getPlayer(args[1]);

							/***************************/

							target.setFlySpeed(0.1f);
							target.sendMessage(main.prefix + GI + "Votre vitesse de vol a été mise par défaut par " + ChatFormatting.GOLD + "La Console");
							
							sender.sendMessage(main.prefix + GI + "Vitesse de vol pour " + ChatFormatting.GOLD + target.getName() + GI + " définit par défaut");
						
						} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");

					} else Bukkit.getServer().dispatchCommand(sender, "flyspeed");
					
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
			}
		}
		
		return false;
	}
	
	/******************************************/
	/* PARTIE COMMANDE POUR LA VITESSE DE VOL */ 
	/******************************************/

}

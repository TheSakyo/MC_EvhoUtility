package fr.TheSakyo.EvhoUtility.commands.others;

import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class FlyCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public FlyCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
   /*****************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LE MODE VOL AU JOUEUR */
   /*    INFO : Ne fonctionne pas en mode spéctateur et créatif     */
   /*****************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.fly")) {
				
				if(args.length == 0) {
					
					if(p.getGameMode() == GameMode.CREATIVE) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous utilisez déjà le Vol en mode de jeux Créatif");
						
					} else if(p.getGameMode() == GameMode.SPECTATOR) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous utilisez déjà le Vol en mode de jeux Spéctateur");
						
					} else {
						
						if(p.getAllowFlight()) {
							
							p.setAllowFlight(false);
							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous n'êtes plus en mode Vol");
							
						} else if(!p.getAllowFlight()) {
							
							p.setAllowFlight(true);
							p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes désormais en mode Vol");
						}
						
					}

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.fly.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								if(target.getGameMode() == GameMode.CREATIVE) {
									
									p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " utilise déjà le Vol en mode de jeux Créatif");
									
								} else if(target.getGameMode() == GameMode.SPECTATOR) {
									
									p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " utilise déjà le Vol en mode de jeux Spéctateur");
									
								} else {
									
									if(target.getAllowFlight()) {
										
										target.setAllowFlight(false);
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été désactivé");
										target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol a été désactivé par " + ChatFormatting.GOLD + p.getName());
										
									} else if(!target.getAllowFlight()) {
										
										target.setAllowFlight(true);
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été activé");
										target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol a été activé par " + ChatFormatting.GOLD + p.getName());
									}
									
								}
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /fly sans arguments");
						
					} else {
						
						if(p.hasPermission("evhoutility.fly.other")) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /fly sans arguments");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous attribuer le mode Vol, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					if(target.getGameMode() == GameMode.CREATIVE) {
						
						sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " utilise déjà le Vol en mode de jeux Créatif");
						
					} else if(target.getGameMode() == GameMode.SPECTATOR) {
						
						sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " utilise déjà le Vol en mode de jeux Spéctateur");
						
					} else {
						
						if(target.getAllowFlight()) {
							
							target.setAllowFlight(false);
							
							sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été désactivé");
							target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol a été désactivé par " + ChatFormatting.GOLD + "La Console");
							
						} else if(!target.getAllowFlight()) {
							
							target.setAllowFlight(true);
							
							sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " a été activé");
							target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Mode Vol a été activé par " + ChatFormatting.GOLD + "La Console");
						}
					}

				} else { sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !"); }
				
			} else { sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !"); }
		}
		
		return false;
	}
	
   /*****************************************************************/
   /* PARTIE COMMANDE POUR ACTIVER/DÉSACTIVER LE MODE VOL AU JOUEUR */
   /*    INFO : Ne fonctionne pas en mode spéctateur et créatif     */
   /*****************************************************************/
	
}

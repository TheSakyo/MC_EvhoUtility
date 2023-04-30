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

import java.util.UUID;

public class GodModeCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public GodModeCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
   /**********************************************************************/
   /* 	PARTIE COMMANDE METTRE LE JOUEUR EN "GODMODE" (Invincible)      */
   /*     INFO : Ne fonctionne pas en mode spéctateur et créatif        */
   /*       2ÈME INFO : 'main.GODS' = liste des joueurs en "GODMODE     */
   /*********************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			UUID uuid = p.getUniqueId();

			if(p.hasPermission("evhoutility.godmode")) {
				
				if(args.length == 0) {
					
					if(p.getGameMode() == GameMode.CREATIVE) {
						
						if(main.GODS.contains(uuid)) {
							
							main.GODS.remove(uuid);
							p.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé");
						 
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas être en godmode en mode de jeux Créatif");
						
					} else if(p.getGameMode() == GameMode.SPECTATOR) {
						
						if (main.GODS.contains(uuid)) {
							
							main.GODS.remove(uuid);
							p.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé");
							
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas être en godmode en mode de jeux Spéctateur");
						
					} else {
						
						if(main.GODS.contains((uuid))) { main.GODS.remove(uuid); }
						else { 
							
							main.GODS.add(uuid);

							/******************************/

							if(p.getHealth() != p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {
								
								p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
							}

							/******************************/

							if(p.getFoodLevel() != 20) p.setFoodLevel(20);
						}
						
					    p.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + (main.GODS.contains(uuid) ? ChatFormatting.GREEN + "activé" : ChatFormatting.RED + "désactivé"));
					}

				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.godmode.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								UUID targetUUID = target.getUniqueId();

								/*************************************/

								if(target.getGameMode() == GameMode.CREATIVE) {
									
									if(main.GODS.contains((targetUUID))) {
										
										main.GODS.remove(targetUUID);

										/******************************/

										p.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " pour " + ChatFormatting.GOLD + target.getName());
										target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + p.getName());
										
									} else p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être en godmode en mode de jeux Créatif");
									
								} else if(target.getGameMode() == GameMode.SPECTATOR) {
									
									if (main.GODS.contains((targetUUID))) {
										
										main.GODS.remove(targetUUID);

										/******************************/

										p.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " pour " + ChatFormatting.GOLD + target.getName());
										target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + p.getName());
										
									} else p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être en godmode en mode de jeux Spéctateur");
									
								} else {
									
									toggleGodMode(p, target);
								    target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + (main.GODS.contains(targetUUID) ? ChatFormatting.GREEN + "activé" : ChatFormatting.RED + "désactivé") + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + p.getName());
								}
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /godmode ou /god sans arguments");
						
					} else {
						
						if(p.hasPermission("evhoutility.godmode.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /godmode ou /god sans arguments");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous mettre en GodMode, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					UUID targetUUID = target.getUniqueId();
					
					if(target.getGameMode() == GameMode.CREATIVE) {
						
						if(main.GODS.contains((targetUUID))) {
							
							main.GODS.remove(targetUUID);

							/******************************/

							sender.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " pour " + ChatFormatting.GOLD + target.getName());
							target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + "La Console");
							
						} else sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être en godmode en mode de jeux Créatif");
						
					} else if(target.getGameMode() == GameMode.SPECTATOR) {
						
						if(main.GODS.contains((targetUUID))) {
							
							main.GODS.remove(targetUUID);

							/******************************/

							sender.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " pour " + ChatFormatting.GOLD + target.getName());
							target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + ChatFormatting.RED + "désactivé" + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + "La Console");
							
						} else sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne peut pas être en godmode en mode de jeux Spéctateur");
						
					} else {


						toggleGodMode(sender, target);
					    target.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + (main.GODS.contains(targetUUID) ? ChatFormatting.GREEN + "activé" : ChatFormatting.RED + "désactivé") + ChatFormatting.GRAY + " par " + ChatFormatting.GOLD + "La Console");
					}
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
		}
		
		return false;
	}
	
   /**********************************************************************/
   /* 	PARTIE COMMANDE METTRE LE JOUEUR EN "GODMODE" (Invincible)      */
   /*     INFO : Ne fonctionne pas en mode spéctateur et créatif        */
   /*       2ÈME INFO : 'main.GODS' = liste des joueurs en "GODMODE     */
   /*********************************************************************/

   /************************************************************************/
   /************************************************************************/
   /************************************************************************/

	/**
	 * Active ou désactive le mode invincible ("Godmode") pour un joueur cible.
	 *
	 * @param sender L'expéditeur de la commande qui recevra les messages de confirmation.
	 * @param target UUID du joueur cible pour lequel activer ou désactiver le mode invincible.
	 */
	public void toggleGodMode(CommandSender sender, Player target) {

		if(main.GODS.contains((target.getUniqueId()))) main.GODS.remove(target.getUniqueId());
		else {

			main.GODS.add(target.getUniqueId());

			/*************************************/

			if(target.getHealth() != target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue()) {

				target.setHealth(target.getAttribute(Attribute.GENERIC_MAX_HEALTH).getDefaultValue());
			}

			/*************************************/

			if(target.getFoodLevel() != 20) target.setFoodLevel(20);
		}

		/****************************************/

		sender.sendMessage(main.prefix + ChatFormatting.YELLOW + "Godmode " + (main.GODS.contains(target.getUniqueId()) ? ChatFormatting.GREEN + "activé" : ChatFormatting.RED + "désactivé") + ChatFormatting.GRAY + " pour " + ChatFormatting.GOLD + target.getName());
	}

}

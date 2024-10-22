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
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;

public class MoreItemCommand implements CommandExecutor {
	
	/* Récupère la class "Main" */
	private final UtilityMain main;
	public MoreItemCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */



	
	/****************************************************************************/
	/* PARTIE COMMANDE POUR AJOUTER N NOMBRES DE L'ITEM DANS LA MAIN DU JOUEUR  */ 
	/****************************************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.more")) {
				
				if(args.length == 0) {
					
					ItemStack HandItem = p.getInventory().getItemInMainHand();
					
					if(HandItem.getType() == Material.AIR) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne possédez aucun item(s) dans votre main principale");
						
						return true;
					}

					if(HandItem.getAmount() == HandItem.getMaxStackSize()) {
						
						p.sendMessage(main.prefix + ChatFormatting.RED + "Vous possédez déja le nombre maximum de cette item dans votre main principale");
						
					} else {
						
						HandItem.setAmount(HandItem.getAmount() + 1);
						p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans votre main principale " + ChatFormatting.GREEN + 1 + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
					}
					
				} else {
					
					if(args.length == 1) AddMoreItem(p, null, args[0]);
					else if(args.length == 2) {
						
						if(p.hasPermission("evhoutility.more.other")) {
							
							if(Bukkit.getServer().getPlayer(args[1]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[1]);
								AddMoreItem(p, target, args[0]);
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
							
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises pour attribuer un temps à un joueurs !");
						
					} else {
						
						if(p.hasPermission("evhoutility.more.other")) {
							
							p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
							p.sendMessage(" ");
							p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez " + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + " /more [<number>] " + ChatFormatting.RED + "ou" + ChatFormatting.RED.toString() + ChatFormatting.BOLD.toString() + " /more <number> [<player>] " + ChatFormatting.RED + "!");
						
						} else p.performCommand("more");
					}
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0 || args.length == 1) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour vous ajouter un ou plusieur items dans votre main principal !");
				sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.GOLD.toString() + ChatFormatting.UNDERLINE.toString() + "INFO : " + ChatFormatting.RED + "Vous pouvez sinon préciser le joueur au niveau du second argument !");
			    sender.sendMessage(" ");
			    sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez dans ce cas là marquer au niveau du premier argument un nombre de l'item qu'il possède dans sa main" );
			
			} else if(args.length == 2) { 
				
				if(Bukkit.getServer().getPlayer(args[1]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[1]);
					AddMoreItem(sender, target, args[0]);
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus de deux arguments !");
		}
		
		return false;
	}
		
	
	/****************************************************************************/
	/* PARTIE COMMANDE POUR AJOUTER N NOMBRES DE L'ITEM DANS LA MAIN DU JOUEUR  */ 
	/****************************************************************************/


	
	
	// Méthode pour ajouter un nombre d'item au joueur ou a un joueur spécifique de l'item qu'il possède dans sa main principale //
	
	private void AddMoreItem(CommandSender sender, Player target, String args) {
		
		if(args == null) {
			
			main.console.sendMessage(main.argsNull);
			main.console.sendMessage(main.errorArgs);

        } else {
			
			if(CustomMethod.isInt(args)) {
				
				if(target != null) {
					
					if(sender == null) {
						
						main.console.sendMessage(main.senderNull);
						main.console.sendMessage(main.errorSender);
	
					} else { 
						
						ItemStack HandItem = target.getInventory().getItemInMainHand();
						
						if(sender instanceof Player p) {

							if(HandItem.getType() == Material.AIR) {
								
								p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède aucun item(s) dans sa main principale");
								return;
							}
							
							if(HandItem.getAmount() == HandItem.getMaxStackSize()) {
								
								p.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " possède déja le nombre maximum de cette item dans sa main principale");

                            } else {
								
								if(HandItem.getAmount() + Integer.parseInt(args) > HandItem.getMaxStackSize()) {
									
									p.sendMessage(main.prefix + ChatFormatting.RED + "Vous avez essayer d'ajouter plus que le nombre maximum de cette item dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + ", veuillez réduire le nombre");
									
								} else {
									
									if(Integer.parseInt(args) == 0) {
										
										p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " un nombre égal à 0");
										
									} else if(Integer.parseInt(args) == 1) {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " " + Integer.parseInt(args) + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
										target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY + " a ajouter dans votre main principal " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
									} else {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " " + Integer.parseInt(args) + ChatFormatting.GRAY + " items de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
										target.sendMessage(main.prefix + ChatFormatting.GOLD + p.getName() + ChatFormatting.GRAY + " a ajouter dans votre main principal " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " items de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
									}
									
								}
                            }
							
						} else { 
							
							if(HandItem.getType() == Material.AIR) {
								
								sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " ne possède aucun item(s) dans sa main principale");
								return;
							}
							
							if(HandItem.getAmount() == HandItem.getMaxStackSize()) {
								
								sender.sendMessage(main.prefix + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " possède déja le nombre maximum de cette item dans sa main principale");

                            } else {
								
								if(HandItem.getAmount() + Integer.parseInt(args) > HandItem.getMaxStackSize()) {
									
									sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous avez essayer d'ajouter plus que le nombre maximum de cette item dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + ", veuillez réduire le nombre");
									
								} else {
									
									if(Integer.parseInt(args) == 0) {
										
										sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.RED + " un nombre égal à 0");
										
									} else if(Integer.parseInt(args) == 1) {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " " + Integer.parseInt(args) + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
										target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY + " a ajouter dans votre main principal " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
									} else {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										sender.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans la main principale de " + ChatFormatting.GOLD + target.getName() + ChatFormatting.GREEN + " " + Integer.parseInt(args) + ChatFormatting.GRAY + " items de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
										target.sendMessage(main.prefix + ChatFormatting.GOLD + "La Console" + ChatFormatting.GRAY + " a ajouter dans votre main principal " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " items de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
									}
									
								}

                            }
						}
					}
				
				} else {
					
					if(sender == null) {
						
						main.console.sendMessage(main.senderNull);
						main.console.sendMessage(main.errorSender);
						
					} else {
						
						if(sender instanceof Player p) {

							ItemStack HandItem = p.getInventory().getItemInMainHand();
							
							if(HandItem.getType() == Material.AIR) {
								
								p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne possédez aucun item(s) dans votre main principale");
								return;
							}
							
							if(HandItem.getAmount() == HandItem.getMaxStackSize()) {
								
								p.sendMessage(main.prefix + ChatFormatting.RED + "Vous possédez déja le nombre maximum de cette item dans votre main principale");

                            } else {
								
								if(HandItem.getAmount() + Integer.parseInt(args) > HandItem.getMaxStackSize()) {
									
									p.sendMessage(main.prefix + ChatFormatting.RED + "Vous avez essayer d'ajouter plus que le nombre maximum de cette item dans votre main principale, veuillez réduire le nombre");
									
								} else {
									
									if(Integer.parseInt(args) == 0) {
										
										p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas ajouter dans votre main principale un nombre égal à 0");
										
									} else if(Integer.parseInt(args) == 1) {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans votre main principale " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " item de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
										
									} else {
										
										HandItem.setAmount(HandItem.getAmount() + Integer.parseInt(args));
										
										p.sendMessage(main.prefix + ChatFormatting.GRAY + "Vous avez ajouter dans votre main principale " + ChatFormatting.GREEN + Integer.parseInt(args) + ChatFormatting.GRAY + " items de :" + ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + " " + HandItem.getType().name());
									}
									
								}
							}

						} else {
							
							sender.sendMessage(main.targetNull);
							sender.sendMessage(main.targetNull);
						}
					}	
				}
				
			} else { 
				
				if(target != null) {
					
					if(sender == null) {
						
						main.console.sendMessage(main.senderNull);
						main.console.sendMessage(main.errorSender);
						return;
					} 
					
				} else {
					
					if(sender == null) {
						
						main.console.sendMessage(main.senderNull);
						main.console.sendMessage(main.errorSender);
						return;
						
					} else { 
						
						if(!(sender instanceof Player)) {
							
							sender.sendMessage(main.targetNull);
							sender.sendMessage(main.targetNull);
							return;
						}	
					}
				}

				/*************************************************/
				
				if(sender instanceof Player p) p.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez utiliser un nombre valide");
				else sender.sendMessage(main.prefix + ChatFormatting.RED + "Veuillez utiliser un nombre valide");
			}
		
		}
	}
	
	// Méthode pour ajouter un nombre d'item au joueur ou a un joueur spécifique de l'item qu'il possède dans sa main principale //
}

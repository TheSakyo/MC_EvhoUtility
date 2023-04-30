package fr.TheSakyo.EvhoUtility.commands.entities;


import java.util.ArrayList;
import java.util.List;

//import net.minecraft.network.protocol.game.PacketPlayOutEntityDestroy;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;


public class LightningCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public LightningCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
		
	
	// Variable de boucle //
	int loop;
	// Variable de boucle //
	
	
	// Variable liste (flèche) //
	List<Arrow> ArrowEntity = new ArrayList<>();
	// Variable liste (flèche) //
	

	
   /***************************************************/
   /* PARTIE COMMANDE POUR FAIRE APPARAÎTRE UN ÉCLAIR */
   /***************************************************/
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
		
		if(sender instanceof Player p) {

			if(p.hasPermission("evhoutility.ligthning")) {
				
				if(args.length == 0) {
					
					p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Un éclair va apparaître !");
					ThunderedArrow(p);
					
				} else {
					
					if(args.length == 1) {
						
						if(p.hasPermission("evhoutility.ligthning.other")) {
							
							if(Bukkit.getServer().getPlayer(args[0]) != null) {
								
								Player target = Bukkit.getServer().getPlayer(args[0]);
								
								p.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez foudroyé " + ChatFormatting.GOLD + target.getName());
								target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Un éclair est tomber sur vous ! Qui aurait pu vous faire ça ?");
								target.getWorld().strikeLightning(target.getLocation());
							
							} else p.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
						
						} else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /lightning ou /light sans arguments");
						
					} else {
						
						if(p.hasPermission("evhoutility.ligthning.other")) p.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
						else p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /lightning ou /light sans arguments");
					}
					
				}
				
			} else p.sendMessage(main.prefix + ChatFormatting.RED + "Vous n'avez pas les permissions requises !");
		
		} else {
			
			if(args.length == 0) {
				
				sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour faire apparaître un éclair, ou essayez de mettre un joueur en premier argument !");
			
			} else if(args.length == 1) { 
				
				if(Bukkit.getServer().getPlayer(args[0]) != null) {
					
					Player target = Bukkit.getServer().getPlayer(args[0]);
					
					sender.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous avez foudroyé " + ChatFormatting.GOLD + target.getName());
					target.sendMessage(main.prefix + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Un éclair est tomber sur vous ! Qui aurait pu vous faire ça ? " + ChatFormatting.GOLD +
							"La Console" + ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + " peut-être ?");

				    target.getWorld().strikeLightning(target.getLocation());
				
				} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Le joueur est introuvable !");
				
			} else sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous ne pouvez pas entrer plus d'un argument !");
		}
		
		return false;
	}
	
	/***************************************************/
	/* PARTIE COMMANDE POUR FAIRE APPARAÎTRE UN ÉCLAIR */
	/***************************************************/
	
	
	
	
	/*************************************************************************************************************/
	/* PETITE METHODE PRIVÉE POUR CRÉER UN FAUX PROJECTILE (FLÈCHE) [POUR DONNER A L'ÉCLAIR L'ENDROIT A FOUDROYÉ] */
	/*************************************************************************************************************/
	
	private void ThunderedArrow(Player p) {
		
		//Créer un projectile (flèche)
		Arrow arrow = p.launchProjectile(Arrow.class);
		
		arrow.setDamage(0); //Rend les data de la flèche à 0
		
		arrow.setSilent(true); //Met le son de lancement sur silencieux
		
		arrow.setCustomNameVisible(false); //Annule tout nom personnalisé
		
		
		// Lance le projectile à la direction où le joueur regarde et multiplie sa vitesse par 4 //
		arrow.setVelocity(p.getEyeLocation().getDirection());
		arrow.getVelocity().multiply(4);
		// Lance le projectile à la direction où le joueur regarde et multiplie sa vitesse par 4 //
		
		
		/* Rend le projectile invisible */

		ClientboundRemoveEntitiesPacket packet = new ClientboundRemoveEntitiesPacket(arrow.getEntityId());
		PlayerEntity.sendPacket(packet);

		/* Rend le projectile invisible */

		//Ajoute la flèche à une liste
		//(Utile en cas de multiple flèche et pouvoir faire plusieurs éclairs sans qu'ils se perdent)
		ArrowEntity.add(arrow);
		
		
		// Boucle pour récupérer lorsque le projectile tombe pour une fois faire apparaître l'éclair //
		loop = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, () -> {

			//Si le projectile existe bien, on essaie de faire apparaître l'éclair
            if(!arrow.isDead()) {

                //Vérifie si le projectile est plus en l'air
                if(arrow.isInBlock() || arrow.isOnGround()) {

                    //Fait apparaître l'éclair à l'emplacement du projectile
                    arrow.getWorld().strikeLightning(arrow.getLocation());

					/***********************************/

                    // Supprime-les entités et les éléments de la liste d'entités si elle n'est pas vide //
                    if(!ArrowEntity.isEmpty()) {

                        for(Arrow arrowList : ArrowEntity) arrowList.remove();

                        ArrowEntity.clear();
                    }
                    // Supprime-les entités et les éléments de la liste d'entités si elle n'est pas vide //

					/***********************************/

                    //Essai d'annuler la boucle
                    loop = 0;
                    Bukkit.getServer().getScheduler().cancelTask(loop);
                }
            }
        }, 0L, 0L);
		// Boucle pour récupérer lorsque le projectile tombe pour une fois faire apparaître l'éclair //
	}
	
	/*************************************************************************************************************/
	/* PETITE METHODE PRIVÉE POUR CRÉER UN FAUX PROJECTILE (FLÈCHE) [POUR DONNER A L'ÉCLAIR L'ENDROIT A FOUDROYÉ] */
	/*************************************************************************************************************/
}

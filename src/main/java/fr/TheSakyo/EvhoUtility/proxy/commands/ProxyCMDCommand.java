package fr.TheSakyo.EvhoUtility.proxy.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ProxyCMDCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private UtilityMain main;
	public ProxyCMDCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	 
	
	
	
	/**********************************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPERER UNE AUTRE COMMANDE BUNGEECORD (UTILISE UN CANAL CUSTOMISÉE) */
	/**********************************************************************************************/
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        
        if(sender instanceof Player p) {
            
            if(args.length < 1) {
            	
                p.sendMessage(main.prefix + ChatColor.RED + "Essayez /proxycmd <command> ou /proxycommand <command>");
                return true;
            }

            String command = wrapArguments(args);

            if(main.useproxycmd.containsKey(p.getUniqueId())) main.useproxycmd.replace(p.getUniqueId(), true);
            else main.useproxycmd.put(p.getUniqueId(), true);

            if(command.startsWith("/")) { main.sendToBungee("Command", command.replace("/", "")); }

            else if(command.startsWith("\\")) { main.sendToBungee("Command", command.replace("\\", "")); }

            else { main.sendToBungee("Command", command); }
    		
    		return true;
        }
        
        if(sender instanceof CommandSender) {
        	
        	sender.sendMessage(main.prefix + ChatColor.RED + "Vous devez être en jeux pour éxécuter une commande vers le Serveur Proxy !");
    		
    		return true;
        }
        
        return false;
    }
    
    /**********************************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPERER UNE AUTRE COMMANDE BUNGEECORD (UTILISE UN CANAL CUSTOMISÉE) */
	/**********************************************************************************************/
    
    



    // Petite méthode pour récupérer des arguments dans une liste "String[]"
    private String wrapArguments(String[] args) {

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < args.length; i++) { builder.append(args[i] + " "); }

        return builder.toString().trim();
    }
    // Petite méthode pour récupérer des arguments dans une liste "String[]"

}

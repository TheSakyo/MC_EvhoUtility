package fr.TheSakyo.EvhoUtility.proxy.commands;

import net.minecraft.ChatFormatting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class ProxyCMDCommand implements CommandExecutor {

	/* Récupère la class "Main" */
	private final UtilityMain main;
	public ProxyCMDCommand(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	 
	
    
	/**********************************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPÉRER UNE AUTRE COMMANDE BUNGEECORD (UTILISE UN CANAL CUSTOMISÉE) */
	/**********************************************************************************************/
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        
        if(sender instanceof Player p) {
            
            if(args.length < 1) {
            	
                p.sendMessage(main.prefix + ChatFormatting.RED + "Essayez /proxycmd <command> ou /proxycommand <command>");
                return true;
            }

            String command = wrapArguments(args);

            if(main.useProxyCMD.containsKey(p.getUniqueId())) main.useProxyCMD.replace(p.getUniqueId(), true);
            else main.useProxyCMD.put(p.getUniqueId(), true);

            if(command.startsWith("/")) { main.sendToBungee("Command", command.replace("/", "")); }
            else if(command.startsWith("\\")) { main.sendToBungee("Command", command.replace("\\", "")); }
            else { main.sendToBungee("Command", command); }
    		
    		return true;
        }
        
        if(sender instanceof CommandSender) {
        	
        	sender.sendMessage(main.prefix + ChatFormatting.RED + "Vous devez être en jeux pour exécuter une commande vers le Serveur Proxy !");
    		return true;
        }
        
        return false;
    }
    
    /**********************************************************************************************/
	/* PARTIE COMMANDE POUR RÉCUPÉRER UNE AUTRE COMMANDE BUNGEECORD (UTILISE UN CANAL CUSTOMISÉE) */
	/**********************************************************************************************/
    
    



    // Petite méthode pour récupérer des arguments dans une liste "String[]"
    private String wrapArguments(String[] args) {

        StringBuilder builder = new StringBuilder();
        for(String arg : args) { builder.append(arg).append(" "); }

        return builder.toString().trim();
    }
    // Petite méthode pour récupérer des arguments dans une liste "String[]"

}

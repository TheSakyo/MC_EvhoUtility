package fr.TheSakyo.EvhoUtility.managers;

import java.util.ArrayList;
import java.util.List;


import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import fr.TheSakyo.EvhoUtility.UtilityMain;


public class TabManager {
	
   /***************************/
   /* PARTIE VARIABLES UTILES */ 
   /***************************/
	
    /* Récupère la class "Main" */
	private final UtilityMain main;
	public TabManager(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
   // Variable pour faire une boucle pour l'animation du Tablist //
   BukkitTask task;
   //Variable pour faire une boucle pour l'animation du Tablist //
   
  
   // Variable pour défini le "header" et le "footer" du Tablist //
   private final List<Component> headers = new ArrayList<>();
   private final List<Component> footers = new ArrayList<>();
   // Variable pour défini le "header" et le "footer" du Tablist //

  
  /* Variable utiles pour charger le format du tablist */
   
  String name1 = null;
 
  String name2 = null;
 
  String name3 = null;
  
 
  String website = null;
 
  String ip = null;
  
  //Variable pour définir le code couleur de l'animation de "ip" et "site web" au niveau du TabList
  String colorfootersection = null;
  
  
  /* Variable utiles pour charger le format du tablist */
  
  
  /***************************/
  /* PARTIE VARIABLES UTILES */ 
  /***************************/
  
  
  
  /*****************************/
  /* CONFIGURATION DU TABLIST */
  /****************************/
  
  // Vérifie les informations de la config "tablist.yml" //
  private void getConfigString() {
	   
    this.name1 = ConfigFile.getString(main.tabConfig, "title1");
    this.name2 = ConfigFile.getString(main.tabConfig, "title2");
    this.name3 = ConfigFile.getString(main.tabConfig, "title3");
    this.website = ConfigFile.getString(main.tabConfig, "website");
    this.ip = ConfigFile.getString(main.tabConfig, "ip");
    this.colorfootersection = ConfigFile.getString(main.tabConfig, "ColorFooterSection");
  }
  // Vérifie les informations de la config "tablist.yml" //
  
  
  // Configure le Tablist //
  public void setupTablist(Player p) {
	  
	getConfigString();
	
	String CS = this.colorfootersection;
	
	ChatFormatting w  = ChatFormatting.WHITE;
	ChatFormatting reset = ChatFormatting.RESET;
	
    if(this.task != null) removeAll();

    String[] title = new String[] {
            w + "»   " + this.name1 + w + "   «",
            w + "»  " + this.name2 + w + "  «",
            w + "» " + this.name3 + w + " «"
    };
    
    addHeader(title[0] + "\n");
    addHeader(title[1] + "\n");
    addHeader(title[2] + "\n");
    
    addFooter("\n" + w + "Site Web : " + this.website + reset + "\n\n" + w + "IP : " + this.ip);
    addFooter("\n" + CS + "S" + w + "ite Web : " + this.website + reset + "\n\n" + CS + "I" + w + "P : " + this.ip);
    addFooter("\n" + w + "S" + CS + "i" + w + "te Web : " + this.website + reset + "\n\n" + w + "I" + CS + "P" + w + " : " + this.ip);
    addFooter("\n" + w + "Si" + CS + "t" + w + "e Web : " + this.website + reset + "\n\n" + w + "IP " + CS + ":" + reset + " " + this.ip);
    addFooter("\n" + w + "Sit" + CS + "e" + w + " Web : " + this.website + reset + "\n\n" + CS + "IP :" + reset + " " + this.ip);
    addFooter("\n" + w + "Site" + CS + " W" + w + "eb : " + this.website + reset + "\n\n" + w + "IP : " + this.ip);
    addFooter("\n" + w + "Site W" + CS + "e" + w + "b : " + this.website + reset + "\n\n" + CS + "I" + w + "P : " + this.ip);
    addFooter("\n" + w + "Site We" + CS + "b" + w + " : " + this.website + reset + "\n\n" + w + "I" + CS + "P" + w + " : " + this.ip);
    addFooter("\n" + w + "Site Web " + CS + ":" + reset + " " + this.website + reset + "\n\n" + w + "IP " + CS + ":" + reset + " " + this.ip);
    addFooter("\n" + CS + "Site Web :" + reset + " " + this.website + reset + "\n\n" + CS + "IP :" + reset + " " + this.ip);

                                /* --------------------------------------------- */

    //Essaie d'Appeler la méthode "ShowTab" //
    try { showTab(p); }
    catch(IllegalPluginAccessException ignored) {}
    //Essaie d'Appeler la méthode "ShowTab" //

  }
  // Configure le Tablist //
  
  
  // Affiche le Tablist //
  public void showTab(Player p) {

    this.task = new BukkitRunnable() {

        int hi = 0;
        int fi = 0;

		public void run() {

          if(headers.isEmpty() && footers.isEmpty()) { hi = 0; fi = 0; }

          else {

            if(Bukkit.getServer().getOnlinePlayers().isEmpty()) { hi = 0; fi = 0; }

            else {

              /*net.minecraft.network.chat.Component headersComponent = CraftChatMessage.fromString(CustomMethod.ComponentToString(headers.get(hi)))[0];
              net.minecraft.network.chat.Component footersComponent = CraftChatMessage.fromString(CustomMethod.ComponentToString(footers.get(fi)))[0];

              ClientboundTabListPacket packet = new ClientboundTabListPacket(headersComponent, footersComponent);
              PlayerEntity.sendPacket(packet);*/

              p.sendPlayerListHeaderAndFooter(headers.get(hi), footers.get(fi));

              if(hi == headers.size() - 1) { hi = 0; }
              else { hi++; }

              if(fi == footers.size() - 1) { fi = 0; }
              else { fi++; }

            }
          }
		}

      }.runTaskTimerAsynchronously(main, 0L, 8L);
  }
  // Affiche le Tablist //
  
  
  // addHeader = ajoute un en-tête ; addFooter = ajoute un pied de page //
  public void addHeader(String header) { this.headers.add(CustomMethod.StringToComponent(ColorUtils.format(header))); }
  
  public void addFooter(String footer) { this.footers.add(CustomMethod.StringToComponent(ColorUtils.format(footer))); }
  // addHeader = ajoute un en-tête ; addFooter = ajoute un pied de page //
  
  
  // Supprime entièrement le Tablist personnalisé //
  public void removeAll() {
    	
    this.task.cancel();

    if(!this.headers.isEmpty()) this.headers.clear();

    if(!this.footers.isEmpty()) this.footers.clear();

  }
 // Supprime entièrement le Tablist personnalisé //

  /*****************************/
  /* CONFIGURATION DU TABLIST */
  /****************************/
}

package fr.TheSakyo.EvhoUtility.registering;

import java.util.List;
import java.util.UUID;

import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.api.Advancements.Advancement;

public class AdvancementsCreated {
	
    // Récupère la class "Main" en tant que "static"
    private static UtilityMain mainInstance = UtilityMain.getInstance();
	
	
	

   /***********************************************************************************/
   /* MÉTHODE D'AJOUT/SUPPRESION DES ACHIEVEMENTS AVEC LA CLASS "ADVANCEMENTSMANAGER' */
   /***********************************************************************************/  
	
	// Ajoute/Supprime des faux achievements //
	public static void AddingFalseAdvancement(List<Advancement> advs, Player p) {
	      
	  for(Advancement advancement : advs) { mainInstance.advManager.addAdvancement(p, advancement); }

	 }
	   
	public static void RemovingFalseAdvancement(List<Advancement> advs, Player p) {
		   
	  for(Advancement advancement : advs) { mainInstance.advManager.removeAdvancement(p, advancement); }
		 
	}
	// Ajoute/Supprime des faux achievements // 
	
	
	
	// Ajoute/Supprime des vrais achievements //
	public static void AddingRealAdvancements(List<Advancement> advs, Player p) { 
		
	  for(Advancement advancement : advs) { AddingRealAdvancement(advancement, p);}
	}

   public static void RemovingRealAdvancements(List<Advancement> advs, Player p) { 
		   
	  for(Advancement advancement : advs) { RemovingRealAdvancement(advancement, p);}
   }
   
   
   
   public static void AddingRealAdvancement(Advancement adv, Player p) { 
		 
	  ConfigFile.set(mainInstance.advconfig, adv.getName() + "." + p.getUniqueId().toString(), p.getName());
	  ConfigFile.saveConfig(mainInstance.advconfig);
	  
	  List<Advancement> advList = List.of(adv);
	  
	  for(UUID players : mainInstance.advManager.getPlayers()) {

		 CheckSavingAdvancements(advList, players);
	  }
   }
  
	public static void RemovingRealAdvancement(Advancement adv, Player p) { 
			   
	   ConfigFile.set(mainInstance.advconfig, adv.getName() + "." + p.getUniqueId().toString(), null);
	   ConfigFile.saveConfig(mainInstance.advconfig);
	   
	   List<Advancement> advList = List.of(adv);
		  
	   for(UUID players : mainInstance.advManager.getPlayers()) {
		  
		  CheckUnSavingAdvancements(advList, players);
	   }
	}
   // Ajoute/Supprime des vrais achievements // 
   
   /***********************************************************************************/
   /* MÉTHODE D'AJOUT/SUPPRESION DES ACHIEVEMENTS AVEC LA CLASS "ADVANCEMENTSMANAGER' */
   /***********************************************************************************/  
   
   
   
   
   
   /********************************************************/
   /* MÉTHODES POUR VÉRIFIER ET SAUVEGARDER L'ACHIEVEMENT  */
   /********************************************************/  
	public static void CheckAdvancementPlayer(Advancement adv, Player p) { 
		
		if(ConfigFile.getString(mainInstance.advconfig, adv.getName() + "." + p.getUniqueId().toString()) != null) {
			 
			 if(ConfigFile.getString(mainInstance.advconfig, adv.getName() + "." + p.getUniqueId().toString()).equalsIgnoreCase(p.getName())) {
				 
				 List<Advancement> advList = List.of(adv);
				 
				 for(UUID players : mainInstance.advManager.getPlayers()) {
					  
					 CheckSavingAdvancements(advList, players);
				  }
			 }
		}
   }
	
   public static void CheckAdvancementsPlayer(List<Advancement> advs, Player p) { 
	   
	   for(Advancement advancement : advs) {
		   
		   if(ConfigFile.getString(mainInstance.advconfig, advancement.getName() + "." + p.getUniqueId().toString()) != null) {
				 
				 if(ConfigFile.getString(mainInstance.advconfig, advancement.getName() + "." + p.getUniqueId().toString()).equalsIgnoreCase(p.getName())) {
					 
					 List<Advancement> advList = List.of(advancement);
					 
					 for(UUID players : mainInstance.advManager.getPlayers()) {
						  
						 CheckSavingAdvancements(advList, players);
					  }
				 }
			}
	   }
   }
   
   
	
   private static void CheckSavingAdvancements(List<Advancement> advs, UUID uuid) {

		  Player p = Bukkit.getServer().getPlayer(uuid);

		  for(Advancement advancement : advs) {
			  
			 if(ConfigFile.getString(mainInstance.advconfig, advancement.getName() + "." + p.getUniqueId().toString()) != null) {
				 
				 if(ConfigFile.getString(mainInstance.advconfig, advancement.getName() + "." + p.getUniqueId().toString()).equalsIgnoreCase(p.getName())) {
					 
					 mainInstance.advManager.addAdvancement(p, advancement);
				   
					 mainInstance.advManager.loadProgress(p, advancement);
					 mainInstance.advManager.updateProgress(p, advancement);
					 mainInstance.advManager.saveProgress(p, advancement.getName().getNamespace());
				 } 
			 }
		  }

	   }
		   
   
   private static void CheckUnSavingAdvancements(List<Advancement> advs, UUID uuid) {

	 Player p = Bukkit.getServer().getPlayer(uuid);

	 for(Advancement advancement : advs) {
		 
		mainInstance.advManager.removeAdvancement(p, advancement);
		
		mainInstance.advManager.unloadProgress(p.getUniqueId(), advancement);
		mainInstance.advManager.updateProgress(p, advancement);
	
	  }
   }
   /********************************************************/
   /* MÉTHODES POUR VÉRIFIER ET SAUVEGARDER L'ACHIEVEMENT  */
   /********************************************************/
}

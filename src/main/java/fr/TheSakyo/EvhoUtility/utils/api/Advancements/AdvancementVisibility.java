package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import java.util.Arrays;

import org.bukkit.entity.Player;

public abstract class AdvancementVisibility {
	
	// Définit la visibilité en "ALWAYS" //
	public static final AdvancementVisibility ALWAYS = new AdvancementVisibility("ALWAYS") {
	// Définit la visibilité en "ALWAYS" //
		
		
		
    // Détecte si un achievement est visible //
	@Override
	public boolean isVisible(Player player, Advancement advancement) { return true; }
	// Détecte si un achievement est visible //
		
	
	
	// Définit la visibilité en d'autres types (PARENT_GRANT, VANILLA, HIDDEN) //
		
	}, PARENT_GRANTED = new AdvancementVisibility("PARENT_GRANTED") {
		
		@Override
		public boolean isVisible(Player player, Advancement advancement) {
			
		  if(advancement.isGranted(player)) return true;
			
		  Advancement parent = advancement.getParent();
		  return parent == null || parent.isGranted(player);
		}
		
		
		
	}, VANILLA = new AdvancementVisibility("VANILLA") {
		
		@Override
		public boolean isVisible(Player player, Advancement advancement) {
			
		  if(advancement.isGranted(player)) return true;
			
		  Advancement parent = advancement.getParent();
			
		  if(parent != null && !parent.isGranted(player)) {
			  
			 Advancement grandParent = parent.getParent();
				
			 return grandParent == null || grandParent.getParent() == null || grandParent.isGranted(player);
		  }
			
		  return true;
		}
		
		
		
	}, HIDDEN = new AdvancementVisibility("HIDDEN") {
		
		@Override
		public boolean isVisible(Player player, Advancement advancement) { return advancement.isGranted(player); }
	};
	
	// Définit la visibilité en d'autres types (PARENT_GRANT, VANILLA, HIDDEN) //
	
	
	
	// Variable utile pour définir le nom de la visibilité //
	private final String name;
	// Variable utile pour définir le nom de la visibilité //
	
	
	
	// Méthodes pour récupérer une visibilité avec la variable "name" //
	public AdvancementVisibility() { name = "CUSTOM"; }
	
	private AdvancementVisibility(String name) { this.name = name; }
	// Méthodes pour récupérer une visibilité avec la variable "name" //
	
	
	
	
	/**
	 * 
	 * @param player Player à vérifier
	 * @param advancement achievement à vérifier
	 * @return true si un achievements doit être visible
	 */
	public abstract boolean isVisible(Player player, Advancement advancement);
	
	
	
	/**
	 * 
	 * @return true si un achievements doit toujours être visible si un enfant doit être visible, la valeur par défaut est true.
	 */
	public boolean isAlwaysVisibleWhenAdvancementAfterIsVisible() { return true; }
	
	
	
	/**
	 * 
	 * @return Nom personnalisé, uniquement pour les visibilités prédéfinies : {@link #ALWAYS}, {@link #PARENT_GRANTED}, {@link #VANILLA}, {@link #HIDDEN}
	 */
	public String getName() { return name; }
	
	
	
	/**
	 * Analyse une visibilité
	 * 
	 * @param name Nom de la visibilité
	 * @return Une visibilité avec une correspondance {@link #getName()} ou {@link #VANILLA}
	 */
	public static AdvancementVisibility parseVisibility(String name) {
		
		for(AdvancementVisibility visibility : Arrays.asList(ALWAYS, PARENT_GRANTED, VANILLA, HIDDEN)) {
			
		  if(visibility.getName().equalsIgnoreCase(name)) { return visibility; }
		}
		
	  return VANILLA;
	}
	
}
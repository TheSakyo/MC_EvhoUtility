package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import net.minecraft.network.chat.Component;

public class JSONMessage {
	
	// Variable pour récupérer le caractçre "json" //
	private final String json;
	// Variable pour récupérer le caractçre "json" //
	
	
	
	
	/**
	 * 
	 * @param json Une représentation JSON d'un message en jeu {@link <a href="https://github.com/skylinerw/guides/blob/master/java/text%20component.md">...</a>}
	 */
	public JSONMessage(String json) { this.json = json; }
	
	
	
	/**
	 * 
	 * @return la représentation JSON d'un message en jeu
	 */
	public String getJson() { return json; }
	
	
	
	/**
	 * 
	 * @return Une représentation {@link Component} d'un message en jeu.
	 */
	public Component getBaseComponent() { return Component.Serializer.fromJson(json); }
	
	
	
	/**
	 * 
	 * @return l'objet json en format "string"
	 */
	@Override
	public String toString() { return json; }
	
}
package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import net.minecraft.network.chat.Component;

/**
 * @param json Variable pour récupérer le caractère "json" //
 */
public record JSONMessage(String json) {


	/**
	 * @return la représentation JSON d'un message en jeu
	 */
	@Override
	public String json() { return json; }


	/**
	 * @return Une représentation {@link Component} d'un message en jeu.
	 */
	public Component getBaseComponent() { return Component.Serializer.fromJson(json); }


	/**
	 * @return l'objet json en format "string"
	 */
	@Override
	public String toString() { return json; }
}
package fr.TheSakyo.EvhoUtility.utils.api.Advancements.exception;

import java.io.Serial;
import java.util.UUID;

public class UnloadProgressFailedException extends RuntimeException {

	// Variables Utiles //
	
	@Serial
	private static final long serialVersionUID = 5052062325162108824L;
	
	private final UUID uuid;
	private String message = "Impossible de d�charger la progression des joueurs en ligne !";
	
	// Variables Utiles //
	
	
	
	// Méthode d'éxception lorsque qu'il y a un échec de la progression du déchargement //
	
	public UnloadProgressFailedException(UUID uuid) { this.uuid = uuid; }
	
	
	public UnloadProgressFailedException(UUID uuid, String message) {
	
	  this.uuid = uuid;
	  this.message = message;
	}
	
	// Méthode d'éxception lorsque qu'il y a un échec de la progression du déchargement //
	
	
	
	
	// Récupère le message d'erreur //
	
	@Override
	public String getMessage() {
	
	  return "Impossible de décharger la progression d'un achievement du joueur avec l'UUID. " + uuid + ": " + message;
	}
	
	// Récupère le message d'erreur //
	
	
}

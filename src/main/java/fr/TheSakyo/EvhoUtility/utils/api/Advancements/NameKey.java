package fr.TheSakyo.EvhoUtility.utils.api.Advancements;

import net.minecraft.resources.ResourceLocation;

public class NameKey {
	
	// Variables utiles //
	
	private final String namespace;
	private final String key;
	
	private transient ResourceLocation mcKey;
	
	// Variables utiles //
	
	
	
	/**
	 * 
	 * @param namespace L'espace de nom, choisissez quelque chose qui représente votre plugin/projet.
	 * @param key La clé unique dans votre espace de nom.
	 */
	public NameKey(String namespace, String key) {
		
	  this.namespace = namespace.toLowerCase();
	  this.key = key.toLowerCase();
	}
	
	
	
	/**
	 * 
	 * @param key La clé dans l'espace de noms par défaut "minecraft" ou un NameSpacedKey séparé par un deux-points
	 */
	public NameKey(String key) {
		
		String[] split = key.split(":");
		
		if(split.length < 2) {
			
		   this.namespace = "minecraft";
		   this.key = key.toLowerCase();
			
		} else {
			
		  this.namespace = split[0].toLowerCase();
		  this.key = key.replaceFirst(split[0] + ":", "").toLowerCase();
		}
	}
	
	
	
	/**
	 * Génère une {@link NameKey}
	 * 
	 * @param from de
	 */
	public NameKey(ResourceLocation from) {
		
	  this.namespace = from.getNamespace().toLowerCase();
	  this.key = from.getPath().toLowerCase();
	}
	
	
	
	/**
	 * 
	 * @return L'espace de nom
	 */
	public String getNamespace() { return namespace; }
	
	
	
	/**
	 * 
	 * @return La clé
	 */
	public String getKey() { return key; }
	
	
	
	/**
	 * Compare à une autre clé
	 * 
	 * @param anotherNameKey Clé de nom à comparer à
	 * @return true si les deux NameKeys correspondent l'une à l'autre
	 */
	public boolean isSimilar(NameKey anotherNameKey) { 
		
	  return namespace.equals(anotherNameKey.getNamespace()) && key.equals(anotherNameKey.getKey()); 
	}
	
	
	
	/**
	 * 
	 * @return A {@link ResourceLocation()} représentation de cette clé de nom
	 */
	public ResourceLocation getResourceLocation() {
		
	  if(mcKey == null) mcKey = new ResourceLocation(namespace, key);
	  return mcKey;
	}
	
	
	
	/**
	 * 
	 * @return si l'objet est égal à
	 */
	@Override
	public boolean equals(Object obj) { return isSimilar((NameKey) obj); }
	
	
	
	/**
	 * 
	 * @return l'objet en format "string"
	 */
	@Override
	public String toString() { return namespace + ":" + key; }
	
}
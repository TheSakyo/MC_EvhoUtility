package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;

import java.util.Iterator;

import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderHelper {

	/****************************************************************************************/
	/* MÉTHODE POUR SUPPRIMER TOUTES LES INFORMATIONS DÉJÀ ÉXISTANTE DANS L'ITEM DE LA MAP */
	/***************************************************************************************/

	public static MapView resetRenderers(MapView map) {
		
		final Iterator<MapRenderer> iterator = map.getRenderers().iterator();
		
		while(iterator.hasNext()) { map.removeRenderer(iterator.next()); }
		
		return map;
	}

	/****************************************************************************************/
	/* MÉTHODE POUR SUPPRIMER TOUTES LES INFORMATIONS DÉJÀ ÉXISTANTE DANS L'ITEM DE LA MAP */
	/***************************************************************************************/
}

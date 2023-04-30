package fr.TheSakyo.EvhoUtility.utils.api.ImageMaps;


import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class RenderHelper {

	/****************************************************************************************/
	/* MÉTHODE POUR SUPPRIMER TOUTES LES INFORMATIONS DÉJÀ ÉXISTANTE DANS L'ITEM DE LA MAP */
	/***************************************************************************************/

	public static MapView resetRenderers(MapView map) {

        for(MapRenderer mapRenderer : map.getRenderers()) map.removeRenderer(mapRenderer);
		return map;
	}

	/****************************************************************************************/
	/* MÉTHODE POUR SUPPRIMER TOUTES LES INFORMATIONS DÉJÀ ÉXISTANTE DANS L'ITEM DE LA MAP */
	/***************************************************************************************/
}

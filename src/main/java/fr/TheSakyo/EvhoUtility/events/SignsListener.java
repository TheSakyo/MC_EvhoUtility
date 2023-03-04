package fr.TheSakyo.EvhoUtility.events;

import java.util.List;

import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import fr.TheSakyo.EvhoUtility.UtilityMain;

public class SignsListener implements Listener {
	
	/* Récupère la class "Main" */
	private UtilityMain main;
	public SignsListener(UtilityMain pluginMain) { this.main = pluginMain; }
	/* Récupère la class "Main" */
	
	
	
	// Évenement quand le joueur écrit dans une pancarte //
	// Traduit les codes couleurs Minecraft, s'il a la permission //
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {

		Player player = e.getPlayer();
		List<Component> linesList = e.lines();

		for(int i = 0; i < linesList.size(); i++) {

			Component line = linesList.get(i);
			String lineString = CustomMethod.ComponentToString(line);

			// Convertie le message en question à envoyer dans la pancarte
			String messageConverted = player.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(lineString) : lineString;
			
			if(e.getPlayer().hasPermission("evhoutility.signscolor")) lineString = ColorUtils.format(lineString);
			e.line(i, CustomMethod.StringToComponent(lineString));
		}
	}
	// Évenement quand le joueur écrit dans une pancarte //
	// Traduit les codes couleurs Minecraft, s'il a la permission //
}

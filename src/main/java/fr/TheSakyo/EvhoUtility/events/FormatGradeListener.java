package fr.TheSakyo.EvhoUtility.events;


import dependancies.emoji4j.EmojiUtils;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.entity.player.PlayerEntity;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.event.node.NodeAddEvent;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.model.user.User;

/*****************************************************************/
/* PARTIE EVENEMENT POUR ACTUALISER LE FORMAT DU GRADE DU JOUEUR */ 
/*****************************************************************/
public class FormatGradeListener implements Listener {
	
	/* Récupère la class "Main" + un évènement compatible LuckPerms */
    UtilityMain main;
    public FormatGradeListener(UtilityMain pluginMain, LuckPerms luckperms) { 
    	
    	//Récupération class "Main"
        this.main = pluginMain;

        //Récupération de chargement d'évènement LuckPerms
        EventBus eventBus = luckperms.getEventBus();

        //Récupération de l'évenement "OnUserLoad"
        eventBus.subscribe(NodeAddEvent.class, this::onUserLoad);
    }
    /* Récupère la class "Main" + un évènement compatible LuckPerms */
    
 
  /*****************************************************************/
  /* PARTIE EVENEMENT POUR ACTUALISER LE FORMAT DU GRADE DU JOUEUR */ 
  /*****************************************************************/
    
     // Evenement quand le joueur charge de grade (Compatible LuckPerms) [Niveau "Tablist", Au-Dessus de la Tête] //
     public void onUserLoad(NodeAddEvent e) {

		 if(!e.isUser()) { return; }

		 User user = (User)e.getTarget();
		 Node node = e.getNode();

		 Player player = Bukkit.getServer().getPlayer(user.getUniqueId());

		 /* -------------------------------------------------- */

		 PlayerEntity playerEntity = new PlayerEntity(player);

		 String userPrefixGroup = user.getCachedData().getMetaData().getPrefix();
		 Group userGroup = main.luckapi.getGroupManager().getGroup(user.getCachedData().getMetaData().getPrimaryGroup());

		 main.formatgrade.playersGroupPrefix.putIfAbsent(user, userPrefixGroup);
		 main.formatgrade.playersGroup.putIfAbsent(user, userGroup);

		 /* -------------------------------------------------- */

		 if(!main.formatgrade.playersGroupPrefix.get(user).equalsIgnoreCase(userPrefixGroup)) { main.formatgrade.playersGroupPrefix.replace(user, userPrefixGroup); }
		 if(main.formatgrade.playersGroup.get(user) != userGroup) { main.formatgrade.playersGroup.replace(user, userGroup); }

		 playerEntity.updateName(null);  // Recharge le Nom Customisé du Joueur
		 playerEntity.updateGroupTeam();  // Recharge la Team des Joueurs

		 /* -------------------------------------------------- */
     }
     // Evenement quand le joueur charge de grade (Compatible LuckPerms) [Niveau "Tablist", Au-Dessus de la Tête] //

    								/* ---------------------------------- */
	
     // Évenement lorsque le parle dans le tchat, le format du tchat s'adapte avec le format configurer dans le fichier "chatformat.yml" //
     @EventHandler(priority = EventPriority.HIGH)
	 public void onChat(AsyncChatEvent e) {

		Player p = e.getPlayer(); // Récupère le Joueur en question

		Component messageChat = null; // Permettra de récupérer le Message du Chat Formaté

		User user = CustomMethod.getLuckPermUserOffline(p.getUniqueId()); // Récupère l'Utilisateur LuckPerm du Joueur

		// Récupère le Grade du Joueur en question (Préfix du Joueur)
    	String gradePrefix = user.getCachedData().getMetaData().getPrefix();

		// Récupère la Couleur du Grade du Joueur (Suffix du Joueur)
    	String gradeColor = user.getCachedData().getMetaData().getSuffix();

							/* ---------------------------- */

		//Si le Grade du Joueur (Préfix du Joueur) est 'NULL', on remplace alors par '**'
		if(gradePrefix == null) gradePrefix = "**";

		//Si laCouleur du Grade du Joueur (Suffix du Joueur) est 'NULL', on remplace alors par le Code Couleur 'RESET'
		if(gradeColor == null) gradeColor = ChatColor.RESET.toString();

							/* ---------------------------- */

							/* ---------------------------- */

		// Récupère le Nom Customisé du Joueur
		String customName = ColorUtils.format(gradeColor + CustomMethod.ComponentToString(p.customName()));

		// Récupère le Nom Customisé du Joueur avec des actions au 'hover' et au 'click' //
		Component customNameComponent = CustomMethod.StringToComponent(customName)
					.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour mentionner le joueur")))
					.clickEvent(ClickEvent.suggestCommand("@" + p.getName()));
		// Récupère le Nom Customisé du Joueur avec des actions au 'hover' et au 'click' //

		// On Convertit dans le Fichier de Configuration de Formatage du Chat 'chatformat.yml', les informations en '%' par des valeurs précise.
		String ConvertVar = ConfigFile.getString(main.chatconfig, "chat_format").replaceAll("%prefix%", gradePrefix).replaceAll("%player%", customName);

		// Récupère les informations convertit en Type 'Component' + remplace le nom du joueur par celui-ci avec les actions au 'hover' et au 'click' //
		Component format = CustomMethod.StringToComponent(ColorUtils.format(ConvertVar + ChatColor.RESET.toString()))
							.replaceText(b -> b.matchLiteral(customName).replacement(customNameComponent));
		// Récupère les informations convertit en Type 'Component' + remplace le nom du joueur par celui-ci avec les actions au 'hover' et au 'click' //


		// Convertie le message a envoyé
		String messageConverted = p.hasPermission("evhoutility.emojify") ? EmojiUtils.emojify(CustomMethod.ComponentToString(e.message())) : CustomMethod.ComponentToString(e.message());

		// * ⬇️ * -- Vérifie si le joueur à la permision pour lui permettre d'écrire avec les codes couleurs ou pas -- * ⬇️ * //
		if(p.hasPermission("evhoutility.chatcolor")) messageChat = CustomMethod.StringToComponent(ColorUtils.format(messageConverted));
		else messageChat = e.message();
		// * ⬆️ * -- Vérifie si le joueur à la permision pour lui permettre d'écrire avec les codes couleurs ou pas -- * ⬆️ * //

		e.message(messageChat); // Redéfinit le message

		/*** ⬇️ Définit le rendu du Message et l'applique au Chat ⬇️ ***/
		 ChatRenderer renderer = ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> Component.translatable("%s %s", format, e.message()));
		 e.renderer(renderer); // Définit le nouveau Message en Rendu
		/*** ⬆️ Définit le rendu du Message et l'applique au Chat ⬆️ ***/
	 }
    // Évenement lorsque le parle dans le tchat, le format du tchat s'adapte avec le format configurer dans le fichier "chatformat.yml" //

 /*****************************************************************/
 /* PARTIE EVENEMENT POUR ACTUALISER LE FORMAT DU GRADE DU JOUEUR */ 
 /*****************************************************************/
     
}


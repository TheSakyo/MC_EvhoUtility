package fr.TheSakyo.EvhoUtility.utils.entity.player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Pair;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.ScoreboardManager;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.TeamPlayer;
import fr.TheSakyo.EvhoUtility.utils.entity.player.utilities.Skin;
import fr.TheSakyo.EvhoUtility.utils.reflections.RemapReflection;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import io.netty.buffer.Unpooled;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.minecraft.ChatFormatting;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.storage.PlayerDataStorage;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R1.CraftServer;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

public class PlayerEntity extends CraftPlayer {

				/* ------------------------------------------------- */

    private static final UtilityMain mainInstance = UtilityMain.getInstance(); // Instance de la 'Class' "Main"


				/* ------------------------------------------------- */

	private String playerCustomName; // Variable permettant de récupérer le Nom Custom du Joueur
	final private String playerActualName; // Variable permettant de récupérer le Nom Par défaut du Joueur
	final private UUID playerUUID; // Variable permettant de récupérer l'UUID du Joueur
	final private Player player; // Variable permettant de récupérer le Joueur
	final private ServerPlayer playerInstance; // Variable permettant de récupérer le Joueur Instancié de 'ServerPlayer'


	private static Player receiver; // Variable permettant de récupérer le Joueur qui recevra les packets

				/* ------------------------------------------------- */

	/************************************/
	/* MÉTHODE CONSTRUCTEUR DE LA CLASS */
	/***********************************/
	public PlayerEntity(Player player) {

		super((CraftServer)Bukkit.getServer(), ((CraftPlayer)player).getHandle());

		this.playerUUID = player.getUniqueId();
		this.playerInstance = ((CraftPlayer)player).getHandle();
		this.playerActualName = Bukkit.getPlayer(this.playerUUID).getName();
		this.player = player;

		if(player.customName() == null) { player.customName(CustomMethod.StringToComponent(this.playerActualName)); }
		if(this.playerCustomName  == null) this.playerCustomName = ColorUtils.format(CustomMethod.ComponentToString(player.customName()));
	}
	/************************************/
	/* MÉTHODE CONSTRUCTEUR DE LA CLASS */
	/***********************************/


	/****************************************************************************/
	/* Récupère la class en question en précisant celui qui recevra les packets */
	/****************************************************************************/

	/**
	 * Retourne une instance de {@link PlayerEntity} en précisant le Joueur qui verra les modifications du {@link PlayerEntity joueur en question}
	 *
	 * @param playerReceiver Le Joueur qui verra les modifications.
	 *
	 * @return Une instance {@link PlayerEntity} du Joueur qui recoil les modifications.
	 */
	public PlayerEntity forReceiver(Player playerReceiver) {

		receiver = playerReceiver;
		return this;
	}
	/****************************************************************************/
	/* Récupère la class en question en précisant celui qui recevra les packets */
	/****************************************************************************/

				/* ------------------------------------------------- */
	/**
	 * Récupère le Nom Custom du Joueur
	 *
	 * @return Le Nom Customisé du Joueur
	 */
	public String getActualCustomName() { return this.playerCustomName; }

	/**
	 * Récupère Véritable Nom du Joueur
	 *
	 * @return Le Véritable Nom du Joueur
	 */
	final public String getActualName() { return this.playerActualName; }

	/**
	 * Récupère l'{@link UUID} du Joueur
	 *
	 * @return L'{@link UUID} du Joueur
	 */
	final public UUID getActualUUID() { return this.playerUUID; }

	/**
	 * Récupère le {@link Player Joueur} avec {@link Bukkit}
	 *
	 * @return Le {@link Player Joueur} avec {@link Bukkit}
	 */
	final public Player getPlayer() { return this.player; }

	/**
	 * Récupère le {@link ServerPlayer Joueur} avec le NMS - Minecraft (Utilise l'API PAPERMC)
	 *
	 * @return Le {@link ServerPlayer Joueur} avec le NMS - Minecraft (Utilise l'API PAPERMC)
	 */
	final public ServerPlayer getPlayerInstance() { return this.playerInstance; }

				/* ------------------------------------------------- */

	/*********************************************************************************************************************************/
	/* MÉTHODE POUR RECHARGER LE GRADE DU JOUEUR, SON 'NICKNAME' (NOM CUSTOM), SON SCOREBOARD ET SA TEAM EN FONCTION DES PARAMÉTRÉS */
	/********************************************************************************************************************************/

	/**
	 * Met à jour le nom, le tableau de bord, le profil et l'équipe du groupe du joueur.
	 *
	 * @param name Le nom du Joueur à afficher (Tablist, Nom personnalisé, Chat)
	 * @param changeProfile Si vrai, le profil du joueur sera modifié.
	 * @param updateProfile Si vrai, le profil du joueur sera mis à jour.
	 * @param updateScoreBoard Si vrai, le tableau de bord sera mis à jour.
	 */
	public void update(Component name, boolean changeProfile, boolean updateProfile, boolean updateScoreBoard) {

				/* ----------------------------------------------------- */

		this.updateName(name); // Change le Nom d'Affichage du Joueur (Tablist, Nom Customisé, Chat)

		if(updateScoreBoard) ScoreboardManager.makeScoreboard(this.player, false); // Créer/Actualise le Scoreboard, si 'updateScoreboard' est "Vrai"

		// On vérifie si 'changeProfil est "Vrai", alors, on essaie de changer le profil du Joueur ou/et son skin //
		if(changeProfile) {

			// Essaie de changer le Profil de Jeux du Joueur en question, si 'updateProfile est "Vrai", Sinon, on retourne une Exception //
			try { this.changePlayerProfile(updateProfile); } // Essaie le changement de profil de jeux
			catch(Exception e) { e.printStackTrace(System.err); } // Retourne une Exception
			// Essaie de changer le Profil de Jeux du Joueur en question, si 'updateProfile est "Vrai", Sinon, on retourne une Exception //
		}
		// On vérifie si 'changeProfil est "Vrai", alors, on essaie de changer le profil du Joueur ou/et son skin //

		this.updateGroupTeam(); // Recharge la 'Team' du Joueur

				/* ----------------------------------------------------- */
	}

	/*********************************************************************************************************************************/
	/* MÉTHODE POUR RECHARGER LE GRADE DU JOUEUR, SON 'NICKNAME' (NOM CUSTOM), SON SCOREBOARD ET SA TEAM EN FONCTION DES PARAMÉTRÉS */
	/********************************************************************************************************************************/

							/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/

	/***************************************************************************************************/
	/* PETITE MÉTHODE PERMETTANT DE RECHARGE LE NOM DU JOUEUR AFFICHÉ (Tablist, Nom Customisé & Chat) */
	/*************************************************************************************************/

	/**
	 * Permet de changer le nom customisé du Joueur.
	 *
	 * @param name Le nom personnalisé du Joueur.
	 */
	public void updateName(Component name) {

		// Récupère l'Utilisateur LuckPerms du Joueur en question
		User user = CustomMethod.getLuckPermUserOffline(this.getActualUUID());

					/* ----------------------------------------------- */

		// Récupère la Couleur du Grade du Joueur (Suffix du Joueur)
		String gradeColor = user.getCachedData().getMetaData().getSuffix();

					/* ----------------------------------------------- */

		// ⬇️ Si la Variable "name" est 'NULL', on vérifie le Nom Customisé, sinon, on vérifie Affiche le Nom Par Défaut ⬇️ //
		if(name == null) {

			if(this.playerCustomName == null) name = CustomMethod.StringToComponent(this.playerActualName);
			else name = CustomMethod.StringToComponent(this.playerCustomName);
		}
		// ⬆️ Si la Variable "name" est 'NULL', on vérifie le Nom Customisé, sinon, on vérifie Affiche le Nom Par Défaut ⬆️ //

		// Récupère le Nom en Paramètre convertit en Chaîne de Caractère.
		String nameConverted = CustomMethod.ComponentToString(name);

		// Si la Variable "name" ne se finit pas par le Code Couleur 'RESET' on lui rajoute
		if(!nameConverted.endsWith(String.valueOf(ChatFormatting.RESET))) { nameConverted += ChatFormatting.RESET; }

					/* ----------------------------------------------- */

		// Récupère le Nom Customisé convertit en 'Component' avec la Couleur en 'GRAS' de son Grade en préfix
		Component newNameWithColorBold = CustomMethod.StringToComponent(ColorUtils.format(gradeColor + ChatFormatting.BOLD + nameConverted));

		// Récupère le Nom Customisé convertit en 'Component'
		String newName = ColorUtils.format(nameConverted);

		this.player.playerListName(newNameWithColorBold); // Change le Nom du Joueur en question sur le 'Tablist'

		/* --------------------------------- */

		// ⬇️ Change le Nom Customisé du Joueur en question ⬇️ //
		this.player.customName(CustomMethod.StringToComponent(nameConverted));
		this.playerCustomName = ColorUtils.format(CustomMethod.ComponentToString(this.player.customName()));
		// ⬆️ Change le Nom Customisé du Joueur en question ⬆️ //
	}

	/***************************************************************************************************/
	/* PETITE MÉTHODE PERMETTANT DE RECHARGE LE NOM DU JOUEUR AFFICHÉ (Tablist, Nom Customisé & Chat) */
	/*************************************************************************************************/

							/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/

	/***************************************************************************************************************************/
	/* PETITE MÉTHODE STATIC PERMETTANT DE CHANGER LA TEAM DU SCOREBOARD DES JOUEURS EN FONCTION DE LEUR GROUPES 'en packets' */
	/**************************************************************************************************************************/

	/**
	 * Met à jour l'équipe du groupe du joueur <i>(Affichage du Grade en préfix sur son nom en haut de sa tête)</i>
	 */
	public void updateGroupTeam() {

		TeamPlayer.loadTeams(); // Recharge tous les Teams

		// ⬇️ On boucle sur les groupes enregistrés dans des Teams ⬇️ //
		TeamPlayer.groupTeams.keySet().forEach(group -> {

			PlayerTeam groupTeam = TeamPlayer.groupTeams.get(group); // Récupère la team du Groupe (Grade) en question

			// Si le Joueur Connecté est dans une team du Groupe (Grade) en question, on lui enlève alors son Nom de la team.
            groupTeam.getPlayers().remove(this.playerActualName);
			/*if(groupTeam.getPlayers().contains(this.playerCustomName)) groupTeam.getPlayers().add(this.playerCustomName);*/

			sendPacket(ClientboundSetPlayerTeamPacket.createRemovePacket(groupTeam), receiver); // Packets de suppression de la 'Team'
			sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(groupTeam, true), receiver); // Packets de mise à jour de la 'Team'
		});
		// ⬆️ On boucle sur les groupes enregistrés dans des Teams ⬆️ //

									/* --------------------------------------------- */

		User onlineUser = CustomMethod.getLuckPermUserOffline(this.player.getUniqueId()); // Récupère l'Utilisateur LuckPerms en fonction du Groupe (Grade) du Joueur Connecté
		Group userGroup = mainInstance.luckApi.getGroupManager().getGroup(onlineUser.getCachedData().getMetaData().getPrimaryGroup()); // Récupère le Groupe (Grade) du Joueur en question


		// Vérifie si le Groupe (Grade) du joueur est dans une team, si c'est le cas, on ajoute donc le Joueur à la team
		if(TeamPlayer.groupTeams.containsKey(userGroup)) {

			PlayerTeam playerTeam = TeamPlayer.groupTeams.get(userGroup); // Récupère la team du Groupe (Grade) en question du Joueur actuel

			// Si le Joueur Connecté n'est pas dans une team de son Groupe (Grade) en question, on lui ajoute ainsi son Nom de la team.
			if(!playerTeam.getPlayers().contains(this.playerActualName)) playerTeam.getPlayers().add(this.playerActualName);
			/*if(!playerTeam.getPlayers().contains(this.playerCustomName)) playerTeam.getPlayers().add(this.playerCustomName);*/


			sendPacket(ClientboundSetPlayerTeamPacket.createRemovePacket(playerTeam), receiver); // Packets de suppression de la 'Team'
			sendPacket(ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(playerTeam, true), receiver); // Packets de mise à jour de la 'Team'
		}
	}

	/***************************************************************************************************************************/
	/* PETITE MÉTHODE STATIC PERMETTANT DE CHANGER LA TEAM DU SCOREBOARD DES JOUEURS EN FONCTION DE LEUR GROUPES 'en packets' */
	/**************************************************************************************************************************/

				/* ------------------------------------------------------------------------- */
				/* ------------------------------------------------------------------------- */
							/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/
				/* ------------------------------------------------------------------------- */
				/* ------------------------------------------------------------------------- */

    /*************************************************************************************************/
	/* PETITE MÉTHODE PRIVÉE PERMETTANT DE CHANGER LE NOM AU DESSUS DE LA TÊTE DU JOUEUR/DES JOUEURS */
	/************************************************************************************************/

	/**
	 * Modifie le Profil de Jeux du joueur en envoyant des paquets aux joueurs en ligne
	 *
	 * @param changeGameProfile Si vrai, le joueur aura un profil de jeux personnalisé <i>(Utile pour Affichage du Nom Custom ou Skin)</i>.
	 */
	private void changePlayerProfile(boolean changeGameProfile) {

		/* ------------------------------------------------------ *//* ------------------------------------------------------ */

				/* ------------------------------------------ *//* ------------------------------------------ */

		// Récupère les Informations 'packets' du Joueur en question
		List<Object> playerUpdated = this.updatePlayer(this.playerActualName, this.playerCustomName, true, changeGameProfile);

		final org.bukkit.inventory.ItemStack[] inventory = this.player.getInventory().getContents(); // Récupère le contenu de l'inventaire du Joueur



				/* ------------------------------------------ *//* ------------------------------------------ */

		/* ⬇️ Récupère les différents 'Packets' du Joueur en question [Voir updatePlayerProfile()] ⬇️ */

		ClientboundRemoveEntitiesPacket destroyPlayer = (ClientboundRemoveEntitiesPacket)playerUpdated.get(0);
		ClientboundPlayerInfoRemovePacket removeInfoPlayer = (ClientboundPlayerInfoRemovePacket)playerUpdated.get(1);
		ClientboundPlayerInfoUpdatePacket addInfoPlayer = (ClientboundPlayerInfoUpdatePacket)playerUpdated.get(2);
		ClientboundRespawnPacket respawnPlayer = (ClientboundRespawnPacket)playerUpdated.get(3);
		ClientboundTeleportEntityPacket teleportPlayer = (ClientboundTeleportEntityPacket)playerUpdated.get(4);
		ClientboundAddPlayerPacket createPlayer = (ClientboundAddPlayerPacket)playerUpdated.get(5);
		ClientboundSetEntityDataPacket metadataPlayer = (ClientboundSetEntityDataPacket)playerUpdated.get(6);
		ClientboundRotateHeadPacket rotatePlayer = (ClientboundRotateHeadPacket)playerUpdated.get(7);
		ClientboundSetEquipmentPacket equipmentPlayer = (ClientboundSetEquipmentPacket)playerUpdated.get(8);

		/* ⬆️ Récupère les différents 'Packets' du Joueur en question [Voir updatePlayerProfile()] ⬆️ */

				/* ------------------------------------------ *//* ------------------------------------------ */


		Skin skin; // Utile pour récupérer un Skin et le charger au Joueur

		// Récupère le Skin du Joueur dans le fichier de configuration 'playerSkin.yml'
		String skinName = ConfigFile.getString(mainInstance.playerSkinConfig, "SKIN." + this.getActualUUID().toString());

		/* Si le Nom du Skin éxiste bien dans le fichier de configuration 'playerSkin.yml' et qu'il porte bien le Nom Récupéré :
		   Alors, on récupère le Skin du Joueur ayant le Nom Customisé. */
		if((skinName != null && !skinName.isBlank()) && skinName.equalsIgnoreCase(this.playerCustomName)) { skin = Skin.get(ChatFormatting.stripFormatting(this.playerCustomName)); }

		/*  Sinon, Si le Nom du Skin éxiste bien dans le fichier de configuration 'playerSkin.yml' et qu'il porte bien le Nom Récupéré :
			Alors, on récupère le Nom du Skin enregistré en question */
		else if((skinName != null && !skinName.isBlank()) && !skinName.equalsIgnoreCase(this.playerCustomName)) { skin = Skin.get(ChatFormatting.stripFormatting(skinName)); }

		// Sinon, on récupère le Skin du Joueur en question
		else skin = Skin.get(this.getActualUUID());

											/* --------------------------------------------------------- */

		PropertyMap playerProperties = this.playerInstance.gameProfile.getProperties(); // Récupère les propriétés du Joueur
		PropertyMap skinProperties = skin.getProfile().getProperties();

		playerProperties.removeAll("textures"); // Supprime les textures du Joueur
		skinProperties.put("textures", new Property("textures", skin.getValue(), skin.getSignature())); // Récupère la propriété de la Texture du Skin

		// Attribue la texture en question du Skin au Joueur directement
		this.playerInstance.gameProfile.getProperties().putAll("textures", skinProperties.get("textures"));

											/* --------------------------------------------------------- */

		// ⬇️ Vérifie si on envoie les packets à tous les joueurs en ligne ou à qu'un seul joueur en ligne ⬇️ //
		List<UUID> playersUUID = new ArrayList<>();

		if(receiver != null) { playersUUID.add(receiver.getUniqueId()); }
		else { Bukkit.getServer().getOnlinePlayers().forEach(onlinePlayer -> playersUUID.add(onlinePlayer.getUniqueId())); }
		// ⬆️ Vérifie si on envoie les packets à tous les joueurs en ligne ou à qu'un seul joueur en ligne ⬆️ //

		final Location oldLocation = this.player.getLocation(); // Récupère la localisation du Joueur

		// ⬇️ Ajoute aux Joueurs Connectés les différents Informations en 'packet' du Joueur en question ⬇️ //
		for(UUID onlineUUID : playersUUID) {

			Player online = Bukkit.getServer().getPlayer(onlineUUID); // Récupère le joueur en question par son UUID.

			// Si le Joueur actuel n'est pas le Joueur en question, on peut envoyer au Joueur actuel le 'packet' de déstruction du Joueur en question.
			if(online != this.player) sendPacket(destroyPlayer, online);
			sendPacket(removeInfoPlayer, online); // Envoie au Joueur actuel, le 'packet' de Suppression des Infos du Joueur en question.

			sendPacket(respawnPlayer, this.player); // Envoie au Joueur en question le 'packet' de respawn
			sendPacket(teleportPlayer, this.player); // Envoie au Joueur en question le 'packet' de téléportation

			this.player.getInventory().setContents(inventory); // Récupère l'inventaire du joueur

			sendPacket(addInfoPlayer, online); // Envoie au Joueur actuel, le 'packet' d'Ajout des Infos du Joueur en question.

			/* ⬇️ On vérifie si le Joueur actuel n'est pas dans la liste des Joueurs ayant l'Option pour cacher les autres Joueurs ;
				   Puis, on vérifie également si le Joueur actuel n'est pas le Joueur en question : Alors, on peut afficher au Joueur actuel
				   les packets pour afficher le Joueur en question. ⬇️ */
			if(!mainInstance.hidePlayers.containsKey(onlineUUID) && online != this.player) {

				sendPacket(createPlayer, online); // Envoie au Joueur actuel, le 'packet' de Création du Joueur en question.
				sendPacket(rotatePlayer, online); // Envoie au Joueur actuel, le 'packet' de Rotation de Tête du Joueur en question.
				sendPacket(equipmentPlayer, online); // Envoie au Joueur actuel, le 'packet' d'Équipement du Joueur en question.
			}
			/* ⬆️ On vérifie si le Joueur actuel n'est pas dans la liste des Joueurs ayant l'Option pour cacher les autres Joueurs ;
				   Puis, on vérifie également si le Joueur actuel n'est pas le Joueur en question : Alors, on peut afficher au Joueur actuel
				   les packets pour afficher le Joueur en question. ⬆️ */

			sendPacket(metadataPlayer, online); // Envoie au Joueur actuel, le 'packet' de MétaDonnée du Joueur en question.

			/* ------------------------------------------ *//* ------------------------------------------ */

			if(changeGameProfile) {


				// ⬇️ Essaie de remapper le Variable "name" du Profil de Jeux du Joueur récupérant Connecté ⬇️ //
				Class<?> remappedGameProfile = RemapReflection.remapClassName(GameProfile.class);
				String name = RemapReflection.remapFieldName(GameProfile.class, "name");
				// ⬆️ Essaie de remapper le Variable "name" du Profil de Jeux du Joueur récupérant Connecté ⬆️ //

				/* ------------------------------------------ */

				// ⬇️ Aprés 1 tick, on essaie de remettre le Nom du Joueur en question Par Défaut ⬇️ //
				new BukkitRunnable() {
					public void run() {

						// ⬇️ On Essaie de changer le Nom du Joueur en question par celui Customisé, Sinon on Affiche l'Erreur à la Console ⬇️ //
						try {

							// ⬇️ Récupère l'Attribut 'name' du Profil de Jeux du Joueur en question depuis son code source et la remplace ⬇️ //
							Field n = remappedGameProfile.getDeclaredField(name);
							n.setAccessible(true);
							n.set(playerInstance.gameProfile, playerActualName);
							// ⬆️ Récupère l'Attribut 'name' du Profil de Jeux du Joueur en question depuis son code source et la remplace ⬆️ //

						} catch(NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(System.err); }
						// ⬆️ On Essaie de changer le Nom du Joueur en question par celui Customisé, Sinon on Affiche l'Erreur à la Console ⬆️ //
					}
				}.runTaskLater(mainInstance, 1);
				// ⬆️ Aprés 1 tick, on essaie de remettre le Nom du Joueur en question Par Défaut ⬆️ //
			}
		}
		// ⬆️ Ajoute aux Joueurs Connectés les différents Informations en 'packet' du Joueur en question ⬆️ //

											/* ---------------------------------------------- */

		/* ------------------------------------------------------ *//* ------------------------------------------------------ */
	}

    /*************************************************************************************************/
	/* PETITE MÉTHODE PRIVÉE PERMETTANT DE CHANGER LE NOM AU DESSUS DE LA TÊTE DU JOUEUR/DES JOUEURS */
	/************************************************************************************************/

							/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/
							/* ---------------------------------------------------*/

    /****************************************************************************************************************/
	/* MÉTHODE PRIVÉE POUR RECHARGER/CHANGER ET RÉCUPÉRER LES INFORMATION (packets) DU JOUEUR (AVEC LE GAMEPROFILE) */
	/****************************************************************************************************************/

	/**
	 * Permet de renvoyer une liste de packet permettant de mettre à jour les informations du joueur (nom, skin, etc.) et d'envoyer les paquets au client.
	 *
	 * @param defaultName Le nom par défaut du joueur.
	 * @param customName Le nom customisé a affiché pour le joueur.
	 * @param changePacket Si vrai, les paquets seront envoyés aux autres Joueurs.
	 * @param changePlayerProfile Si vrai, le profil de jeux du joueur sera modifié.
	 *
	 * @return Renvoie une liste de paquets.
	 */
	private List<Object> updatePlayer(final String defaultName, String customName, boolean changePacket, boolean changePlayerProfile) {

		GameProfile playerProfile; // Utile pour créer un nouveau Profil de Jeux


		/* --------------- INITIALISE LES DIFFÉRENTS 'PACKETS' -------------- */
		ClientboundPlayerInfoRemovePacket removeInfoPlayer = null;
		ClientboundRemoveEntitiesPacket destroyPlayer;
		ClientboundPlayerInfoUpdatePacket addInfoPlayer;
		ClientboundRespawnPacket respawnEntity;
		ClientboundTeleportEntityPacket teleportEntity;
		ClientboundAddPlayerPacket createPlayer;
		ClientboundSetEntityDataPacket metadataPlayer;
		ClientboundRotateHeadPacket rotatePlayer;
		ClientboundSetEquipmentPacket equipmentPlayer;
		/* --------------- INITIALISE LES DIFFÉRENTS 'PACKETS' -------------- */


		/*ServerLevel worldPlayer = ((CraftWorld)this.player.getWorld()).getHandle().getLevel();*/
		byte Yaw = (byte)(this.player.getLocation().getYaw() * 256.0F / 360.0F); /* Calcul "Yaw" du Joueur */

					/* ----------------------------------------------- */

								/* ----------------------------- */

		// ⬇️ On enregistre le 'packet' pour supprimer les Informations du Joueur en question à en question si le paramètre 'changePacket' est "Vrai" ⬇️ //
		if(changePacket) {

			removeInfoPlayer = new ClientboundPlayerInfoRemovePacket(List.of(this.playerInstance.getUUID())); // 'packet' pour Supprimer les Informations du Joueur
		}
		// ⬆️ On enregistre le 'packet' pour supprimer les Informations du Joueur en question à en question si le paramètre 'changePacket' est "Vrai" ⬆️ //

								/* ----------------------------- */

		if(changePlayerProfile) {

			// ⬇️ Essaie de remapper le Variable "gameProfile" récupérant le Profil de Jeux du Joueur ⬇️ //
			Class<?> remappedPlayer = RemapReflection.remapClassName(this.playerInstance.getClass().getSuperclass());
			String gameProfile = RemapReflection.remapFieldName(this.playerInstance.getClass().getSuperclass(), "gameProfile");
			// ⬆️ Essaie de remapper le Variable "gameProfile" récupérant le Profil de Jeux du Joueur ⬆️ //

								/* ------------------------------------------------ */

			// Si, le paramètre 'name' est "NULL", on Créer un Profil de Jeux portant le Nom Par Défaut Définit.
			if(customName == null) { playerProfile = new GameProfile(this.getActualUUID(), ChatFormatting.stripFormatting(defaultName)); }

			// Sinon, on Créer un Profil de Jeux portant le Nom Customisé Définit.
			else { playerProfile = new GameProfile(this.getActualUUID(), ChatFormatting.stripFormatting(customName)); }

								/* ------------------------------------------------ */

			// ⬇️ On Essaie de changer le Profil de Jeux du Joueur par celui Customisé, Sinon on Affiche l'Erreur à la Console ⬇️ //
			try {

				// ⬇️ Récupère l'Attribut 'gameProfile' du Joueur depuis son code source et la remplace ⬇️ //
				Field gp = remappedPlayer.getDeclaredField(gameProfile);
				gp.setAccessible(true);
				gp.set(this.playerInstance, playerProfile);
				// ⬆️ Récupère l'Attribut 'gameProfile' du Joueur depuis son code source et la remplace ⬆️ //


			} catch(NoSuchFieldException | IllegalAccessException e) { e.printStackTrace(System.err); }
			// ⬆️ On Essaie de changer le Profil de Jeux du Joueur par celui Customisé, Sinon on Affiche l'Erreur à la Console ⬆️ //

		}
							/* ----------------------------------------------- */
							/* ------------ ENREGISTREMENT DES PACKET -------- */
							/* ----------------------------------------------- */

		// On enregistre les différents 'packet' du Joueur en question à en question si le paramètre 'changePacket' est "Vrai".
		if(changePacket) {

		         //~ -- * 'packet' | "Respawn" de l'Entité du Joueur * -- ~//
				respawnEntity = new ClientboundRespawnPacket(

					this.playerInstance.getCommandSenderWorld().getMinecraftWorld().dimensionTypeId(),
					this.playerInstance.getCommandSenderWorld().getMinecraftWorld().dimension(),
					BiomeManager.obfuscateSeed(this.playerInstance.getCommandSenderWorld().getMinecraftWorld().getSeed()),
					this.playerInstance.gameMode.getGameModeForPlayer(),
					this.playerInstance.gameMode.getPreviousGameModeForPlayer(),
					this.playerInstance.getCommandSenderWorld().getMinecraftWorld().isDebug(),
					this.playerInstance.getCommandSenderWorld().getMinecraftWorld().isFlat(),
					(byte)3,
					this.playerInstance.getLastDeathLocation(),
					this.playerInstance.getPortalCooldown()
				);
				//~ -- * 'packet' | "Respawn" de l'Entité du Joueur * -- ~//

									/* ----------------------------------------------- */


				// ~ -- * 'packet' | "Respawn" de l'Entité du Joueur * -- ~//
				teleportEntity = createDataSerializer(data -> {

					data.writeVarInt(this.playerInstance.getId());
					data.writeDouble(this.playerInstance.getX());
					data.writeDouble(this.playerInstance.getY());
					data.writeDouble(this.playerInstance.getZ());
					data.writeByte((byte)((int)(this.playerInstance.getYRot() * 256.0F / 360.0F)));
					data.writeByte((byte)((int)(this.playerInstance.getXRot() * 256.0F / 360.0F)));
					data.writeBoolean(this.playerInstance.onGround());
					return new ClientboundTeleportEntityPacket(data);
				});
				//~ -- * 'packet' | "Respawn" de l'Entité du Joueur * -- ~//
	
									/* ----------------------------------------------- */


				destroyPlayer = new ClientboundRemoveEntitiesPacket(this.playerInstance.getId()); // 'packet' pour Supprimer le Joueur
				addInfoPlayer = new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, this.playerInstance); // 'packet' pour Ajouter des Informations du Joueur
				createPlayer = new ClientboundAddPlayerPacket(this.playerInstance); // 'packet' pour Créer le Joueur

									/* ----------------------------------------------- */

				//~ -- * 'packet' | "MetaData" de l'Entité du Joueur * -- ~//

				SynchedEntityData synchedEntityData = this.playerInstance.getEntityData();
				synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte)127);
				metadataPlayer = new ClientboundSetEntityDataPacket(this.playerInstance.getId(), synchedEntityData.getNonDefaultValues());

				//~ -- * 'packet' | "MetaData" de l'Entité du Joueur * -- ~//

							/* ----------------------------- */

				//~ -- * 'packet' | Rotation de Tête du Joueur * -- ~//
				rotatePlayer = createDataSerializer(data -> {

					data.writeVarInt(this.playerInstance.getId());
					data.writeByte(Yaw);
					return new ClientboundRotateHeadPacket(data);
				});
				//~ -- * 'packet' | Rotation de Tête du Joueur * -- ~//

							/* ----------------------------- */

				//~ -- * 'packet' | Équipement du Joueur * -- ~//

				// Liste 'equipmentList' stockant l'Équipement du Joueur sur chaque 'slot' d'Équipement
				List<Pair<EquipmentSlot, ItemStack>> equipmentList = new ArrayList<>();

				// ⬇️ Récupère les différents Équipements du Joueur (Casque, Plastron, Jambière & Botte) ⬇️ //
				org.bukkit.inventory.ItemStack helmet = this.player.getInventory().getHelmet();
				org.bukkit.inventory.ItemStack chesplate = this.player.getInventory().getChestplate();
				org.bukkit.inventory.ItemStack leggings = this.player.getInventory().getLeggings();
				org.bukkit.inventory.ItemStack boots = this.player.getInventory().getBoots();
				// ⬆️ Récupère les différents Équipements du Joueur (Casque, Plastron, Jambière & Botte) ⬆️ //

				// ⬇️ Récupère les Items du Joueur sur ses mains (Main Principale & Main Secondaire) ⬇️ //
				org.bukkit.inventory.ItemStack mainHand = this.player.getInventory().getItemInMainHand();
				org.bukkit.inventory.ItemStack offHand = this.player.getInventory().getItemInOffHand();
				// ⬆️ Récupère les Items du Joueur sur ses mains (Main Principale & Main Secondaire) ⬆️ //

				// ⬇️ Ajoute à la Liste 'equipmentList' les différents Équipements du Joueur dans leur 'slot' respective ⬇️ //
				equipmentList.add(new Pair<>(EquipmentSlot.HEAD, CraftItemStack.asNMSCopy(helmet)));
				equipmentList.add(new Pair<>(EquipmentSlot.CHEST, CraftItemStack.asNMSCopy(chesplate)));
				equipmentList.add(new Pair<>(EquipmentSlot.LEGS, CraftItemStack.asNMSCopy(leggings)));
				equipmentList.add(new Pair<>(EquipmentSlot.FEET, CraftItemStack.asNMSCopy(boots)));

				equipmentList.add(new Pair<>(EquipmentSlot.MAINHAND, CraftItemStack.asNMSCopy(mainHand)));
				equipmentList.add(new Pair<>(EquipmentSlot.OFFHAND, CraftItemStack.asNMSCopy(offHand)));
				// ⬆️ Ajoute à la Liste 'equipmentList' les différents Équipements du Joueur dans leur 'slot' respective ⬆️ //

				// Créer ensuite le 'Packet'
				equipmentPlayer = new ClientboundSetEquipmentPacket(this.playerInstance.getId(), equipmentList);

				//~ -- * 'packet' | Équipement du Joueur * -- ~//

							/* ----------------------------- */

			// Retourne les Informations 'packet' du Joueur en question
			return List.of(destroyPlayer, removeInfoPlayer, addInfoPlayer, respawnEntity, teleportEntity, createPlayer, metadataPlayer, rotatePlayer, equipmentPlayer);
		}
                            /* ----------------------------------------------- */
							/* ------------ ENREGISTREMENT DES PACKET -------- */
							/* ----------------------------------------------- */

		return List.of(this);
	}

	/****************************************************************************************************************/
	/* MÉTHODE PRIVÉE POUR RECHARGER/CHANGER ET RÉCUPÉRER LES INFORMATION (packets) DU JOUEUR (AVEC LE GAMEPROFILE) */
	/****************************************************************************************************************/


  /* ----------------------------------------------------------------------------------------------------------------- */

	/**
	 * Réinitialise un joueur en utilisant son nom.
	 *
	 * Cette méthode réinitialise l'inventaire, les niveaux d'expérience, la nourriture, la santé
	 * et la position du joueur, et envoie un paquet de réapparition au client.
	 *
	 * @param playerName Le nom du joueur à réinitialiser.
	 */
	public static void resetPlayerByName(String playerName) {

		Player player = Bukkit.getPlayer(playerName);
		if(player == null) throw new NullPointerException("Player is null !");

		/**********************************************/

		ServerPlayer nmsPlayer = ((CraftPlayer)player).getHandle();
		GameProfile profile = new GameProfile(nmsPlayer.getUUID(), nmsPlayer.displayName);
		CraftPlayer craftPlayer = nmsPlayer.getBukkitEntity();
		ServerLevel serverLevel = nmsPlayer.level().getMinecraftWorld();

		nmsPlayer.getInventory().clearContent();
		nmsPlayer.getEnderChestInventory().clearContent();
		nmsPlayer.setExperienceLevels(0);
		nmsPlayer.setExperiencePoints(0);
		nmsPlayer.getFoodData().setFoodLevel(20);
		nmsPlayer.getFoodData().setSaturation(20);
		nmsPlayer.setHealth(nmsPlayer.getMaxHealth());

		/**********************************************/

		nmsPlayer.setPos(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
		ClientboundRespawnPacket packet =  new ClientboundRespawnPacket(nmsPlayer.level().dimensionTypeId(),
				nmsPlayer.getRespawnDimension(), BiomeManager.obfuscateSeed(serverLevel.getSeed()),
				nmsPlayer.gameMode.getGameModeForPlayer(), nmsPlayer.gameMode.getPreviousGameModeForPlayer(),
				serverLevel.isDebug(), serverLevel.isFlat(), (byte)3, Optional.of(GlobalPos.of(serverLevel.dimension(), nmsPlayer.getOnPos())),
				nmsPlayer.portalCooldown);

		PlayerEntity.sendPacket(packet);
	}

	/**
	 * Récupère l'UUID d'un Joueur à travers sn Pseudonyme depuis les Serveurs de Mojang.
	 *
	 * @param UUID_URL Le lien vers les enregistrements des 'Universally Unique identifier' des différents Joueurs de Minecraft.
	 * @param playerName Le Pseudonyme du Joueur en question.
	 *
	 * @return L'UUID du Joueur en question depuis les serveurs de Mojang.
	 */
	public static UUID getUUIDByPlayerName(String playerName, String UUID_URL) {

		UUID uuid = null; // Permettra de renvoyer l'UUID du Joueur en question

					/* ------------------------------------------ */

		try {

			HttpRequest request = HttpRequest.newBuilder(new URI(String.format(UUID_URL != null ? UUID_URL : Skin.PLAYER_UUID_FROM_USERNAME_URL, playerName)))
					                         .setHeader("Content-Type", "application/json").GET().timeout(Duration.ofSeconds(5)).build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			if(response.statusCode() == HttpURLConnection.HTTP_OK) {

				Object responseParser = new JSONParser().parse(response.body());

				String uuidStr = (String)((JSONObject)responseParser).get("id");
				uuid = UUID.fromString(uuidStr.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
			}

			return uuid;

		} catch(IOException | URISyntaxException | ParseException | InterruptedException e) {

			mainInstance.console.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Une Erreur est survenue lors de la récupération de l'UUID avec le pseudonyme : "
											+ ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + playerName);
			return null;
		}


    }
						/* ----------------------------------------------------------------------------- */

	/**
	 * Récupère les données du profil d'un Joueur à travers son UUID depuis les Serveurs de Mojang.
	 *
	 * @param TEXTURE_URL Le lien vers les enregistrements des textures des différents Joueurs de Minecraft.
	 * @param uuid        L'UUID du Joueur en question.
	 *
	 * @return Les données du profil du Joueur en question depuis les serveurs de Mojang.
	 */
	public static JSONObject getSessionByPlayerUUID(UUID uuid, String TEXTURE_URL) {

		Object responseParser = null;

					/* ------------------------------------------ */

		try {

			HttpRequest request = HttpRequest.newBuilder(new URI(String.format(TEXTURE_URL != null ? TEXTURE_URL : Skin.SKIN_DATA_UUID_DOWNLOAD_URL,
														uuid.toString().replace("-", "")))).setHeader("Content-Type", "application/json").GET()
														.timeout(Duration.ofSeconds(5)).build();

			HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

			if(response.statusCode() == HttpURLConnection.HTTP_OK) responseParser = new JSONParser().parse(response.body());

			if(responseParser == null) throw new NullPointerException("La Session est null !");
			return ((JSONObject)responseParser);

		} catch(IOException | URISyntaxException | ParseException | InterruptedException e) {

			mainInstance.console.sendMessage(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD.toString() + "Une Erreur est survenue lors de la récupération du profil avec l'UUID : "
											+ ChatFormatting.YELLOW.toString() + ChatFormatting.BOLD.toString() + uuid.toString());
			return null;
		}
	}

  /* ----------------------------------------------------------------------------------------------------------------- */

				/* -------------------------------------------------------------------------------------- */
				/* 		⬆️	UTILE POUR L'ENVOI DE 'PACKETS' ET QUELQUES FONCTIONNEMENTS UTILES	⬆️		  */
				/* ------------------------------------------------------------------------------------- */

	/**
	 *  Renvoi une interface fonctionnelle qui peut lever une exception
 	 */
	@FunctionalInterface
    public interface UnsafeSupplier<T> { T get() throws Exception; }

	/**
	 *  Définit une interface fonctionnelle qui peut lever une exception
 	 */
	@FunctionalInterface
    public interface UnsafeRunnable { void run() throws Exception; }

					/* --------------------------------------------- */

	/**
	 *  Définit une interface fonctionnelle qui prend un seul argument et renvoie une valeur.
 	 */
    @FunctionalInterface
    public interface UnsafeFunction<K, T> { T apply(K k) throws Exception; }

					/* --------------------------------------------- */

    /**
     * Exécute l'exécutable donné et s'il lève une exception, imprime la trace de la pile.
     *
     * @param run L'exécutable à exécuter.
     */
    public static void unsafe(UnsafeRunnable run) {

        try { run.run(); }
        catch(Exception e) { e.printStackTrace(System.err); }
    }

					/* --------------------------------------------- */

	/**
	 * Crée un {@link FriendlyByteBuf}, le transmet à un rappel et renvoie le résultat du rappel.
	 * Le rappel est une fonction qui prend un {@link FriendlyByteBuf} et renvoie un type générique.
	 * Le rappel est l'endroit où sont écrites les données dans le tampon
	 *
	 * @param callback La fonction qui sera appelée pour sérialiser les données.
	 *
	 * @return Un sérialiseur de données.
	 */
	public static <T> T createDataSerializer(UnsafeFunction<FriendlyByteBuf, T> callback) {

        FriendlyByteBuf data = new FriendlyByteBuf(Unpooled.buffer());
        T result = null;

		/**************************************/

        try { result = callback.apply(data); }
        catch(Exception e) { e.printStackTrace(System.err); }
        finally { data.release(); }
        return result;
    }

				/* ------------------------------------------ */

	/**
	 * Il envoie un paquet à un joueur, ou à tous les joueurs si le joueur est nul
	 *
	 * @param packet Le paquet à envoyer
	 * @param player Le joueur auquel le paquet est envoyé. Si nul, il sera envoyer à tous les joueurs en ligne.
	 */
	public static void sendPacket(Packet<?> packet, Player player) {

		if(player != null) ((CraftPlayer)player).getHandle().connection.send(packet);
		else { for(Player p : Bukkit.getServer().getOnlinePlayers()) sendPacket(packet, p); }
	}

	/**
	 * Il envoie un paquet à tous les joueurs
	 *
	 * @param packet Le paquet à envoyer
	 */
	public static void sendPacket(Packet<?> packet) { for(Player p : Bukkit.getServer().getOnlinePlayers()) { sendPacket(packet, p); } }

	/**
	 * Il envoie un paquet à une liste de joueurs
	 *
	 * @param packet Le paquet à envoyer
	 * @param players Les joueurs auxquels le paquet est envoyé.
	 */
	public static void sendPacket(Packet<?> packet, List<Player> players) { players.forEach(p -> sendPacket(packet, p)); }

			/* ------------------------------------------ */

	/**
	 *
	 * Ceci permet d'effectuer uen tâche synchrone
	 *
	 * @param plugin - Le plugin en question
	 * @param runnable - La tâche à éffectué
	 */
	public static void sync(Plugin plugin, Runnable runnable) { Bukkit.getScheduler().runTask(plugin, runnable); }

	/**
	 *
	 * Ceci permet d'effectuer uen tâche asynchrone
	 *
	 * @param plugin - Le plugin en question
	 * @param runnable - La tâche à éffectué
	 */
    public static void async(Plugin plugin, Runnable runnable) { Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable); }

			/* ------------------------------------------ */

				/* -------------------------------------------------------------------------------------- */
				/* 		⬆️	UTILE POUR L'ENVOI DE 'PACKETS' ET QUELQUES FONCTIONNEMENTS UTILES	⬆️		  */
				/* ------------------------------------------------------------------------------------- */

  /* ----------------------------------------------------------------------------------------------------------------- */

}

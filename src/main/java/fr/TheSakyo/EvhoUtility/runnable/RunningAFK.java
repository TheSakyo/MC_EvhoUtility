package fr.TheSakyo.EvhoUtility.runnable;

import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import net.minecraft.ChatFormatting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import net.luckperms.api.model.user.User;

import java.util.UUID;

public class RunningAFK extends BukkitRunnable {
	
	// Variable Utiles //
	
	private final UUID playerUUID;
	private final User user;

	private Boolean isAFK;


    private final UtilityMain mainInstance = UtilityMain.getInstance();
	
	// Variable Utiles //
	
	
	// Constructeur de la class "RunningAFK" //
	public RunningAFK(UUID uuid, User user, Boolean afk, int time) {
		
		this.playerUUID = uuid;
		this.user = user;
		this.isAFK = afk;

        mainInstance.time.remove(uuid);
		mainInstance.time.put(uuid, time);
	}
	// Constructeur de la class "RunningAFK" //



	/*************************/
	/* BOUCLE POUR L'AUTOAFK */
	/*************************/
	@Override
	public void run() {

		for(UUID uuid : mainInstance.time.keySet()) {

			if(uuid == playerUUID) {

				final Player player = Bukkit.getServer().getPlayer(uuid);

				if(player != null && player.isOnline()) {

					final int playerTime = mainInstance.time.get(uuid);
					final String ColorGrade = user.getCachedData().getMetaData().getSuffix();
					final String player_customName = ColorUtils.format(ColorGrade + ChatFormatting.BOLD + CustomMethod.ComponentToString(player.customName()));

					/* Essaie de récupérer le temps du Joueur (temps sans rien faire), pour ensuite gérer son status AFK */
					try {

                        int AFKTime = 900;
                        if(playerTime == AFKTime) {

							player.playerListName(CustomMethod.StringToComponent(player_customName + mainInstance.getAfkList));
							player.sendMessage(ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous êtes AFK !");
							this.isAFK = Boolean.TRUE;

						} else {

							if(this.isAFK == Boolean.FALSE) {

								player.playerListName(CustomMethod.StringToComponent(player_customName));
								player.sendMessage(ChatFormatting.GRAY.toString() + ChatFormatting.ITALIC.toString() + "Vous n'êtes plus AFK !");
								this.isAFK = null;
							}
						}
						mainInstance.time.replace(uuid, playerTime+1);

						/* if(mainInstance.time.get(player) == 3600) {

							mainInstance.time.remove(player);
							player.kickPlayer(ChatFormatting.RED + "Vous avez été AFK pendant trop longtemps !");

						} */

					} catch(NullPointerException ignored) {}
					/* Essaie de récupérer le temps du Joueur (temps sans rien faire), pour ensuite gérer son status AFK */

				} else { mainInstance.time.remove(uuid); this.cancel(); }
			}
		}
	}
	/*************************/
	/* BOUCLE POUR L'AUTOAFK */
	/*************************/
}
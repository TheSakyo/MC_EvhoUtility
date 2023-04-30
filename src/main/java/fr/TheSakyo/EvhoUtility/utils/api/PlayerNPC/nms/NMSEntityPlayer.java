package fr.TheSakyo.EvhoUtility.utils.api.PlayerNPC.nms;

import com.mojang.authlib.GameProfile;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.ServerVersion;
import fr.TheSakyo.EvhoUtility.PaperMC.PaperPlugin;
import fr.TheSakyo.EvhoUtility.PaperMC.nms.NMSUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class NMSEntityPlayer {

    private static Class<?> entityHumanClass; // Variable récupérant la 'class' "entityHuman"
    private static Constructor<?> entityPlayerConstructor; // Variable récupérant le constructeur de la 'class' "entityPlayer"
    private static Method getGameProfile; // Variable récupérant la méthode récupérant le profil de jeux


                            /* ------------------------------------------------------------------ */


    /**
     * Charge tous les deux 'class' du NMS "entityPlayer" et "entityHuman", récupérant diverses informations utiles
     *
     */
    public static void load() throws ClassNotFoundException, NoSuchMethodException {

        ServerVersion serverVersion = PaperPlugin.getServerVersion(); // Récupère la version du Serveur
        // Variable récupérant la 'class' "entityPlayer"
        Class<?> entityPlayerClass = NMSUtils.getMinecraftClass("server.level.EntityPlayer"); // Initialise la 'class' "entityPlayer" en question
        entityHumanClass = NMSUtils.getMinecraftClass("world.entity.player.EntityHuman"); // Initialise la 'class' "entityHuman" en question

        /** // ⬇️ Récupère la bonne variable du NMS récupérant le profil de Jeux du Joueur en fonction de la version actuel du Serveur ⬇️ //
        if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_17_1)) { getGameProfile = entityPlayerClass.getMethod("getProfile"); }
        else if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_1)) { getGameProfile = entityPlayerClass.getMethod("fp"); }
        else if(serverVersion.equals(ServerVersion.VERSION_1_18_2)) { getGameProfile = entityHumanClass.getMethod("fq"); }
        else if(serverVersion.equals(ServerVersion.VERSION_1_19)) { getGameProfile = entityHumanClass.getMethod("fz"); }
        else if(serverVersion.equals(ServerVersion.VERSION_1_19_1) || serverVersion.equals(ServerVersion.VERSION_1_19_2)) { getGameProfile = entityHumanClass.getMethod("fy"); }
        else { getGameProfile = entityHumanClass.getMethod("fD"); }
        // ⬆️ Récupère la bonne variable du NMS récupérant le profil de Jeux du Joueur en fonction de la version actuel du Serveur ⬆️ // **/

        // Récupère la variable du NMS récupérant le profil de jeux du joueur
        getGameProfile = entityHumanClass.getMethod("fM");

        /** // ⬇️ Récupère le bon constructeur du NMS de la 'class' "entityPlayer" en question en fonction de la version actuel du Serveur ⬇️ //
        if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_2) || serverVersion.isNewerThanOrEqual(ServerVersion.VERSION_1_19_3)) { entityPlayerConstructor = entityPlayerClass.getConstructor(MinecraftServer.class, ServerLevel.class, GameProfile.class); }
        else { entityPlayerConstructor = entityPlayerClass.getConstructor(MinecraftServer.class, ServerLevel.class, GameProfile.class, NMSUtils.getMinecraftClass("world.entity.player.ProfilePublicKey")); }
        // ⬆️ Récupère le bon constructeur du NMS de la 'class' "entityPlayer" en question en fonction de la version actuel du Serveur ⬆️ // **/

        // Récupère le constructeur du NMS de la 'class' "entityPlayer" en question
        entityPlayerConstructor = entityPlayerClass.getConstructor(MinecraftServer.class, ServerLevel.class, GameProfile.class/**, NMSUtils.getMinecraftClass("world.entity.player.ProfilePublicKey")**/);
    }


                            /* ------------------------------------------------------------------ */


    /**
     * Créer une instance d'une {@link ServerPlayer Entité de Joueur}.
     *
     * @param minecraftServer Le {@link MinecraftServer Serveur Minecraft} associé a cette entité
     * @param worldServer Le {@link ServerLevel Monde Minecraft} associé a cette entité
     * @param gameProfile Le {@link GameProfile Profil de Jeux} associé a cette entité
     *
     *
     * @return Une nouvelle {@link ServerPlayer Entité de Joueur}
     */
    public static ServerPlayer newEntityPlayer(MinecraftServer minecraftServer, ServerLevel worldServer, GameProfile gameProfile) {

        // On vérifie si le nombre de paramètres du constructeur de la 'class' "entityPlayer" est supérieur à '3'
        boolean longConstructor = ((entityPlayerConstructor.getParameterTypes()).length > 3);

        // ⬇️ On essaie d'instancier une nouvelle entité de Joueur sinon, une exception sera envoyée ⬇️ //
        try {

            // ⬇️ Si le nombre de paramètres du constructeur de la 'class' "entityPlayer" est supérieur ou inférieur à '3', on retourne avec le nombre de paramètres associé ⬇️ //
            if(longConstructor) return (ServerPlayer)entityPlayerConstructor.newInstance(new Object[] { minecraftServer, worldServer, gameProfile, null });
            return (ServerPlayer)entityPlayerConstructor.newInstance(new Object[] { minecraftServer, worldServer, gameProfile });
            // ⬆️ Si le nombre de paramètres du constructeur de la 'class' "entityPlayer" est supérieur ou inférieur à '3', on retourne avec le nombre de paramètres associé ⬆️ //

        } catch(Exception e) {

            e.printStackTrace(System.err);
            return null;
        }
        // ⬆️ On essaie d'instancier une nouvelle entité de Joueur sinon, une exception sera envoyée ⬆️ //
    }

    /**
     * Récupère le {@link GameProfile Profil de Jeux} associé a une {@link ServerPlayer Entité de Joueur} précis.
     *
     * @param player L'{@link ServerPlayer Entité de Joueur} en question
     *
     * @return Le {@link GameProfile Profil de Jeux} associé a l'{@link ServerPlayer Entité de Joueur} demandé
     */
    public static GameProfile getGameProfile(ServerPlayer player) {

        UtilityMain.getInstance(); // On récupère l'instance de la class principale du Plugin
        ServerVersion serverVersion = UtilityMain.getServerVersion(); // Récupère la version du Serveur

        // ⬇️ On récupère le profil de jeux comme il faut en récupérant la version du serveur en question sinon, une exception est envoyée ⬇️ //
        try {

            /**if(serverVersion.isOlderThanOrEqual(ServerVersion.VERSION_1_18_1)) return (GameProfile)getGameProfile.invoke(player, new Object[0]);**/
            return (GameProfile)getGameProfile.invoke(entityHumanClass.cast(player), new Object[0]);

        } catch(Exception e) {

            e.printStackTrace(System.err);
            return null;
        }
        // ⬆️ On récupère le profil de jeux comme il faut en récupérant la version du serveur en question sinon, une exception est envoyée ⬆️ //
    }
}
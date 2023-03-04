package fr.TheSakyo.EvhoUtility.utils.entity.player.utilities;

import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.mojang.authlib.GameProfile;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;
import java.util.UUID;

/**
 * Un skin associé à un Joueur Minecraft, représenté par sa valeur et sa signature.
 */
public final class Skin {


    /**
     * Les données de la peau (nom du joueur associé).
     */
    private final String skinName;

    /**
     * Les données de la peau (sa valeur).
     */
    private final String value;

    /**
     * La signature de la peau (requise par Mojang).
     */
    private final String signature;


    /**
     *
     * Constructeur <i>(pour créer des nouvelles instanciations)</i> de {@link Skin}
     *
     * @param skinName Le Nom du Joueur du Skin associé
     * @param value - La Valeur de la {@link Skin peau} du Joueur.
     * @param signature - La Signature de la {@link Skin peau} du Joueur.
     */
    private Skin(String skinName, String value, String signature) {

        this.skinName = skinName;
        this.value = value;
        this.signature = signature;
    }

    /**
     *
     * Permet de récupérer une nouvelle instanciation de {@link Skin}
     *
     * @param skinName Le Nom du Joueur du Skin associé
     * @param value - La Valeur de la {@link Skin peau} du Joueur.
     * @param signature - La Signature de la {@link Skin peau} du Joueur.
     */
    public static Skin get(String skinName, String value, String signature) { return new Skin(skinName, value, signature); }


    /**
     * Renvoie la valeur de la {@link Skin peau} du Joueur.
     *
     * @return La valeur de la {@link Skin peau} du Joueur.
     */
    public String getValue() { return value; }

    /**
     * Renvoie la Signature de la {@link Skin peau} du Joueur.
     *
     * @return La Signature de la {@link Skin peau} du Joueur.
     */
    public String getSignature() { return signature; }


    /**
     * Renvoie le {@link com.mojang.authlib.GameProfile Profil de Jeu} de la {@link Skin peau} du Joueur.
     *
     * @return La {@link com.mojang.authlib.GameProfile Profil de Jeu} de la {@link Skin peau} du Joueur.
     */
    public GameProfile getProfile() {

        return new GameProfile(UUID.randomUUID(), this.skinName + String.valueOf(new Random().nextInt((100 - 1) + 1) - 1));
    }


    // URL pour obtenir l'UUID d'un Joueur enregistré dans les Serveurs de Mojang.
    public static final String SKIN_DATA_UUID_DOWNLOAD_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false";

    // URL pour obtenir le Pseudonyme d'un Joueur enregistré dans les Serveurs de Mojang.
    public static final String PLAYER_UUID_FROM_USERNAME_URL = "https://api.mojang.com/users/profiles/minecraft/%s";

    /**
     * Prend un {@link UUID} Mojang et le convertit en un véritable UUID
     *
     * @param mojangUUID L'{@link UUID} du joueur dont vous souhaitez obtenir le skin.
     *
     * @return Un objet {@link UUID}.
     */
    public static UUID toRealUUID(String mojangUUID) {

        String least = mojangUUID.replace("-", "").substring(0, 16);
        String most = mojangUUID.replace("-", "").substring(16);
        return new UUID(Long.parseUnsignedLong(least, 16), Long.parseUnsignedLong(most, 16));
    }

    /**
     * Récupère le Skin d'un Joueur à partir de son {@link String pseudonyme}.<br/>
     * <i>(Il convient de noter que le téléchargement à l'aide d'un nom d'utilisateur est moins efficace.).</i>
     *
     * @param username Le nom d'utilisateur du joueur dont on veut télécharger le skin.
     * @return La {@link Skin peau} du joueur, ou null si quelque chose s'est mal passé.
     */
    public static Skin get(String username) {

        String url = String.format(PLAYER_UUID_FROM_USERNAME_URL, username);

        try {

            URLConnection connection = new URL(url).openConnection();
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());
            JSONObject jsonObject = (JSONObject)new JSONParser().parse(inputStream);

            String id = (String)jsonObject.get("id");
            return get(toRealUUID(id));

        } catch(IOException | ParseException e) { e.printStackTrace(); }

        return null;
    }

    /**
     * Récupère le Skin d'un Joueur à partir de son {@link UUID}.
     *
     * @param id L'UUID du lecteur dont il faut télécharger le skin.
     *
     * @return La {@link Skin peau} du joueur, ou null si quelque chose s'est mal passé.
     */
    public static Skin get(UUID id) {

        if(id == null) return new Skin("", "", "");

        String url = String.format(SKIN_DATA_UUID_DOWNLOAD_URL, id.toString().replace("-", ""));

        try {

            URLConnection connection = new URL(url).openConnection();
            InputStreamReader inputStream = new InputStreamReader(connection.getInputStream());

            JSONObject jsonObject = (JSONObject)new JSONParser().parse(inputStream);

            JSONArray properties = (JSONArray)jsonObject.get("properties");
            JSONObject property = (JSONObject)properties.get(0);

            return new Skin((String)jsonObject.get("name"), (String)property.get("value"), (String)property.get("signature"));

        } catch(IOException | ParseException e) { e.printStackTrace(); }

        return null;
    }

                            /* ------------------------------------------------------------------------------------------- */
                            /* ------------------------------------------------------------------------------------------- */

    // ************************************************************************************** //
	// *** // --- Méthode pour Créer un Item qui sera la Tête d'un Joueur spécifié --- // *** //
	// ************************************************************************************** //
	public static ItemStack PlayerHead(CommandSender sender, Player p, String target) {

		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
		SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();

                                    /* ------------------------------ */

        PlayerProfile profile = new CraftPlayerProfile(UUID.randomUUID(), null);
        /*skullMeta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(PlayerEntity.getUUIDByPlayerName(target, null)));*/
        skullMeta.setOwningPlayer(Bukkit.getServer().getOfflinePlayer(Bukkit.getServer().getPlayerUniqueId(target)));

                                    /* ------------------------------ */

        // ⬇️ On Essaie d'obtenir la texture du Skin du Joueur et de l'affecter sur une tête de Joueur. ⬇️ //
        try {

            Skin skin = Skin.get(target);
            profile.getProperties().add(new ProfileProperty("textures", skin.getValue(), skin.getSignature()));
            skullMeta.setPlayerProfile(profile);

        } catch(Exception ignored) {}
        // ⬆️ On Essaie d'obtenir la texture du Skin du Joueur et de l'affecter sur une tête de Joueur. ⬆️ //

                                    /* ------------------------------ */

        skullMeta.displayName(CustomMethod.StringToComponent(ChatColor.YELLOW + "Tête de " + ChatColor.GOLD + target));

        if(sender != null) {

            String YI = ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString(); // Couleur Jaune + Italic
            String GI = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString(); // Couleur Gris + Italic

            p.sendMessage(UtilityMain.getInstance().prefix + ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "Vous obtenez la tête de " +  YI + target + GI + " !");
        }

		skull.setItemMeta(skullMeta);
		return skull;
	}
	// ************************************************************************************** //
	// *** // --- Méthode pour Créer un Item qui sera la Tête d'un Joueur spécifié --- // *** //
	// ************************************************************************************** //
}

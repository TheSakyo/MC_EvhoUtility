package fr.TheSakyo.EvhoUtility.utils.entity.entities;

import fr.TheSakyo.EvhoUtility.PaperMC.nms.craftbukkit.NMSCraftPlayer;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.managers.HologramManager;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
public class HologramEntity {

    private UtilityMain main; // La class Main du Plugin

                                /* ------------------------- */
    private ServerLevel serverLevel; // Le Monde où se situe l'Hologramme (Porte-Armure)

                                /* ------------------------- */
    private String title; // Le Titre de l'Hologramme (Porte-Armure)
    private String text; // Le Texte de l'Hologramme (Porte-Armure) [ce qu'il sera affiché]
    private Location location; // La localisation de l'entité de l'Hologramme (Porte-Armure)

                                /* ------------------------- */

    private ArmorStand armorStand; // Permet de récupérer l'Entité de l'Hologramme (Porte-Armure)

    private Boolean isDestroyed = Boolean.TRUE; // Si l'Entité de l'Hologramme (Porte-Armure) est détruite ou non

                                /* ------------------------- */
    private List<HologramEntity> armorlist = new ArrayList<HologramEntity>(); // Liste des Hologrammes dans une nouvelle ligne pour l'appararition de l'Entité (Porte-Armure)

    private final List<HologramEntity> entityHolograms = new ArrayList<HologramEntity>(); // Liste finale des Hologrammes dans une nouvelle ligne (Portes Armures)

                        /* ----------------------------------- */
                        /* ----------------------------------- */

    /**
     * Renvoie une Nouvelle instande d'{@link HologramEntity hologramme}.
     *
     * @param main Une Instance du plugin {@link UtilityMain}
     * @param p Le joueur qui subira les changements
     * @param ID L'Identifiant de l'Entité de l'{@link HologramEntity hologramme}
     * @param title Le titre de l'{@link HologramEntity hologramme}
     * @param text Le texte à afficher
     * @param loc La localisation de l'{@link HologramEntity hologramme}
     * @param saveConfig Doit-on sauvegarder l'{@link HologramEntity hologramme} dans un fichier de configuration
     * @param newInstance Doit-on construire de nouveau l'{@link HologramEntity hologramme}
     *
     * @return Une Nouvelle instande d'{@link HologramEntity hologramme}.
     */
    public HologramEntity(UtilityMain main, Player p, Integer ID, String title, String text, Location loc, boolean saveConfig, boolean newInstance) {

        ServerLevel level = ((CraftWorld)loc.getWorld()).getHandle(); // Récupère le Monde de la localisation en question depuis le 'NMS'

        this.main = main;
        this.serverLevel = level; // Définit le Monde où se situe l'Hologramme

        this.title = title; // Définit le Titre de l'Hologramme
        this.location = loc; // Définit la localisation de l'Hologramme
        this.text = text; // Définit le texte de l'Hologramme


        if(this.armorStand == null) this.armorStand = new ArmorStand(EntityType.ARMOR_STAND, level); // Définit l'Entité de l'Hologramme
        if(ID != null) this.armorStand.setId(ID.intValue()); // Redéfinit l'identifiant de l'entité de l'hologramme, si cela a été demandé dans les paramètres

        if(newInstance) this.build(p, text, saveConfig); // Si on veut faire une nouvelle instance, on construit alors l'Hologramme
    }
                        /* ----------------------------------- */
                        /* ----------------------------------- */

    /**
     * Renvoie le {@link String Titre} de l'{@link HologramEntity hologramme}.
     *
     * @return Le {@link String Titre} de l'{@link HologramEntity hologramme}.
     */
    public String getTitle() { return this.title; }

    /**
     * Renvoie le {@link String Texte} de l'{@link HologramEntity hologramme} [ce qui est censé être affiché].
     *
     * @return Le {@link String Titre} de l'{@link HologramEntity hologramme}[ce qui est censé être affiché].
     */
    public String getText() { return this.text; }

    /**
	 * Renvoie les {@link HologramEntity lignes} de l'{@link HologramEntity hologramme}.
	 *
	 * @return Les {@link HologramEntity lignes} de l'{@link HologramEntity hologramme}.
	 */
    public List<HologramEntity> getLine() { return this.entityHolograms; }

    /**
     * Renvoie la {@link Location Localisation} où se situe l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
     *
     * @return La {@link Location Localisation} où se situe l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
     */
    public Location getLocation() { return this.location; }

    /**
     * Renvoie l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
     *
     * @return L'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
     */
    public ArmorStand getEntity() { return this.armorStand; }

     /**
     * Vérifie si l'{@link HologramEntity hologramme} est détruit ou non.
     *
     * @return Une Valeur Booléenne.
     */
    public Boolean isDestroyed() { return this.isDestroyed; }

                            /* ----------------------------- */

    /**
     * Supprime la liste des {@link HologramEntity lignes} de l'{@link HologramEntity hologramme}.
     *
     */
    public void clearLine() { entityHolograms.clear(); }

                        /* ----------------------------------- */
                        /* ----------------------------------- */

    /**
	 * Supprime l'{@link HologramEntity hologramme} en supprimant ses {@link ArmorStand entité(s)} associé(s).
	 *
     * @param p Le joueur qui subira les changements
     * @param unsaveConfig Si oui ou non, on supprime l'hologramme dans son fichier de configuration.
     *
	 */
    public void remove(Player p, boolean unsaveConfig) {

        if(p != null) { NMSCraftPlayer.sendPacket(p, new ClientboundRemoveEntitiesPacket(getEntity().getId())); } // On Supprime l'hologramme
        else {

            // On Supprime l'hologramme pour tous les joueurs en lignes
            Bukkit.getServer().getOnlinePlayers().forEach(player -> NMSCraftPlayer.sendPacket(player, new ClientboundRemoveEntitiesPacket(getEntity().getId())));

            this.isDestroyed = Boolean.TRUE; // On Définit l'hologramme étant détruit
        }

        // Décharger l'hologramme de son fichier de configuration si on l'a demandé
        if(unsaveConfig) HologramManager.unsaveHologram(this.getTitle());
    }

                        /* ----------------------------------- */
                        /* ----------------------------------- */

    /**
	 * Construit l'{@link HologramEntity hologramme} avec son texte en utilsant des {@link ArmorStand entité(s)}.
	 *
     * @param p Le joueur qui subira les changements
     * @param text Le texte de l'{@link HologramEntity hologramme} à afficher (Nom Customisé de {@link ArmorStand}).
     * @param saveConfig Définit si on enregistre les modifications dans le fichier de configuration ou pas.
     *
	 */
    private void build(Player p, String text, boolean saveConfig) {

        main.SuccessHolograms = null; // Permettra de récupérer le message de succès après la construction de l'hologramme.
        HologramEntity hologram = null; // Permettra de récupérer une nouvelle instance d'hologramme pour chaque ligne.

        List<String> originalList = new ArrayList<String>(); // Permettra de stocker le texte affiché (contenu de l'hologramme).
        List<String> finalList = new ArrayList<String>(); // Permettra de stocker le texte affiché final (contenu de l'hologramme).

        // Si le premier caracètre du texte a affiché contient le caractère '//n', on lui enlève alors, ce caractère
        if(text.startsWith("//n")) text = text.replaceFirst("//n", "");
        originalList.add(text); // On Ajoute texte a affiché dans une liste originale qui stockera le contenu

                                /* ------------------------------------------------------ */

        // On enregistre chaque ligne si le contenu contient le caractère '//n'
        for(String myString : originalList) finalList.addAll(Arrays.asList(myString.trim().split("//n")));
        originalList.clear(); // On Supprime la liste origtinal récupéré, car le contenu a été stocker dans une autre liste

                                /* ------------------------------------------------------ */

        // ⬇️ On Boucle sur toutes les ligne(s) récupérée(s) pour construire l'hologramme ⬇️ //
        for(int i = 0; i < finalList.size(); i++) {

        	String content = finalList.get(i); // Récupère le contenu de la ligne en question

            // ⬇️ Supprime les espaces au début et a la fin du contenu de la ligne en question ⬇️ //
        	if(content.startsWith(" ")) content = content.substring(" ".length());
        	if(content.endsWith(" ")) content = content.substring(0, content.length() - " ".length());
            // ⬆️ Supprime les espaces au début et a la fin du contenu de la ligne en question ⬆️ //

			if(i == 0) { hologram = this; } // Si le nombre de lignes est égal à 0, on définit l'instande de l'Hologramme en question étant l'hologramme lui-même

            // Sinon, si le nombre de lignes eest égal à 1, 2, ou 3, on ajoute une nouvelle instance de l'hologramme pour ajouter une nouvelle ligne
			else if(i == 1 || i == 2 || i == 3) {

				Location LastLocEntities = armorlist.get(0).getLocation(); // Récupère la dernière localisation de la ligne suivante

                // Créer la nouvelle ligne à partir de la localisation récupérée
				Location newLine = new Location(LastLocEntities.getWorld(), LastLocEntities.getX(), LastLocEntities.getY() - 0.26, LastLocEntities.getZ());

                // Définit la nouvelle instande de l'Hologramme
				hologram = new HologramEntity(main, p, Integer.valueOf(hologram.getEntity().getId() + 1), this.getTitle(), text, newLine, saveConfig, false);

            // Sinon, si le nombre de lignes est supérieur à 4, on envoie un message d'erreur
			} else if(i >= 4) {

				main.ErrorLineHolograms = main.prefix + ChatColor.RED + "Vous ne pouvez pas avoir plus de 4 lignes, le reste a donc été supprimé"; // Enregistre le message d'Erreur
				hologram = null; // Définit l'instande de l'Hologramme en question en "NULL"
			}

			if(hologram != null) { this.spawn(p, content, hologram); } // Si l'instande de l'Hologramme en question n'est pas nul, on le fait appraître
        }
        // ⬆️ On Boucle sur toutes les ligne(s) récupérée(s) pour construire l'hologramme ⬆️ //


        // Charge l'hologramme dans son fichier de configuration si on l'a demandé
        if(saveConfig) HologramManager.saveHologram(this.getTitle(), entityHolograms);

        // On enregistre le message de succès aprés la construction des lignes
        if(main.SuccessHolograms == null) main.SuccessHolograms = main.prefix + ChatColor.GREEN + "L'Hologramme " + ChatColor.GOLD + this.getTitle() + ChatColor.GREEN + " a été Créé !";
        finalList.clear(); // On vide la liste finale nous permettant de construire chaque ligne

        this.isDestroyed = Boolean.FALSE; // On Définit l'hologramme n'étant pas détruit
    }

                        /* ----------------------------------- */


    /**
	 * Définit les différents paramètrage de l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme} pour ensuite le faire apparaître.
	 *
     * @param p Le joueur qui subira les changements
     * @param text Le texte de l'{@link HologramEntity hologramme} à afficher (Nom Customisé de {@link ArmorStand}).
     * @param hg L'{@link HologramEntity Hologramme} à faire appraitre ({@link ArmorStand}).
     *
	 */
    private void spawn(Player p, String text, HologramEntity hg) {

        ArmorStand entity = hg.getEntity(); // Récupère l'entité -Porte-Armure)

        // Si la liste des hologrammes n'est pas vide, on la vide
        if(!armorlist.isEmpty()) { armorlist.clear(); }

                                 /* ----------------------------- */

         entity.setInvisible(true); // Rend le Porte-Armure Invisible(Hologramme).
		 entity.setShowArms(false); // Désactive les bras du Porte-Armure (Hologramme).
         entity.setNoGravity(true); // Désactive la gravité du Porte-Armure (Hologramme).
         entity.setSmall(true); // Rend le Porte-Armure Petit (Hologramme).
         entity.setCustomName(PaperAdventure.asVanilla(CustomMethod.StringToComponent(ColorUtils.format(text)))); // Définit le nom customisé du Porte-Armure (Hologramme).
         entity.setCustomNameVisible(true); // Active le Nom Customisé du Porte-Armure (Hologramme).
         entity.setPos(hg.getLocation().getX(), hg.getLocation().getY(), hg.getLocation().getZ()); // Définit la position du Porte-Armure (Hologramme).
         entity.setRot(hg.getLocation().getYaw(), hg.getLocation().getPitch()); // Définit la rotation du Porte-Armure (Hologramme).

        armorlist.add(hg); // Ajoute l'hologrames à la liste correspondante
        entityHolograms.add(hg); // Ajoute l'hologrames à la liste correspondante

        // Fait appraître le Porte-Armure et actualise ses métadonnées pour le Joueur en question
        if(p != null) {

            NMSCraftPlayer.sendPacket(p, new ClientboundAddEntityPacket(entity));
            NMSCraftPlayer.sendPacket(p, new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true));

        // Sinon, on fait appraître le Porte-Armure et actualise ses métadonnées pour tous les joueurs
        } else {

            // ⬇️ on fait appraître le Porte-Armure et actualise ses métadonnées pour tous les joueurs en ligne ⬇️ //
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {

                NMSCraftPlayer.sendPacket(player, new ClientboundAddEntityPacket(entity));
                NMSCraftPlayer.sendPacket(player, new ClientboundSetEntityDataPacket(entity.getId(), entity.getEntityData(), true));
            });
            // ⬆️ on fait appraître le Porte-Armure et actualise ses métadonnées pour tous les joueurs en ligne ⬆️ //
        }
    }

                        /* ----------------------------------- */
                        /* ----------------------------------- */

    /**
	 * Renvoie le {@link ServerLevel Monde} où se situe l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
	 *
	 * @return Le {@link ServerLevel Monde} où se situe l'{@link ArmorStand entité} de l'{@link HologramEntity hologramme}.
	 */
    private ServerLevel getLevel() { return this.serverLevel; }
}

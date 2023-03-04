package fr.TheSakyo.EvhoUtility.commands.others;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.config.ConfigFile;
import fr.TheSakyo.EvhoUtility.managers.ZoneManager;
import fr.TheSakyo.EvhoUtility.utils.ParticleUtil;
import fr.TheSakyo.EvhoUtility.utils.custom.CustomMethod;
import fr.TheSakyo.EvhoUtility.utils.custom.methods.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.luckperms.api.model.group.Group;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ZoneCommand  implements CommandExecutor {


    /* Récupère la class "Main" */
    private UtilityMain main;
    public ZoneCommand(UtilityMain pluginMain) { this.main = pluginMain; }
    /* Récupère la class "Main" */


    // Mise En Page En-Tête et Pied De Page de la partie "help" //
    String evhozone = ChatColor.GRAY + "========= " + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Evho" + ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Zone" + ChatColor.GRAY + " =========";
    String footer = ChatColor.GRAY + "===========================";
    //Mise En Page En-Tête et Pied De Page de la partie "help" //


    // Préfix "EvhoZone" //
    String prefixzone = ChatColor.WHITE + "[" + ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD.toString() + "Evho" + ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + "Zone" + ChatColor.WHITE + "]" + ChatColor.RESET + " ";
    // Préfix "EvhoZone" //


    // Format du Chat (couleur grise en italic)
    String GI = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {

        String helpType = prefixzone + ChatColor.RED + "Essayez \"/zone\" ou \"/zone help ou ?\" pour voir les commandes de gestion des zones d'EvhoZone !";

        if(sender instanceof Player p) {

            String WorldName = p.getWorld().getName(); // Récupère le nom du monde du Joueur.

            if(p.hasPermission("evhoutility.zone")) {

                if(args.length >= 1) {

                    String prefixType = prefixzone + ChatColor.RED + "Essayez /zone " + args[0] + " ";

                    if(args.length == 1) {

                        if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) { Bukkit.dispatchCommand(p, "zone"); }

                        else if(args[0].equalsIgnoreCase("list")) {

                            Set<String> zones = ConfigFile.getConfigurationSection(main.zoneconfig, "ZONE").getKeys(false); // Liste des noms de régions enregistrés

                            if(zones != null && !zones.isEmpty()) {

                                String primaryColor = ChatColor.GOLD.toString(); // Récupère la couleur primaire du message pour chaque zone à envoyer au Joueur

                                p.sendMessage(ChatColor.GRAY + "========= " + prefixzone + ChatColor.GRAY + "=========");
                                p.sendMessage(" ");
                                p.sendMessage(" ");

                                p.sendMessage(ChatColor.AQUA.toString() + ChatColor.UNDERLINE.toString() + "Liste des Zone(s) dans le serveur :");

                                for(String ZONE : zones) {

                                    p.sendMessage(" ");

                                    // Si la zone en question est une zone globale, on change sa couleur primaire
                                    if(ZONE.equalsIgnoreCase("default_" + WorldName)) primaryColor = ChatColor.DARK_AQUA.toString();

                                    Component zoneName = CustomMethod.StringToComponent(primaryColor + ChatColor.BOLD.toString() + ZONE);

                                    zoneName = zoneName.clickEvent(ClickEvent.runCommand("/zone teleport " + ZONE));
                                    zoneName = zoneName.hoverEvent(HoverEvent.showText(CustomMethod.StringToComponent("Cliquez pour vous y téléporter")));

                                    Component message = CustomMethod.StringToComponent(ChatColor.WHITE + "- ").append(zoneName);

                                    /* ------------------------------------------------ */

                                    // Si la zone en question est une zone globale, on ajoute une petite information sur ce que signifie la zone en question
                                    if(ZONE.equalsIgnoreCase("default_" + WorldName)) {

                                        Component infoZone = CustomMethod.StringToComponent(ChatColor.YELLOW.toString() + ChatColor.ITALIC.toString()
                                                + " (Zone Globale du Monde '" + WorldName + "')");
                                        message = message.append(infoZone);
                                    }

                                    /* ------------------------------------------------ */

                                    p.sendMessage(message); // On envoie le message de la zone au Joueur
                                }

                                p.sendMessage(" ");
                                p.sendMessage(" ");
                                p.sendMessage(ChatColor.GRAY + "===========================");

                            } else { p.sendMessage(prefixzone + GI + "Aucune Zone dans le Serveur !"); }

                        } else if(args[0].equalsIgnoreCase("reloadgroups")) {

                            Set<String> keysZoneSectionCfg = ConfigFile.getConfigurationSection(main.zoneconfig, "ZONE").getKeys(false);

                            if(keysZoneSectionCfg != null && !keysZoneSectionCfg.isEmpty()) {

                                for(String key : keysZoneSectionCfg) { ZoneManager.resetGroupsForZone(key, false); }

                                if(keysZoneSectionCfg.size() <= 1) {

                                    p.sendMessage(prefixzone + GI + "Tous le(s) Grade(s) du Serveur on été rechargé sur la Zone " + ChatColor.YELLOW + keysZoneSectionCfg.stream().toList().get(0) + GI + " !");

                                } else {

                                    p.sendMessage(prefixzone + GI + "Tous le(s) Grade(s) du Serveur on été rechargé sur les Zones en question !");
                                }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "Aucune Zone dans le Serveur !"); }

                        } else if(args[0].equalsIgnoreCase("listgroups") || args[0].equalsIgnoreCase("teleport") ||
                                args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete") ||
                                args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("hide")) { p.sendMessage(prefixType + "<zoneName>"); }

                        else if(args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("pos2")) { p.sendMessage(prefixType + "[<teleport>] <zoneName>"); }
                        else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) { p.sendMessage(prefixType + "<zoneName> <grade>"); }

                        else if(args[0].equalsIgnoreCase("rename")) { p.sendMessage(prefixType + "<zoneName> <new zoneName>"); }

                        else { p.sendMessage(helpType); }

                    } else if(args.length == 2) {

                        if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("list") ||
                                args[0].equalsIgnoreCase("reloadgroups")) { p.sendMessage(prefixType); }

                        else if(args[0].equalsIgnoreCase("listgroups")) {

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                List<Group> groups = ZoneManager.getGroupsForZone(args[1]);

                                String AI = ChatColor.AQUA.toString() + ChatColor.ITALIC.toString();

                                if(groups != null && !groups.isEmpty()) {

                                    p.sendMessage(ChatColor.GRAY + "========= " + prefixzone + ChatColor.GRAY + "=========");
                                    p.sendMessage(" ");
                                    p.sendMessage(" ");

                                    if(args[1].equalsIgnoreCase("default_" + WorldName)) {

                                        p.sendMessage(AI + "Liste des Grade(s) dans le serveur pour permettre de sortir de la Zone Globale :");

                                    } else {

                                        p.sendMessage(AI + "Liste des Grade(s) dans le serveur pour permettre d'être dans la Zone " + ChatColor.YELLOW.toString() +
                                                ChatColor.ITALIC.toString() + args[1].toUpperCase() + AI + " :");
                                    }


                                    for(Group group : groups) {

                                        String prefixLuck = group.getCachedData().getMetaData().getPrefix();

                                        p.sendMessage(" ");
                                        p.sendMessage(ChatColor.WHITE + "- " + ColorUtils.format(prefixLuck));
                                    }

                                    p.sendMessage(" ");
                                    p.sendMessage(" ");
                                    p.sendMessage(ChatColor.GRAY + "===========================");

                                } else { p.sendMessage(prefixzone + ChatColor.RED + "Aucun Grade(s) du Serveur ont accès a la Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " !"); }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("teleport")) {

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                if(ZoneManager.hasRegion(args[1])) {

                                    if(ZoneManager.hasPerm(args[1], p)) {

                                        p.teleport(ZoneManager.getCenterRegion(args[1]));
                                        p.sendMessage(prefixzone + GI + "Vous avez été téléporté vers la Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " !");

                                    } else { p.sendMessage(prefixzone + ChatColor.RED + "Impossible de vous téléportez dans cette zone !"); }

                                } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'a aucune Région !"); }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("create")) {

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(!ZoneManager.isExist(args[1])) {

                                ZoneManager.create(args[1]);
                                ZoneManager.resetGroupsForZone(args[1], true);

                                if(args[1].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "La Zone Globale du monde actuel a été créer !");
                                else p.sendMessage(prefixzone + GI + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " a été créer !");

                            } else {

                                if(args[1].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "La Zone Globale du monde actuel éxiste déjà !");
                                else p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " éxiste déjà !");
                            }

                        } else if(args[0].equalsIgnoreCase("delete")) {

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                ZoneManager.delete(args[1]);

                                if(args[1].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "La Zone Globale du monde actuel a été supprimer !");
                                else p.sendMessage(prefixzone + GI + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " a été supprimer !");

                            } else {

                                if(args[1].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "La Zone Globale du monde actuel n'éxiste pas !");
                                else p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !");
                            }

                        } else if(args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("hide")) {

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                World world = ZoneManager.getWorld(args[1]);
                                List<Double> firstPos = ZoneManager.getFirstPos(args[1]);
                                List<Double> secondPos = ZoneManager.getSecondPos(args[1]);

                                if(args[0].equalsIgnoreCase("show")) {

                                    if(world == null || firstPos == null || secondPos == null) {

                                        p.sendMessage(prefixzone + ChatColor.RED + "Veuillez bien définir les positions avant de vouloir afficher les délimitations de la Zone !");
                                        return false;
                                    }

                                                             /* -------------------------------------- */

                                    Location firstPosLocation = ZoneManager.getFirstLocationPos(args[1]); // Récupère la localisation de la 1er position de la zone en question
                                    Location secondPosLocation = ZoneManager.getSecondLocationPos(args[1]); // Récupère la localisation de la 2ᵉ position de la zone en question

                                    // Récupère une liste de localisation générant un cube, pour ensuite générer des effets de particule dans chaque localisation
                                    Collection<Location> cuboidLocation = ParticleUtil.edgesCuboid(firstPosLocation, secondPosLocation, 0.30);

                                                             /* -------------------------------------- */

                                    BukkitTask task = ParticleUtil.getTask(p.getUniqueId(), args[1].toUpperCase()); // Récupère la tâche actuelle associée au joueur du gestionnaire de particulière

                                    // Si la tâche actuelle du gestionnaire de particulière n'est pas null et n'est pas annulé, alors on affiche une erreur à Joueur
                                    if(task != null && !task.isCancelled()) {

                                        if(args[1].equalsIgnoreCase("default_" + WorldName)) {

                                            p.sendMessage(prefixzone + ChatColor.RED + "Les démilitations de La Zone Globale du monde actuel sont déjà affichés !");
                                            return false;

                                        } else {

                                            p.sendMessage(prefixzone + ChatColor.RED + "Les démilitations de La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " sont déjà affichés !");
                                            return false;
                                        }

                                    // Sinon, on affiche les effets de particule demandée autour de la zone, on désigne d'effectuer cela dans une boucle (tâche à effectuer)
                                    } else new ParticleUtil(Particle.COMPOSTER).show(p, cuboidLocation, true, args[1].toUpperCase());

                                                             /* -------------------------------------- */

                                    if(args[1].equalsIgnoreCase("default_" + WorldName)) {

                                        p.sendMessage(prefixzone + GI + "Vous pouvez désormais voir les démilitations de La Zone Globale du monde actuel !");

                                    } else p.sendMessage(prefixzone + GI + "Vous pouvez désormais voir les démilitations de La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " !");

                                } else if(args[0].equalsIgnoreCase("hide")) {

                                    BukkitTask task = ParticleUtil.getTask(p.getUniqueId(), args[1].toUpperCase()); // Récupère la tâche actuelle associée au joueur du gestionnaire de particulière

                                    // Si la tâche actuelle du gestionnaire de particulière n'est pas null et n'est pas annulé, alors on l'annule et on supprime la tâche
                                    if(task != null && !task.isCancelled()) { ParticleUtil.removeTask(p.getUniqueId(), args[1].toUpperCase()); }

                                     // Sinon, on affiche une erreur, car la tâche n'éxiste pas
                                     else {

                                        if(args[1].equalsIgnoreCase("default_" + WorldName)) {

                                            p.sendMessage(prefixzone + ChatColor.RED + "Les démilitations de La Zone Globale du monde actuel ne sont pas affichés !");

                                        } else p.sendMessage(prefixzone + ChatColor.RED + "Les démilitations de La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " ne sont pas affichés !");

                                        return false;
                                    }

                                                             /* -------------------------------------- */

                                    if(args[1].equalsIgnoreCase("default_" + WorldName)) {

                                        p.sendMessage(prefixzone + GI + "Vous ne voyez plus les démilitations de La Zone Globale du monde actuel !");

                                    } else p.sendMessage(prefixzone + GI + "Vous ne voyez plus les démilitations de La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " !");
                                }

                            } else {

                                if(args[1].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "La Zone Globale du monde actuel n'éxiste pas !");
                                else p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !");
                            }

                        } else if(args[0].equalsIgnoreCase("pos1")) {

                            double x = p.getLocation().getX();
                            double y = p.getLocation().getY();
                            double z = p.getLocation().getZ();

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                String worldName = WorldName;

                                ZoneManager.setWorld(args[1], worldName);
                                ZoneManager.setFirstPos(args[1], x, y, z);
                                p.sendMessage(prefixzone + GI + "1er Position définit sur la Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " dans le monde " + ChatColor.GOLD + worldName + GI + " !");

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("pos2")) {

                            double x = p.getLocation().getX();
                            double y = p.getLocation().getY();
                            double z = p.getLocation().getZ();

                            if(args[1].equalsIgnoreCase("default")) args[1] = "default_" + WorldName;

                            if(ZoneManager.isExist(args[1])) {

                                World world = ZoneManager.getWorld(args[1]);
                                List<Double> firstPos = ZoneManager.getFirstPos(args[1]);

                                if(firstPos != null && world != null) {

                                    if(p.getWorld() == world) {

                                        if(firstPos.get(0) == x && firstPos.get(1) == y && firstPos.get(1) == z) {

                                            p.sendMessage(prefixzone + ChatColor.RED + "Impossible de définir la deuxième position sur la même coordonnée que la première !");

                                        } else {

                                            ZoneManager.setSecondPos(args[1], x, y, z);
                                            p.sendMessage(prefixzone + GI + "2ème Position définit sur la Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " dans le monde " + ChatColor.GOLD + world.getName() + GI + " !");
                                        }

                                    } else { p.sendMessage(prefixzone + ChatColor.RED + "Le Monde où vous êtes ne trouve pas la Zone demandée !"); }

                                } else { p.sendMessage(prefixzone + ChatColor.RED + "Veuillez bien définir la première position avant de définir la deuxième !"); }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) { p.sendMessage(prefixType + "<zoneName> <grade>"); }
                        else if(args[0].equalsIgnoreCase("rename")) { p.sendMessage(prefixType + "<zoneName> <new zoneName>"); }
                        else { p.sendMessage(helpType); }

                    } else if(args.length == 3) {

                        Group group = main.luckapi.getGroupManager().getGroup(args[2]);

                        if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("list") || args[0].equalsIgnoreCase("reloadgroups")) { p.sendMessage(prefixType); }

                        else if(args[0].equalsIgnoreCase("listgroups") || args[0].equalsIgnoreCase("teleport") ||
                                args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("delete") ||
                                args[0].equalsIgnoreCase("show") || args[0].equalsIgnoreCase("hide")) { p.sendMessage(prefixType + "<zoneName>"); }

                        else if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {

                            if(ZoneManager.isExist(args[1])) {

                                if(group != null) {

                                    String formatedPrefixLuck = ColorUtils.format(group.getCachedData().getMetaData().getPrefix());

                                    if(args[0].equalsIgnoreCase("add")) { ZoneManager.addGroupForZone(args[1], group.getName()); }
                                    else { ZoneManager.removeGroupForZone(args[1], group.getName()); }

                                    p.sendMessage(prefixzone + GI + "Vous avez modifié l'accés du Grade \"" + formatedPrefixLuck + GI + "\" de la Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " !");

                                } else { p.sendMessage(prefixzone + ChatColor.RED + "Le Grade demandé est introuvable !"); }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("pos1") || args[0].equalsIgnoreCase("pos2")) {

                            if(args[2].equalsIgnoreCase("default")) args[2] = "default_" + WorldName;
                            World world = ZoneManager.getWorld(args[2]);

                            if(ZoneManager.isExist(args[2])) {

                                if(args[1].equalsIgnoreCase("teleport")) {

                                    if(args[0].equalsIgnoreCase("pos1")) {

                                        if(ZoneManager.getFirstPos(args[2]) != null && world != null) {

                                            Location location = ZoneManager.getFirstLocationPos(args[2]);
                                            p.teleport(location);

                                            /* -------------------------------------------------------- */

                                            if(args[2].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "Vous vous êtes téléporter à la première position de la Zone Globale");
                                            else p.sendMessage(prefixzone + GI + "Vous vous êtes téléporter à la première position de la zone " + ChatColor.YELLOW + args[1].toUpperCase());

                                        } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone n'a pas de première position !"); }

                                        /* ------------------------------------------------ */

                                    } else if(args[0].equalsIgnoreCase("pos2")) {

                                        if(ZoneManager.getSecondPos(args[2]) != null && world != null) {

                                            Location location = ZoneManager.getSecondLocationPos(args[2]);
                                            p.teleport(location);

                                            /* -------------------------------------------------------- */

                                            if(args[2].equalsIgnoreCase("default_" + WorldName)) p.sendMessage(prefixzone + GI + "Vous vous êtes téléporter à la deuxième position de la Zone Globale");
                                            else p.sendMessage(prefixzone + GI + "Vous vous êtes téléporter à la deuxième position de la zone " + ChatColor.YELLOW + args[1].toUpperCase());

                                        } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone n'a pas de seconde position !"); }
                                    }

                                } else { p.sendMessage(prefixType + "[<teleport>] <zoneName>"); }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else if(args[0].equalsIgnoreCase("rename")) {

                            if(ZoneManager.isExist(args[1])) {

                                if(args[1].equalsIgnoreCase("default") || args[1].equalsIgnoreCase("default_" + WorldName)) {

                                    p.sendMessage(prefixzone + ChatColor.RED + "La Zone Globale du monde actuel ne peut pas être renommé !");

                                } else {

                                    ZoneManager.rename(args[1], args[2]);
                                    p.sendMessage(prefixzone + GI + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + GI + " a été renommée en " + ChatColor.YELLOW + args[2].toUpperCase() + GI  + " !");

                                }

                            } else { p.sendMessage(prefixzone + ChatColor.RED + "La Zone " + ChatColor.YELLOW + args[1].toUpperCase() + ChatColor.RED + " n'éxiste pas !"); }

                        } else { p.sendMessage(helpType); }

                    } else { p.sendMessage(helpType); }

                } else {

                    sender.sendMessage(evhozone);
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.DARK_GREEN + "Information : " + ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + "'default' à la place de '<zoneName>' permettra d'effectuer l'action en question pour la Zone Globale du monde.");
                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "help ou ?" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Affiche la Liste des commandes de d'EvhoZone.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "list" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Affiche la Liste des Zones dans le Serveurs.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "reloadgroups" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Recharge les Grades des Zones dans le Serveurs.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "<create/delete> <zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Créer ou Supprime une Zone.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "<show/hide> <zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Affiche ou Cache les délimitations de la Zone en question.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "listgroups <zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Affiche la Liste des Grades du Serveur de la Zone en question.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "teleport <zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Vous téléporte à la Zone en question.");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "<pos1/pos2> [<teleport>] <zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Détermine la première et la deuxième position de la Zone en fonction d'où est situé le Joueur."
                            + ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + " (L'Argument '<teleport>' vous permet de vous téléportez dans la position en question de la zone.)");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "rename <zoneName> <new zoneName>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Renomme une Zone en particulier."
                            + ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + " (Impossible de renommer les Zones Globales des mondes)");
                    sender.sendMessage("");
                    sender.sendMessage(ChatColor.AQUA + "/zone " + ChatColor.DARK_AQUA + "<add/remove> <zoneName> <grade>" + ChatColor.WHITE + ":" + ChatColor.GRAY + " Ajoute ou Supprime un Grade de la Zone en question pour lui y donner l'accés."
                            + ChatColor.GOLD.toString() + ChatColor.ITALIC.toString() + " (Concernant les Zones Globales des mondes sa sera les grades qui auront accès pour sortir de cette zone).");

                    sender.sendMessage("");
                    sender.sendMessage("");
                    sender.sendMessage(footer);
                }

            } else { p.sendMessage(prefixzone + ChatColor.RED + "Vous n'avez pas les permissions pour gérer des zones !");}

        } else { sender.sendMessage(prefixzone + ChatColor.RED + "Vous devez être en jeux pour gérer une Zone !"); }

        return false;
    }
}

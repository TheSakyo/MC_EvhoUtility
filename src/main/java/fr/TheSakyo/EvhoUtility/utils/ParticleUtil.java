package fr.TheSakyo.EvhoUtility.utils;

import fr.TheSakyo.EvhoUtility.UtilityMain;
import fr.TheSakyo.EvhoUtility.utils.custom.enums.AINSI.AinsiColor;
import net.minecraft.ChatFormatting;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class ParticleUtil {

    private BukkitTask task; // Variable récupérant la tâche en cours actuel
    private final Particle particle; // Variable récupérant la particule à jouer

    private static final Map<UUID, Map<String, BukkitTask>> particleTasks = new HashMap<>(); // Variable statique récupérant la liste des tâches en cours pour chaques joueurs


    /**
     * Instancie une nouvelle {@link ParticleUtil gestion de Particule}.
     *
     * @param particle La {@link Particle Particule} à utiliser
     *
     */
    public ParticleUtil(Particle particle) { this.particle = particle; }

                                /* ----------------------------------------------------------------------------- */

    /**
     * Récupère une collection de {@link Location localisations} à partir de deux localisations définîtes générant ensuite les bords d'un cube.
     *
     * @param pos1 La {@link Location localisation} du premier coin du cube
     * @param pos2 La {@link Location localisation} du deuxième coin du cube
     * @param distance La distance entre les localisations
     *
     * @return La liste de {@link Location localisations} générant un cube entre les deux localisations demandées.
     */
    public static Collection<Location> edgesCuboid(Location pos1, Location pos2, double distance) {

        List<Location> result = new ArrayList<>(); // Liste permettra de retourner le résultat des localisations récupérées
        World world = pos1.getWorld(); // Récupère le monde de la localisation du premier coin

        double minX = Math.min(pos1.getX(), pos2.getX()); // Récupère la localisation minimale de la coordonnée 'X' entre les deux localisations récupérées en paramètre
        double minY = Math.min(pos1.getY(), pos2.getY()); // Récupère la localisation minimale de la coordonnée 'Y' entre les deux localisations récupérées en paramètre
        double minZ = Math.min(pos1.getZ(), pos2.getZ()); // Récupère la localisation minimale de la coordonnée 'Z' entre les deux localisations récupérées en paramètre
        double maxX = Math.max(pos1.getX(), pos2.getX()); // Récupère la localisation maximale de la coordonnée 'X' entre les deux localisations récupérées en paramètre
        double maxY = Math.max(pos1.getY(), pos2.getY()); // Récupère la localisation maximale de la coordonnée 'Y' entre les deux localisations récupérées en paramètre
        double maxZ = Math.max(pos1.getZ(), pos2.getZ()); // Récupère la localisation maximale de la coordonnée 'Z' entre les deux localisations récupérées en paramètre

        /* ⬇️ Pour la coordonnée 'X' minimale récupérée allant jusqu'à la coordonnée 'X' maximale d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬇️ */
        for(double x = minX; x < maxX; x += distance) {

            result.add(new Location(world, x, pos1.getY(), pos1.getZ()));
            result.add(new Location(world, x, pos1.getY(), pos2.getZ()));
            result.add(new Location(world, x, pos2.getY(), pos1.getZ()));
            result.add(new Location(world, x, pos2.getY(), pos2.getZ()));
        }
        /* ⬆️ Pour la coordonnée 'X' minimale récupérée allant jusqu'à la coordonnée 'X' maximale récupérée d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬆️ */


        /* ⬇️ Pour la coordonnée 'Y' minimale récupérée allant jusqu'à la coordonnée 'Y' maximale récupérée d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬇️ */
        for(double y = minY; y < maxY; y += distance) {

            result.add(new Location(world, pos1.getX(), y, pos1.getZ()));
            result.add(new Location(world, pos1.getX(), y, pos2.getZ()));
            result.add(new Location(world, pos2.getX(), y, pos1.getZ()));
            result.add(new Location(world, pos2.getX(), y, pos2.getZ()));
        }
        /* ⬆️ Pour la coordonnée 'Y' minimale récupérée allant jusqu'à la coordonnée 'Y' maximale récupérée d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬆️ */


        /* ⬇️ Pour la coordonnée 'Z' minimale récupérée allant jusqu'à la coordonnée 'Z' maximale récupérée d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬇️ */
        for(double z = minZ; z < maxZ; z += distance) {

            result.add(new Location(world, pos1.getX(), pos1.getY(), z));
            result.add(new Location(world, pos1.getX(), pos2.getY(), z));
            result.add(new Location(world, pos2.getX(), pos1.getY(), z));
            result.add(new Location(world, pos2.getX(), pos2.getY(), z));
        }
        /* ⬆️ Pour la coordonnée 'Z' minimale récupérée allant jusqu'à la coordonnée 'Z' maximale récupérée d'un intervalle
           de la distance définit entre les particules, alors on ajoute chaque localisation étant censé générer les bords du cuboïde ⬆️ */

        return Collections.unmodifiableCollection(result);  // Retourne une collection de la liste récupérant le résultat des localisations générant un cube
    }
                                /* ----------------------------------------------------------------------------- */
                                /* ----------------------------------------------------------------------------- */
                                /* ----------------------------------------------------------------------------- */
                                /* ----------------------------------------------------------------------------- */

    /**
     * Fait apparaître des particules pour le joueur demandé dans les {@link Location localistions} rentrées.
     *
     * @param p Le Joueur en question qui verra les particules
     * @param locations Les {@link Location localisation}s en question
     * @param runnable  Doit-on effectuer une tâche répétée ?
     * @param taskName Le nom de la {@link BukkitTask tâche} à effectuer (si le paramètre runnable est défini sur 'vrai', sinon 'null')
     *
     */
    public void show(Player p, Collection<Location> locations, boolean runnable, String taskName) {

        // ⬇️ Si le type de données de la particule n'est un pas un type 'VIDE', on envoie un message d'erreur au joueur et on crée alors une exception ⬇️ //
        if(this.particle.getDataType() != Void.class) {

            this.task = null; // Définit la tâche actuelle sur 'null'

            removeTask(p.getUniqueId(), taskName); // Supprime la tâche en cours en question du Joueur, s'il y a
            p.sendMessage(ChatFormatting.RED + "Une erreur est survenue à l'affichage de la particule, veuillez contacter un développeur ou un administrateur !"); // Erreur au Joueur

            // Une Exception est apparue détaillant l'erreur
            throw new IllegalArgumentException(AinsiColor.RED + "Cette particule '" + AinsiColor.YELLOW + this.particle.name() + AinsiColor.RED + "' n'est pas valide avec \"ParticleUtil\" ! Veuillez essayer une autre particule ayant un type de donnée 'VOID' !");
        }
        // ⬆️ Si le type de données de la particule n'est un pas un type 'VIDE', on envoie un message d'erreur au joueur et on crée alors une exception ⬆️ //

                                                    /* ------------------------------------------------ */
                                                    /* ------------------------------------------------ */

        /* Si on demande d'effectuer une tâche répétée, Pour toutes les localisations récupérées en paramètre, on affiche ensuite une particule demandée au joueur
           à la localisation en question en définissant une tâche à répéter dans une boucle */
        if(runnable) {

            /* Initialise la tâche à réaliser, dans la boucle répétée, on effectue une action :
               Pour toutes les localisations récupérées en paramètre, on affiche une particule demandée au joueur à la localisation en question */
            this.task = new BukkitRunnable() { public void run() { locations.forEach(loc -> p.spawnParticle(particle, loc, 3)); } }.runTaskTimerAsynchronously(UtilityMain.getInstance(), 0L, 5L);

            setTask(p.getUniqueId(), taskName, this.task); // Ajoute une nouvelle tâche en question du joueur dans sa liste des tâches en cours

        /* Sinon, Pour toutes les localisations récupérées en paramètre, on affiche ensuite une particule demandée au joueur à la localisation en question
           sans définir de tâche */
        } else locations.forEach(loc -> p.spawnParticle(this.particle, loc, 3));
    }

                                /* ----------------------------------------------------------------------------- */
                                /* ----------------------------------------------------------------------------- */

    /**
     * Récupère la {@link BukkitTask tâche} qui est en cours.
     *
     *
     * @return La {@link BukkitTask tâche} qui est en cours.
     */
    public BukkitTask getTask() { return task; }

                                /* ----------------------------------------------------------------------------- */

    /**
     * Récupère une {@link BukkitTask tâche} en cours pour un joueur en question du {@link ParticleUtil gestionnaire de Particule}.
     *
     * @param uuid L'UUID du Joueur qui est associé à la {@link BukkitTask tâche}
     * @param taskName Le nom de la {@link BukkitTask tâche} à récupérer
     *
     * @return La {@link BukkitTask tâches}  en question qui est en cours.
     */
    public static BukkitTask getTask(UUID uuid, String taskName) {

        if(!particleTasks.containsKey(uuid) || !particleTasks.get(uuid).containsKey(taskName)) return null;
        return particleTasks.get(uuid).get(taskName);
    }

    /**
     * Définit une {@link BukkitTask tâche} en cours pour un joueur en question dans le {@link ParticleUtil gestionnaire de Particule}.
     *
     * @param uuid L'UUID du Joueur qui sera associé à la {@link BukkitTask tâche}
     * @param taskName Le nom de la {@link BukkitTask tâche} à enregistrer
     * @param task La {@link BukkitTask tâche} à enregistrer
     *
     */
    public static void setTask(UUID uuid, String taskName, BukkitTask task) {

        Map<String, BukkitTask> taskMap = new HashMap<>();
        taskMap.putIfAbsent(taskName, task);

        particleTasks.putIfAbsent(uuid, taskMap);
        if(particleTasks.containsKey(uuid) && particleTasks.get(uuid).containsKey(taskName)) particleTasks.get(uuid).replace(taskName, task);
    }

    /**
     * Supprime une {@link BukkitTask tâche} en cours d'un joueur en question du {@link ParticleUtil gestionnaire de Particule}.
     *
     * @param uuid UUID du Joueur qui est associé à la {@link BukkitTask tâche}
     * @param taskName Le nom de la {@link BukkitTask tâche} à supprimer
     *
     */
    public static void removeTask(UUID uuid, String taskName) {

        BukkitTask task = getTask(uuid, taskName); // Récupère la tâche actuelle associée au joueur du gestionnaire de particulière

        // ⬇️ Si la tâche actuelle du gestionnaire de particulière n'est pas null et n'est pas annulé, alors on l'annule et on supprime la tâche ⬇️ //
        if(task != null && !task.isCancelled()) {

            task.cancel(); // Annule la tâche en question

            /* ⬇️ On vérifie si le joueur à bien des tâches en cours, alors on supprime la tâche en question, si on la trouve,
               puis on le supprime de la liste s'il n'a aucune autre tâche en cours ⬇️ */
            if(particleTasks.containsKey(uuid)) {

                Map<String, BukkitTask> taskMap = particleTasks.get(uuid); // On récupère la liste des tâches en cours pour le joueur en question

                taskMap.remove(taskName); // On supprime la tâche en question de la liste, s'il s'y trouve
                if(taskMap.isEmpty()) particleTasks.remove(uuid); // On supprime le joueur de la liste des tâches en cours, s'il n'en a aucun en cours
            }
            /* ⬆️ On vérifie si le joueur à bien des tâches en cours, alors on supprime la tâche en question, si on la trouve,
               puis on le supprime de la liste s'il n'a aucune autre tâche en cours ⬆️ */
        }
        // ⬆️ Si la tâche actuelle du gestionnaire de particulière n'est pas null et n'est pas annulé, alors on l'annule et on supprime la tâche ⬆️ //
    }

                                                        /* --------------------------------------------- */

    /**
     * Supprime toute les {@link BukkitTask tâches} d'un joueur en question du {@link ParticleUtil gestionnaire de Particule} qui sont en cours.
     *
     * @param uuid L'UUID du Joueur qui est associé à la {@link BukkitTask tâche}
     *
     */
    public static void removeAllTask(UUID uuid) {

        // ⬇️ On vérifie si le joueur à bien des tâches en cours, alors on supprime toutes ses tâches ⬇️ //
        if(particleTasks.containsKey(uuid)) {

            particleTasks.get(uuid).values().forEach(task -> { if(!task.isCancelled()) task.cancel(); }); // On annule les tâches en cours
            particleTasks.clear(); // On supprime toutes les tâches du Joueur
        }
        // ⬆️ On vérifie si le joueur à bien des tâches en cours, alors on supprime toutes ses tâches ⬆️ //
    }
}
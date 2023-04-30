package fr.TheSakyo.EvhoUtility.utils.api;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class Metrics {

    private final Plugin plugin; // Variable récupérant le plugin actuel

    private final MetricsBase metricsBase; // Variable récupérant la 'class' étant une base de Metrics

    /*******************************************************/
    /*******************************************************/

    /**
     * Instancie un nouveau {@link Metrics}
     *
     * @param plugin Le Plugin associé à {@link Metrics}
     * @param serviceId l'Identifiant du Service {@link Metrics} (Ex : Ressource BStats)
     */
    public Metrics(JavaPlugin plugin, int serviceId) {

        this.plugin = plugin; // Initialise le plugin

        /*****************************************/

        File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats"); // Récupère le dossier des Ressources BStats du Serveur
        File configFile = new File(bStatsFolder, "config.yml"); // Récupère fichier de configuration des Ressources BStats du Serveur
        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile); // Recharge le fichier de configuration

        /*****************************************/

        // ⬇️ Si l'UUID du Serveur n'est pas défini dans le fichier de configuration en question alors, on ajoute certaine configuration par défaut ⬇️ //
        if(!config.isSet("serverUuid")) {

            config.addDefault("enabled", Boolean.TRUE); // Active les ressources BStats
            config.addDefault("serverUuid", UUID.randomUUID().toString()); // Définit une UUID pour le Serveur
            config.addDefault("logFailedRequests", Boolean.FALSE); // Désactive les logs lors d'erreur de requête
            config.addDefault("logSentData", Boolean.FALSE); // Désactive les logs lors d'envoi de données
            config.addDefault("logResponseStatusText", Boolean.FALSE); // Désactive les logs lors de réponse de status

            /****************************/

            // ⬇️ ~~~ Définit une en tête au fichier de configuration en question ~~~ ⬇️ //

            List<String> headers = getStringsHeaders();
            config.options().setHeader(headers).copyDefaults(true);

            // ⬆️ ~~~ Définit une en tête au fichier de configuration en question ~~~ ⬆️ //

            /****************************/

            try { config.save(configFile);
            } catch(IOException ignored) {}
        }
        // ⬆️ Si l'UUID du Serveur n'est pas défini dans le fichier de configuration en question alors, on ajoute certaine configuration par défaut ⬆️ //


        boolean enabled = config.getBoolean("enabled", true); // Récupère si les ressources BStats sont activés depuis le fichier config
        String serverUUID = config.getString("serverUuid"); // Récupère l'UUID du Serveur depuis le fichier config
        boolean logErrors = config.getBoolean("logFailedRequests", false); // Récupère si les logs sont activés lors d'erreur de requête
        boolean logSentData = config.getBoolean("logSentData", false); // Récupère si les logs sont activés lors d'envoi de données
        boolean logResponseStatusText = config.getBoolean("logResponseStatusText", false); // Récupère si les logs sont activés lors de réponse de status

        Objects.requireNonNull(plugin); // Vérifie si le plugin n'est pas null

        // Récupère une instance de la 'class' étant une base de Metrics
        this.metricsBase = new MetricsBase("bukkit", serverUUID, serviceId, enabled, this::appendPlatformData, this::appendServiceData, submitDataTask -> Bukkit.getScheduler().runTask(plugin, submitDataTask), plugin::isEnabled, (message, error) -> this.plugin.getLogger().log(Level.WARNING, message, error), message -> this.plugin.getLogger().log(Level.INFO, message), logErrors, logSentData, logResponseStatusText);
    }

    @NotNull
    private static List<String> getStringsHeaders() {

        String firstLine = "BStats (https://bStats.org) recueille certaines informations de base pour les auteurs de plugins, comme le nombre de personnes qui utilisent " +
                "leur plugin et le nombre total de joueurs.";

        String secondLine = "Il est recommandé de garder BStats activé, mais si vous n'êtes pas à l'aise avec cela, vous pouvez désactiver ce paramètre.";

        String thirdLine = "Il n'y a pas de performance associée à l'activation des mesures, et les données envoyées à bStats sont totalement anonymes.\"";

        /****************************/

        return List.of(firstLine, secondLine, thirdLine);
    }

    /**
     * Ajoute des graphiques customisés pour Metrics pour le Serveur
     *
     * @param chart Le graphique customisé en question
     */
    public void addCustomChart(CustomChart chart) { this.metricsBase.addCustomChart(chart); }

    /**
     * Ajoute des données pour la Plateforme Metrics pour le Serveur
     *
     * @param builder Les données en Objet JSON
     */
    private void appendPlatformData(JsonObjectBuilder builder) {

        builder.appendField("playerAmount", getPlayerAmount());
        builder.appendField("onlineMode", Bukkit.getOnlineMode() ? 1 : 0);
        builder.appendField("bukkitVersion", Bukkit.getVersion());
        builder.appendField("bukkitName", Bukkit.getName());
        builder.appendField("javaVersion", System.getProperty("java.version"));
        builder.appendField("osName", System.getProperty("os.name"));
        builder.appendField("osArch", System.getProperty("os.arch"));
        builder.appendField("osVersion", System.getProperty("os.version"));
        builder.appendField("coreCount", Runtime.getRuntime().availableProcessors());
    }

    /**
     * Ajoute des données pour le Service Metrics pour le Serveur
     *
     * @param builder Les données pour le Service en Objet JSON
     */
    private void appendServiceData(JsonObjectBuilder builder) { builder.appendField("pluginVersion", this.plugin.getPluginMeta().getVersion()); }

    /**
     * Récupère le nombre de Joueurs avec Metrics pour le Serveur
     *
     * @return Le nombre de Joueurs avec Metrics pour le Serveur
     */
    private int getPlayerAmount() {

        try {

            Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
            return onlinePlayersMethod.getReturnType().equals(Collection.class) ? ((Collection<?>)onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).size() :
                    ((Player[])onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).length;

        } catch(Exception e) { return Bukkit.getOnlinePlayers().size(); }
    }

                     /* ---------------------------------------------------------------------------------------- */
                     /* ---------------------------------------------------------------------------------------- */

    /**
     * 'Class' pour La Base de Metrics
     *
     */
    public static class MetricsBase {

        public static final String METRICS_VERSION = "2.2.1"; // Variable récupérant la version de Metrics

        private static final ScheduledExecutorService scheduler; // Variable récupérant la tâche d'exécution pour Metrics

        private static final String REPORT_URL = "https://bStats.org/api/v2/data/%s"; // Variable récupérant l'URL de report de BStats

        private final String platform; // Variable récupérant le nom de la plateforme pour Metrics

        private final String serverUuid; // Variable récupérant l'UUID du Serveur pour Metrics

        private final int serviceId; // Variable récupérant l'identifiant du Service pour Metrics

        private final Consumer<Metrics.JsonObjectBuilder> appendPlatformDataConsumer; // Variable récupérant des données pour la Plateforme Metrics

        private final Consumer<Metrics.JsonObjectBuilder> appendServiceDataConsumer; // Variable récupérant des données de Service Metrics

        private final Consumer<Runnable> submitTaskConsumer; // Variable récupérant les tâches envoyées de Metrics

        private final Supplier<Boolean> checkServiceEnabledSupplier; // Variable vérifiant les services est activés de Metrics

        private final BiConsumer<String, Throwable> errorLogger; // Variable récupérant les Erreurs loggers

        private final Consumer<String> infoLogger; // Variable récupérant les Informations loggers

        private final boolean logErrors; // Variable vérifiant si l'affichage Erreurs sont activées

        private final boolean logSentData; // Variable vérifiant si l'affichage des Données envoyées sont activées

        private final boolean logResponseStatusText; // Variable vérifiant si l'affichage des Réponses de Status sont activées

        /**
         * On initialise la tâche d'exécution pour Metrics
         */
        static { scheduler = Executors.newScheduledThreadPool(1, task -> new Thread(task, "bStats-Metrics")); }

        private final Set<Metrics.CustomChart> customCharts = new HashSet<>(); // Variable récupérant les graphiques customisés pour Metrics

        private final boolean enabled; // Variable vérifiant si Metrics est activé

        /*******************************************************/
        /*******************************************************/

        /**
         * Instancie un nouveau {@link MetricsBase}
         *
         * @param platform La Plateforme {@link Metrics}
         * @param serverUuid UUID du Serveur
         * @param serviceId l'Identifiant du Service {@link Metrics} (Ex : Ressource BStats)
         * @param enabled Active ou non {@link Metrics}
         * @param appendPlatformDataConsumer Des données pour la Plateforme {@link Metrics}
         * @param appendServiceDataConsumer  Des données de Service {@link Metrics}
         * @param submitTaskConsumer Des Tâches Envoyés pour {@link Metrics}
         * @param checkServiceEnabledSupplier Des Services activés pour {@link Metrics}
         * @param errorLogger Système de logs pour les erreurs
         * @param infoLogger Système de logs pour les informations
         * @param logErrors Activons-nous les Erreurs dans les logs ?
         * @param logSentData Activons-nous les Données Envoyées dans les logs ?
         * @param logResponseStatusText Activons-nous les Réponses de Status dans les logs ?
         */
        public MetricsBase(String platform, String serverUuid, int serviceId, boolean enabled, Consumer<Metrics.JsonObjectBuilder> appendPlatformDataConsumer, Consumer<Metrics.JsonObjectBuilder> appendServiceDataConsumer, Consumer<Runnable> submitTaskConsumer, Supplier<Boolean> checkServiceEnabledSupplier, BiConsumer<String, Throwable> errorLogger, Consumer<String> infoLogger, boolean logErrors, boolean logSentData, boolean logResponseStatusText) {

            this.platform = platform; // Initialise la Plateforme
            this.serverUuid = serverUuid; // Initialise UUID du Serveur
            this.serviceId = serviceId; // Initialise l'Identifiant du Service
            this.enabled = enabled; // Initialise l'Activation de Metrics
            this.appendPlatformDataConsumer = appendPlatformDataConsumer; // Initialise toutes les données de la Plateforme
            this.appendServiceDataConsumer = appendServiceDataConsumer; // Initialise toutes les données de Service
            this.submitTaskConsumer = submitTaskConsumer; // Initialise toutes les Tâches Envoyés
            this.checkServiceEnabledSupplier = checkServiceEnabledSupplier; // Initialise tous les Services Activés
            this.errorLogger = errorLogger; // Initialise le système de logs pour les Erreurs
            this.infoLogger = infoLogger; // Initialise le système de logs pour les Informations
            this.logErrors = logErrors; // Initialise l'Activation ou non des Erreurs dans les logs
            this.logSentData = logSentData; // Initialise l'Activation ou non des Données Envoyés dans les logs
            this.logResponseStatusText = logResponseStatusText; // Initialise l'Activation ou non des Réponses de Status dans les logs

            checkRelocation(); // On vérifie si n'y pas de mise à jour de localisation BStats

            if(enabled) startSubmitting(); // Si Metrics est bien activé, on démarre les tâches à envoyer
        }

        /**
         * Ajoute des graphiques customisés pour Metrics pour le Serveur
         *
         * @param chart Le graphique customisé en question
         */
        public void addCustomChart(Metrics.CustomChart chart) { this.customCharts.add(chart); }

        /**
         * Démarre les tâches envoyées de Metrics
         *
         */
        private void startSubmitting() {

            // ⬆️ On effectue une tâche et on vérifie chaques données envoyées, si Metric est désactivé ou le service actuel, on annule la tâche ⬆️ //
            Runnable submitTask = () -> {

                if(!this.enabled || !(Boolean) this.checkServiceEnabledSupplier.get()) { scheduler.shutdown(); return; }
                if(this.submitTaskConsumer != null) { this.submitTaskConsumer.accept(this::submitData); }
                else { submitData(); }
            };
            // ⬆️ On effectue une tâche et on vérifie chaques données envoyées, si Metric est désactivé ou le service actuel, on annule la tâche ⬆️ //

            /**************************************************/

            long initialDelay = (long)(60000.0D * (3.0D + Math.random() * 3.0D)); // On récupère le délai initial
            long secondDelay = (long)(60000.0D * Math.random() * 30.0D); // On récupère le délai en seconde

            /**************************************************/

            // ⬇️ On démarre les tâches envoyées ⬇️ //
            scheduler.schedule(submitTask, initialDelay, TimeUnit.MILLISECONDS);
            scheduler.scheduleAtFixedRate(submitTask, initialDelay + secondDelay, 1800000L, TimeUnit.MILLISECONDS);
            // ⬆️ On démarre les tâches envoyées ⬆️ //
        }

        /**
         * Recharge toutes les données Metrics à envoyé
         *
         */
        private void submitData() {

            Metrics.JsonObjectBuilder baseJsonBuilder = new Metrics.JsonObjectBuilder(); // On récupère les données de base Metrics
            this.appendPlatformDataConsumer.accept(baseJsonBuilder); // On accepte ses données dans la plateforme

            Metrics.JsonObjectBuilder serviceJsonBuilder = new Metrics.JsonObjectBuilder(); // On récupère les données de service Metrics
            this.appendServiceDataConsumer.accept(serviceJsonBuilder);// On accepte ses données dans le service

            /**************************************************/

            // On Récupère les données de graphiques Metrics //

            Metrics.JsonObjectBuilder.JsonObject[] chartData = this.customCharts.stream().map(customChart -> customChart.getRequestJsonObject(this.errorLogger, this.logErrors))
                    .filter(Objects::nonNull).toArray(JsonObjectBuilder.JsonObject[]::new);

            // On Récupère les données de graphiques Metrics //

            /**************************************************/

            // ⬇️ On exécute ensuite une tâche pour essayer d'envoyer to
            serviceJsonBuilder.appendField("id", this.serviceId); // On ajoute aux données de service Metrics l'identifiant du service
            serviceJsonBuilder.appendField("customCharts", chartData); // On ajoute aux données de service Metrics les données de graphiques Metrics

            baseJsonBuilder.appendField("service", serviceJsonBuilder.build()); // On ajoute aux de base Metrics le service Metrics
            baseJsonBuilder.appendField("serverUUID", this.serverUuid); // On ajoute aux données de base Metrics l'UUID du Serveur
            baseJsonBuilder.appendField("metricsVersion", "2.2.1"); // On ajoute aux données de base Metrics la version Metrics
            Metrics.JsonObjectBuilder.JsonObject data = baseJsonBuilder.build(); // On construit alors les données

            /**************************************************/

            // ⬇️ On exécute ensuite une tâche pour essayer d'envoyer toutes ses données, sinon on affiche une erreur ⬇️ //
            scheduler.execute(() -> {

                try { sendData(data); }
                catch(Exception e) { if(this.logErrors) this.errorLogger.accept("Impossible de soumettre les données métriques de bStats", e); }
            });
            // ⬆️ On exécute ensuite une tâche pour essayer d'envoyer toutes ses données, sinon on affiche une erreur ⬆️ //
        }

        /**
         * Envoie les données Metrics récupéré
         *
         * @param data Les Données en question en Objet JSON
         *
         */
        private void sendData(Metrics.JsonObjectBuilder.JsonObject data) throws Exception {

            // Si l'envoie de données dans les logs est activée, on informe les données qui seront envoyées
            if(this.logSentData) this.infoLogger.accept("Envoi des données métriques bStats : " + data.toString());

            // ⬇️ Récupère l'URL de report de BStats, puis on essaie de se connecter à cette URL ⬇️ //
            String url = String.format("https://bStats.org/api/v2/data/%s", this.platform);
            HttpsURLConnection connection = (HttpsURLConnection)(new URL(url)).openConnection();
            // ⬆️ Récupère l'URL de report de BStats, puis on essaie de se connecter à cette URL ⬆️ //

            /**************************************************/

            // ⬇️ On exécute ensuite une tâche pour essayer d'envoyer to
            byte[] compressedData = compress(data.toString());  // On compresse les données qui seront envoyées

            // ⬇️ On définit les méthodes de requêtes à la connexion de l'URL de report de BStats ⬇️ //

            connection.setRequestMethod("POST");
            connection.addRequestProperty("Accept", "application/json");
            connection.addRequestProperty("Connection", "close");
            connection.addRequestProperty("Content-Encoding", "gzip");
            connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("User-Agent", "Metrics-Service/1");
            connection.setDoOutput(true);

            // ⬆️ On définit les méthodes de requêtes à la connexion de l'URL de report de BStats ⬆️ //

            /**************************************************/

            /* ⬇️ On récupère les données de l'URL qui ont été reçu, et on essaie d'ajouter à celle-ci les données en question à envoyer,
               sinon si une exception survient, on affiche l'erreur ⬇️ */
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            try {

                outputStream.write(compressedData);
                outputStream.close();

            } catch(Throwable throwable) {

                try { outputStream.close(); }
                catch(Throwable throwableException) { throwable.addSuppressed(throwableException); }
                throw throwable;
            }
             /* ⬆️ On récupère les données de l'URL qui ont été reçu, et on essaie d'ajouter à celle-ci les données en question à envoyer,
               sinon si une exception survient, on affiche l'erreur ⬆️ */

            /**************************************************/

            StringBuilder builder = new StringBuilder(); // On construit une chaîne de caractère

            /* ⬇️ On récupère les informations de la connexion de l'URL de report de BStats, on récupère don les informations de chaques lignes dans la chaîne de caractère
               qui a été construit, sinon si une exception survient, on affiche l'erreur ⬇️ */
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            try {

                String line;
                while((line = bufferedReader.readLine()) != null) builder.append(line);
                bufferedReader.close();

            } catch(Throwable throwable) {

                try { bufferedReader.close(); }
                catch(Throwable throwableException) { throwable.addSuppressed(throwableException); }
                throw throwable;
            }
            /* ⬇️ On récupère les informations de la connexion de l'URL de report de BStats, on récupère don les informations de chaques lignes dans la chaîne de caractère
               qui a été construit, sinon si une exception survient, on affiche l'erreur ⬇️ */

            /**************************************************/

            // Si l'envoie de réponse de status dans les logs est activée, on informe dans le slogs les réponses récupérés
            if(this.logResponseStatusText) this.infoLogger.accept("Envoyé des données à bStats et reçu une réponse : " + builder);
        }

        /**
         * Vérifie la mise à jour de localisation BStats
         *
         */
        private void checkRelocation() {

            // Si la relocalisation BStats est null, ou alors est fausse, relocalise les ressources BStats
            if(System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {

                String defaultPackage = new String(new byte[] { 111, 114, 103, 46, 98, 115, 116, 97, 116, 115 }); // Récupère le paquet par défaut
                String examplePackage = new String(new byte[] { 121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 103, 101 }); // Récupère un exemple de paquet

                /**************************************************/

                // Si le nom du paquet de Metrics commence par le paquet par défaut ou l'exemple de 'package', on informe que n'a pas pu être relocalisé.
                if(MetricsBase.class.getPackage().getName().startsWith(defaultPackage) || MetricsBase.class.getPackage().getName().startsWith(examplePackage)) throw new IllegalStateException("bStats Metrics class has not been relocated correctly!");
            }
        }

        /**
         * Permet de compresser une chaîne de caractère
         *
         * @param str La chaîne de caractère à compresser
         *
         * @return La chaîne de caractère en byte[] (octet)
         * @throws IOException Une exception est survenue ?, une erreur sera donc détaillée dans la console !
         */
        private static byte[] compress(String str) throws IOException {

            if(str == null) return null; // Si la chaîne de caractère est null, on renvoie null

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // On initialise un tableau d'octet de sortie
            GZIPOutputStream gzip = new GZIPOutputStream(outputStream); // On récupère le flux de sortie à partir du tableau d'octet de sortie

            /**************************************************/

            /* ⬇️ On essaie d'envoyer sur le flux de sortie les octets de la chaîne de caractère en question en ayant un format UTF_8,
               sinon, si une exception survient, on envoie une erreur ⬇️ */
            try {

                gzip.write(str.getBytes(StandardCharsets.UTF_8));
                gzip.close();

            } catch(Throwable throwable) {

                try { gzip.close(); }
                catch(Throwable throwableException) { throwable.addSuppressed(throwableException); }
                throw throwable;
            }
            /* ⬆️ On essaie d'envoyer sur le flux de sortie les octets de la chaîne de caractère en question en ayant un format UTF_8,
               sinon, si une exception survient, on envoie une erreur ⬆️ */

            /**************************************************/

            return outputStream.toByteArray();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link AdvancedBarChart}
     *
     */
    public static class AdvancedBarChart extends CustomChart {

        private final Callable<Map<String, int[]>> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception {

            Metrics.JsonObjectBuilder valuesBuilder = new Metrics.JsonObjectBuilder();
            Map<String, int[]> map = this.callable.call();

            /****************************************************/

            if(map == null || map.isEmpty()) return null;

            /****************************************************/

            boolean allSkipped = true;
            for(Map.Entry<String, int[]> entry : map.entrySet()) {

                if(entry.getValue().length == 0) continue;
                allSkipped = false;
                valuesBuilder.appendField(entry.getKey(), entry.getValue());
            }

            /****************************************************/

            if(allSkipped) return null;
            return(new Metrics.JsonObjectBuilder()).appendField("values", valuesBuilder.build()).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
                     /* ---------------------------------------------------------------------------------------- */
                    /* ---------------------------------------------------------------------------------------- */

    /**
     * 'CLASS' REPRÉSENTANT LES GRAPHIQUES DE DONNÉES CUSTOMISÉE {@link CustomChart}
     *
     */
    public static abstract class CustomChart {

        private final String chartId;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected CustomChart(String chartId) {

            if(chartId == null) throw new IllegalArgumentException("chartId ne doit pas être nul.");
            this.chartId = chartId;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public Metrics.JsonObjectBuilder.JsonObject getRequestJsonObject(BiConsumer<String, Throwable> errorLogger, boolean logErrors) {

            Metrics.JsonObjectBuilder builder = new Metrics.JsonObjectBuilder();
            builder.appendField("chartId", this.chartId);

            /****************************************************/

            try {

                Metrics.JsonObjectBuilder.JsonObject data = getChartData();

                if(data == null) return null;
                builder.appendField("data", data);

            } catch(Throwable t) { if(logErrors) errorLogger.accept("Failed to get data for custom chart with id " + this.chartId, t); return null; }

            /****************************************************/

            return builder.build();
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected abstract Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~ */
        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject defaultChartData(Callable<Map<String, Integer>> callable) throws Exception {

            Metrics.JsonObjectBuilder valuesBuilder = new Metrics.JsonObjectBuilder();
            Map<String, Integer> map = callable.call();

            /****************************************************/

            if(map == null || map.isEmpty()) return null;
            boolean allSkipped = true;

            /****************************************************/

            for(Map.Entry<String, Integer> entry : map.entrySet()) {

                if(entry.getValue() == 0) continue;
                allSkipped = false;
                valuesBuilder.appendField(entry.getKey(), entry.getValue());
            }

            /****************************************************/

            if(allSkipped) return null;
            return(new Metrics.JsonObjectBuilder()).appendField("values", valuesBuilder.build()).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
                     /* ---------------------------------------------------------------------------------------- */

    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link SimpleBarChart}
     *
     */
    public static class SimpleBarChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception {

            Metrics.JsonObjectBuilder valuesBuilder = new Metrics.JsonObjectBuilder();
            Map<String, Integer> map = this.callable.call();

            /****************************************************/

            if(map == null || map.isEmpty()) return null;
            for(Map.Entry<String, Integer> entry : map.entrySet()) { valuesBuilder.appendField(entry.getKey(), new int[] { entry.getValue()}); }

            /****************************************************/

            return(new Metrics.JsonObjectBuilder()).appendField("values", valuesBuilder.build()).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link MultiLineChart}
     *
     */
    public static class MultiLineChart extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception { return this.defaultChartData(this.callable); }
    }

                     /* ---------------------------------------------------------------------------------------- */
    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link AdvancedPie}
     *
     */
    public static class AdvancedPie extends CustomChart {

        private final Callable<Map<String, Integer>> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception { return this.defaultChartData(this.callable); }
    }
                     /* ---------------------------------------------------------------------------------------- */
    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link SingleLineChart}
     *
     */
    public static class SingleLineChart extends CustomChart {

        private final Callable<Integer> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public SingleLineChart(String chartId, Callable<Integer> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception {

            int value = this.callable.call();
            if(value == 0) return null;

            /***********************************/

            return(new Metrics.JsonObjectBuilder()).appendField("value", value).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link SimplePie}
     *
     */
    public static class SimplePie extends CustomChart {

        private final Callable<String> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public SimplePie(String chartId, Callable<String> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        protected Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception {

            String value = this.callable.call();
            if(value == null || value.isEmpty()) return null;

            /***********************************/

            return(new Metrics.JsonObjectBuilder()).appendField("value", value).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */

    /**
     * 'CLASS' ÉTANT UN GRAPHIQUE DE DONNÉES CUSTOMISÉE {@link DrillDownPie}
     *
     */
    public static class DrillDownPie extends CustomChart {

        private final Callable<Map<String, Map<String, Integer>>> callable;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public DrillDownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {

            super(chartId);
            this.callable = callable;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public Metrics.JsonObjectBuilder.JsonObject getChartData() throws Exception {

            Metrics.JsonObjectBuilder valuesBuilder = new Metrics.JsonObjectBuilder();

            /***********************************/

            Map<String, Map<String, Integer>> map = this.callable.call();
            if(map == null || map.isEmpty()) return null;

            /***********************************/

            boolean reallyAllSkipped = true;
            for(Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {

                Metrics.JsonObjectBuilder valueBuilder = new Metrics.JsonObjectBuilder();

                /*********************/

                boolean allSkipped = true;
                for(Map.Entry<String, Integer> valueEntry : map.get(entryValues.getKey()).entrySet()) {

                    valueBuilder.appendField(valueEntry.getKey(), valueEntry.getValue());
                    allSkipped = false;
                }

                /*********************/

                if(!allSkipped) {

                    reallyAllSkipped = false;
                    valuesBuilder.appendField(entryValues.getKey(), valueBuilder.build());
                }
            }

            /***********************************/

            if(reallyAllSkipped) return null;
            return(new Metrics.JsonObjectBuilder()).appendField("values", valuesBuilder.build()).build();
        }
    }

                     /* ---------------------------------------------------------------------------------------- */
                     /* ---------------------------------------------------------------------------------------- */
                     /* ---------------------------------------------------------------------------------------- */

    /**
     * 'CLASS' PERMETTANT DE CONSTRUIRE UN OBJET JSON
     *
     */
    public static class JsonObjectBuilder {

        private StringBuilder builder = new StringBuilder();

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        private boolean hasAtLeastOneField = false;

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder() { this.builder.append("{"); }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder appendNull(String key) {

            appendFieldUnescaped(key, "null");
            return this;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder appendField(String key, String value) {

            if(value == null) throw new IllegalArgumentException("La valeur JSON ne doit pas être nulle.");

            /***********************************/

            appendFieldUnescaped(key, "\"" + escape(value) + "\"");
            return this;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder appendField(String key, int value) {

            appendFieldUnescaped(key, String.valueOf(value));
            return this;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder appendField(String key, JsonObject object) {

            if(object == null) throw new IllegalArgumentException("La valeur JSON ne doit pas être nulle.");

            /***********************************/

            appendFieldUnescaped(key, object.toString());
            return this;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObjectBuilder appendField(String key, String[] values) {

            if(values == null) throw new IllegalArgumentException("La valeur JSON ne doit pas être nulle.");

            /***********************************/

            String escapedValues = Arrays.stream(values).map(value -> "\"" + escape(value) + "\"").collect(Collectors.joining(","));
            appendFieldUnescaped(key, "[" + escapedValues + "]");
            return this;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public void appendField(String key, int[] values) {

            if (values == null) throw new IllegalArgumentException("La valeur JSON ne doit pas être nulle.");

            /***********************************/

            String escapedValues = Arrays.stream(values).<CharSequence>mapToObj(String::valueOf).collect(Collectors.joining(","));
            appendFieldUnescaped(key, "[" + escapedValues + "]");
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public void appendField(String key, JsonObject[] values) {

            if(values == null) throw new IllegalArgumentException("La valeur JSON ne doit pas être nulle.");

            /***********************************/

            String escapedValues = Arrays.stream(values).map(JsonObject::toString).collect(Collectors.joining(","));
            appendFieldUnescaped(key, "[" + escapedValues + "]");
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        private void appendFieldUnescaped(String key, String escapedValue) {

            if(this.builder == null) throw new IllegalStateException("La valeur JSON ne doit pas être nulle.");
            if(key == null) throw new IllegalArgumentException("La clé JSON ne doit pas être nulle.");

            /***********************************/

            if(this.hasAtLeastOneField) this.builder.append(",");
            this.builder.append("\"").append(escape(key)).append("\":").append(escapedValue);
            this.hasAtLeastOneField = true;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        public JsonObject build() {

            if(this.builder == null) throw new IllegalStateException("JSON a déjà été construit.");

            /***********************************/

            JsonObject object = new JsonObject(this.builder.append("}").toString());
            this.builder = null;
            return object;
        }

        /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

        private static String escape(String value) {

            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < value.length(); i++) {

                char c = value.charAt(i);
                if(c == '"') {builder.append("\\\""); }
                else if (c == '\\') { builder.append("\\\\"); }
                else if (c <= '\017') { builder.append("\\u000").append(Integer.toHexString(c)); }
                else if (c <= '\037') { builder.append("\\u00").append(Integer.toHexString(c)); }
                else builder.append(c);
            }

            /***********************************/

            return builder.toString();
        }

        public static class JsonObject {

            private final String value;

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

            public JsonObject(String value) { this.value = value; }

            /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

            public String toString() { return this.value; }
        }
    }
}

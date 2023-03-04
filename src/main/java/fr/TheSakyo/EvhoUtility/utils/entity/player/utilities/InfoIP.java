package fr.TheSakyo.EvhoUtility.utils.entity.player.utilities;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.TheSakyo.EvhoUtility.UtilityMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimeZone;
import java.util.UUID;

public class InfoIP {

	/* -------------------------------------------------------------------------- */

	private String country = "Unknown"; //Variable "inconnu" pour récupérer le pays
	private String countryCode = "Unknown"; //Variable "inconnu" pour récupérer le code du pays
	private String region = "Unknown"; //Variable "inconnu" pour récupérer la region
	private String city = "Unknown"; //Variable "inconnu" pour récupérer la ville
	private String inetProvider = "Unknown"; //Variable "inconnu" pour récupérer le fournisseur d'accès internet
	private String weather = "Unknown"; //Variable "inconnu" pour récupérer la météo
	private String forecast = "Unknown"; //Variable "inconnu" pour récupérer la prévision météo
	private TimeZone timezone = TimeZone.getTimeZone("GMT"); //Variable pour récupérer la zone

	/* -------------------------------------------------------------------------- */

	// Récupère les informations du joueur en question //
	public static InfoIP get(Player player) {
		return get(player.getUniqueId(), player.getAddress().getHostString());
	}
	// Récupère les informations du joueur en question //

	// Récupère les informations de l'ip en question //
	public static InfoIP get(UUID uuid, String ip) {
		return new InfoIP(uuid, ip);
	}
	// Récupère les informations de l'ip en question //

	/* -------------------------------------------------------------------------- */

	// ⬇️ Méthode pour essayer de récupérer des informations à partir de l'ip en question ⬇️ //
	protected InfoIP(UUID uuid, String ip) {

		// On essaie de se connecter en récupérant des informations à partir de l'IP gràce à un site spécifique //
		try {

			JsonObject json = JsonParser.parseString(getStringFromURL(uuid, new URL("http://ip-api.com/json/" + ip))).getAsJsonObject();

			//On vérifie si on arrive à trouver le status étant un succés
			if(json.get("status").getAsString().equals("success")) {

				country = json.get("country").getAsString(); //On définit le pays retourné
				countryCode = json.get("countryCode").getAsString(); //On le code du pays définit retourné
				region = json.get("region").getAsString(); //On définit la région retournée
				city = json.get("city").getAsString(); //On définit la ville retournée
				inetProvider = json.get("org").getAsString(); //On définit le fournisseur d'accès internet cretourné
				timezone = TimeZone.getTimeZone(json.get("timezone").getAsString()); //On définit la zone retournée
			}

		} catch(MalformedURLException e) { Bukkit.getLogger().warning("InfoIP : Erreur en faisant les trucs du web."); }
		// On essaie de se connecter en récupérant des informations à partir de l'IP gràce à un site spécifique //

		UtilityMain.cacheInfo.replace(uuid, this); //Créer le joueur dans le cache
	}
	// ⬆️ Méthode pour essayer de récupérer des informations à partir de l'ip en question ⬆️ //

	/* -------------------------------------------------------------------------- */

	 public TimeZone getTimeZone() { return timezone; } //Méthode pour récupèrer la zone
	 public String getCountry() { return country; } //Méthode pour récupèrer le pays
	 public String getCountryCode() { return countryCode; } //Méthode le code du pays (fr, en etc...)
	 public String getRegion() { return region; } //Méthode pour récupèrer la région
	 public String getCity() { return city; } //Méthode pour récupèrer la ville
	 public String getInetProvider() { return inetProvider; } //Méthode pour récupèrer le fournisseur d'accès internet
	 public String getWeather() { return weather; } //Méthode pour récupèrer la météo
	 public String getWeatherForecast() { return forecast; } //Méthode pour récupèrer la prévision météo

		/* -------------------------------------------------------------------------- */

	// Méthode pour récupérer une chaîne de catactère dans la redirection d'une 'url' spécifié //
	protected String getStringFromURL(UUID uuid, URL url, String[]... requestProps)  {

			StringBuilder builder = new StringBuilder();

			try {

				URLConnection connection = url.openConnection(); //Variable pour récupérer la conexion de l'url
				connection.setDoOutput(true);

				// *** Essait de se connecter à l'url en définissant une propriété de requête à la connexion *** //
				for(String[] requestProp : requestProps) { connection.addRequestProperty(requestProp[0], requestProp[1]); }
				connection.connect();
				// *** Essait de se connecter à l'url en définissant une propriété de requête à la connexion *** //

					/* ---------------------------------- */

				// *** Récupère les différentes informations retournée *** //
				try(BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {

					String line;
					while((line = reader.readLine()) != null) builder.append(line);
				}
				// *** Récupère les différentes informations retournée *** //

				return builder.toString();

		} catch(IOException e) { throw new RuntimeException(e); }
	}
	// Méthode pour récupérer une chaîne de catactère dans la redirection d'une 'url' spécifié //

		/* -------------------------------------------------------------------------- */
}
package fr.TheSakyo.EvhoUtility.utils.reflections;

import dependancies.xyz.jpenilla.reflectionremapper.ReflectionRemapper;
public class RemapReflection {

    /* Crée une nouvelle instance 'ReflectionRemapper' en utilisant
       les mappages de réobfuscation de 'Paper' à partir de son '.jar' */
	static final ReflectionRemapper reflectRemapper = ReflectionRemapper.forReobfMappingsInPaperJar();

	/****************************************************************************************************************/
	/* PETITES MÉTHODES POUR RÉCUPÉRER UNE "CLASS" ET UTILISER LE REMAPPAGE POUR BIEN UTILISER LE 'NMS' de "PAPER" */
	/***************************************************************************************************************/

	// ⬇️ ** Récupère une variable de "class" à remappé ** ⬇️ //
	public static String remapFieldName(Class<?> c, String string) {

		Class<?> remapClass = remapClassName(c); //On récupère la "class" en paramètre pour le remappé

		//Retourne la variable remappé à partir de la "class" remappé
		return reflectRemapper.remapFieldName(remapClass, string);
	}
	// ⬆️ ** Récupère une variable de "class" à remappé ** ⬆️ //

					/* ------------------- */

	// ⬇️ ** Récupère une méthode de "class" à remappé ** ⬇️ //
	public static String remapMethodName(Class<?> c, String string, Class<?>... paramTypes) {

		Class<?> remapClass = remapClassName(c); //On récupère la "class" en paramètre pour le remappé

		//Retourne la variable remappé à partir de la "class" remappé
		return reflectRemapper.remapMethodName(remapClass, string, paramTypes);
	}
	// ⬆️ ** Récupère une méthode de "class" à remappé ** ⬆️ //

					/* ------------------- */

	// ⬇️️ ** Récupère la Class à Remappé ** ⬇️️ //
	public static Class<?> remapClassName(Class<?> c) {

		//Récupère le nom de la "class" remappé à partir de la "class" en paramètre
		final String runtimeName = reflectRemapper.remapClassName(c.getName());

		try { return Class.forName(runtimeName); } //Essaie de récupèrer la "Class" remappé par son nom

		//Sinon, si la "Class" est introuvable, on affiche une erreur détaillée à la console
		catch(ClassNotFoundException e) { e.printStackTrace(System.err); }

		return null; // On retourne "NULL" (dans le cas, si rien n'a été retourné).
	}
	// ⬆️️ ** Récupère la Class à Remappé ** ⬆️️ //

	/****************************************************************************************************************/
	/* PETITES MÉTHODES POUR RÉCUPÉRER UNE "CLASS" ET UTILISER LE REMAPPAGE POUR BIEN UTILISER LE 'NMS' de "PAPER" */
	/***************************************************************************************************************/
}

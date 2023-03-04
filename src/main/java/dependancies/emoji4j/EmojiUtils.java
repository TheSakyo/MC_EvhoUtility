package dependancies.emoji4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils to deal with emojis
 * 
 * @author Krishna Chaitanya Thota
 *
 */
public class EmojiUtils extends AbstractEmoji {

	
	/**
	 * Obtient un emoji par unicode, code court, entité html décimale ou entité html hexadécimale.
	 *
	 * @param code unicode, code court, entité html décimale ou entité html hexadécimale
	 *
	 * @return L'Emoji en question
	 */
	public static Emoji getEmoji(String code) {

		/*Matcher m = shortCodePattern.matcher(code);

		// test pour les shortcodes avec deux points
		if(m.find()) { code = m.group(1); }*/
		
		Emoji emojiResult = null;

		for(Emoji emoji : EmojiManager.data()) {


			if(emoji.getEmoji() != null && emoji.getEmoji().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
			if(emoji.getEmoticons() != null && emoji.getEmoticons().contains(code)) { emojiResult = emoji; break; }
			if(emoji.getAliases() != null && emoji.getAliases().contains(code)) { emojiResult = emoji; break; }

											/* ------------------------------------------- */

			if(emoji.getDecimalSurrogateHtml() != null && emoji.getDecimalSurrogateHtml().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
			if(emoji.getDecimalHtmlShort() != null && emoji.getDecimalHtmlShort().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
			if(emoji.getDecimalHtml() != null && emoji.getDecimalHtml().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
			if(emoji.getHexHtmlShort() != null && emoji.getHexHtmlShort().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
			if(emoji.getHexHtml() != null && emoji.getHexHtml().equalsIgnoreCase(code)) { emojiResult = emoji; break; }
		}

		return emojiResult;
	}

	/**
	 * Vérifie si un Emoji existe pour l'entité unicode, code court, décimale ou entité html hexadécimale
	 * 
	 * @param code unicode, code court, entité html décimale ou entité html hexadécimale
	 *
	 * @return Une valeur booléenne
	 */
	public static boolean isEmoji(String code) { return getEmoji(code) == null ? false : true; }

	/**
	 * Convertit les courts codes des emojis ou les entités html en chaîne avec des emojis.
	 *
	 * @param text Chaîne à emojifier
	 *
	 * @return Chaîne emojifiée
	 */
	public static String emojify(String text) { return emojify(text, 0); }
	
	private static String emojify(String text, int startIndex) {

		text = processStringWithRegex(text, shortCodeOrHtmlEntityPattern, startIndex, true);
		
		// Les émotions doivent être traitées dans un deuxième temps.
		// Cela évitera les conflits avec les shortcodes. Par exemple : :p:p devrait ne pas être traité comme un shortcode, mais comme une émoticône.
		text = processStringWithRegex(text, EmojiManager.getEmoticonRegexPattern(), startIndex, true);
		return text;
	}

	/**
	 * Méthode commune utilisée pour traiter la chaîne à remplacer par des emojis
	 * 
	 * @param text la chaîne de caractère à remplacer
	 * @param pattern Le {@link Pattern Patronage} à traité
	 * @param startIndex L'Index où démarrer le traitage
	 * @param recurseEmojify Récupérons-nous l'Emoji remplacé ?
	 *
	 * @return Une chaîne de caractère remplacé
	 */
	private static String processStringWithRegex(String text, Pattern pattern, int startIndex, boolean recurseEmojify) {

		Matcher matcher = pattern.matcher(text);
		StringBuilder sb = new StringBuilder();
		int resetIndex = 0;
		
		if(startIndex > 0) { matcher.region(startIndex, text.length()); }

		while(matcher.find()) {

			String emojiCode = matcher.group();

			Emoji emoji = getEmoji(emojiCode);

			// Remplace les mots-clés correspondants par des emojis.
			if(emoji != null) { matcher.appendReplacement(sb, emoji.getEmoji()); }
			else {

				if(htmlSurrogateEntityPattern2.matcher(emojiCode).matches()) {

					String highSurrogate1 = matcher.group("H1");
					String highSurrogate2 = matcher.group("H2");
					String lowSurrogate1 = matcher.group("L1");
					String lowSurrogate2 = matcher.group("L2");
					matcher.appendReplacement(sb, processStringWithRegex(highSurrogate1+highSurrogate2, shortCodeOrHtmlEntityPattern, 0, false));

					//basically this handles &#junk1;&#10084;&#65039;&#junk2; scenario
					//verifies if &#junk1;&#10084; or &#junk1; are valid emojis via recursion
					//if not move past &#junk1; and reset the cursor to &#10084;
					if(sb.toString().endsWith(highSurrogate2)) { resetIndex = sb.length() - highSurrogate2.length(); }
					else { resetIndex = sb.length(); }

					sb.append(lowSurrogate1);
					sb.append(lowSurrogate2);
					break;

				} else if(htmlSurrogateEntityPattern.matcher(emojiCode).matches()) {

					// Pourrait être des entités html individuelles présumer que paire de substitution
					String highSurrogate = matcher.group("H");
					String lowSurrogate = matcher.group("L");

					matcher.appendReplacement(sb, processStringWithRegex(highSurrogate, htmlEntityPattern, 0, true));
					resetIndex = sb.length();

					sb.append(lowSurrogate);
					break;

				} else matcher.appendReplacement(sb, emojiCode);
			}
		}
		matcher.appendTail(sb);
		
		/* Ne pas récurer emojify en arrivant ici par htmlSurrogateEntityPattern2 ... ainsi nous avons la possibilité de vérifier si la queue fait partie d'une entité
		  de substitution */
		if(recurseEmojify && resetIndex > 0) { return emojify(sb.toString(), resetIndex); }
		return sb.toString();
	}

	/**
	 * Compte tous les emojis valides passés en chaîne.
	 *
	 * @param text Chaîne dans laquelle il faut compter les caractères emoji.
	 *
	 * @return retourne le nombre d'emojis.
	 */
	public static int countEmojis(String text) {

		String htmlifiedText = htmlify(text);

		// regex pour identifier les entités html dans le texte htmlifié
		Matcher matcher = htmlEntityPattern.matcher(htmlifiedText);

		int counter = 0;
		while(matcher.find()) {

			String emojiCode = matcher.group();
			if(isEmoji(emojiCode)) counter++;
		}
		return counter;
	}

	/**
	 * Convertit les caractères unicode du texte en caractères décimaux html correspondants entités.
	 *
	 * @param text Chaîne de caractère à htmlifier
	 *
	 * @return Chaîne de caractères htmlifiée
	 */
	public static String htmlify(String text) {

		String emojifiedStr = emojify(text);
		return htmlifyHelper(emojifiedStr, false, false);
	}

	/**
	 * Convertit les caractères unicode du texte en caractères décimaux html correspondants entités.
	 *
	 * @param text Chaîne à htmlifier
	 * @param asSurrogate Doit-on convertir une chaîne de caractère comportant des valeurs héxadécimales
	 *
	 * @return Chaîne de caractères htmlifiée
	 */
	public static String htmlify(String text, boolean asSurrogate) {

		String emojifiedStr = emojify(text);
		return htmlifyHelper(emojifiedStr, false, asSurrogate);
	}
	
	/**
	 * Convertit les caractères unicode du texte en caractères hexadécimaux html correspondants entités.
	 *
	 * @param text Chaîne de caractère hexadécimale à htmlifier
	 *
	 * @return chaîne de caractère hexadécimale htmlifiée
	 */
	public static String hexHtmlify(String text) {
		String emojifiedStr = emojify(text);
		return htmlifyHelper(emojifiedStr, true, false);
	}

	

	/**
	 * Convertit les emojis, les htmls hexagonaux, décimaux, les émoticônes dans une chaîne en codes courts.
	 *
	 * @param text Chaîne de caractère à raccourcir
	 *
	 * @return chaîne de caractère courte codifiée
	 */
	public static String shortCodify(String text) {

		String emojifiedText = emojify(text);

		// TODO - cette approche est moche, il faut trouver une manière optimale de remplacer les emojis
		// Aucun moyen idéal trouvé afin d'identifier les emojis dans la chaîne passée.
		// les caractères comme <3 ont plusieurs caractères, mais n'en ont pas de paires de substitution
		// donc à ce stade, nous itérons à travers tous les emojis et les remplaçons par des codes courts
		for(Emoji emoji : EmojiManager.data()) {

			String shortCodeBuilder = ":" + emoji.getAliases().get(0) + ":";
			emojifiedText = emojifiedText.replace(emoji.getEmoji(), shortCodeBuilder);
		}
		return emojifiedText;
	}
	
	/**
	 * Supprime tous les caractères emojis de la chaîne passée. Cette méthode ne supprime pas les caractères html, les shortcodes.
	 * Pour supprimer tous les shortcodes, caractères html, emojify et ensuite passer la chaîne emojifiée à cette méthode.
	 *
	 * @param emojiText Chaîne dont il faut supprimer les emojis.
	 *
	 * @return chaîne dépouillée d'emojis
	 */
	public static String removeAllEmojis(String emojiText) {
		
		for(Emoji emoji : EmojiManager.data()) emojiText = emojiText.replace(emoji.getEmoji(), "");
		return emojiText;
	}
}

package dependancies.emoji4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;


/**
 * Emoji
 *
 * @author Krishna Chaitanya Thota
 *
 */
public class Emoji extends AbstractEmoji {

	private String emoji;

	private List<String> aliases;

	private String hexHtml;

	private String decimalHtml;

	private String decimalHtmlShort;

	private String hexHtmlShort;

	private String decimalSurrogateHtml;
	
	private List<String> emoticons;

						/* --------------------------------------------------------------------------------------------------------------------- */

	/**
	 * Obtient le caractère emoji unicode
	 *
	 * @return Le caractère emoji unicode
	 */
	public String getEmoji() { return emoji; }

	/**
	 * Définit le caractère emoji unicode
	 *
	 * @return Définit le caractère emoji unicode
	 */
	public void setEmoji(String emoji) {

		setDecimalHtml(EmojiUtils.htmlifyHelper(emoji,false, false));
		setHexHtml(EmojiUtils.htmlifyHelper(emoji,true, false));
		
		setDecimalSurrogateHtml(EmojiUtils.htmlifyHelper(emoji,false, true));
		this.emoji = emoji;
	}

	/**
	 * Obtient la liste de tous les shortcodes pour l'emoji. Le shortcode n'est pas entouré de deux points.
	 *
	 * @return Liste de tous les alias
	 */
	public List<String> getAliases() { return aliases; }

	/**
	 * Définit la liste de tous les shortcodes pour l'emoji. Le shortcode n'est pas entouré de deux points.
	 *
	 * @param aliases liste de tous les shortcodes à définir
	 *
	 */
	public void setAliases(List<String> aliases) { this.aliases = aliases; }

	/**
	 * Obtient l'entité html hexadécimale pour l'emoji.
	 *
	 * @return L'Entité html hexadécimale pour l'emoji
	 */
	public String getHexHtml() { return hexHtml; }

	/**
	 * Définit l'entité html hexadécimale pour l'emoji.
	 *
	 * @param hexHtml L'Entité html hexadécimale à définir
	 *
	 */
	public void setHexHtml(String hexHtml) {

		this.hexHtml = hexHtml;
		Matcher matcher = htmlSurrogateEntityPattern.matcher(hexHtml);

		if(matcher.find()) {

			String signifiantHtmlEntity = matcher.group("H");
			this.setHexHtmlShort(signifiantHtmlEntity);

		} else this.setHexHtmlShort(hexHtml);

	}

	/**
	 * Obtient l'entité html décimale pour l'emoji.
	 *
	 * @return L'Entité html décimale pour l'emoji.
	 */
	public String getDecimalHtml() { return decimalHtml; }

	/**
	 * Définit l'entité html décimale pour l'emoji.
	 *
	 * @param decimalHtml L'Entité html décimale à définir
	 */
	public void setDecimalHtml(String decimalHtml) {

		this.decimalHtml = decimalHtml;
		Matcher matcher = htmlSurrogateEntityPattern.matcher(decimalHtml);

		if(matcher.find()) {

			String signifiantHtmlEntity = matcher.group("H");
			this.setDecimalHtmlShort(signifiantHtmlEntity);

		} else this.setDecimalHtmlShort(decimalHtml);

	}

	/**
	 * Obtient la liste des émoticônes associées à l'emoji.
	 *
	 * @return Liste de toutes les émoticônes associées à l'emoji.
	 */
	public List<String> getEmoticons() { return emoticons; }

	/**
	 * Définit la liste des émoticônes associées à l'emoji.
	 *
	 * @param emoticons La liste de toutes les émoticônes associées à définir.
	 *
	 */
	public void setEmoticons(List<String> emoticons) { this.emoticons = emoticons; }

	/**
	 * Obtient le HTML de substitution décimal.
	 *
	 * @return Le HTML de substitution décimal.
	 */
	public String getDecimalSurrogateHtml() { return decimalSurrogateHtml; }

	/**
	 * Définit le HTML de substitution décimal.
	 *
	 * @param decimalSurrogateHtml Le HTML de substitution décimal à définir.
	 *
	 */
	public void setDecimalSurrogateHtml(String decimalSurrogateHtml) { this.decimalSurrogateHtml = decimalSurrogateHtml; }

	/**
	 * Obtient le décimal HTML cours.
	 *
	 * @return Le décimal HTML cours.
	 */
	public String getDecimalHtmlShort() { return decimalHtmlShort; }

	/**
	 * Définit le décimal HTML cours.
	 *
	 * @param decimalHtmlShort Le décimal HTML cours à définir.
	 *
	 */
	public void setDecimalHtmlShort(String decimalHtmlShort) { this.decimalHtmlShort = decimalHtmlShort; }

	/**
	 * Obtient l'hexadécimal HTML cours.
	 *
	 * @return L'Hexadécimal HTML cours.
	 */
	public String getHexHtmlShort() { return hexHtmlShort; }

	/**
	 * Définit l'hexadécimal HTML cours.
	 *
	 * @param hexHtmlShort L'Hexadécimal HTML cours à définir.
	 *
	 */
	public void setHexHtmlShort(String hexHtmlShort) { this.hexHtmlShort = hexHtmlShort; }

}

package dependancies.emoji4j;

import java.util.regex.Pattern;

/**
 * 
 * @author Krishna Chaitanya Thota
 *
 */
public abstract class AbstractEmoji {

	protected static final Pattern shortCodePattern = Pattern.compile(":(\\w+):");
	
	protected static final Pattern htmlEntityPattern = Pattern.compile("&#\\w+;");
	
	protected static final Pattern htmlSurrogateEntityPattern = Pattern.compile("(?<H>&#\\w+;)(?<L>&#\\w+;)");
	
	protected static final Pattern htmlSurrogateEntityPattern2 = Pattern.compile("&#\\w+;&#\\w+;&#\\w+;&#\\w+;");
	
	protected static final Pattern shortCodeOrHtmlEntityPattern = Pattern.compile(":\\w+:|(?<H1>&#\\w+;)(?<H2>&#\\w+;)(?<L1>&#\\w+;)(?<L2>&#\\w+;)|(?<H>&#\\w+;)(?<L>&#\\w+;)|&#\\w+;");

						/* --------------------------------------------------------------------------------------------------------------------- */

	/**
	 * Aide pour convertir les caractères emoji en entités html dans une chaîne de caractères
	 * 
	 * @param text Chaîne de caractères à htmlifier
	 * @param isHex La chaîne de caractère comporte-t-il des valeurs hexadécimales ?
	 * @return chaîne de caractères htmlifiée
	 */
	protected static String htmlifyHelper(String text, boolean isHex, boolean isSurrogate) {

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < text.length(); i++) {

			int ch = text.codePointAt(i);

			if(ch <= 128) { sb.appendCodePoint(ch); }
			else if(ch > 128 && (ch < 159 || (ch >= 55296 && ch <= 57343))) {
				// Aucun caractères html illégaux
				// voir : http://en.wikipedia.org/wiki/Character_encodings_in_HTML
				// à la section sur les caractères illégaux
				continue;
			} else {
				if(isHex) { sb.append("&#x" + Integer.toHexString(ch) + ";"); }
				else {

					if(isSurrogate) {

						double H = Math.floor((ch - 0x10000) / 0x400) + 0xD800;
						double L = ((ch - 0x10000) % 0x400) + 0xDC00;
						sb.append("&#"+String.format("%.0f", H)+";&#"+String.format("%.0f", L)+";");

					} else sb.append("&#" + ch + ";");
				}
			}
		}
		return sb.toString();
	}
}

package dependancies.emoji4j;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;


/**
 * Charge tous les emojis à partir d'un paquet de ressources.
 *
 * @author Krishna Chaitanya Thota
 *
 */
public class EmojiManager {
	private static Pattern emoticonRegexPattern;
	
	private static List<Emoji> emojiData;

	static {
		try {

			Type listType = new TypeToken<ArrayList<Emoji>>() {}.getType();

			InputStream stream = EmojiManager.class.getResourceAsStream("/emoji.json");
			Scanner s = new Scanner(stream).useDelimiter("\\A");
 			String result = s.hasNext() ? s.next() : "";

			emojiData = new Gson().fromJson(result, listType);

			stream.close();
			processEmoticonsToRegex();

		} catch(Exception e) { throw new RuntimeException(e.getMessage(), e); }
	}

	/**
	 * Retourne les données complètes de l'emoji
	 *
	 * @return Liste d'objets emoji
	 */
	public static List<Emoji> data() { return emojiData; }
	
	/**
	 * Retourne la Regex qui peut correspondre à toutes les émoticônes dans une chaîne de caractères.
	 *
	 * @return regex pattern for emoticons
	 */
	public static Pattern getEmoticonRegexPattern() { return emoticonRegexPattern; }


	/**

	 * Traite les données Emoji en emoticon regex
	 */
	private static void processEmoticonsToRegex() {
		
		List<String> emoticons = new ArrayList<String>();
		
		for(Emoji e: emojiData) {

			if(e.getEmoticons() != null) emoticons.addAll(e.getEmoticons());
		}
		
		// La liste des émotions doit être prétraitée pour traiter les instances de sous-tranches comme :-) :-
		// Sans ce prétraitement, les émoticônes dans une chaîne ne seront pas traitées correctement.
		for(int i = 0; i < emoticons.size(); i++) {

			for(int j = i+1; j < emoticons.size(); j++) {

				String o1 = emoticons.get(i);
				String o2 = emoticons.get(j);
				
				if(o2.contains(o1)) {

					String temp = o2;
					emoticons.remove(j);
					emoticons.add(i, temp);
				}
			}
		}
		emoticonRegexPattern = getAsciiEmojiRegex(emoticons);
	}

	public static void addStopWords(String... stopwords) {

		if(stopwords ==null || stopwords.length == 0) return;

		StringBuilder sb = new StringBuilder();
		for(String stopword : stopwords) {

			sb.append(stopword);
			sb.append("|");
		}

		String emojiRegex = emoticonRegexPattern.toString();
		sb.append(emojiRegex);
		emoticonRegexPattern =  Pattern.compile(sb.toString());
	}

	public static void clearStopWords() {

		//reconstruire la liste d'emoji
		processEmoticonsToRegex();
	}

	private static Pattern getAsciiEmojiRegex(List<String> emojiList) {

		StringBuilder sb = new StringBuilder();
		for(String emoticon: emojiList) {

			if(sb.length() != 0) sb.append("|");
			sb.append(Pattern.quote(emoticon));
		}
		return Pattern.compile(sb.toString());
	}
}

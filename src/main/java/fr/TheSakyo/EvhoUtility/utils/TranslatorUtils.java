package fr.TheSakyo.EvhoUtility.utils;

import java.io.IOException;

import dependancies.org.jsoup.Jsoup;
import dependancies.org.jsoup.nodes.Document;
import dependancies.org.jsoup.select.Elements;

public class TranslatorUtils {

  public static String translate(String msg, String from, String to) {

    Document doc;
    
    String resulting = null;
    
	try { 
		
		//Connection au lien
		doc = Jsoup.connect("https://translate.google.com.tr/m?sl=" + from + "&tl=" + to + "&hl=" + to + "&q=" + msg.replaceAll(" ", "+")).get();
		
	    //Sélectionne la traduction (ressemble à ceci : <div class="result-container">)
	    Elements result = doc.select("div.result-container");
	    
	    //Affiche le texte
	 	resulting = result.text();
	 	
	} catch (IOException e) { e.printStackTrace(System.err); } 
	
	return resulting;

  }
}
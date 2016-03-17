package odysseus.ocr.fr;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Correcteur {
	/* Le matcher qui permet d'obtenir l'index du dernier match de la regex */
	public static int indexOf(Pattern pattern, String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}

	/* Le correcteur qui remplace les portions incorrectes */
	public String matcherCorrecteur(String input, String regex, String name){
		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(input);
		while (matcher.find()){
			int index=indexOf(Pattern.compile(">(?<![A-Za-z,.;:!?éèêiîïùûôë!\"#$%&'()*+,\\-/=@ \t\r\n\f])"), matcher.group());
			String longueurString=regex.substring(0, index+1);
			int longueur=longueurString.length();
			String lettre;
			if (!matcher.group().substring(longueur+1, longueur+2).equals("<")){
				lettre=matcher.group().substring(longueur, longueur+2);
			}
			else{
				lettre=matcher.group().substring(longueur, longueur+1);
			}
							System.out.println("il faut changer le(s) caractère(s) : "+lettre+" ("+name+")");
			String aRemplacer=matcher.group();
			input=input.replace(aRemplacer, lettre);
		}
		return input;
	}
	
	/* Rattachement des hyperliens notes en notes de bas de page */
	public String rattachementNotesHyperLien(String input, String numeroDeNote){
		String note="<p rend=\"margin noindent\"><ref target=\"#_ftnref"+numeroDeNote+"\">\\["+numeroDeNote+"\\]</ref>[A-Za-z0-9,.;:!?éèêiîïùûôëàâçÉ!\"#$%&'()*+,\\-/=@\\s—«»œ|°]+</p>";
		String note2="<p rend=\"noindent\"><ref target=\"#_ftnref"+numeroDeNote+"\">\\["+numeroDeNote+"\\]</ref>[A-Za-z0-9,.;:!?éèêiîïùûôëàâçÉ!\"#$%&'()*+,\\-/=@\\s—œ|°«»]+</p>";
		String note3="<p><ref target=\"#_ftnref"+numeroDeNote+"\">\\["+numeroDeNote+"\\]</ref>[A-Za-z0-9,.;:!?éèêiîïùûôëàâçÉ!\"#$%&'()*+,\\-/=@\\s—«»œ|°]+</p>";
		
		Pattern pattern=Pattern.compile(note);
		Matcher matcher=pattern.matcher(input);
		Pattern pattern2=Pattern.compile(note2);
		Matcher matcher2=pattern2.matcher(input);
		Pattern pattern3=Pattern.compile(note3);
		Matcher matcher3=pattern3.matcher(input);

		while (matcher.find()){
			System.out.println("trouvé pour la note : "+numeroDeNote);
			int stringTotale=(matcher.group().length());
			int longueurAvantRemp=("<p rend=\"margin noindent\"><ref target=\"#_ftnref"+numeroDeNote+"\">["+numeroDeNote+"]</ref>").length();
			int longueurASoustraire=("</p>").length();
			String stringRemplacement=matcher.group().substring(longueurAvantRemp, stringTotale-longueurASoustraire);
//			System.out.println(stringRemplacement);
			input=input.replace("<ref target=\"#_ftn"+numeroDeNote+"\">["+numeroDeNote+"]</ref>", "<note place=\"bottom\"><phr rend=\"ls\">"+stringRemplacement+"</phr></note>");
			input=input.replace(matcher.group(), "");
		}
		while (matcher2.find()){
			System.out.println("trouvé pour la note2 : "+numeroDeNote);
			int stringTotale2=(matcher2.group().length());
			int longueurAvantRemp=("<p rend=\"noindent\"><ref target=\"#_ftnref"+numeroDeNote+"\">["+numeroDeNote+"]</ref>").length();
			int longueurASoustraire=("</p>").length();
			String stringRemplacement=matcher2.group().substring(longueurAvantRemp, stringTotale2-longueurASoustraire);
			input=input.replace("<ref target=\"#_ftn"+numeroDeNote+"\">["+numeroDeNote+"]</ref>", "<note place=\"bottom\"><phr rend=\"ls\">"+stringRemplacement+"</phr></note>");
			input=input.replace(matcher2.group(), "");
		}
		while (matcher3.find()){
			System.out.println("trouvé pour la note3 : "+numeroDeNote);
			int stringTotale=(matcher3.group().length());
			int longueurAvantRemp=("<p><ref target=\"#_ftnref"+numeroDeNote+"\">["+numeroDeNote+"]</ref>").length();
			int longueurASoustraire=("</p>").length();
			String stringRemplacement=matcher3.group().substring(longueurAvantRemp, stringTotale-longueurASoustraire);
//			System.out.println(stringRemplacement);
			input=input.replace("<ref target=\"#_ftn"+numeroDeNote+"\">["+numeroDeNote+"]</ref>", "<note place=\"bottom\"><phr rend=\"ls\">"+stringRemplacement+"</phr></note>");
			input=input.replace(matcher3.group(), "");
		}
		return input;	
	}
	
	/* Met en quote.c toute séquence d'au moins x mots entre guillemets ouvrants et fermants*/
	public String characterQuote(String input){
		String quote="\\s«[A-Za-z0-9,.;:!?éèêiîïùûôëàâçÉ!\"#$%&'()*+,\\-/=@—œ|°\\s]{2,}»";
		Pattern pattern=Pattern.compile(quote);
		Matcher matcher=pattern.matcher(input);
		while (matcher.find()){
			System.out.println("une quote trouvée ! : "+matcher.group());
			int longueurAvantRemp=(" «").length();
			int stringTotale=matcher.group().length();
			int longueurASoustraire=("»").length();
			String stringRemplacement=matcher.group().substring(longueurAvantRemp, stringTotale-longueurASoustraire);
//			System.out.println("remplacement : "+stringRemplacement);
			input=input.replace(matcher.group(), " <quote>«"+stringRemplacement+"»</quote>");
		}
		return input;
	}
	public String erreursOCR(String input){
		String aCorriger="V[A-ZÉÔÎÛ]{1}[a-zéèêiîïùûôëàâç]+";
		Pattern pattern=Pattern.compile(aCorriger);
		Matcher matcher=pattern.matcher(input);
		while (matcher.find()){
			String replacement1=matcher.group().substring(1, 2);
			String replacement2=matcher.group().substring(2, matcher.group().length());
//			System.out.println("trouvé une erreur d'OCR : "+matcher.group()+", remplacé par : "+replacement1+replacement2);
			input=input.replace(matcher.group(), "l'"+replacement1+replacement2);
		}
		return input;
	}
	public String erreursParagraphes(String input){
		String aCorriger="</l>\\s*<l>[a-zéèêiîïùûôëàâç]+";
		Pattern pattern=Pattern.compile(aCorriger);
		Matcher matcher=pattern.matcher(input);
		while (matcher.find()){
			input=input.replaceAll("</l>\\s*<l>", " ");
		}
		return input;
	}
	
}

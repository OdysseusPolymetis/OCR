package odysseus.ocr.fr;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class ModifyOCR {
	/* Méthode pour mettre tout le txt d'entrée sur une ligne */ 
	public static String stringEnUneLigne(BufferedReader buffIn) throws IOException {
		StringBuilder tout = new StringBuilder();
		String ligne;
		while( (ligne = buffIn.readLine()) != null) {
			tout.append(ligne);
		}
		return tout.toString();
	}

	/* La Main, où on donne les valeurs des regex à trouver et à corriger, et où on appelle le printeur */
	public static void main(String[] args) throws Exception {
		System.setProperty( "file.encoding", "UTF-8" );
		FileReader fileIn = new FileReader("./Documents/TestComplet.txt");
		BufferedReader buffIn = new BufferedReader(fileIn);
		String content = stringEnUneLigne(buffIn);
		content=SpellCheckerOCR.spellCheck(content);
//		
		content=content.replaceAll("'", "'");
		content=content.replaceAll("’", "'");
		content=content.replaceAll(" ", " ");
		content=content.replace(" ", " ");
		content=content.replace("\u2014", "-");
		content=content.replaceAll("•","");
		content=content.replaceAll("\\(\\(\\s", "« ");
		content=content.replaceAll("«", "«");
		String stringItalique="<hi rend=\"i\">[A-Za-z,.;:!?éèêiîïùûôë!\"#$%&'()*+,\\-/=@ \t\r\n\f]{1,2}</hi>";
		String stringPhr="</phr>[A-Za-z,.;:!?éèêiîïùûôë!\"#$%&'()*+,\\-/=@ \t\r\n\f]{1,2}<phr rend=\"ls\">";
		String stringSeg="<seg rend=\"grame\">[A-Za-z,.;:!?éèêiîïùûôë!\"#$%&'()*+,\\-/=@ \t\r\n\f]{1,2}</seg>";
		String stringSpelle="<seg rend=\"spelle\">[A-Za-z,.;:!?éèêiîïùûôë!\"#$%&'()*+,\\-/=@ \t\r\n\f]{1,2}</seg>";
		
		
		Correcteur correcteur=new Correcteur();
		content=correcteur.matcherCorrecteur(content, stringItalique, "italique");
		content=correcteur.matcherCorrecteur(content, stringPhr, "Phr");
		content=correcteur.matcherCorrecteur(content, stringSeg, "segment");
		content=correcteur.matcherCorrecteur(content, stringSpelle, "Spelle");
		for (int i=0; i<100; i++){
			content=correcteur.rattachementNotesHyperLien(content, Integer.toString(i));
		}
		content=correcteur.characterQuote(content);
		content=correcteur.erreursOCR(content);
		content=correcteur.erreursParagraphes(content);
		content=content.replace("<?xml version encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		System.out.println(content);
		
		XMLPrettyPrint printer=new XMLPrettyPrint();
		printer.prettyPrint(content, "UTF-8", "./Corrections/Results.xml");
	}
}

package odysseus.ocr.fr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.PlainTextDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

public class SpellCheckerOCR {

	public static String encodageUTF8(String entree) throws UnsupportedEncodingException{
		byte[] someBytes = entree.getBytes();
		String encodingName = "UTF-8";
		String result = new String ( someBytes, encodingName );

		return result;
	}

	public static List<String> makeLatinDict(String path) throws Exception{
		File file=new File(path);
		List<String>lines=readLines(file);
		List<String>entreesDictionnaire=new ArrayList<String>();
		for (String line:lines){
			String units []=line.split("\u0009");
			for (String unit:units){
				String tmpLat="";
				if (unit.matches("[0-9]+")==false){
					if (unit.contains(" ")){
						tmpLat=unit.substring(0, unit.indexOf(" "));
					}
					else{
						tmpLat=unit;
					}
					entreesDictionnaire.add(tmpLat);
				}
			}
		}
		FileWriter writer = new FileWriter("./Dictionnaire/LatinWords.txt"); 
		for(String str: entreesDictionnaire) {
			writer.write(str.toLowerCase()+"\n");
		}
		writer.close();


		return entreesDictionnaire;
	}


	public static String spellCheck(String txt) throws Exception{
		File dir = new File("c:/spellchecker/");
				File dirLat = new File("c:/spellcheckerLat/");
		String result="";
		txt=txt.replaceAll("\\s*[.]\\s*", ". ");
		txt=txt.replaceAll("\u00A0", " ");
		List<String> items = Arrays.asList(txt.split("\\s"));

		Directory directory = FSDirectory.open(dir);
				Directory directoryLat = FSDirectory.open(dirLat);

		@SuppressWarnings("resource")
		SpellChecker spellChecker = new SpellChecker(directory);
				@SuppressWarnings("resource")
				SpellChecker spellCheckerLat = new SpellChecker(directoryLat);

		File file=new File("./Dictionnaire/DictionnaireGutenberg.txt");
		File file2=new File("./Dictionnaire/ODS5.txt");
		InputStreamReader isr = new InputStreamReader(new FileInputStream(new File("./Dictionnaire/DictionnaireGutenberg.txt")), "UTF-8");
		PlainTextDictionary dictionary = new PlainTextDictionary(isr);
		StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		spellChecker.indexDictionary(dictionary, config, true);
		List<String>lines=readLines(file);
		List<String>lines2=readLines(file2);
		List<String>latin=makeLatinDict("./Dictionnaire/LatinDict.txt");

				InputStreamReader isrLat = new InputStreamReader(new FileInputStream(new File("./Dictionnaire/LatinWords.txt")), "UTF-8");
				PlainTextDictionary dictionaryLat = new PlainTextDictionary(isrLat);
				StandardAnalyzer analyzerLat = new StandardAnalyzer(Version.LUCENE_36);
				IndexWriterConfig configLat = new IndexWriterConfig(Version.LUCENE_36, analyzerLat);
				spellCheckerLat.indexDictionary(dictionaryLat, configLat, true);

		Set<String>setTousMotsFrancais=new HashSet<String>();
		setTousMotsFrancais.addAll(lines);
		setTousMotsFrancais.addAll(lines2);
		String[] tableauMotsFrancais = setTousMotsFrancais.toArray(new String[setTousMotsFrancais.size()]);
		//		setTousMots.addAll(latin);
		List<String>motsFrSansLat=new ArrayList<String>();
		for (int counter=0;counter<items.size();counter++){
			String item=items.get(counter);
			String missingPunct="";
			if (!item.contains("<")&&!item.contains(">")){
				String tmp=item;
				tmp=tmp.replaceAll("[{}*,;.:)(?!]+", "");
				tmp=tmp.replaceAll("\\\\", "");
				tmp=tmp.replaceAll("\\[", "");
				Pattern p = Pattern.compile(tmp+"([,;.:)(?!]+)");
				Matcher m = p.matcher(item);
				while (m.find()) {
					missingPunct=m.group(1);
				}
				if (item.matches("[a-zéèêôîûâàùïë]{1}[A-ZÉ]+[a-zéèêôîûâàùïë]*")){
					Pattern p1 = Pattern.compile("[a-zéèêôîûâàùïë]{1}[A-ZÉ]+");
					Pattern p3 = Pattern.compile("[A-ZÉ]+[a-zéèêôîûâàùïë]*");

					Matcher m1 = p1.matcher(item);
					Matcher m3 = p3.matcher(item);
					while (m1.find()&&m3.find()) {
						items.set(counter, m3.group()); 	
					}

				}
				if (item.matches("[a-zéèêôîûâàùïë]{2,}[A-ZÉ]+[a-zéèêôîûâàùïë]*")){
					Pattern p2 = Pattern.compile("[a-zéèêôîûâàùïë]{2,}[A-ZÉ]+");
					Pattern p3 = Pattern.compile("[A-ZÉ]+[a-zéèêôîûâàùïë]*");
					Matcher m2 = p2.matcher(item);
					Matcher m3 = p3.matcher(item);
					while (m2.find()&&m3.find()){
						String split []=item.split("[A-ZÉ]");
						items.set(counter, split[0]+" "+m3.group());
					}
				}

				if (item.matches("[a-z]+[A-ZÉ]+[a-zéèêôîûâàùïë]*")){
					//		        	String remplacement="";
					Pattern espaceGuillemets=Pattern.compile("«[a-z]+[A-ZÉ]+");
					Matcher espGuil=espaceGuillemets.matcher(item);
					Pattern espaceApres=Pattern.compile("[A-ZÉ]+[a-zéèêôîûâàùïë]*");
					Matcher espApr=espaceApres.matcher(item);

					while (espGuil.find()&&espApr.find()){
						String split []=item.split("[a-z]");
						items.set(counter, split[0]+" "+espApr.group());
					}
				}
				item=item.replaceAll("[*,;.:)(?!]+", "");
				item=item.replaceAll("\u00A0", "");
				item=item.replaceAll("\\\\", "");
				item=item.replaceAll("\\[", "");
				item=item.replaceAll("œ", "oe");
				item=item.replaceAll("æ", "ae");
			}
			
			if (!item.contains(" ")&&!item.matches("[A-ZÉa-zéèêôîûâàùïë]+-[A-ZÉa-zéèêôîûâàùïë]+")&&!item.equals("")&&!item.contains("<")&&!item.contains(">")&&!item.matches("^[A-ZÉ]+[a-zéèêôîûâàùïë]*")&&setTousMotsFrancais.contains(item.toLowerCase())==false&&!item.contains("rend=")&&item.contains("’")==false&&item.contains("'")==false&&latin.contains(item)==false&&StringUtils.indexOfAny(item, tableauMotsFrancais)>=0){
				List<List<String>> results = new ArrayList<List<String>>();
				DoubleWordsInDictionary.search(item, setTousMotsFrancais, new Stack<String>(), results);
				for (List<String> resultWords : results) {
					Set<String>solutions=new HashSet<String>();
			        for (@SuppressWarnings("unused") String word : resultWords) {
			            solutions.add(resultWords.get(resultWords.size()-2));
			            solutions.add(resultWords.get(resultWords.size()-1));
			        }
			       String motsSolutionsPropres=solutions.toString();
			       motsSolutionsPropres=motsSolutionsPropres.replace("[", "");
			       motsSolutionsPropres=motsSolutionsPropres.replace("]", "");
			       motsSolutionsPropres=motsSolutionsPropres.replace(",", "");
			        items.set(counter, motsSolutionsPropres);
			    }
				item=(items.get(counter));
				int suggestionsNumber=1;
				String[] suggestions = spellChecker.
						suggestSimilar(item, suggestionsNumber);

				if (suggestions!=null && suggestions.length>0&&item.contains(" ")==false) {				
					for (String word : suggestions) {
						items.set(counter, word+missingPunct);
					}
				}
				
				int suggestionsLatin = 1;
				String[] suggestionsLat = spellCheckerLat.
						suggestSimilar(item, suggestionsLatin);
				if (suggestionsLat!=null && suggestionsLat.length>0&&item.contains(" ")==false) {	
					for (String latWord : suggestionsLat) {
						System.out.println(item);
						System.out.println("proposition : "+latWord);
					}
				}

			}
		}

		StringBuffer buff=new StringBuffer();
		for (String word:items){
			buff.append(word+" ");
		}
		result=buff.toString();
		result=encodageUTF8(result);

		System.out.println(motsFrSansLat);

		return result;

	}
	public static List<String> readLines(File file) throws Exception {
		if (!file.exists()) {
			return new ArrayList<String>();
		}
		@SuppressWarnings("resource")
		BufferedReader reader = new BufferedReader(new FileReader(file));
		List<String> results = new ArrayList<String>();
		String line = reader.readLine();
		while (line != null) {
			results.add(line);
			line = reader.readLine();
		}


		return results;
	}
	static String readFile(String path, Charset encoding) 
			throws IOException 
	{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

}

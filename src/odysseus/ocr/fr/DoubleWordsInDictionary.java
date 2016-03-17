package odysseus.ocr.fr;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class DoubleWordsInDictionary {
	public static void search(String input, Set<String> dictionary,
	        Stack<String> words, List<List<String>> results) {

	    for (int i = 0; i < input.length(); i++) {
	        // take the first i characters of the input and see if it is a word
	        String substring = input.substring(0, i + 1);

	        if (dictionary.contains(substring)) {
	            // the beginning of the input matches a word, store on stack
	            words.push(substring);

	            if (i == input.length() - 1) {
	                // there's no input left, copy the words stack to results
	                results.add(new ArrayList<String>(words));
	            } else {
	                // there's more input left, search the remaining part
	                search(input.substring(i + 1), dictionary, words, results);
	            }

	            // pop the matched word back off so we can move onto the next i
	            words.pop();
	        }
	    }
	}
}

package search;

import java.io.*;
import java.util.*;

/**
 * This class encapsulates an occurrence of a keyword in a document. It stores the
 * document name, and the frequency of occurrence in that document. Occurrences are
 * associated with keywords in an index hash table.
 * 
 * @author Sesh Venugopal
 * 
 */
class Occurrence {
	/**
	 * Document in which a keyword occurs.
	 */
	String document;
	
	/**
	 * The frequency (number of times) the keyword occurs in the above document.
	 */
	int frequency;
	
	/**
	 * Initializes this occurrence with the given document,frequency pair.
	 * 
	 * @param doc Document name
	 * @param freq Frequency
	 */
	public Occurrence(String doc, int freq) {
		document = doc;
		frequency = freq;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "(" + document + "," + frequency + ")";
	}
}

/**
 * This class builds an index of keywords. Each keyword maps to a set of documents in
 * which it occurs, with frequency of occurrence in each document. Once the index is built,
 * the documents can searched on for keywords.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in descending
	 * order of occurrence frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash table of all noise words - mapping is from word to itself.
	 */
	HashMap<String,String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashMap<String,String>(100,2.0f);
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.put(word,word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeyWords(docFile);
			//System.out.println(kws); //delete this line later
			mergeKeyWords(kws);
		}
		
	}

	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeyWords(String docFile) 
	throws FileNotFoundException {
		HashMap<String,Occurrence> keyWord = new HashMap<String,Occurrence>(200, 2.0f);
		Scanner keyboard;
		try{
			 keyboard = new Scanner(new File(docFile)); //can't initialize scanner here because says it's not closed?
		 while(keyboard.hasNext()){
			//initialize scanner before try so it can go to catch
			String file = keyboard.next();
			String word= getKeyWord(file);
			if(word!=null){
				Occurrence occur = new Occurrence(docFile,1);
				if(keyWord.containsKey(word)==false){
					keyWord.put(word,occur);	
				}
				else{
					keyWord.get(word).frequency++;
				}
			}
		 }
	    }
	  catch(FileNotFoundException error){
		 System.out.println("File not found");
	  }
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		return keyWord;
	}
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeyWords(HashMap<String,Occurrence> kws) {
		// COMPLETE THIS METHOD
		ArrayList<Occurrence> arr = new ArrayList<Occurrence>();
		Iterator<String> iterate = kws.keySet().iterator(); //iterator over set of keys
		while (iterate.hasNext()){ 

		   String tempKey=iterate.next(); //get next element
		   Occurrence occr=kws.get(tempKey); //get the key
		   
			if (keywordsIndex.containsKey(tempKey)){ //if the keywordsIndex HashMap contains the key
				arr=keywordsIndex.get(tempKey); //get the key
				arr.add(occr); //add it to the occurrence
				insertLastOccurrence(arr);
			} 
			else{
				arr = new ArrayList<Occurrence>();
				arr.add(occr);
				insertLastOccurrence(arr);
				keywordsIndex.put(tempKey, arr); //put the key and value associated with it in the keywordsIndex HashMap
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * TRAILING punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyWord(String word) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		word=word.trim();
		word=word.toLowerCase();
		
		if(!(word.charAt(0)>='a'&&word.charAt(0)<='z')){
			return  null;
		}
		int index=-1;
		if(index==-1){
			for(int a=0; a<word.length();a++){
				if(word.charAt(a)>='a'&&word.charAt(a)<='z'){
					index=a;
				}
			}
			if(index==-1){ //means there was no letter
				return null;
			}
			else if(index!=-1&&index!=0){
				String checkPunct = word.substring(0, index+1);
				for(int x=0; x<checkPunct.length()-1;x++){
		     		if(!(checkPunct.charAt(x)>='a'&&checkPunct.charAt(x)<='z')){
		     			return null;
		     		}
		     	}
			}
		}
		//^\\p{Punct}+|\\p{Punct}+$
		word=word.replace("!", "");
		word=word.replace(".", "");
		word=word.replace("?", "");
		word=word.replace(",", "");
		word=word.replace(":", "");
		word=word.replace(";", "");
		//word.replaceAll(',', '');
     	if(noiseWords.containsKey(word)){
     		return null;
     	}
     	for(int i=0; i<word.length();i++){
     		if(!(word.charAt(i)>='a'&& word.charAt(i)<='z')) {
     			return null;
     		}
     	}
   
		return word;
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * same list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion of the last element
	 * (the one at index n-1) is done by first finding the correct spot using binary search, 
	 * then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<Integer> nums = new ArrayList<Integer>();
		if(occs.size()==1){
			return null;
		}
		int numToAdd = occs.get(occs.size()-1).frequency;
		int numIndex = occs.size()-1;
		int low = 0;
		int high = occs.size() - 2; 
		int middle=-1;
			while(high >= low) {
		       middle = (low + high) / 2;
		       nums.add(middle);
		       if(occs.get(middle).frequency == numToAdd) {
		            occs.add(middle, occs.get(numIndex));
		            break;
		       }
		       if(occs.get(middle).frequency > numToAdd) {
		         	low = middle + 1; //cause it's descending order
		       }
		    
		       else if(occs.get(middle).frequency <= numToAdd){
		    	   high=middle-1;
		       }
		    }
			if(occs.get(middle).frequency>numToAdd){
				occs.add(middle+1, occs.get(numIndex)); 
		    }
			else{
				occs.add(middle, occs.get(numIndex)); 
			}
			occs.remove(occs.size()-1);
			
		return nums;
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of occurrence frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will appear before doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matching documents, the result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword kw2 probably
	 * @return List of NAMES of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matching documents,
	 *         the result is null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		// COMPLETE THIS METHOD
		// THE FOLLOWING LINE HAS BEEN ADDED TO MAKE THE METHOD COMPILE
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<Occurrence> word1 = keywordsIndex.get(kw1);
		//this instantiates word1 array
		ArrayList<Occurrence> word2 = keywordsIndex.get(kw2);
		
		System.out.println(word1);
		System.out.println(word2);
		int index1=0;
		int index2=0;
		Occurrence occ1 = word1.get(index1);
		Occurrence occ2 =word2.get(index2);
		while(word1!=null && word2!=null && index1<word1.size() && index2<word2.size() && names.size()<5){
			occ1 = word1.get(index1);
			occ2 = word2.get(index2);
			int freq1 = occ1.frequency;
			int freq2 = occ2.frequency;
			int max =Math.max(freq1,freq2);
			if(max==freq1){
				if(!names.contains(occ1.document)){
				names.add(occ1.document);
				}
				index1++; 
			}
			else{
				if(!names.contains(occ2.document)){
				names.add(occ2.document);
				}
				index2++;
			}
		}
		while(word1!=null&&index1<word1.size()&&names.size()<5){ //only want 5 items
			if(!names.contains(occ1.document)){ //if resulting list does not already contain document then add it ro resulting 
				names.add(occ1.document);
				}
				index1++; //obtain next element
		}
		while(word2!=null&&index2<word2.size()&&names.size()<5){ //making sure resulting list is less than 5 
			if(!names.contains(occ2.document)){
				names.add(occ2.document); //if it doesnt contain it add it 
				}
				index2++;
		}
		
		return names; //return resulting list
	}
}

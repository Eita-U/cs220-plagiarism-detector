package plagdetect;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class PlagiarismDetector implements IPlagiarismDetector {
	int N;
	Map<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
	Map<String, Set<String>> ngramResult = new HashMap<String, Set<String>>();
	
	public PlagiarismDetector(int n) {
		this.N = n;
	}
	
	@Override
	public int getN() {
		return N;
	}

	@Override
	public Collection<String> getFilenames() {
		Collection<String> nameList = new HashSet<>();
		for(String name : result.keySet()) {
			nameList.add(name);
		}
		return nameList;
	}

	@Override
	public Collection<String> getNgramsInFile(String filename) {
		Collection<String> set = ngramResult.get(filename);
		return set;
	}

	@Override
	public int getNumNgramsInFile(String filename) {
		Collection<String> collection = getNgramsInFile(filename);
		return collection.size();
	}

	@Override
	public Map<String, Map<String, Integer>> getResults() {
		return result;
	}

	@Override
	public void readFile(File file) throws IOException {
		Set<String> set = new HashSet();
		try {
		      Scanner scan = new Scanner(file);
		      while (scan.hasNextLine()) {
		    	  String sentence = scan.nextLine();
		    	  String[] arr = sentence.split(" ");
		    	  for (int i = 0; i < arr.length - N + 1; i++) {
		    		  String ngram = "";
		    		  for(int j = 0; j < N; j++) {
		    			  if(j == N - 1) {
		    				  ngram += arr[i + j];
		    			  }else {
		    				  ngram += arr[i + j] + " ";
		    			  }
		    		  }
		    		  set.add(ngram);
		    	  }
		      }
		} catch(Exception e) {
			//Error occured
		}
		Map<String, Integer> map = new HashMap<>();
		for(Entry<String, Set<String>> entry : ngramResult.entrySet()) {
			Iterator it = set.iterator();
			int numNgrams = 0;
			String key = entry.getKey();
			Set<String> ngrams = entry.getValue();
			while(it.hasNext()) {
				if(ngrams.contains(it.next())) {
					numNgrams++;
				}
			}
			map.put(key, numNgrams);
		}
		result.put(file.getName(), map);
		for(Map.Entry<String, Map<String, Integer>> entry : result.entrySet()) {
			String key = entry.getKey();
			Map<String, Integer> value = entry.getValue();
			value.put(file.getName(), result.get(file.getName()).get(key));
		}
		ngramResult.put(file.getName(), set);
	}

	@Override
	public int getNumNGramsInCommon(String file1, String file2) {
		if(result.get(file1).get(file2) == null) {
			return 0;
		}else {
			return result.get(file1).get(file2);
		}
	}

	@Override
	public Collection<String> getSuspiciousPairs(int minNgrams) {
		Collection<String> set = new HashSet();
		for(Entry<String, Map<String, Integer>> entry1 : result.entrySet()) {
			String filename1 = entry1.getKey();
			Map<String, Integer> map = entry1.getValue();
			for(Entry<String, Integer> entry2 : map.entrySet()) {
				String filename2 = entry2.getKey();
				int numCommon;
				if(entry2.getValue() == null) {
					numCommon = 0;
				}else {
					numCommon = entry2.getValue();
				}
				if(numCommon >= minNgrams) {
					String pair;
					if(filename1.compareTo(filename2) < 0) {
						pair = String.format("%s %s %d", filename1, filename2, numCommon);
					}else {
						pair = String.format("%s %s %d", filename2, filename1, numCommon);
					}
					set.add(pair);
				}
			}
		}
		return set;
	}

	@Override
	public void readFilesInDirectory(File dir) throws IOException {
		// delegation!
		// just go through each file in the directory, and delegate
		// to the method for reading a file
		for (File f : dir.listFiles()) {
			readFile(f);
		}
	}
}

package junit;

import java.io.File;
import java.util.Collection;

import plagdetect.IPlagiarismDetector;
import plagdetect.PlagiarismDetector;

class TestAllDocs {
	
	static final String DOCS = "docs/alldocs";

    public static IPlagiarismDetector makeDetector(int n) throws Exception {
        // n is the size of an n-gram
        IPlagiarismDetector detector = new PlagiarismDetector(n);
        detector.readFilesInDirectory(new File(DOCS));
        return detector;
    }
    
	public static void main(String[] args) throws Exception {
		//
		// TODO you probably want to add more code or more tests
		// This just gets things started
		//
		
		
		// how long does it take to create a detector?
		long start = System.currentTimeMillis();
		IPlagiarismDetector detector = makeDetector(3);
		long total = System.currentTimeMillis() - start;
		System.out.printf("It took %.1f seconds to read the documents\n", total/1000.0);
		
		// how long does it take to search for suspicious pairs?
		start = System.currentTimeMillis();
		// TODO: 500 is too big here! These are not long enough essays to
		// have 500 ngrams in common!
		// The average essay is less than 500 words long.
		// What is a good number to put here? Try some different values to
		// figure that out!
		Collection<String> pairs = detector.getSuspiciousPairs(100);
		for (String pair : pairs) {
			System.out.println(pair);
		}
		total = System.currentTimeMillis() - start;
		System.out.printf("It took %.1f seconds to check for suspicious pairs in the documents\n", total/1000.0);
	}
	
	

}

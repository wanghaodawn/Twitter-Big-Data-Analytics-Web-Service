import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class wordcountReducer {
	public static main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		// Initialize variables
		String input;
		String word = null;
		String currentWord = null;
		int currentCount = 0;

		// While we have input on Stdin
		while ((input = br.readLine()) != null) {
			try {
				String[] parts = input.split("\t");
				word = parts[0];
				int count = Integer.parseInt(parts[1]);

				// We have sorted input, check if we are on the same word
				if (currentWord != null && currentWord.equals(word)) {
					currentCount++;
				} else {
					if (currentWord != null) {
						// This is the first word if not output count
						System.out.println(currentWord + "\t" + currentCount);
					}

					currentCount = count;
					currentWord = word;
				}
			} catch (Exception e) {
				continue;
			}
		}

		// Print out the last word if missed
		if (currentWord != null && currentWord.equals(word)) {
			System.out.println(currentWord + "\t" + currentCount);
		}
	}
}
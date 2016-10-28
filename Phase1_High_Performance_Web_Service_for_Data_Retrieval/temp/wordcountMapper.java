import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class wordcountMapper {
	public static main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String input;
		// While we have input on Stdin
		while ((input = br.readLine()) != null) {
			// Initialize tokenizer on string input
			StringTokenizer tokenizer = new StringTokenizer(input);
			while (tokenizer.haMoreTokens()) {
				// Get the next word
				String word = tokenizer.nextToken();
				// Output word \t 1
				System.out.println(word + "\t" + 1);
			}
		}
	}
}
import java.lang.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.text.*;
import org.json.*;

class Q2_test {
	public static void main(String[] args) throws IOException {
		
		// Read data
		File inFile = new File("part-00111");
		BufferedReader br = new BufferedReader(new FileReader(inFile));

		String sCurrentLine;
		int i = 0;
		while ((sCurrentLine = br.readLine()) != null && i < 1) {
            // System.out.println(sCurrentLine);
            i++;
            
            // Parse JSON
            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (jsonObject) obj;

            System.out.println(jsonObject.get("created_at"));
    	}

	}
}
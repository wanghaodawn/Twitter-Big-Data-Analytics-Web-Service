import java.io.*;
import java.util.*;

public class saveFiles {

    public static void main(String args[]) throws IOException {

        // Read data
        File inFile = new File("tweet-sort");
        
        // If file doesnt exists, then create it
        if (!inFile.exists()) {
            System.err.println("No file called: " + "tweet");
            System.exit(-1);
        }

        BufferedReader br = null;

        // Read string from the input file
        String currentLine;
        String space = "_";
        String encode = "UTF8";
        String colon = ":";
        String enter = "\n";
        
        br = new BufferedReader(new FileReader(inFile));
        int i = 0;
        while ((currentLine = br.readLine()) != null) {
            
            String[] array = currentLine.split("\t");
            
            // Ignore data without hashtags
            if (array.length <= 5) {
                continue;
            }

            // String value = array[0];
            for (int j = 5; j < array.length; j++) {
                
                String name = array[3] + space + array[j];
                
                File fout = new File(name);

                if (!fout.exists()) {
                    fout.createNewFile();
                }

                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout, true), encode));

                out.write(array[0] + colon + array[1] + colon + array[2] + colon + array[4] + enter);
                out.flush();
                out.close();
            }

            if (i % 10000 == 0) {
                System.gc();
                System.out.println(i);
            } 
            i++;
        }
    }
}
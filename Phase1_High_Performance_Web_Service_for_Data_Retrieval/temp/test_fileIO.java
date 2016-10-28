import java.io.*;
import java.util.*;

public class test_fileIO {

    public static void main(String args[]) throws IOException {

        String encode = "UTF8";

        File fout = new File("test_IO");

        if (!fout.exists()) {
            fout.createNewFile();
        }

        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fout, true), encode));

        out.newLine();
        out.write("12321312");
        out.flush();
        out.close();
    }
}
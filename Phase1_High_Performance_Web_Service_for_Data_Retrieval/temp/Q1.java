import java.lang.*;
import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.text.*;

class Q1{
	public static void main(String[] args) {
		String key = "4024123659485622445001958636275419709073611535463684596712464059093821";
		String message = "URYEXYBJB";
		System.out.println(encrypt(key, message));
	}

	private static String encrypt(String key, String message) {
		
		// Step 1
		// Get X and Y
		BigInteger X = new BigInteger("64266330917908644872330635228106713310880186591609208114244758680898150367880703152525200743234420230");
		BigInteger Y = new BigInteger(key);
		
		// Compute Greatest Common Divisor
		BigInteger Z = X.gcd(Y);

		// Step 2
		// Compute K
		BigInteger K = Z.mod(new BigInteger("25")).add(new BigInteger("1"));
		int k = K.intValue();

		// message -> I
		char[] char_array = message.toCharArray();
		int len = char_array.length;
		int n = (int) Math.sqrt(len);
		for (int i = 0; i < len; i++) {
			char_array[i] -= k;
			if (char_array[i] < 'A') {
				char_array[i] += 26;
			}
		}

		// Step 3 - Spiral
		int index = 0;
		char[][] temp = new char[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				temp[i][j] = char_array[index++];
			}
		}

		index = 0;
		int left = 0, top = 0, right = n-1, bottom = n-1;
		while (left <= right && top <= bottom) {
			for (int j = left; j <= right; j++) {
				char_array[index++] = temp[top][j];
			} 
            top++;
            
            for (int i = top; i <= bottom; i++) {
            	char_array[index++] = temp[i][right];
            }
            right--;
            
            for (int j = right; j >= left; j--) {
            	char_array[index++] = temp[bottom][j];
            }
            bottom--;
            
            for (int i = bottom; i >= top; i--) {
            	char_array[index++] = temp[i][left];
            }
            left++;
		}
		String I = new String(char_array);

		// Step 4
		TimeZone.setDefault(TimeZone.getTimeZone("EST"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss\n");
		Date date = new Date();

		StringBuilder final_String = new StringBuilder("elder,9344-4243-4066\n");
		final_String.append(dateFormat.format(date));
		final_String.append(I + "\n");

		return final_String.toString();
	}

}
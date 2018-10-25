import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class InputfileShrinker {

	public static void main(String[] args) {
		String inputFilePath = "./input/H1B_FY_2016.csv";
		String outputFilePath = "./input/H1B_FY_2016sm.csv";
		try {
			BufferedReader bfreader = new BufferedReader(new FileReader(inputFilePath));
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath));
			String line = new String();
			int counter = 0;
			while ((line = bfreader.readLine()) != null) {
				writer.append(line);
				counter++;
				if (counter > 20000) break;
				writer.newLine();
			}
			bfreader.close();
			writer.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

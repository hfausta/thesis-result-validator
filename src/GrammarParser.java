import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.TreeMap;


public class GrammarParser {
	
	private String file;
	private TreeMap<String, Integer> generatedSymbols;
	
	public GrammarParser(String file) throws Exception {
		this.file = file;
		generatedSymbols = new TreeMap<String, Integer>();
		getSymbols();
	}
	
	private void getSymbols() throws Exception {
		FileInputStream fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		BufferedReader br = new BufferedReader(new InputStreamReader(dis));
		String currentLine;
		
		while((currentLine = br.readLine()) != null) {
			if(currentLine.contains("::=")) {
				String[] lineSplit = currentLine.split(" ::= ");
				generatedSymbols.put(lineSplit[0], 0);
			}
		}
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public TreeMap<String, Integer> getGeneratedSymbols() {
		return generatedSymbols;
	}

	public void setGeneratedSymbols(TreeMap<String, Integer> generatedSymbols) {
		this.generatedSymbols = generatedSymbols;
	}
	
}

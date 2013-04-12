import java.util.ArrayList;
import java.util.TreeMap;

public class Validator {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		String arFile = "/home/henry/Downloads/grammars/result1.ar";
		//String xmlFile = "/home/henry/thesis/attaccaOriginal/DiG/attacca/output/attacca-generated.xml";
		//String goldFile = "/home/henry/thesis/attaccaOriginal/DiG/attacca/output/attacca-gold-standard.xml";
		String xmlFile = "/home/henry/thesis/newvalidation/xml/DiGHadoop_2.xml";
		String goldFile = "/home/henry/thesis/nova/attacca-new-gold.xml";
		GrammarParser grammarParser = new GrammarParser(arFile);
		XmlParser xmlParser = new XmlParser(xmlFile, grammarParser.getGeneratedSymbols());
		GoldParser goldParser = new GoldParser(goldFile);
		
		TreeMap<String, ArrayList<String>> accessionLeafGenerated = xmlParser.getAccessionLeaf();
		TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeafGenerated = xmlParser.getSymbolAccessionLeaf();
		String sps1 = xmlParser.getSps1();
		int first = xmlParser.getFirst();
		String sps2 = xmlParser.getSps2();
		int second = xmlParser.getSecond();
		TreeMap<String, ArrayList<String>> cassArrayAccessionLeaf = goldParser.getAccessionLeaf();
		
		Calculator calculator = new Calculator(accessionLeafGenerated, symbolAccessionLeafGenerated, cassArrayAccessionLeaf, sps1, sps2);
		System.out.println("SPS 1 : " + sps1 + " - " + first);
		System.out.println("SPS 2 : " + sps2 + " - " + second);
		System.out.println("TP : " + calculator.getTruePositive());
		System.out.println("FP : " + calculator.getFalsePositive());
		System.out.println("TN : " + calculator.getTrueNegative());
		System.out.println("FN : " + calculator.getFalseNegative());
		System.out.println("Sensitivity : " + calculator.getSensitivity());
		System.out.println("Specificity : " + calculator.getSpecificity());
		System.out.println("Precision : " + calculator.getPrecision());
		System.out.println("Positive Predictive Value : " + calculator.getPositivePredictiveValue());
		System.out.println("F-Score : " + calculator.getfScore());
	}

}

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XmlParser {

	private String file;
	private TreeMap<String, ArrayList<String>> accessionLeaf;
	private TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeaf;
	private TreeMap<String, Integer> symbol;
	private String sps1, sps2;
	private int first, second;
	
	public XmlParser(String file, TreeMap<String, Integer> symbol) throws SAXException, IOException {
		this.file = file;
		this.symbol = symbol;
		accessionLeaf = new TreeMap<String, ArrayList<String>>();
		symbolAccessionLeaf = new TreeMap<String, TreeMap<String, ArrayList<String>>>();
		accessionLeafParser();
		printAccessionLeafResult();
		printSymbolAccessionLeafResult();
		countW();
	}
	
	private void accessionLeafParser() throws SAXException, IOException {
		DOMParser parser = new DOMParser();
		parser.parse(file);
		Document doc = parser.getDocument();
		Element root = doc.getDocumentElement();
		
		for(Node node = root.getFirstChild();node != null;node = node.getNextSibling()) {
			if(node.getNodeName().equals("accession")) {
				String accessionName = null;
				for(Node accessionNode = node.getFirstChild();accessionNode != null;accessionNode = accessionNode.getNextSibling()) {
					if(accessionNode.getNodeName().equals("name")) {
						accessionName = accessionNode.getFirstChild().getNodeValue();
					} else if(accessionNode.getNodeName().equals("feature")) {
						getLeafFeature(accessionNode, accessionName);
					} else if(accessionNode.getNodeName().equals("nil-match")) {
						if(accessionLeaf.containsKey(accessionName)) {
							ArrayList<String> updated = accessionLeaf.get(accessionName);
							updated.add("nil-match");
							accessionLeaf.put(accessionName, updated);
						} else {
							ArrayList<String> leaves = new ArrayList<String>();
							leaves.add("nil-match");
							accessionLeaf.put(accessionName, leaves);
						}
					}
				}
			}
		}
	}
	
	private void getLeafFeature(Node node, String accessionName) {
		String featureName = null;
		boolean leaf = true;
		for(Node featureNode = node.getFirstChild();featureNode != null;featureNode = featureNode.getNextSibling()) {
			if(featureNode.getNodeName().equals("name")) {
				featureName = featureNode.getFirstChild().getNodeValue();
			} else if(featureNode.getNodeName().equals("nil-match")) {
				if(accessionLeaf.containsKey(accessionName)) {
					accessionLeaf.get(accessionName).add("nil-match");
				} else {
					ArrayList<String> leaves = new ArrayList<String>();
					leaves.add("nil-match");
					accessionLeaf.put(accessionName, leaves);
				}
				if(symbol.containsKey(featureName)) {
					if(symbolAccessionLeaf.containsKey(featureName)) {
						if(symbolAccessionLeaf.get(featureName).containsKey(accessionName)) {
							symbolAccessionLeaf.get(featureName).get(accessionName).add("nil-match");
						} else {
							ArrayList<String> newLeaf = new ArrayList<String>();
							newLeaf.add("nil-match");
							symbolAccessionLeaf.get(featureName).put(accessionName, newLeaf);
						}
					} else {
						TreeMap<String, ArrayList<String>> newAccessionLeaf = new TreeMap<String, ArrayList<String>>();
						ArrayList<String> newLeaf = new ArrayList<String>();
						newLeaf.add("nil-match");
						newAccessionLeaf.put(accessionName, newLeaf);
						symbolAccessionLeaf.put(featureName, newAccessionLeaf);
					}
					symbol.put(featureName, symbol.get(featureName) + 1);
				}
			} else if(featureNode.getNodeName().equals("feature")) {
				leaf = false;
				getLeafFeature(featureNode, accessionName);
				if(symbol.containsKey(featureName)) {
					getSymbolLeafFeature(featureNode, accessionName, featureName);
				}
			}
		}
		if(leaf) {
			if(accessionLeaf.containsKey(accessionName)) {
				accessionLeaf.get(accessionName).add(featureName);
			} else {
				ArrayList<String> leaves = new ArrayList<String>();
				leaves.add(featureName);
				accessionLeaf.put(accessionName, leaves);
			}
		}
	}
	
	private void getSymbolLeafFeature(Node node, String accessionName, String symbolName) {
		String featureName = null;
		boolean leaf = true;
		for(Node featureNode = node.getFirstChild();featureNode != null;featureNode = featureNode.getNextSibling()) {
			if(featureNode.getNodeName().equals("name")) {
				featureName = featureNode.getFirstChild().getNodeValue();
			} else if(featureNode.getNodeName().equals("feature")) {
				leaf = false;
				if(!featureName.equals(symbolName)) {
					getSymbolLeafFeature(featureNode, accessionName, symbolName);
				}
			} else if(featureNode.getNodeName().equals("nil-match")) {
				if(!featureName.equals(symbolName)) {
					if(symbolAccessionLeaf.containsKey(symbolName)) {
						if(symbolAccessionLeaf.get(symbolName).containsKey(accessionName)) {
							symbolAccessionLeaf.get(symbolName).get(accessionName).add("nil-match");
						} else {
							ArrayList<String> newLeaf = new ArrayList<String>();
							newLeaf.add("nil-match");
							symbolAccessionLeaf.get(symbolName).put(accessionName, newLeaf);
						}
					} else {
						TreeMap<String, ArrayList<String>> newAccessionLeaf = new TreeMap<String, ArrayList<String>>();
						ArrayList<String> newLeaf = new ArrayList<String>();
						newLeaf.add("nil-match");
						newAccessionLeaf.put(accessionName, newLeaf);
						symbolAccessionLeaf.put(symbolName, newAccessionLeaf);
					}
					symbol.put(symbolName, symbol.get(symbolName) + 1);
				}
			}
		}
		if(leaf) {
			if(symbolAccessionLeaf.containsKey(symbolName)) {
				if(symbolAccessionLeaf.get(symbolName).containsKey(accessionName)) {
					symbolAccessionLeaf.get(symbolName).get(accessionName).add(featureName);
				} else {
					ArrayList<String> leaves = new ArrayList<String>();
					leaves.add(featureName);
					symbolAccessionLeaf.get(symbolName).put(accessionName, leaves);
				}
			} else {
				ArrayList<String> leaves = new ArrayList<String>();
				leaves.add(featureName);
				TreeMap<String, ArrayList<String>> newAccessionLeaf = new TreeMap<String, ArrayList<String>>();
				newAccessionLeaf.put(accessionName, leaves);
				symbolAccessionLeaf.put(symbolName, newAccessionLeaf);
			}
			symbol.put(symbolName, symbol.get(symbolName) + 1);
		}
	}
	
	private void printAccessionLeafResult() throws IOException {
		FileWriter fw = new FileWriter("AccessionLeaf");
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Entry<String, ArrayList<String>> entry : accessionLeaf.entrySet()) {
		     bw.write(entry.getKey() + "\n");
		     for(String s : entry.getValue()) {
		    	 bw.write("        " + s + "\n");
		     }
		}
		bw.close();
	}
	
	private void printSymbolAccessionLeafResult() throws IOException {
		FileWriter fw = new FileWriter("SymbolAccessionLeaf");
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Entry<String, TreeMap<String, ArrayList<String>>> entry : symbolAccessionLeaf.entrySet()) {
		     bw.write(entry.getKey() + "\n");
		     for(Entry<String, ArrayList<String>> val : symbolAccessionLeaf.get(entry.getKey()).entrySet()) {
		    	 bw.write("     " + val.getKey() + "\n");
		    	 for(String s : symbolAccessionLeaf.get(entry.getKey()).get(val.getKey())) {
		    		 bw.write("          " + s + "\n");
		    	 }
		     }
		}
		bw.close();
	}
	
	private void countW() {
		first = 0;
		second = 0;
		for(Entry<String, Integer> entry : symbol.entrySet()) {
			if(entry.getValue() > first) {
				String tempS = sps1;
				sps1 = entry.getKey();
				sps2 = tempS;
				
				int tempI = first;
				first = entry.getValue();
				second = tempI;
			} else {
				if(entry.getValue() > second) {
					second = entry.getValue();
					sps2 = entry.getKey();
				}
			}
		}
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public TreeMap<String, ArrayList<String>> getAccessionLeaf() {
		return accessionLeaf;
	}

	public void setAccessionLeaf(TreeMap<String, ArrayList<String>> accessionLeaf) {
		this.accessionLeaf = accessionLeaf;
	}

	public TreeMap<String, TreeMap<String, ArrayList<String>>> getSymbolAccessionLeaf() {
		return symbolAccessionLeaf;
	}

	public void setSymbolAccessionLeaf(
			TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeaf) {
		this.symbolAccessionLeaf = symbolAccessionLeaf;
	}

	public TreeMap<String, Integer> getSymbol() {
		return symbol;
	}

	public void setSymbol(TreeMap<String, Integer> symbol) {
		this.symbol = symbol;
	}

	public String getSps1() {
		return sps1;
	}

	public void setSsp1(String sps1) {
		this.sps1 = sps1;
	}

	public String getSps2() {
		return sps2;
	}

	public void setSps2(String sps2) {
		this.sps2 = sps2;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getSecond() {
		return second;
	}

	public void setSecond(int second) {
		this.second = second;
	}
	
}

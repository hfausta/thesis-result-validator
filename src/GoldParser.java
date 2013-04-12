import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class GoldParser {
	
	private String file;
	private TreeMap<String, ArrayList<String>> accessionLeaf;
	
	public GoldParser(String file) throws SAXException, IOException {
		this.file = file;
		accessionLeaf = new TreeMap<String, ArrayList<String>>();
		cassArrayParser();
		printAccessionLeafResult();
	}
	
	private void cassArrayParser() throws SAXException, IOException {
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
					}
				}
			}
		}
	}
	
	private void getLeafFeature(Node node, String accessionName) {
		String featureName = null;
		for(Node featureNode = node.getFirstChild();featureNode != null;featureNode = featureNode.getNextSibling()) {
			if(featureNode.getNodeName().equals("name")) {
				featureName = featureNode.getFirstChild().getNodeValue();
			} else if(featureNode.getNodeName().equals("nil-match")) {
				if(featureName.equals("CassArray")) {
					if(accessionLeaf.containsKey(accessionName)) {
						accessionLeaf.get(accessionName).add("nil-match");
					} else {
						ArrayList<String> leaves = new ArrayList<String>();
						leaves.add("nil-match");
						accessionLeaf.put(accessionName, leaves);
					}					
				}
			} else if(featureNode.getNodeName().equals("feature")) {
				getLeafFeature(featureNode, accessionName);
				if(featureName.equals("CassArray")) {
					getSymbolLeafFeature(featureNode, accessionName, featureName);
				}
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
					if(accessionLeaf.containsKey(accessionName)) {
						accessionLeaf.get(accessionName).add("nil-match");
					} else {
						ArrayList<String> leaves = new ArrayList<String>();
						leaves.add("nil-match");
						accessionLeaf.put(accessionName, leaves);
					}
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
	
	private void printAccessionLeafResult() throws IOException {
		FileWriter fw = new FileWriter("GoldLeaf");
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Entry<String, ArrayList<String>> entry : accessionLeaf.entrySet()) {
		     bw.write(entry.getKey() + "\n");
		     for(String s : entry.getValue()) {
		    	 bw.write("        " + s + "\n");
		     }
		}
		bw.close();
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
	
}

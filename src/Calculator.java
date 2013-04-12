import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;


public class Calculator {

	private TreeMap<String, ArrayList<String>> accessionLeafGenerated;
	private TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeafGenerated;
	private TreeMap<String, ArrayList<String>> cassArrayAccessionLeaf;
	private String sps1, sps2;
	private Integer truePositive = 0, 
			trueNegative = 0, 
			falsePositive = 0, 
			falseNegative = 0;
	private Double sensitivity = 0.0,
			specificity = 0.0,
			positivePredictiveValue = 0.0,
			fScore = 0.0,
			precision = 0.0,
			recall = 0.0;
	
	public Calculator(TreeMap<String, ArrayList<String>> accessionLeafGenerated,
			TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeafGenerated,
			TreeMap<String, ArrayList<String>> cassArrayAccessionLeaf,
			String sps1, String sps2) {
		this.accessionLeafGenerated = accessionLeafGenerated;
		this.symbolAccessionLeafGenerated = symbolAccessionLeafGenerated;
		this.cassArrayAccessionLeaf = cassArrayAccessionLeaf;
		this.sps1 = sps1;
		this.sps2 = sps2;
		compare();
		calculateFScore();
	}

	private void compare() {
		System.out.println("Missed : ");
		for(Entry<String, ArrayList<String>> goldEntry : cassArrayAccessionLeaf.entrySet()) {
			/*
			for(String gold : cassArrayAccessionLeaf.get(goldEntry.getKey())) {
				boolean goldFound = true;
				boolean sps1Found = false;
				boolean sps2Found = false;
				
				if(symbolAccessionLeafGenerated.get(sps1).containsKey(goldEntry.getKey())) {
					for(int j = 0;j < symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).size();j++) {
						if(gold.equals(symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).get(j))) {
							sps1Found = true;
							symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).set(j, null);
							break;
						}
					}
				}
				
				if(symbolAccessionLeafGenerated.get(sps2).containsKey(goldEntry.getKey())) {
					for(int k = 0;k < symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).size();k++) {
						if(gold.equals(symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).get(k))) {
							sps2Found = true;
							symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).set(k, null);
							break;
						}
					}
				}
				
				if((goldFound) && (sps1Found || sps2Found)) {
					truePositive++;
				} else if((goldFound) && (!sps1Found && !sps2Found)) {
					falseNegative++;
				} else if((!goldFound) && (sps1Found || sps2Found)) {
					falsePositive++;
				} else if((!goldFound) && (!sps1Found && !sps2Found)) {
					trueNegative++;
				}
			}
			
			if(symbolAccessionLeafGenerated.get(sps1).containsKey(goldEntry.getKey())) {
				for(String feature : symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey())) {
					if(feature != null) {
						falsePositive++;
					}
				}
			}
			
			if(symbolAccessionLeafGenerated.get(sps2).containsKey(goldEntry.getKey())) {
				for(String feature : symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey())) {
					if(feature != null) {
						falsePositive++;
					}
				}
			}*/
			int tp = 0, fn = 0, fp = 0, tn = 0;
			if(accessionLeafGenerated.containsKey(goldEntry.getKey())) {
				for(String leafGenerated : accessionLeafGenerated.get(goldEntry.getKey())) {
					boolean goldFound = false;
					boolean sps1Found = false;
					boolean sps2Found = false;
				
					for(int i = 0;i < cassArrayAccessionLeaf.get(goldEntry.getKey()).size();i++) {
						if(leafGenerated.equals(cassArrayAccessionLeaf.get(goldEntry.getKey()).get(i))) {
							goldFound = true;
							cassArrayAccessionLeaf.get(goldEntry.getKey()).set(i, null);
							break;
						}
					}
					if(symbolAccessionLeafGenerated.get(sps1).containsKey(goldEntry.getKey())) {
						for(int j = 0;j < symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).size();j++) {
							if(leafGenerated.equals(symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).get(j))) {
								sps1Found = true;
								symbolAccessionLeafGenerated.get(sps1).get(goldEntry.getKey()).set(j, null);
								break;
							}
						}
					}
					if(symbolAccessionLeafGenerated.get(sps2).containsKey(goldEntry.getKey())) {
						for(int k = 0;k < symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).size();k++) {
							if(leafGenerated.equals(symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).get(k))) {
								sps2Found = true;
								symbolAccessionLeafGenerated.get(sps2).get(goldEntry.getKey()).set(k, null);
								break;
							}
						}
					}
					
					if((goldFound) && (sps1Found || sps2Found)) {
						truePositive++;
						tp++;
					} else if((goldFound) && (!sps1Found && !sps2Found)) {
						falseNegative++;
						fn++;
					} else if((!goldFound) && (sps1Found || sps2Found)) {
						falsePositive++;
						fp++;
					} else if((!goldFound) && (!sps1Found && !sps2Found)) {
						trueNegative++;
						tn++;
					}
					
				}
				
			} else {
				System.out.println(goldEntry.getKey());
				falseNegative += cassArrayAccessionLeaf.get(goldEntry.getKey()).size();
			}
			double precision = (double)tp / ((double)tp + (double)fp);
			double recall = (double)tp / ((double)tp + (double)fn);
			double f = 2*((precision*recall)/(precision+recall));
			double a = ((double)tp + (double)tn)/((double)tp + (double)tn + (double)fp + (double)fn);
			//System.out.println(goldEntry.getKey() + "  F-score:" + f + "  TP:" + tp + "  FP:" + fp + "  FN:" + fn + "  TN:" + tn);
			if(tp == 0) {
				System.out.println(goldEntry.getKey());
			}
			
		}
	}
	
	private void calculateFScore() {
		sensitivity = (double)truePositive / (double)(truePositive + falseNegative);
		specificity = (double)trueNegative / (double)(trueNegative + falsePositive);
		precision = (double)truePositive / (double)(truePositive + falsePositive);
		positivePredictiveValue = (double)truePositive / (double)(truePositive + falsePositive);
		//fScore = 2*sensitivity*positivePredictiveValue/(sensitivity + positivePredictiveValue);
		fScore = 2*((precision*sensitivity)/(precision+sensitivity));
		//fScore = (2*precision*sensitivity)/(precision+sensitivity);
	}

	public TreeMap<String, ArrayList<String>> getAccessionLeafGenerated() {
		return accessionLeafGenerated;
	}

	public void setAccessionLeafGenerated(
			TreeMap<String, ArrayList<String>> accessionLeafGenerated) {
		this.accessionLeafGenerated = accessionLeafGenerated;
	}

	public TreeMap<String, TreeMap<String, ArrayList<String>>> getSymbolAccessionLeafGenerated() {
		return symbolAccessionLeafGenerated;
	}

	public void setSymbolAccessionLeafGenerated(
			TreeMap<String, TreeMap<String, ArrayList<String>>> symbolAccessionLeafGenerated) {
		this.symbolAccessionLeafGenerated = symbolAccessionLeafGenerated;
	}

	public TreeMap<String, ArrayList<String>> getCassArrayAccessionLeaf() {
		return cassArrayAccessionLeaf;
	}

	public void setCassArrayAccessionLeaf(
			TreeMap<String, ArrayList<String>> cassArrayAccessionLeaf) {
		this.cassArrayAccessionLeaf = cassArrayAccessionLeaf;
	}

	public String getSps1() {
		return sps1;
	}

	public void setSps1(String sps1) {
		this.sps1 = sps1;
	}

	public String getSps2() {
		return sps2;
	}

	public void setSps2(String sps2) {
		this.sps2 = sps2;
	}

	public Integer getTruePositive() {
		return truePositive;
	}

	public void setTruePositive(Integer truePositive) {
		this.truePositive = truePositive;
	}

	public Integer getTrueNegative() {
		return trueNegative;
	}

	public void setTrueNegative(Integer trueNegative) {
		this.trueNegative = trueNegative;
	}

	public Integer getFalsePositive() {
		return falsePositive;
	}

	public void setFalsePositive(Integer falsePositive) {
		this.falsePositive = falsePositive;
	}

	public Integer getFalseNegative() {
		return falseNegative;
	}

	public void setFalseNegative(Integer falseNegative) {
		this.falseNegative = falseNegative;
	}

	public Double getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(Double sensitivity) {
		this.sensitivity = sensitivity;
	}

	public Double getSpecificity() {
		return specificity;
	}

	public void setSpecificity(Double specificity) {
		this.specificity = specificity;
	}

	public Double getPositivePredictiveValue() {
		return positivePredictiveValue;
	}

	public void setPositivePredictiveValue(Double positivePredictiveValue) {
		this.positivePredictiveValue = positivePredictiveValue;
	}

	public Double getfScore() {
		return fScore;
	}

	public void setfScore(Double fScore) {
		this.fScore = fScore;
	}

	public Double getPrecision() {
		return precision;
	}

	public void setPrecision(Double precision) {
		this.precision = precision;
	}

	public Double getRecall() {
		return recall;
	}

	public void setRecall(Double recall) {
		this.recall = recall;
	}
	
}

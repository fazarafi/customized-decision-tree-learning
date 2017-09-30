package customdtl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class DatasetPreProcessor {
	private Instances datasetInstances;
	private double threshold;
	
	public static void main(String[] args) {
		DatasetPreProcessor dsp = new DatasetPreProcessor("iris.arff");
		dsp.calculateThreshold(0);
		
	}
	
	public DatasetPreProcessor(String filename) {
		try {
			setDataset("files/"+filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Instances getDatasetInstances() {
		return datasetInstances;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setDataset(String filename) throws Exception {
		if (DTLUtil.isFileExist(filename)) {
			String fileType = filename.split("\\.")[1];
			if (fileType.equals("csv")) {
				CSVLoader loader = new CSVLoader();
			    loader.setSource(new File(filename));
			    datasetInstances = loader.getDataSet();
			} else {
				if (DataSource.isArff(filename)) {
					DataSource source = new DataSource(filename);	
					datasetInstances = source.getDataSet();
				}
			}			
		}
		int classIndex = datasetInstances.numAttributes() - 1;
		if (datasetInstances.classIndex() == -1)
			datasetInstances.setClassIndex(classIndex);
	}
	
	public void calculateThreshold(int attIdx) {
		Instances tempInst = new Instances(datasetInstances);
		tempInst.sort(attIdx);
		int classIdx = tempInst.classIndex();
		int numInstances = tempInst.numInstances();
		double[] valueArray = new double[numInstances];  
		double[] classArray = new double[numInstances];
		
		List<Integer> cutPointList = new ArrayList<Integer>();
		boolean first = true;
		double prevValue = 0d;
		for (int i=0; i<numInstances; i++) {
			valueArray[i] = tempInst.instance(i).value(attIdx);
			classArray[i] = tempInst.instance(i).value(classIdx);
			if (first) {
				prevValue = classArray[0];
				first = false;
			} else {
				if (prevValue != classArray[i]) {
					cutPointList.add(i-1);
				}
				prevValue = classArray[i];
			}
		}
		
//		System.out.println("atribut");
//		for (int i=0; i<numInstances; i++) {
//			System.out.print(i+">"+valueArray[i]+" ");	
//		}
//		
//		System.out.println("");
//		System.out.println("class");
//		for (int i=0; i<numInstances; i++) {
//			System.out.print(i+">"+classArray[i]+" ");	
//		}
//		System.out.println("");
//		System.out.println(cutPointList);
		
		int chosenCutPoint = 0;
		double maxIG = 0d;
		int counter = 0;
		for (int cutPoint : cutPointList) {
			if (counter>10) {
				break;
			}
			double cutPointIG = calculateIG(cutPoint);
			if (cutPointIG > maxIG) {
				maxIG = cutPointIG;
				chosenCutPoint = cutPoint; 
			}
			counter++;
		}
		threshold = chosenCutPoint; 
	}
	
	
	
	
	public double calculateIG(int cutPoint) {
		double ig = 0d;
		
		
		return 0d;
	}
	
	public double calculateEntropy(int pop, int total) {
		double entropy = 0d;
		return -((pop/total)*log_b(2,pop/total))-(((total-pop)/total)*log_b(2,(total-pop)/total));
	}
	
	public static double log_b(int basis, double num) {
		return Math.log(num)/Math.log(basis);
	}
	
	
	
}

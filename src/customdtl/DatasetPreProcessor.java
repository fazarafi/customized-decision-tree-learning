package customdtl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
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
		Instances sortedInst = new Instances(datasetInstances);
		sortedInst.sort(attIdx);
		int classIdx = sortedInst.classIndex();
		int numInstances = sortedInst.numInstances();
		double[] valueArray = new double[numInstances];  
		double[] classArray = new double[numInstances];
		
		List<Integer> cutPointList = new ArrayList<Integer>();
		boolean first = true;
		double prevValue = 0d;
		for (int i=0; i<numInstances; i++) {
			valueArray[i] = sortedInst.instance(i).value(attIdx);
			classArray[i] = sortedInst.instance(i).value(classIdx);
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
		
		System.out.println("atribut");
		for (int i=0; i<numInstances; i++) {
			System.out.print(i+">"+valueArray[i]+" ");	
		}
		
		System.out.println("");
		System.out.println("class");
		for (int i=0; i<numInstances; i++) {
			System.out.print(i+">"+classArray[i]+" ");	
		}
		System.out.println("");
		System.out.println(cutPointList);
		
		int chosenCutPoint = 0;
		double maxIG = 0d;
		int counter = 0;
		for (int cutPoint : cutPointList) {
			if (counter>2) {
				break;
			}
			double cutPointIG = calculateIG(cutPoint,sortedInst);
			System.out.println(cutPoint+". "+cutPointIG);
			if (cutPointIG > maxIG) {
				maxIG = cutPointIG;
				chosenCutPoint = cutPoint; 
			}
			counter++;
			
		}
		threshold = chosenCutPoint; 
	}
	
	public static double calculateIG(int cutPoint, Instances sortedInst) {
		double entAll = DTLUtil.calculateEntropyF(sortedInst);
		double ig = entAll;
		Instances[] splittedInst = splitInstances(cutPoint,sortedInst);
		for (int i=0; i<2; i++) {
			Attribute classAtt = splittedInst[i].attribute(splittedInst[i].classIndex());
			ArrayList<String> arr_val = DTLUtil.possibleAttributeValue(splittedInst[i], classAtt);
			System.out.println(arr_val);
			
			double entSubsetIns = DTLUtil.calculateEntropyForContF(splittedInst[i],arr_val);
			ig -= (splittedInst[i].numInstances() / sortedInst.numInstances()) * entSubsetIns;	
		}
		return ig;
	}
	
	
	public static Instances[] splitInstances(int cutPoint, Instances inst) {
		Instances[] splittedInst = new Instances[2];
		splittedInst[0] = new Instances(inst, 0, cutPoint+1);
		splittedInst[1] = new Instances(inst, cutPoint+1, inst.numInstances()-cutPoint-1); 
		return splittedInst; 
	}
	
	
}




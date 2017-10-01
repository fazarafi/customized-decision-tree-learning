package customdtl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

public class DatasetPreProcessor {
	private Instances mainInst;
	private double threshold[];
	private Random randomizer;
	
	public static void main(String[] args) {
		try {
			DatasetPreProcessor dsp = new DatasetPreProcessor("iris.arff");
			dsp.calcThresholdIfNominal();
			for (double t : dsp.getThreshold()) {
				System.out.println(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
	
	public DatasetPreProcessor(String filename) throws Exception {
		randomizer = new Random();
		setDataset("files/"+filename);
		threshold = new double[mainInst.numAttributes()];	
	}
	
	public DatasetPreProcessor(Instances inputInst) throws Exception {
		randomizer = new Random();
		mainInst = new Instances(inputInst);
		threshold = new double[mainInst.numAttributes()];	
	}
	
	public Instances getDatasetInstances() {
		return mainInst;
	}
	
	public void setDatasetInstances(Instances newInst) {
		mainInst = newInst;
	}
	
	public double[] getThreshold() {
		return threshold;
	}

	public void setDataset(String filename) throws Exception {
		if (DTLUtil.isFileExist(filename)) {
			String fileType = filename.split("\\.")[1];
			if (fileType.equals("csv")) {
				CSVLoader loader = new CSVLoader();
			    loader.setSource(new File(filename));
			    mainInst = loader.getDataSet();
			} else {
				if (DataSource.isArff(filename)) {
					DataSource source = new DataSource(filename);	
					mainInst = source.getDataSet();
				}
			}			
		}
		int classIndex = mainInst.numAttributes() - 1;
		if (mainInst.classIndex() == -1)
			mainInst.setClassIndex(classIndex);
	}
	
	public void calcThresholdIfNominal() {
		int numAtts = mainInst.numAttributes();
		for (int i=0; i<numAtts; i++) {
			if (i!=mainInst.classIndex() && mainInst.attribute(i).isNumeric()) {
				calculateThreshold(i);
			}
		}
	}
	
	public void calculateThreshold(int attIdx) {
		Instances sortedInst = new Instances(mainInst);
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
		while (counter<10 && !cutPointList.isEmpty()) {
			int index = randomizer.nextInt(cutPointList.size());
			double cutPointIG = calculateIG(cutPointList.get(index),sortedInst);
			if (cutPointIG > maxIG) {
				maxIG = cutPointIG;
				chosenCutPoint = cutPointList.get(index); 
			}
			cutPointList.remove(index); // memastikan angka random tidak berulang
			counter++;
		}
		System.out.println(chosenCutPoint);
		threshold[attIdx] = (valueArray[chosenCutPoint]+valueArray[chosenCutPoint+1])/2; 
	}
	
	public static double calculateIG(int cutPoint, Instances sortedInst) {
		double entAll = DTLUtil.calculateEntropyF(sortedInst);
		double ig = entAll;
		Instances[] splittedInst = DTLUtil.splitInstances(cutPoint,sortedInst);
		for (int i=0; i<2; i++) {
			double entSubsetIns = DTLUtil.calculateEntropyF(splittedInst[i]);
			ig -= ((double) splittedInst[i].numInstances() / (double)sortedInst.numInstances()) * entSubsetIns;	
		}
		return ig;
	}
}



//System.out.println("atribut");
//for (int i=0; i<numInstances; i++) {
//	System.out.print(i+">"+valueArray[i]+" ");	
//}
//
//System.out.println("");
//System.out.println("class");
//for (int i=0; i<numInstances; i++) {
//	System.out.print(i+">"+classArray[i]+" ");	
//}
//System.out.println("");
//System.out.println(cutPointList);

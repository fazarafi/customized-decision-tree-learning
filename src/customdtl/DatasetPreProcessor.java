package customdtl;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import weka.core.Instance;

import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class DatasetPreProcessor {
	private Instances mainInst;
	private double threshold[];
	private Random randomizer;
	
        public Instances changeAttrValue() {
            Instances ins = new Instances(mainInst);
            for (Instance singleIns : ins) {
                for (int i=0;i<threshold.length;i++) {
                    if (i!=mainInst.classIndex() && mainInst.attribute(i).isNumeric()) {
                        // ini numeric
                        if (singleIns.value(singleIns.attribute(i))>threshold[i]) {
                            String s = "<"+threshold[i];
                            singleIns.setValue(singleIns.attribute(i), s);
                        }
                    } // kalo nominal biarin aja
                }
            }
            
            return ins;
        }
        
	public static void main(String[] args) {
		try {
			DatasetPreProcessor dsp = new DatasetPreProcessor("coba.arff");
			System.out.println(dsp.mainInst);
                        
			for (double t : dsp.getThreshold()) {
				System.out.println(t);
			}
                        
//                        dsp.makeNominal("coba.arff");
                        
                        
                        
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

//		for (int i=0; i<numInstances; i++) {
//			System.out.print(i+">"+classArray[i]+" ");	
//		}
//		System.out.println("");
//		System.out.println(cutPointList);

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
//		System.out.println(chosenCutPoint);
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
        
        public void makeNominal(String filename) throws Exception {
            this.calcThresholdIfNominal();
            File file = new File("files/"+filename);

            // creates the file
            file.createNewFile();

            // creates a FileWriter Object
            FileWriter writer = new FileWriter(file);
            // Writes the content to the file
            writer.write("@relation " + mainInst.relationName() + "\n");
            writer.write("\n");
            for (int i = 0; i < mainInst.numAttributes(); i++) {
                if (mainInst.attribute(i).isNumeric()) {
                    writer.write("@attribute " + mainInst.attribute(i).name() + " {a,b}\n");
                } else {
                    writer.write(mainInst.attribute(i) + "\n");
                }
            }
            writer.write("\n");
            writer.write("@data\n");
            for (Instance singleIns : mainInst) {
                for (int i = 0; i < singleIns.numAttributes(); i++) {
                    // kalo numeric, ubah ke nominal
                    if (threshold[i] == 0.0) {
                        writer.write(singleIns.toString(i));
                    } else {
                        // bikin threshold
                        if (singleIns.value(i) < threshold[i]) {
                            writer.write("a");
                        } else {
                            writer.write("b");
                        }
                    }
                    if (i != singleIns.numAttributes() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }
            writer.flush();
            writer.close();
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

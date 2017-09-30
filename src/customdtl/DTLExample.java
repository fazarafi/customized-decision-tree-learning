package customdtl;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Remove;

public class DTLExample {
	private Classifier myClassifier;
	private Instances trainingDataset;
		
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		boolean isStopped = false;
		while (!isStopped) {
			System.out.println("===========================");
			DTLUtil.printAllFiles();
			System.out.println("Nama file dataset: ");
			String filename = sc.next();
			DTLExample dtlModel = new DTLExample();
			try {
				dtlModel.setTrainingDataset(loadData("files/"+filename));
				if (dtlModel.getTrainingDataset()!=null) {
				System.out.println("Indeks kelas di akhir? y/n");
				String str = sc.next();
				int classIndex = 0;
				if (str.equals("y")) {
					classIndex = dtlModel.getTrainingDataset().numAttributes() - 1;
				} else {
					System.out.print("Jadi di indeks ke? ");
					classIndex = sc.nextInt();
				}
				if (dtlModel.getTrainingDataset().classIndex() == -1)
		        	dtlModel.getTrainingDataset().setClassIndex(classIndex);
                    System.out.println(dtlModel.getTrainingDataset().attribute(0).toString());
                    System.out.println(dtlModel.getTrainingDataset().attribute(2).toString());
                    System.out.println("=========================== TRAINING STARTED");
					dtlModel.trainModel();
					System.out.println(dtlModel.getMyClassifier());
					System.out.println("=========================== TRAINING FINISHED");
					
					dtlModel.filterData();
					dtlModel.selfTesting();
					dtlModel.saveModel();	
					dtlModel.testModel("files/"+filename);
				} else {
					System.out.println("file not found.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sc.close();
		
	}
	
	public DTLExample() {
		
	}
	
	public Classifier getMyClassifier() {
		return myClassifier;
	}

	public void setMyClassifier(Classifier myClassifier) {
		this.myClassifier = myClassifier;
	}

	public Instances getTrainingDataset() {
		return trainingDataset;
	}

	public void setTrainingDataset(Instances trainingDataset) {
		this.trainingDataset = trainingDataset;
	}
	
	public void trainModel() throws Exception {
		Classifier DTL = new J48();
		DTL.buildClassifier(this.getTrainingDataset());
		this.setMyClassifier(DTL);
	}
	
	public void filterData() throws Exception {
		Resample resampleFilter = new Resample();
		resampleFilter.setBiasToUniformClass(1.0);
		resampleFilter.setInputFormat(this.getTrainingDataset());
		resampleFilter.setNoReplacement(false);
		resampleFilter.setSampleSizePercent(100);
		this.setTrainingDataset(Filter.useFilter(this.getTrainingDataset(), resampleFilter));
	}
	
	public void removeAttribute(String attributeId) throws Exception {
		Instances inst = new Instances(this.getTrainingDataset());
		if (!(inst.classIndex()<0)) {
			Remove remove = new Remove();
		    remove.setAttributeIndices(""+attributeId);
		    remove.setInvertSelection(false);
		    remove.setInputFormat(inst);
		    this.setTrainingDataset(Filter.useFilter(inst, remove));
		}
	}
	
	public void testModel(String filename) throws Exception {
		if (DTLUtil.isFileExist(filename) && DataSource.isArff(filename)) {
			DataSource source = new DataSource(filename);
			int lastIndex = this.getTrainingDataset().classIndex();
			int correctClass = 0;
	        
			Instances test = source.getDataSet();
	        test.setClassIndex(lastIndex);
	        
	        for(int i=0; i<test.numInstances(); i++) {   
	            double index = this.getMyClassifier().classifyInstance(test.instance(i));
	            String className = this.getTrainingDataset().
	            		attribute(lastIndex).value((int)index);
	            if (className.equals(test.instance(i).toString(lastIndex)))
	            	correctClass++;
	        }
	        System.out.println("Akurasi: "+(double) correctClass/test.numInstances()*100+" %");
		}
	}
	
	public void selfTesting() throws Exception {
		Evaluation evalResult = new Evaluation(this.getTrainingDataset());
		// Ten-Fold Cross Validation
		evalResult.crossValidateModel(this.getMyClassifier(), this.getTrainingDataset(), 10, new Random(1));
		System.out.println(evalResult.toSummaryString());
	}
	
	public double classifyInputData(Instance input) throws Exception {
		double label = this.getMyClassifier().classifyInstance(input);
		return label;
	}
	
	public void saveModel() throws Exception {
		SerializationHelper.write("model/J48.model", this.getMyClassifier());
	}
	
	public void loadModel() throws Exception {
		Classifier cls = (Classifier) SerializationHelper.read("model/J48.model");
		this.setMyClassifier(cls);
	}

	public static Instances loadData(String filename) throws Exception {
		Instances dataTrain = null;
		if (DTLUtil.isFileExist(filename)) {
			String fileType = filename.split("\\.")[1];
			if (fileType.equals("csv")) {
				CSVLoader loader = new CSVLoader();
			    loader.setSource(new File(filename));
			    dataTrain = loader.getDataSet();
			} else {
				if (DataSource.isArff(filename)) {
					DataSource source = new DataSource(filename);	
					dataTrain = source.getDataSet();
				}
			}			
		}
		
	    return dataTrain;
    }
}

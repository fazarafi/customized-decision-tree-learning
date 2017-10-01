package customdtl;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.FastVector;
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
	private static Scanner sc = new Scanner(System.in);
	
		
	public static void main(String[] args) {
		boolean isStopped = false;
		while (!isStopped) {
            try {
                System.out.println("===============================");
                DTLUtil.printAllFiles();
                System.out.println("Nama file dataset: mushrooms.csv");
                String filename = new String("mushrooms.csv");
                DTLExample dtlModel = new DTLExample();
				dtlModel.setTrainingDataset(loadData("files/"+filename));
				if (dtlModel.getTrainingDataset()!=null) {
					System.out.println("Indeks kelas di akhir? y/n");
					String str = new String("");
					int classIndex;
					
					if (str.equals("y")) {
						classIndex = dtlModel.getTrainingDataset().numAttributes() - 1;
					} else {
						System.out.print("Jadi di indeks ke? ");
						classIndex = sc.nextInt();
					}
					if (dtlModel.getTrainingDataset().classIndex() == -1) {
			        	dtlModel.getTrainingDataset().setClassIndex(classIndex);
					}
					
					System.out.println("Remove attribute? (y/n)");
					str = sc.next();
					if (str.equals("y")) {
						System.out.print("Indeks Atribut ke? ");
						String attId = sc.next();
						dtlModel.removeAttribute(attId);
					}
					
					System.out.println("Filter dataset dengan Resample? (y/n)");
	                str = sc.next();
					if (str.equals("y")) {
						dtlModel.filterData();
						System.out.println("Dataset di-filter!");
					} else {
						System.out.println("Dataset tidak di-filter!");
					}
					
					System.out.println("=========================== TRAINING STARTED");
					dtlModel.trainModel();
					System.out.println("=========================== TRAINING FINISHED");
					
					System.out.println("Tes model dengan 10-fold Cross Validation? (y/n)");
					str = sc.next();
					if (str.equals("y")) {
						dtlModel.crossValTesting();
					}
					
					System.out.println("Tes model dengan train-test Percentage? (y/n)");
					str = sc.next();
					if (str.equals("y")) {
						System.out.println("Berapa persen? (y/n)");
						int trainPerc = sc.nextInt();
						dtlModel.percSplitTesting(trainPerc);
					}
					
					System.out.println("Save Model? (y/n)");
					str = sc.next();
					if (str.equals("y")) {
						dtlModel.saveModel();
					}
					
					System.out.println("CLASSIFY DATA");
					System.out.println("Load Model untuk Classify Instance? (y/n)");
					str = sc.next();
					if (str.equals("y")) {
						dtlModel.loadModel();
						System.out.println("Model berhasil di-load!");
					} 
					
					int numAtt = dtlModel.getTrainingDataset().numAttributes();
					int classIdx = dtlModel.getTrainingDataset().classIndex();
					System.out.println("Jumlah Atribut ="+numAtt);
					System.out.println("Atribut Kelas="+dtlModel.getTrainingDataset().classIndex());
					System.out.println("Contoh Masukan ="+dtlModel.getTrainingDataset().instance(0).toString());
					FastVector fvWekaAttributes = new FastVector(numAtt);
					for (int i=0; i<numAtt;i++) {
						fvWekaAttributes.addElement(dtlModel.getTrainingDataset().attribute(i));
					}
					Instance unseenInst = new DenseInstance(numAtt);
					for (int i=0; i<numAtt;i++) {
						if (i!=classIdx) {
							System.out.print("Atribut ke-"+i+": ");
							String input = sc.next();
							unseenInst.setValue((Attribute)fvWekaAttributes.elementAt(i), input);
						}
					}
					System.out.println("Terklasifikasi: "+dtlModel.getTrainingDataset().attribute(classIdx)
							.value((int)dtlModel.classifyInputData(unseenInst)));
				} else {
					System.out.println("file not found.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
            isStopped = true;
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
		C45Classifier DTL = new C45Classifier();
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
			int classIdx = this.getTrainingDataset().classIndex();
			int correctClass = 0;
			Instances test = source.getDataSet();
	        test.setClassIndex(classIdx);
	        
	        for(int i=0; i<test.numInstances(); i++) {   
	            double index = this.getMyClassifier().classifyInstance(test.instance(i));
	            String className = this.getTrainingDataset().
	            		attribute(classIdx).value((int)index);
	            if (className.equals(test.instance(i).toString(classIdx)))
	            	correctClass++;
	        }
	        System.out.println("Akurasi: "+(double) correctClass/test.numInstances()*100+" %");
		}
	}
	
	public void crossValTesting() throws Exception {
		Evaluation evalResult = new Evaluation(this.getTrainingDataset());
		// Ten-Fold Cross Validation
		evalResult.crossValidateModel(this.getMyClassifier(), this.getTrainingDataset(), 2, new Random(1));
		System.out.println(evalResult.toSummaryString());
	}
	
	public void percSplitTesting(int trainPerc) throws Exception {
		Instances[] splittedInst =  DTLUtil.splitInstances(
				(trainPerc/100)*trainingDataset.numInstances(), trainingDataset);
		// Training data
		Classifier cls = new ID3Classifier();
		cls.buildClassifier(splittedInst[0]);
		
		// Testing 
		int classIdx = splittedInst[0].classIndex();
		int correctClass = 0;
		for(int i=0; i<splittedInst[1].numInstances(); i++) {   
            double index = this.getMyClassifier().classifyInstance(splittedInst[1].instance(i));
            String className = this.getTrainingDataset().
            		attribute(classIdx).value((int)index);
            if (className.equals(splittedInst[1].instance(i).toString(classIdx)))
            	correctClass++;
        }
        System.out.println("Akurasi: "+(double) correctClass/splittedInst[1].numInstances()*100+" %");
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
	
	public static void handleMissingAttribute(Instances data) {
		int numAttributes = data.numAttributes();
		for (int i=0; i<numAttributes; i++) {
			data.deleteWithMissing(i);
		}
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
			
			handleMissingAttribute(dataTrain);
		}
		
	    return dataTrain;
    }
}

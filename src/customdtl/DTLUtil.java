package customdtl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class DTLUtil {
	public static boolean isFileExist(String filename) {
		File tmpDir = new File(filename);
		return tmpDir.exists();
	}

	public static String[] printAllFiles() {
		File folder = new File("files");
		File[] listOfFiles = folder.listFiles();
		String[] filenames = new String[listOfFiles.length + 1];
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				System.out.println((i + 1) + "- " + listOfFiles[i].getName());
				filenames[i + 1] = listOfFiles[i].getName();
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());
			}
		}

		return filenames;
	}

	// count the number of instance that has class index i
	public static int count(Instances ins, int i) {
		int total = 0;
		for (Instance singleIns : ins) {
			if ((int) singleIns.classValue() == i)
				total += 1;
		}
		return total;
	}

	// fungsi bantuan untuk prosedur di bawahnya
	public static int[] getClassesDataF(Instances ins) {
		int[] arr_class = new int[ins.numClasses()];
		for (int i = 0; i < arr_class.length; i++) {
			arr_class[i] = count(ins, i);
		}
		return arr_class;
	}

	// 2 fungsi bantuan untuk prosedur di bawahnya (2/2)
	public static double calculateEntropyF(Instances ins) {
		int[] arr_class = getClassesDataF(ins);
		double ent = 0; // entropy
		for (int i = 0; i < arr_class.length; i++) {
			if (arr_class[i] != 0) {
				double prob = (double) arr_class[i] / (double) ins.numInstances();
				ent += prob * Math.log(prob);
			}
		}
		ent *= (double) -1 / Math.log(2d);

		return ent;
	}

	// hitung information gain
	public static double calculateIgF(Instances ins, Attribute att) {
		double entAll = calculateEntropyF(ins);
		double infGain = entAll;
		ArrayList<String> arr_val = possibleAttributeValue(ins, att);

		for (String s : arr_val) {
			Instances subsetIns = filterInstances(ins, att, s);
			double entSubsetIns = calculateEntropyF(subsetIns);
			infGain -= ((double) subsetIns.numInstances() / (double) ins.numInstances()) * entSubsetIns;
		}
		return infGain;
	}

	// cari possible value dari atribute
	public static ArrayList<String> possibleAttributeValue(Instances ins, Attribute att) {
		ArrayList<String> arr_val = new ArrayList<>();

		for (Instance singleIns : ins) {
			if (!arr_val.contains(singleIns.stringValue(att)))
				arr_val.add(singleIns.stringValue(att));
		}
		return arr_val;
	}

	public static double calculateGainRatio(Instances ins, Attribute att) {
		double entAll = calculateEntropyF(ins);
		double infGain = entAll;
		ArrayList<String> arr_val = possibleAttributeValue(ins, att);

		for (String s : arr_val) {
			Instances subsetIns = filterInstances(ins, att, s);
			double entSubsetIns = calculateEntropyF(subsetIns);
			infGain -= ((double) subsetIns.numInstances() / (double) ins.numInstances()) * entSubsetIns;
		}
		return infGain / calculateSplitInfo(ins, att);
	}

	public static double calculateSplitInfo(Instances ins, Attribute att) {
		Map<String,Integer> freqMap = countFreqAtt(ins, att);
		double splitInfo = 0d;
		
		for (Map.Entry<String, Integer> singleFreq : freqMap.entrySet()) {
		    double prob = (double) singleFreq.getValue() / (double) ins.numInstances();
			splitInfo += prob * Math.log(prob);
		}
		splitInfo *= (double) -1 / Math.log(2d);
		
		return splitInfo;
	}

	public static Map<String,Integer> countFreqAtt(Instances ins, Attribute att) {
		ArrayList<String> arr_val = possibleAttributeValue(ins, att);
		Map<String,Integer> freqMap = new HashMap<>(); 
		for (String val : arr_val) {
			freqMap.put(val,0);
		}
		
		for (Instance singleIns : ins) {
			freqMap.put(singleIns.stringValue(att), freqMap.get(singleIns.stringValue(att))+1);
		}
		return freqMap;
	}

	public static Instances filterInstances(Instances ins, Attribute att, String value) {
		Instances newIns = new Instances(ins);

		for (int i = newIns.numInstances() - 1; i >= 0; i--) {
			Instance singleIns = newIns.get(i);
			if (!singleIns.stringValue(att).equals(value)) {
				newIns.delete(i);
			}
		}

		return newIns;
	}

	public static Instances[] splitInstances(int cutPoint, Instances inst) {
		Instances[] splittedInst = new Instances[2];
		splittedInst[0] = new Instances(inst, 0, cutPoint + 1);
		splittedInst[1] = new Instances(inst, cutPoint + 1, inst.numInstances() - cutPoint - 1);
		return splittedInst;
	}
}

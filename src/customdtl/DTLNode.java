/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import java.util.ArrayList;
import weka.core.Instances;
import weka.core.Instance;
import weka.core.Attribute;

/**
 *
 * @author adesu
 */
public class DTLNode {
    
    public String className; // nama kelas apabila node adalah daun
    public int classIndex; // index kelas apabila node adalah daun
    
    public int[] classes; // data kelas dan jumlahnya (untuk kalkulasi entropy)
    public double entropy; // entropy dari data     
    
    public DTLNode parent; // parent dari node ini (untuk akar, parent == null)
    public Attribute attributeToCheck; // atribute yang di check di node itu
    public ArrayList<Attribute> possibleAttribute; // atribute yang masih tersisa
    
    public ArrayList<Double> ig; // ig dari atribute yang mungkin, indeks sama dengan indeks possible atribute
    public ArrayList<DTLNode> children;
    
    public ArrayList<String> attributeValues; // value yang mungkin dari attribute
    
    public DTLNode(){
        className = null;
        classIndex = -1;
        // classes di bangkitkan saat pemanggilan fungsi
        // entropy tidak perlu diinisialisasi
        parent = null;
        attributeToCheck = null;
        possibleAttribute = new ArrayList<>();
        ig = new ArrayList<>();
        children = new ArrayList<>();
        // attributeValues di bangkitkan saat pemanggilan fungsi
    }
        
    public boolean isRoot(){
        return parent == null;
    }
    
    public boolean isLeaf(){
        // return !(className.isEmpty());
        return classIndex == -1;
    }
    
    // count the number of instance that has class index i
    public int count(Instances ins, int i) {
        int total = 0;
        for (Instance singleIns : ins) {
            if (singleIns.classIndex()==i)
                total += 1;
        }
        return total;
    }
    
    // fungsi bantuan untuk prosedur di bawahnya
    public int[] getClassesDataF(Instances ins) {
        int[] arr_class = new int[ins.numClasses()];
        for (int i = 0; i < arr_class.length; i++) {
            arr_class[i] = count(ins, i);
        }
        
        return arr_class;
    }
    
    // hitung tiap kelas dan masukkan data ke classes (untuk perhitungan entropy)
    public void getClassesData(Instances ins) {
        classes = getClassesDataF(ins);
    }
    
    // 2 fungsi bantuan untuk prosedur di bawahnya (1/2)
    public double calculateEntropyF(Instances ins, int[] arr_class) {
        double ent = 0; // entropy
        for (int i = 0; i < arr_class.length; i++) {
            double prob = (double) arr_class[i] / (double) ins.numInstances();
            ent += prob * Math.log(prob);
        }
        ent *= -1 / Math.log(2d);
        
        return ent;
    }
    // 2 fungsi bantuan untuk prosedur di bawahnya (2/2)
    public double calculateEntropyF(Instances ins) {
        int[] arr_class = getClassesDataF(ins);
        double ent = 0; // entropy
        for (int i = 0; i < arr_class.length; i++) {
            double prob = (double) arr_class[i] / (double) ins.numInstances();
            ent += prob * Math.log(prob);
        }
        ent *= -1 / Math.log(2d);
        
        return ent;
    }
    
    // formulasi entropy
    public void calculateEntropy(Instances ins) {
        entropy = calculateEntropyF(ins, classes);
    }
    
    // dapatkan list atribute yang mungkin
    public void fillArrayPossibleAttribut(Instances ins) {
        if (parent == null) {
            int jmlAtr = ins.numAttributes();
            for (int i = 0; i < jmlAtr; i++) {
                // masukin semua yg mungkin di instances-nya
                possibleAttribute.add(ins.attribute(i));
            }
        } else { // ada parentnya
            int jmlAtr = parent.possibleAttribute.size();
            for (int i = 0; i < jmlAtr; i++) {
                // masukin possible atribute parent, KECUALI atribute parentnya itu sndiri
                if (!parent.possibleAttribute.get(i).equals(parent.attributeToCheck)) {
                    possibleAttribute.add(parent.possibleAttribute.get(i));
                }
            }
        }
    }
    
    public Instances filterInstances(Instances ins, Attribute att, String value) {
        Instances newIns = new Instances(ins);
        
        for (int i = newIns.numInstances() - 1; i >= 0; i--) {
            Instance singleIns = newIns.get(i);
            if (!singleIns.stringValue(att).equals(value)) {
                newIns.delete(i);
            }
        }
        
        return newIns;
    }
    
    // cari possible value dari atribute
    public ArrayList<String> possibleAttributeValue(Instances ins, Attribute att) {
        ArrayList<String> arr_val = new ArrayList<>();
        
        for (Instance singleIns : ins) {
            if (!arr_val.contains(singleIns.stringValue(att)))
                arr_val.add(singleIns.stringValue(att));
        }
        
        return arr_val;
    }
    
    // hitung information gain
    public double calculateIgF(Instances ins, Attribute att) {
        double entAll = calculateEntropyF(ins);
        double infGain = entAll;
        ArrayList<String> arr_val = possibleAttributeValue(ins, att);
        
        for (String s : arr_val) {
            Instances subsetIns = filterInstances(ins, att, s);
            double entSubsetIns = calculateEntropyF(subsetIns);
            infGain -= (subsetIns.numInstances()/ins.numInstances())*entSubsetIns;
        }
        
        return infGain;
    }

    // isi array of information gain
    public void calculateIg(Instances ins){
        for (Attribute att : possibleAttribute) {
            ig.add(calculateIgF(ins, att));
        }
    }
    
    // cari index attribute dengan ig max
    public int getIndexBestAttribute() {
        int index = 0;
        for (int i=0;i<ig.size();i++) {
            if (ig.get(i) > ig.get(index))
                index = i;
        }
        
        return index;
    }
    
    public void saveAttributeValues(Instances ins) {
        attributeValues = possibleAttributeValue(ins, attributeToCheck);
    }
}

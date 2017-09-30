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
    
    public int[] classes;
    public double entropy; // entropy dari data     
    
    public DTLNode parent; // parent dari node ini (untuk akar, parent == null)
    public Attribute attributeToCheck; // atribute yang di check di node itu
    public ArrayList<Attribute> possibleAttribute; // atribute yang masih tersisa
    
    public ArrayList<Double> ig; // ig dari atribute yang mungkin, indeks sama dengan indeks possible atribute
//    public String valueTo; 
//    public Instances data;
//    public ArrayList<DTLNode> children;
    
    
    
    
    public DTLNode(){
        className = null;
        classIndex = -1;
        parent = null;
        possibleAttribute = new ArrayList<Attribute>();
        
    }
    
//    public String getClassName(){
//        return className;
//    }
//    
//    public void setClassName(String s){
//        className = s;
//    }
    
    // dapatkan list atribute yang mungkin
    public void fillArrayPossibleAttribut(Instances ins){
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
    
    // fungsi bantuan untuk prosedur di bawahnya
    public double calculateEntropyF(Instances ins, int[] arr_class) {
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
    
//    public void calculateIG(){
//        
//    }
    
    // menyimpan parent dari node tersebut
    // (Root/akar tidak akan memanggil prosedur ini)
    public void setParent(DTLNode parent){
        
    }
    
    // bangkitkan anak dari node
    // don't forget to set parent in the end!!!!
    public void addChild(DTLNode child){
        
    }
    
    public boolean isRoot(){
        return parent == null;
    }
    
    public boolean isLeaf(){
//        return !(className.isEmpty());
        return classIndex == -1;
    }
    
//    public void removeParent(){
//        
//    }
    
    // count the number of instance that has class index i
    public int count(Instances ins, int i) {
        int total = 0;
        for (Instance singleIns : ins) {
            if (singleIns.classIndex()==i)
                total += 1;
        }
        return total;
    }
}

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
    
    public double entropy; // entropy dari data 
    public double ig;
    public Attribute attributeToCheck; // atribute yang di check di node itu
//    public String valueTo; 
//    public Instances data;
    public DTLNode parent;
//    public ArrayList<DTLNode> children;
    public ArrayList<Attribute> possibleAttribute; // atribute yang masih tersisa
    public int classes[];
    public String className; // nama kelas apabila node adalah daun
    public int classIndex; // index kelas apabila node adalah daun
    
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
    
    // hitung tiap kelas dan masukkan data ke classes (untuk perhitungan entropy)
    public void getClassesData(Instances ins) {
        classes = new int[ins.numClasses()];
        for (int i = 0; i < classes.length; i++) {
            classes[i] = count(ins, i);
        }
    }
    
    // formulasi entropy
    public void calculateEntropy(Instances ins){
        entropy = 0;
        for (int i = 0; i < classes.length; i++) {
            double prob = (double) classes[i] / (double) ins.numInstances();
            entropy += prob * Math.log(prob);
        }
        entropy *= -1 / Math.log(2d);
    }
    
    public void calculateIG(){
        
    }
    
    public void setParent(DTLNode parent){
        
    }
    
    public void addChild(DTLNode child){
        
    }
    
    public boolean isRoot(){
        return true;
    }
    
    public boolean isLeaf(){
        return true;
    }
    
    public void removeParent(){
        
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
}

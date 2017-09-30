/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import java.util.ArrayList;
import weka.core.Instances;
import weka.core.Instance;

/**
 *
 * @author adesu
 */
public class DTLNode {
    
    private double Entropy; // entropy dari data 
    private double IG;
//    public String AttributeName;
//    public String ValueTo; 
//    public ArrayList<Instances>;
//    public DTLNode Parent;
//    public ArrayList<DTLNode> Children;
//    public ArrayList<String> PossibleAttribut;
    public int classes[];
    public String className; // nama kelas apabila node adalah daun
    public int classIndex; // index kelas apabila node adalah daun
    
    public DTLNode(){
        className = null;
        classIndex = -1;
    }
    
//    public String getClassName(){
//        return className;
//    }
//    
//    public void setClassName(String s){
//        className = s;
//    }
    
    public void fillArrayPossibleAttribut(){
        
    }
    
    public void calculateEntropy(){
        
    }
    
    public double getEntropy(){
        return Entropy;
    }
    
    public void calculateIG(){
        
    }
    
    public double getIG(){
        return IG;
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

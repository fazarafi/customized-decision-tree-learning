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
    public Attribute attributeToCheck;
//    public String valueTo; 
//    public Instances data;
    public DTLNode parent;
//    public ArrayList<DTLNode> children;
    public ArrayList<Attribute> possibleAttribute;
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
    
    public void fillArrayPossibleAttribut(){
        
    }
    
    public void calculateEntropy(){
        
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import java.util.ArrayList;
import weka.core.Instances;

/**
 *
 * @author adesu
 */
public class DTLNode {
    
    private double Entropy;
    private double IG;
    public String AtrributeName;
    public String ValueTo; 
//    public ArrayList<Instances>;
    public DTLNode Parent;
    public ArrayList<DTLNode> Children;
    public ArrayList<String> PossibleAttribut;
    private String className;
    
    public DTLNode(){
        className = "";
    }
    
    public String getClassName(){
        return className;
    }
    
    public void setClassName(String s){
        className = s;
    }
    
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
}

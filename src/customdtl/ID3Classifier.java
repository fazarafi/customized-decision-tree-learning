package customdtl;

import weka.classifiers.AbstractClassifier;
import java.util.ArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;
import java.lang.*;

public class ID3Classifier extends AbstractClassifier{
    
    public DTLNode tree;
    
    public DTLNode buildTree(Instances ins) {
        DTLNode node = new DTLNode();
        if (isAllSame(ins)){ // jadikan node ini sebagai node daun!!
            // BASIS
            node.className = ins.get(0).toString(ins.classIndex());
            node.classIndex = ins.classIndex();
        } else {
            // REKURENS
            
            // rootNode.className = null; (ada di konstruktor)
            // rootNode.classIndex = -1; (ada di konstruktor)
            
            // HITUNG ENTROPY
            node.getClassesData(ins);
            node.calculateEntropy(ins);
            
            // DAPATKAN ATRIBUT YANG MASIH MUNGKIN
            node.fillArrayPossibleAttribut(ins);
            
            // HITUNG IG TIAP POSSIBLE ATRIBUTE
            node.calculateIg(ins);
            node.attributeToCheck = node.possibleAttribute.get(node.getIndexBestAttribute());
            node.saveAttributeValues(ins);
            
            // BANGKITKAN ANAK
            ArrayList<String> childString = node.possibleAttributeValue(ins, node.attributeToCheck);
            for (String s : childString) {
                Instances subsetIns = node.filterInstances(ins, node.attributeToCheck, s);
                DTLNode childNode = buildTree(subsetIns);
                childNode.parent = node;
                node.children.add(childNode);
            }
        }
        
        return node;
    }
    
    @Override
    public void buildClassifier(Instances ins) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // create roots
        tree = buildTree(ins);
    }
    
    public static int getClassIndex(Instance ins, DTLNode node) {
        //missing attribute
        if (node.isLeaf()) {
            return node.classIndex; // basis
        } else { // bukan daun
            String val = ins.stringValue(node.attributeToCheck);
            int index = node.attributeValues.indexOf(val);
            DTLNode child = node.children.get(index);
            return getClassIndex(ins, child);
        }
    }
    
    @Override
    public double classifyInstance(Instance ins) {
        return (double) getClassIndex(ins, tree);
    }
    
    public boolean isAllSame(Instances ins){
        Instance firstIns = ins.firstInstance();
        for(int i = 1; i<ins.numInstances();i++) {
            if (!ins.get(i).toString(ins.classIndex()).equals(firstIns.toString(ins.classIndex())) ){
                return false;
            } 
        }
        return true;
    }
    
}

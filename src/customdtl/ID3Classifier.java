package customdtl;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;
import java.lang.*;

public class ID3Classifier extends AbstractClassifier{
    
    public DTLNode tree;
    
    @Override
    public void buildClassifier(Instances ins) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    // create roots
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
            
            // BANGKITKAN ANAK
        }
        
        tree = rootNode;
    }
    
    @Override
    public double classifyInstance(Instance ins) {
        //missing attribute
        if (tree.isLeaf()) {
            return (double) tree.classIndex; // basis
        } else {
            //rekurens
            return 0d;
        }        
    }
    
    public boolean isAllSame(Instances ins){
        Instance firstIns = ins.firstInstance();
        for(int i = 1; i<ins.numInstances();i++) {
//            System.out.println("beforeif");
            if (!ins.get(i).toString(ins.classIndex()).equals(firstIns.toString(ins.classIndex())) ){
//                System.out.println("before false");
                return false;
            } 
        }
        return true;
    }
    
}

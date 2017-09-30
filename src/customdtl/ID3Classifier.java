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
        DTLNode rootNode = new DTLNode();
        if (isAllSame(ins)){
            rootNode.className = ins.get(0).toString(ins.classIndex());
            rootNode.classIndex = ins.classIndex();
        } else {
            // rootNode.className = null;
            // rootNode.classIndex = -1;
            
            // hitung masukkan data ke classes
            rootNode.classes = new int[ins.numClasses()];
            for (int i=0;i<rootNode.classes.length;i++) {
                rootNode.classes[i] = rootNode.count(ins, i);
            }
            
            // hitung entropy
            rootNode.entropy = 0;
            for (int i=0;i<rootNode.classes.length;i++) {
                double prob = (double) rootNode.classes[i] / (double) ins.numInstances();
                rootNode.entropy += prob*Math.log(prob);
            }
            rootNode.entropy *= -1/Math.log(2d);
            
            // list atribute yang mungkin
            if (rootNode.parent == null) {
                int jmlAtr = ins.numAttributes();
                for (int i=0;i<jmlAtr;i++) {
                    // masukin semua yg mungkin di instances-nya
                    rootNode.possibleAttribute.add(ins.attribute(i));
                }
            } else { // ada parentnya
                int jmlAtr = rootNode.parent.possibleAttribute.size();
                for (int i=0;i<jmlAtr;i++) {
                    // masukin possible atribute parent, KECUALI atribute parentnya itu sndiri
                    if (!rootNode.parent.possibleAttribute.get(i).equals(rootNode.parent.attributeToCheck))
                        rootNode.possibleAttribute.add(rootNode.parent.possibleAttribute.get(i));
                }
            }
        }
        
        tree = rootNode;
    }
    
    @Override
    public double classifyInstance(Instance ins) {
        //missing attribute
        if (!tree.className.isEmpty()) {
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

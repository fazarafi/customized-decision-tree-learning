package customdtl;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

public class ID3Classifier extends AbstractClassifier{
    
    public ID3Classifier(){
    
    }
    
    @Override
    public void buildClassifier(Instances ins) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    // create roots
        DTLNode rootNode = new DTLNode();
        if (isAllSame(ins)){

        }
    }
    
    @Override
    public double classifyInstance(Instance ins) {
    //missing attribute
        return 0;
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

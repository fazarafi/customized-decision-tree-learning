package customdtl;

import weka.classifiers.AbstractClassifier;
import weka.core.Instance;
import weka.core.Instances;

public class ID3Classifier extends AbstractClassifier{

    public ID3Classifier(){
    
    }
    
    @Override
    public void buildClassifier(Instances i) throws Exception {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public double classifyInstance(Instance i) {
    //missing attribute
        return 0;
    }

}

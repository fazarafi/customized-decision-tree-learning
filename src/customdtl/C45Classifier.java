package customdtl;

import static customdtl.ID3Classifier.getClassIndex;
import java.util.ArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instances;

public class C45Classifier extends ID3Classifier{
        
        public double accuracy; //dapet dari model tree yang udah di training
        public double threshold;
        ArrayList<Rule> rules;
        
	@Override
	public void buildClassifier(Instances input) throws Exception {
		// TODO Auto-generated method stub
		
	}
        
//        public void reducedErrorPrunning(){
//            
//        }
        
        public private QSortRules(int kiri,int kanan){
            
        }
        
        public void rulePostPrunning(DTLNode node,Instances ins){
            ArrayList<Logic> al = new ArrayList<Logic>();
            int idx = 0;
            convertToRule(node,al,idx);
            for(int i=0;i<al.size();i++){
                rules.get(i).prunning(accuracy,ins);
            }
            QsortRules();
        }
        
        public void convertToRule(DTLNode node,ArrayList<Logic> al,int currentIdx){ 
            // nilai current index awal =0 ,class al sudah di create sebelum dipanggil rekursif 
            
            try {
            //missing attribute
                if (node.isLeaf()) {
                    Rule r;
                    for(int i=0;i<currentIdx;i++){
                        Logic ll = al.get(i);
                        Logic newl = Logic(ll.value,ll.attribute);
                        r.addLogic(ll);
                    }
                    r.getValueClass()
                    rules.add(r);
//                  r.setValueClass(node.classIndex);
                } else { // bukan daun
                    currentIdx++;
                    for(int i=0;i<node.children.size();i++){
                        Logic l = new Logic(node.attributeValues.get(i),node.attributeToCheck)
                        al.add(currentIdx,l);
                        convertToRule(node.children.get(i),al,currentIdx);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
}
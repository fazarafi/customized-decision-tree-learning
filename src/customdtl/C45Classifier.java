package customdtl;

import static customdtl.ID3Classifier.getClassIndex;
import java.util.ArrayList;
import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class C45Classifier extends ID3Classifier{
    public static Instances allIns;
    public static DatasetPreProcessor dPP;
    public double accuracy; //dapet dari model tree yang udah di training
//        public double threshold;
    public ArrayList<Rule> rules = new ArrayList<>();
    
    public void printAllRules(){
        for(int i=0;i<rules.size();i++){
            rules.get(i).printRule();
        }
    }
    @Override
    public DTLNode buildTree(Instances ins, DTLNode newParent) {
        try {
            DTLNode node = new DTLNode(newParent);
            if (isAllSame(ins)) { // jadikan node ini sebagai node daun!!
                // BASIS
                node.className = ins.get(0).toString(ins.classIndex());
                node.classIndex = (int) ins.get(0).classValue();
//                System.out.println("leaf");
            } else {
                // REKURENS

                // rootNode.className = null; (ada di konstruktor)
                // rootNode.classIndex = -1; (ada di konstruktor)
                // HITUNG ENTROPY
                node.getClassesData(ins);
                node.calculateEntropy(ins);

                // DAPATKAN ATRIBUT YANG MASIH MUNGKIN
                node.fillArrayPossibleAttribut(ins);
                
                // CHECK APABILA ATRIBUT YANG MASIH MUNGKIN
                if (node.possibleAttribute.isEmpty()) {
                    node.classIndex = (int) ins.get(0).classValue();
                    int[] arr_class = DTLUtil.getClassesDataF(ins);
                    int max = 0;
                    for (int i=0;i<arr_class.length;i++){
                        if (arr_class[i]>arr_class[max])
                            max = i;
                    }
                    node.classIndex = max;
                } else {
                
                // HITUNG IG TIAP POSSIBLE ATRIBUTE
                node.calculateIg(ins);
                node.calculateGainRatio(ins);
                node.attributeToCheck = node.possibleAttribute.get(node.getIndexBestAttributeByGainRatio());
//                  System.out.println("bawah");
                node.saveAttributeValues(ins);
//                System.out.println(node.attributeValues);

                // BANGKITKAN ANAK
        
                ArrayList<String> childString = DTLUtil.possibleAttributeValue(ins, node.attributeToCheck);
                for (String s : childString) {
                    Instances subsetIns = DTLUtil.filterInstances(ins, node.attributeToCheck, s);
//                    System.out.println("ins = "+ins.numInstances()+", sub = "+subsetIns.numInstances());
                    DTLNode childNode = buildTree(subsetIns, node);
                    node.children.add(childNode);
                }
                }
            }

            return node;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
//   
        
    @Override
    public double classifyInstance(Instance ins) {
        return (double) getClassIndexC45(ins, rules);
    }
        
    @Override
    public void buildClassifier(Instances ins) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // create tree (root)
        dPP = new DatasetPreProcessor(ins);
        allIns = ins;
        tree = this.buildTree(ins, null);
//        System.out.println("aman build");
        rulePostPrunning(tree,allIns);
//        printAllRules();
//        System.out.println("build classifier selesai");
    }
        
        public void swap(int i, int j) {
            Rule temp1 = new Rule(rules.get(i));
            Rule temp2 = new Rule(rules.get(j));
            rules.set(i, temp2);
            rules.set(j, temp1);
        }
        
//        public void reducedErrorPrunning(){
//            
//        }
        
        public void qSortRules(int kiri,int kanan){
            int i,div;
            if(kiri<kanan){
                double accleft = rules.get(kiri).getAccuration();
                div = kiri;
                for(i=kiri+1;i<kanan;i++){
                    if(rules.get(i).getAccuration()<=accleft){
                        div++;
                        swap(div,i);
                    }
                }
                swap(kiri,div);
                qSortRules(kiri,div);            
                qSortRules(div+1,kanan);
            }
        }
        
        public void rulePostPrunning(DTLNode node,Instances ins) throws Exception {
            ArrayList<Logic> al = new ArrayList<>();
            int idx = 0;
            convertToRule(node,al,idx);
//            System.out.println("convert ke rule");
//            printAllRules();
            for(int i=0;i<rules.size();i++){
                rules.get(i).prunning(accuracy,ins);
            }
            qSortRules(0,rules.size()-1);
        }
        
        public void convertToRule(DTLNode node,ArrayList<Logic> al,int currentIdx) throws Exception{ 
            // nilai current index awal =0 ,class al sudah di create sebelum dipanggil rekursif 
            
            try {
            //missing attribute
                if (node.isLeaf()) {
                    Rule r = new Rule();
                    for(int i=0;i<currentIdx;i++){
                        Logic ll = al.get(i);
                        Logic newl = new Logic(ll.value,ll.attribute);
                        r.addLogic(newl);
                    }
                    r.setValueClass(allIns.attribute(allIns.classIndex()).value(node.classIndex));
                    r.setIdxClass(node.classIndex);
                    rules.add(r);
//                  r.setValueClass(node.classIndex);
                } else { // bukan daun
                    currentIdx++;
                    for(int i=0;i<node.children.size();i++){
                        Logic l = new Logic(node.attributeValues.get(i),node.attributeToCheck);
//                        System.out.println(l);
                        if(currentIdx >= al.size()){
                            al.add(l);
                        }else al.add(currentIdx,l);
                        
                        convertToRule(node.children.get(i),al,currentIdx);
                    }
                }
            }catch (Exception e) {
//                System.out.println(e);
//                System.out.println("exception di convert to rule");
            }
        }
                
	public static int getClassIndexC45(Instance ins,ArrayList<Rule> rulesAfterPrunning) {
//            System.out.println("masuk classify");
            boolean isRuleMatch = false;
            int idxAns = -1;
            int ii = 0;
            while(!isRuleMatch && ii<rulesAfterPrunning.size()){
//                System.out.println("loop ke " + ii);
                ArrayList<Logic> logics = rulesAfterPrunning.get(ii).getRule();
                boolean isAtrValid = true;
                for(int i=0;i<logics.size();i++){
                    String s = ins.toString((Attribute)logics.get(i).attribute); //dapetin ins value dari atribut di logic
                    if (!s.equals(logics.get(i).value)){ //bandingin value di ins sama di logic sesuai atribute
                        isAtrValid = false;
                    }
                }
                if(isAtrValid){
                    isRuleMatch = true;
                    idxAns = rulesAfterPrunning.get(ii).getIdxClass();
                }
                ii++;
            }
            if(isRuleMatch){
                return idxAns;
            }else{
                int[] arr_class = DTLUtil.getClassesDataF(allIns);
                int max = 0;
                for (int i=0;i<arr_class.length;i++){
                    if (arr_class[i]>arr_class[max])
                        max = i;
                }
                return max;
            }
        }
}

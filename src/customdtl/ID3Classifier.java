package customdtl;

import java.io.Serializable;
import weka.classifiers.AbstractClassifier;
import java.util.ArrayList;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Attribute;

public class ID3Classifier extends AbstractClassifier implements Serializable{
    
    public DTLNode tree;
    
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
//                if (node.possibleAttribute.isEmpty()) {
//                    node.classIndex = (int) ins.get(0).classValue();
//                    int[] arr_class = DTLUtil.getClassesDataF(ins);
//                    int max = 0;
//                    for (int i=0;i<arr_class.length;i++){
//                        if (arr_class[i]>arr_class[max])
//                            max = i;
//                    }
//                    node.classIndex = max;
//                } else {
                
                // HITUNG IG TIAP POSSIBLE ATRIBUTE
                node.calculateIg(ins);
//                System.out.println("atas, "+node.possibleAttribute+"     "+node.getIndexBestAttributeByIg());
                node.attributeToCheck = node.possibleAttribute.get(node.getIndexBestAttributeByIg());
//                System.out.println("bawah");
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

            return node;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }
    
    @Override
    public void buildClassifier(Instances ins) throws Exception {
//      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // create tree (root)
        tree = buildTree(ins, null);
    }
    
    public static int getClassIndex(Instance ins, DTLNode node) {
        try {
            //missing attribute
            if (node.isLeaf()) {
//                System.out.println("leaf");
                // BASIS
                return node.classIndex; // basis
            } else { // bukan daun
                // REKURENS
                Attribute a = node.attributeToCheck;
//                System.out.println(a);
                String val = ins.stringValue(a);
//                System.out.println(val);
//                System.out.println(node.attributeValues);
                int index = node.attributeValues.indexOf(val);
                // if not found (attribute doesn't exist in data train)
                if (index == -1)
                    index = 0; // let's just use a default value
//                System.out.println(index);
                DTLNode child = node.children.get(index);
//                System.out.println("check");
                return getClassIndex(ins, child);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return 0;
    }
    
    @Override
    public double classifyInstance(Instance ins) {
        return (double) getClassIndex(ins, tree);
    }
    
    public boolean isAllSame(Instances ins) {
        Instance firstIns = ins.firstInstance();
        for(int i = 0; i<ins.numInstances();i++) {
            if (!ins.get(i).toString(ins.classIndex()).equals(firstIns.toString(ins.classIndex())) ){
                return false;
            } 
        }
        return true;
    }
    
}

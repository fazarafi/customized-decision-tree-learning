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
                node.classIndex = ins.get(0).classIndex();
                System.out.println("leaf");
            } else {
                // REKURENS

                // rootNode.className = null; (ada di konstruktor)
                // rootNode.classIndex = -1; (ada di konstruktor)
                // HITUNG ENTROPY
                node.getClassesData(ins);
                node.calculateEntropy(ins);

                // DAPATKAN ATRIBUT YANG MASIH MUNGKIN
                node.fillArrayPossibleAttribut(ins);
                System.out.println(node.possibleAttribute);
                
                // HITUNG IG TIAP POSSIBLE ATRIBUTE
                node.calculateIg(ins);
                node.attributeToCheck = node.possibleAttribute.get(node.getIndexBestAttribute());
                System.out.println(node.attributeToCheck);
                node.saveAttributeValues(ins);
                System.out.println(node.attributeValues);

                // BANGKITKAN ANAK
                ArrayList<String> childString = node.attributeValues;
                for (String s : childString) {
                    Instances subsetIns = DTLUtil.filterInstances(ins, node.attributeToCheck, s);
                    System.out.println("ins = "+ins.numInstances()+", subsetIns = "+subsetIns.numInstances());
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
                // BASIS
                return node.classIndex; // basis
            } else { // bukan daun
                // REKURENS
                Attribute a = node.attributeToCheck;
//                System.out.println("ins = "+ins);
                String val = ins.stringValue(a);
//                System.out.println("ins = "+ins);
//                System.out.println("val = "+val);
                int index = node.attributeValues.indexOf(val);
                DTLNode child = node.children.get(index);
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
//            System.out.print(ins.get(i).toString(ins.classIndex()));
//            System.out.println(","+firstIns.toString(ins.classIndex()));
            if (!ins.get(i).toString(ins.classIndex()).equals(firstIns.toString(ins.classIndex())) ){
                return false;
            } 
        }
        return true;
    }
    
}

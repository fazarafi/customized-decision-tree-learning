package customdtl;

import weka.classifiers.AbstractClassifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

public class C45Classifier extends ID3Classifier {
        public static Instances allIns;
        public static DatasetPreProcessor dPP;
        
        public void buildClassifier(Instances ins) throws Exception {
    //      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            // create tree (root)
        	dPP = new DatasetPreProcessor(ins);
            allIns = ins;
            tree = buildTree(ins, null);
            System.out.println("DARI C45");
        }
        
	// override
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
                    if (index == -1) {
                        System.out.println("oioi");
                        // let's just use most common value
                        int[] arr_class = DTLUtil.getClassesDataF(allIns);
                        //find max
                        int max = 0;
                        for (int i=0;i<arr_class.length;i++){
                            if (arr_class[i]>arr_class[max])
                                max = i;
                        }
                        return max;
    //                System.out.println(index);
                    }
                    DTLNode child = node.children.get(index);
    //                System.out.println("check");
                    return getClassIndex(ins, child);
                }
            } catch (Exception e) {
                System.out.println(e);
            }
            return 0;
        }

}

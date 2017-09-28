/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import java.util.ArrayList;
import weka.core.Instances;

/**
 *
 * @author adesu
 */
public class DTLNode {
    double Entropy;
    double IG;
    String AtrributeName;
    String ValueTo;
    ArrayList<Instances> Data;
    DTLNode Parent;
    ArrayList<DTLNode> Children;
    ArrayList<String> PossibleAttribut;
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import weka.core.Attribute;

/**
 *
 * @author adesu
 */
public class Logic {
    
    public String value;
    public Attribute attribute;
    public Logic(String val,Attribute at){
        this.value = val;
        this.attribute = at;
    }
}

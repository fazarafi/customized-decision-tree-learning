/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customdtl;

import java.util.ArrayList;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author adesu
 */
public class Rule implements Comparable<>{
    private double akurasi;
    private ArrayList<Logic> logics;
    private String valueClass;
    public Rule(ArrayList<Logic> l,string val){
        for(int i=0;i<l.size();i++){
            Logic ll = new Logic(l.get(i).value,l.get(i).attribute);
            logics.add(ll);
        }
        valueClass = val;
    }
    
    public Rule(){
    
    }
    
    public double getAccuration(){
        return akurasi;
    }
    
    private void calculateAccuration(Instances validate){
        int valid = 0;
        int invalid = 0;
        for (Instance ins:validate){
            boolean isAtrValid = true;
            for(int i=0;i<logics.size();i++){
                String s = ins.toString((Attribute)logics[i].attribute); //dapetin ins value dari atribut di logic
                if (!s.equals(logics[i].value)){ //bandingin value di ins sama di logic sesuai atribute
                    isAtrValid = false;
                }
            }
        
            if(isAtrValid){
               if(ins.toString(ins.classIndex()).equals(valueClass)){
                   valid++;
               }else{
                   invalid++;
               }
            }
        }
//            System.out.println(i + ". " + ins.attribute(i).name());
        akurasi = valid/valid+invalid;
    }
    
    public void addLogic(Logic l){
        logics.add(l);
    }
    
    public void setValueClass(String s){
        valueClass = s;
    }
    
    public String getValueClass(){
        return valueClass;
    }
    
    
    //sementara 1 logic
    public Logic deleteLogic(int idx){
        Logic l = getLogic(idx);  
        logics.remove(idx);
        return l;
    }
    
    public Logic getLogic(int idx){
        return logics.get(idx);
    }
    
    public void insertLogic(Logic l,int idx){
        logics.add(idx,l);
    }
    
    public void prunning(double thresholdAccuracy,Instances ins){
        double threshold = thresholdAccuracy;
        int idxDeleted = -1;
//        boolean isChanged = false;
        for(int i=0;i<logics.size();i++){
            Logic l = deleteLogic(i);
            calculateAccuration(ins);
            if(getAccuration()>threshold){
//              isChanged = true;
                idxDeleted = i;
                threshold = getAccuration();
            }
            insertLogic(l,i);
        }
        if(idxDeleted != -1){
            Logic l = deleteLogic(idxDeleted);
            calculateAccuration(ins);
        }
    }
}
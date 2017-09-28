package customdtl;

import java.util.Scanner;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class MainProgram {
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) throws Exception {
//        System.out.println("Dyas aja");
        Scanner scan = new Scanner(System.in);
        int pil = scan.nextInt();
        System.out.print("Masukkan nama file train : ");
        String filename = scan.next();
        
        //isi sesuai path
        ConverterUtils.DataSource source = new ConverterUtils.DataSource(("C:\\Program Files\\Weka-3-8\\data\\" + filename));
        
        Instances train = source.getDataSet();
        ID3Classifier id3 = new ID3Classifier();
        
    }
}

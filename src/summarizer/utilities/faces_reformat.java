package summarizer.utilities;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
 
public class faces_reformat {
    public static final int split_number = 10;

    public static String reformat_line(String line){
        String new_line = "";
        String[] arrOfStr = line.split(":", split_number);
        int cnt = 0;
        for(String a: arrOfStr){
            cnt++;
            if(cnt == 1) continue;
            new_line = new_line + a.charAt(0) + " ";    
        }
        return new_line;
    }


     public static void main(String[] args){
        try { 
 
            /* read txt file */
            String pathname = "soccer_faces.txt";
            File filename = new File(pathname); 

            /* write txt file */
            File writename = new File("output.txt");
            writename.createNewFile(); 
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));


            InputStreamReader reader = new InputStreamReader(new FileInputStream(filename));
            BufferedReader br = new BufferedReader(reader); 


            String line = "";
            String reformat_line = "";

            line = br.readLine();
            if(line != null)    reformat_line = reformat_line(line);
            // System.out.println(reformat_line); 
            // System.out.println(" ");
         
            out.write(reformat_line + "\r\n");           


            while (line != null) {
                line = br.readLine();              
                if(line != null){
                    reformat_line = reformat_line(line);
                    out.write(reformat_line + "\r\n");
                    // System.out.println(reformat_line); 
                    // System.out.println(" ");
                }
                
                              
            }

            
            out.flush(); 
            out.close();
 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Laurent
 */
public class ReadFileTest {
    public static void main(String[] args) {
        readFile("/Users/mac/polykaraoke/src/myfile.txt");
    }

    public static void readFile(String pathToFile){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(pathToFile)));
            String ligne;
            while((ligne = reader.readLine()) != null){
                if(ligne.startsWith("Tel :")){
                    System.out.println(ligne);
                }
            }
        } catch (Exception ex){
            System.err.println("Error. "+ex.getMessage());
        }
    }
}
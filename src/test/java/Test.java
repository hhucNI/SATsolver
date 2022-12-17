import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.example.Main;
public class Test {

    @org.junit.Test
    public void testAssignAndProp(){

        int cap=100;
        ArrayList<String> sites = new ArrayList<String>(cap);
        for(int i=0;i<100;i++){
            sites.add(null);
        }
//        sites.add("Google");
//        sites.add("Runoob");
//        sites.add("Taobao");
//        sites.add("Weibo");
        sites.set(8,"jbjbjbjb");
        for (String site : sites) {
            System.out.println(site+"-");
        }
    }
    @org.junit.Test
    public void TestAllFiles(){
        String dir="D:\\1javawork\\software_analysis_projs\\SATsolver\\src\\main\\resources\\Test";
        File fDir=new File(dir);
        String[] list = fDir.list();
        for (String file : list) {
            boolean notAnAnswer=false;
            if(file.endsWith("cnf") || file.endsWith("CNF")){
                try {
                   notAnAnswer = Main.CheckAnswer(Main.SATsolver(fDir.getPath()+File.separator+file));
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
            if(notAnAnswer) throw new RuntimeException();

        }
    }

}

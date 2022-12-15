import java.util.ArrayList;

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

}

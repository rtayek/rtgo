package iox;
import utilities.Et;
public enum Init3 {
    first;
    Init3() { //
        System.out.println(et);
        if(!once){
            try {
                // initializ hear
                once=true;
            } catch(Exception e) {
                System.out.println(e);
                System.exit(1);
            }
        }
    }
    public boolean once=false;
    public final Et et=new Et();
}

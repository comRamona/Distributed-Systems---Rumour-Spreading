import java.util.Random;

public class MainClass {
    public static void main(String[] args) {
        if(args[0].equals("q1")) {
            Q1.main(args);
        }
        else if(args[0].equals("q2")) {
            Q2.main(args);
        }
        else if(args[0].equals("q3")) {
           new Q3Helper(args, 0.05f);

        }
    }
}

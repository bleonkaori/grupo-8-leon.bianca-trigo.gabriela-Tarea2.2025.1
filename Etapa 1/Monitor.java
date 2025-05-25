import java.io.PrintStream;
import java.util.Scanner;

public class Monitor extends Subscriber {
    private PrintStream ps;

    public Monitor(String name, String topicName, PrintStream ps) {
        super(name, topicName);
        this.ps = ps;
    }

    @Override
    public void update(String message) {
        try (Scanner sc = new Scanner(message)) {
            while (sc.hasNextInt()) {
                int x = sc.nextInt();
                if (sc.hasNextInt()) {
                    int y = sc.nextInt();

                    double dist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));

                    if (dist > 500) {
                        ps.println(getName() + "," + getTopicName() + "," + x + "," + y);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Vuelve a cursar media. Mentira, lo escribiste mal. :(");
        }
    }
}

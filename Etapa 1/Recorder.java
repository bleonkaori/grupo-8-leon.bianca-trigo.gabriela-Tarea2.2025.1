import java.io.PrintStream;
import java.util.Scanner;

public class Recorder extends Subscriber {
   protected Recorder() {}  // to ban calls to this constructor.
   public Recorder(String name, String topicName, PrintStream out) {
      super(name, topicName);
      this.out = out;
   }
   public void update(String message){
      try(Scanner in = new Scanner(message)){
         int x = in.nextInt();
         int y = in.nextInt();
         out.println(getName() + "," + getTopicName()+"," + x + "," + y);
      }
      catch(Exception e){
         System.err.println("El mensaje ingresado es inválido.");
      }
   }
   private PrintStream out;
} 
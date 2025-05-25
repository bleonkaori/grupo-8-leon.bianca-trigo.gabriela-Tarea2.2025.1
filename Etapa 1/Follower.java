import java.io.PrintStream; // es la que nos va a ayudar a lo del txt en la sección 2.1 de la tarea

public class Follower extends Subscriber {
   private PrintStream out;

   public Follower(String name, String topicName, PrintStream out) {
      super(name, topicName); //clase padre Component
      this.out = out;
   }
   public void update(String message) {
      out.println(getName() +" "+ getTopicName() +" "+ message); // esto se imprime usando el atributo out
   }
}

//Follower.java lo que hace es especializar la clase subscriber
//acordemonos que nos tiene que imprimir las notificaciones que quiere imoprimir el streamer
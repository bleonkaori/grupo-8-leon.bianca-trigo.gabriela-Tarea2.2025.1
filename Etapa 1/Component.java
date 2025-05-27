import javafx.scene.Node;

//lo declaramos abstracta porque no representa un componente completo por si sola
//solo sirve como base para subclases que implementa getView()

public abstract class Component {
   protected Component (){}  // to ban creation of publisher or subscriber without name.


   public Component(String componentName, String topicName){
      name = componentName;
      this.topicName = topicName;
   }

   public String getName(){
      return name;
   }

   public String getTopicName(){
      return topicName;
   }
   public abstract Node getView();

   protected String name;
   protected String topicName;
}
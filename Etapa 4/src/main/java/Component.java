import javafx.scene.Node;

/**
 * Superclase abstracta para todos los elementos visuales del simulador
 * (VideoPublisher, VideoFollower, GPSCarPublisher, CarTracker, etc.).
 *
 * Define un nombre lógico ({@link #name}) y, opcionalmente, un tópico
 * ({@link #topicName}).  Las subclases deben implementar
 * {@link #getView()} para entregar el nodo JavaFX que se añadirá a la GUI.
 *
 * @author  Grupo 8 – León Bianca y Trigo Gabriela
 * @version 2025-05-28
 */
public abstract class Component {

   /** Nombre que se mostrará en la interfaz. */
   protected String name;

   /** Tópico con el que se asocia el componente (puede ser vacío). */
   protected String topicName;

   /** Constructor protegido sin argumentos: impide instancias vacías. */
   protected Component (){}   // to ban creation of publisher or subscriber without name.

   /**
    * Construye un componente con nombre y tópico.
    *
    * @param name nombre lógico.
    * @param topicName     tópico asociado.
    */
   public Component(String name, String topicName){
      this.name = name;
      this.topicName = topicName;
   }

   /** @return nombre lógico del componente. */
   public String getName(){
      return name;
   }

   /** @return tópico asociado al componente. */
   public String getTopicName(){
      return topicName;
   }

   /**
    * Devuelve el nodo JavaFX que representa visualmente al componente.
    *
    * @return nodo JavaFX (HBox, Label, etc.).
    */
   public abstract Node getView();
}

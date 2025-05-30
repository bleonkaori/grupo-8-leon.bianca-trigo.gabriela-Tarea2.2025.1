import javafx.scene.Node;

/**
 * Superclase abstracta; padre de los elementos visuales del simulador
 * (VideoPublisher, VideoFollower, GPSCarPublisher, CarTracker, etc.).
 *
 * Establece un nombre ({@link #name}) y, si se desea, un tópico ({@link #topicName}).
 * Las clases hijas deben implementar {@link #getView()} para devolver el componente JavaFX
 * que se mostrará en la interfaz gráfica.
 *
 * @author  Grupo 8 – Gabriela Trigo y Bianca León
 * @version 2025-05-28
 */
public abstract class Component {

   /**
    * Nombre que se mostrará en la interfaz.
    */
   protected String name;

   /**
    * Tópico con el que se asocia el componente (puede ser vacío).
    */
   protected String topicName;

   /**
    * Constructor protegido sin argumentos: impide instancias vacías.
    */
   protected Component() {
   }   // to ban creation of publisher or subscriber without name.

   /**
    * Construye un componente con nombre y tópico.
    *
    * @param name      nombre lógico.
    * @param topicName tópico asociado.
    */
   public Component(String name, String topicName) {
      this.name = name;
      this.topicName = topicName;
   }

   /**
    * @return El nombre que identifica a este publicador o suscriptor.
    */
   public String getName() {
      return name;
   }

   /**
    * @return El nombre del tópico al que está conectado este publicador o suscriptor.
    */
   public String getTopicName() {
      return topicName;
   }

   /**
    * Devuelve el elemento visual (como un HBox, Label, etc.) que se mostrará en la interfaz gráfica.
    *
    * @return Un nodo de JavaFX que representa visualmente a este publicador o suscriptor en la pantalla.
    */
   public abstract Node getView();
}



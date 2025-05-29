import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.Node;

//etapa 2 boton q permite reproducir video, en la 1 solo lo muestra

public class VideoFollower extends Subscriber {
   private HBox view;
   private Button video;

   public VideoFollower(String name, String topicName) {
      super(name, topicName);
      video = new Button("Nothing yet."); //Se crea un boton vacio
      Label info = new Label(" "+topicName + " -> " + name + ":");
      view = new HBox(10,info, video);


   }

   @Override
   public void update(String message) {
      video.setText(message); //Aqui es para actualizar con el nuevo link
   }
   @Override
   public Node getView(){
      return view;
   }


}

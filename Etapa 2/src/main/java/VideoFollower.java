import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.util.Duration;


//etapa 2 extiende Subscriber
//se reproduce el video en esta etapa.

public class VideoFollower extends Subscriber {
   private HBox view; //para poner el boton y etiquetas
   private Button video; //mostrar el ultimo link publicado
   private String currentUrl = null; //ultimo link recibido, este sera el que reproduce

   public VideoFollower(String name, String topicName) {
      super(name, topicName);

      video = new Button("Nothing yet."); //Se crea un boton vacio
      Label info = new Label(" "+topicName + " -> " + name + ":");
      view = new HBox(10,info, video);


      //cuando se presione el boton:
      video.setOnAction(e->{
         if(currentUrl != null && !currentUrl.isEmpty()) {
            try{
               //se carga el video desde el link recibido
               Media media = new Media(currentUrl.trim());
               MediaPlayer mediaPlayer = new MediaPlayer(media);
               MediaView mediaView = new MediaView(mediaPlayer);
               mediaView.setPreserveRatio(true);

               //estos son solo tamaños
               mediaView.setFitWidth(640);
               mediaView.setFitHeight(360);

               //Botones para controlar:

               //el boton para iniciar
               Button play = new Button(">");
               play.setOnAction(ev -> {
                  mediaPlayer.play();

                  //print de prueba
                  System.out.println("El video se esta reproduciendo");
               });

               //para volver 10s
               Button reverse = new Button("<<");
               reverse.setOnAction(ev -> {
                  mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
                  System.out.println("Volviendo 10s");

               });


               //slider del volumen
               Slider volumen = new Slider(0, 1, 0.7); //empieza con volumen a 70%
               mediaPlayer.volumeProperty().bind(volumen.valueProperty());

               Label volumeLabel = new Label("Volume");


               //organizacion de controles
               HBox controles = new HBox(10, play, reverse, volumeLabel,volumen);

               controles.setPadding(new Insets(10));
               controles.setAlignment(Pos.CENTER);

               //aqui es la estructura de la ventana
               BorderPane root = new BorderPane();
               root.setCenter(mediaView); //el video va arriba
               root.setBottom(controles); //controles abajo
               root.setPadding(new Insets(10));

               //se crea la ventana del reproductor
               Stage videoStage = new Stage();
               videoStage.setTitle("Video Viewer");
               videoStage.setScene(new Scene(root, 640, 420));


               //cuando se detiene y el reproductor al cerrar la ventana
               videoStage.setOnCloseRequest(ev->{
                  mediaPlayer.stop();
                  mediaPlayer.dispose();
               });

               videoStage.show();

               //si es que se desea que el video se reproduzca de una,
               //descomentar mediaPlayer.play().
               //lo deje comentado para que se vea que funciona el boton ">" para darle play
               //al video


               mediaPlayer.play(); //al abrir se reproduce
            }catch (Exception ex){
               System.out.println("Error reproducir video");
            }
         }
      });


   }


   //opciones que ya estaban en la etapa1
   @Override
   public void update(String message) {
      currentUrl = message;
      video.setText(message); //Aqui es para actualizar con el nuevo link
   }
   @Override
   public Node getView(){
      return view;
   }


}

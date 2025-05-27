import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class VideoFollower extends Subscriber {

   private final HBox view;
   private final Button button;
   private String lastUrl = "Nothing yet";

   public VideoFollower(String name, String topic) {
      super(name, topic);

      Label label = new Label(topic + "→" + name + ": ");
      button = new Button(lastUrl);
      button.setPrefWidth(320);
      button.setOnAction(e -> playVideo(lastUrl));

      view = new HBox(6, label, button);
   }

   @Override
   public void update(String url) {
      lastUrl = url;
      button.setText(url);
   }

   public HBox getView() {
      return view;
   }

   //reproductor del video
   private void playVideo(String url) {
      if (url == null || url.isBlank())
         return;

      //Esto es el videi reproductor, se crea la media luego el video y luego que se vea el video
      Media media = new Media(url);
      MediaPlayer mp = new MediaPlayer(media);
      MediaView mv = new MediaView(mp);
      mv.setPreserveRatio(true);

      //atrasar el video
      Button reverse = new Button("<<");
      reverse.setOnAction(e ->
              mp.seek(mp.getCurrentTime().subtract(Duration.seconds(10)))
      );

      //pausar el video
      Button pause = new Button("❚❚");  //empieza en pausa porque arranca reproduciendo
      pause.setOnAction(e -> { //la acción que ejecuta si se presiona el botón de pausa
         if (mp.getStatus() == MediaPlayer.Status.PLAYING) { //si se esta reproduciendo, entonces el video pasa a pausa
            mp.pause(); //aquí se pausa y se cambia el dibujo al tipico triangulode play
            pause.setText("▶");   //cambia a Play
         } else {
            mp.play();
            pause.setText("❚❚");    //cambia a Pause
         }
      });

      //avanzar el video
      Button forward = new Button(">");
      forward.setOnAction(e -> //si se oprime el botón de avanzar entonces se avanzan 10 segundos más
              mp.seek(mp.getCurrentTime().add(Duration.seconds(10)))
      );

      //volumen
      Slider volumen = new Slider(0, 1, 0.7); //el volumen es una slider como el de youtube
      mp.volumeProperty().bind(volumen.valueProperty()); //en esta línea necesite ayuda ya que no sabía como usar bind


      HBox botones = new HBox(8, reverse, pause, pause, new Label("Volume"), volumen); //en esta HBox le asignamos los botones a la ventana del video reproductor
      botones.setPadding(new Insets(8));
      botones.setAlignment(Pos.CENTER);

      //ventana divisora
      BorderPane root = new BorderPane();
      root.setCenter(mv);
      root.setBottom(botones);
      root.setPadding(new Insets(10));

      //escenario
      Stage primaryStage = new Stage();
      primaryStage.setTitle("Video player");
      primaryStage.setScene(new Scene(root, 640, 420));
      primaryStage.setOnCloseRequest(e -> {mp.stop(); mp.dispose();
      });
      primaryStage.show();

      mp.play();
   }
}
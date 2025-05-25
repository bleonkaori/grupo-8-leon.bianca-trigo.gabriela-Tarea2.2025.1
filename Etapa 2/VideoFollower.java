import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Suscriptor de videos (Etapa 2):
 *  – Muestra un botón con la última URL publicada.
 *  – Al hacer clic abre una ventana que reproduce el video
 *    con la misma interfaz que tenías en la clase Ayudita.
 */
public class VideoFollower extends Subscriber {

   private final HBox   view;      // nodo a insertar en el VBox derecho
   private final Button btn;       // botón que muestra la URL
   private String lastUrl = null;  // última URL recibida

   public VideoFollower(String name, String topicName) {
      super(name, topicName);

      btn  = new Button("press");
      btn.setOnAction(e -> playVideo(lastUrl));   //Configura lo que pasa cuando se hace clic en el botón
      view = new HBox(5, btn);
   }

   //topic hace el llamado cuando hace algo
   @Override
   public void update(String message) {
      lastUrl = message; //Guarda la nueva URL que ingresó
      btn.setText(lastUrl); //luego esa URL actualiza e texto (certamen 1)
   }

   public HBox getView() { return view; }


   //Aquí la ventana que muestra el video
   private void playVideo(String url) {
      if (url == null || url.isBlank()) return;   // nada que reproducir

      Media        media       = new Media(url); //se crea la media
      MediaPlayer  mediaPlayer = new MediaPlayer(media); //luego el reproductor
      MediaView    mediaView   = new MediaView(mediaPlayer); //ahora e video

      //Barra simple
      Slider slider = new Slider(0, 100, 0); //Barra de progreso con rango de 0 a 100%
      slider.setShowTickMarks(false);
      slider.setShowTickLabels(false);
      slider.setStyle("-fx-accent: red; -fx-padding: 8 0 8 0;");

      /*Para esta función no caché muy bien como hacerla
      porque era onda la barra deslizadora de youtube, que se sincroniza con el video*/

      mediaPlayer.currentTimeProperty().addListener(obs -> {
         double cur  = mediaPlayer.getCurrentTime().toSeconds();
         double tot  = mediaPlayer.getTotalDuration().toSeconds();
         if (!slider.isValueChanging() && tot > 0) {
            slider.setValue(cur / tot * 100);
         }
      });

      //slider que hace que si se mueve la barra se cambia el video
      slider.valueChangingProperty().addListener((obs, wasCh, isCh) -> {
         if (!isCh) {
            double tot = mediaPlayer.getTotalDuration().toSeconds();
            mediaPlayer.seek(Duration.seconds(slider.getValue() / 100 * tot));
         }
      });

      VBox  root  = new VBox(mediaView, slider);
      Scene scene = new Scene(root, 600, 400);

      Stage win = new Stage();
      win.setTitle("Reproduciendo video...");
      win.setScene(scene);

      //la fnción "clean"
      win.setOnCloseRequest(e -> {
         mediaPlayer.stop();
         mediaPlayer.dispose();
      });

      win.show();
      mediaPlayer.play();
   }
}

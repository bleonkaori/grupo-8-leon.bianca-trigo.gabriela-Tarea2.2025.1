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
   private final Button btn;
   private String lastUrl = "Nothing yet.";

   public VideoFollower(String name, String topic) {
      super(name, topic);

      Label lbl = new Label(topic + "→" + name + ": ");
      btn = new Button(lastUrl);
      btn.setPrefWidth(320);
      btn.setOnAction(e -> playVideo(lastUrl));

      view = new HBox(6, lbl, btn);
   }

   @Override
   public void update(String url) {
      lastUrl = url;
      btn.setText(url);
   }

   public HBox getView() {
      return view;
   }

   /* ---------- reproductor ---------- */
   private void playVideo(String url) {
      if (url == null || url.isBlank()) return;

      /* Media, Player, View */
      Media media = new Media(url);
      MediaPlayer mp = new MediaPlayer(media);
      MediaView mv = new MediaView(mp);
      mv.setPreserveRatio(true);

      /* ─── Botón Rewind (««) ─── */
      Button btnRewind = new Button("<<");
      btnRewind.setOnAction(e ->
              mp.seek(mp.getCurrentTime().subtract(Duration.seconds(10)))
      );

      /* ─── Botón Play/Pause (toggle) ─── */
      Button btnPlayPause = new Button("❚❚");            // empieza en “Pause” porque arranca reproduciendo
      btnPlayPause.setOnAction(e -> {
         if (mp.getStatus() == MediaPlayer.Status.PLAYING) {
            mp.pause();
            btnPlayPause.setText("▶");                  // cambia a Play
         } else {
            mp.play();
            btnPlayPause.setText("❚❚");                // cambia a Pause
         }
      });

      /* ─── Botón Forward (») ─── */
      Button btnFwd = new Button(">");
      btnFwd.setOnAction(e ->
              mp.seek(mp.getCurrentTime().add(Duration.seconds(10)))
      );

      /* ─── Volumen ─── */
      Slider vol = new Slider(0, 1, 0.7);
      mp.volumeProperty().bind(vol.valueProperty());

      /* ─── Banda de controles ─── */
      HBox controls = new HBox(8, btnRewind, btnPlayPause, btnFwd,
              new Label("Volume"), vol);
      controls.setPadding(new Insets(8));
      controls.setAlignment(Pos.CENTER);

      /* ─── Layout ventana ─── */
      BorderPane root = new BorderPane();
      root.setCenter(mv);
      root.setBottom(controls);
      root.setPadding(new Insets(10));

      Stage win = new Stage();
      win.setTitle("Video player");
      win.setScene(new Scene(root, 640, 420));
      win.setOnCloseRequest(e -> {
         mp.stop();
         mp.dispose();
      });
      win.show();

      mp.play();
   }
}
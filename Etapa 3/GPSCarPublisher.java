import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/** Publica posiciones “t x y” a razón de 1 Hz. */
public class GPSCarPublisher extends Publisher {

    /* ----- estructura interna ----- */
    private static class Pos {
        final int t; final double x, y;
        Pos(int t, double x, double y) { this.t = t; this.x = x; this.y = y; }
    }

    private final List<Pos> puntos = new ArrayList<>();
    private int idx = 0;
    private Timeline tl;

    /* -------------- ctor -------------- */
    public GPSCarPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);

        /* pedir archivo */
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccione archivo GPS (formato: t x y)");
        File file = fc.showOpenDialog(new Stage());
        if (file == null) return;                     // usuario canceló

        if (!leerArchivo(file)) {                    // ← devuelve false si no leyó nada
            new Alert(Alert.AlertType.ERROR,
                    "Archivo vacío o sin líneas válidas: " + file.getName())
                    .showAndWait();
            return;                                  // no arranca
        }

        /* timeline 1 Hz */
        tl = new Timeline(new KeyFrame(Duration.seconds(1), e -> publicar()));
        tl.setCycleCount(Timeline.INDEFINITE);
        tl.play();
    }

    /* ---------- helpers ---------- */
    private boolean leerArchivo(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.trim().split("\\s+");
                if (p.length < 3) continue;                  // línea incompleta
                try {
                    int    t = Integer.parseInt(p[0]);
                    double x = Double.parseDouble(p[1]);
                    double y = Double.parseDouble(p[2]);
                    puntos.add(new Pos(t, x, y));
                } catch (NumberFormatException ignored) {
                    /* ignora línea no numérica */
                }
            }
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR,
                    "Error leyendo archivo:\n" + ex.getMessage()).showAndWait();
            return false;
        }
        puntos.sort((a, b) -> Integer.compare(a.t, b.t));
        return !puntos.isEmpty();
    }

    private void publicar() {
        if (idx >= puntos.size()) { tl.stop(); return; }
        Pos p = puntos.get(idx++);
        publishNewEvent(p.t + " " + p.x + " " + p.y);
    }
}

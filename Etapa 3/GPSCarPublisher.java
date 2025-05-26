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

//es el que va a actualizar las ubicaciones a razón de 1 segundo
public class GPSCarPublisher extends Publisher {

    private static class Pos {
        final int t; final double x, y;
        Pos(int t, double x, double y){
            this.t = t;
            this.x = x;
            this.y = y;
        }
    }

    private final List<Pos> puntos = new ArrayList<>();
    private int index = 0;
    private Timeline tiempo;

    /* -------------- ctor -------------- */
    public GPSCarPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);

        //usamos lo que nos piden que es ell filechooser que contiene las posiciones de los usuarios
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccione archivo GPS (formato: t x y)");
        File file = fc.showOpenDialog(new Stage());
        if (file == null)
            return;

        if (!leerArchivo(file)) {   //devuelve un booolean si es que no lee ningun archivo
            new Alert(Alert.AlertType.ERROR,
                    "Archivo vacío: " + file.getName())
                    .showAndWait();
            return;                                  // no arranca
        }

        //Aqui es donde se apica lo de que se actualiza cada segundo (en verdad esta medido en Hz)
        tiempo = new Timeline(new KeyFrame(Duration.seconds(1), e -> publicar()));
        tiempo.setCycleCount(Timeline.INDEFINITE);
        tiempo.play();
    }

    //aqui hacemos la función que será la que leerá las líneas de código del texto "config.txt"
    private boolean leerArchivo(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.trim().split("\\s+");
                if (p.length < 3)
                    continue;  //sto ocurre cuando la línea esta incompelta, o sea solo hay uno o dos valores, en vez de 3
                try {
                    int    t = Integer.parseInt(p[0]);
                    double x = Double.parseDouble(p[1]);
                    double y = Double.parseDouble(p[2]);
                    puntos.add(new Pos(t, x, y));
                } catch (NumberFormatException ignored) {}
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR,
                    "Error leyendo archivo:\n" + e.getMessage()).showAndWait();
            return false;
        }
        puntos.sort((a, b) -> Integer.compare(a.t, b.t));
        return !puntos.isEmpty();
    }

    private void publicar() {
        if (index >= puntos.size()) {
            tiempo.stop();
            return;
        }

        Pos p = puntos.get(index++);
        publishNewEvent(p.t + " " + p.x + " " + p.y);
    }
}

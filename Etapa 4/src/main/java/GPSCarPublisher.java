import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Publicador de posiciones GPS.
 *
 * <p>Lee un archivo de texto con líneas <code>t&nbsp;x&nbsp;y</code> donde
 * <code>t</code>, <code>x</code> y <code>y</code> son <code>double</code>.
 * Interpola posiciones intermedias y publica un punto por segundo (1 Hz)
 * en el tópico indicado.</p>
 *
 * <p>Las posiciones publicadas son consumidas por {@link CarTracker}.</p>
 *
 * @author  Grupo&nbsp;8 – León&nbsp;Bianca y Trigo&nbsp;Gabriela
 * @version 2025-05-28
 *
 * @see Publisher
 * @see CarTracker
 */
public class GPSCarPublisher extends Publisher {

    /* ───────── clase interna Pos ───────── */

    /**
     * Punto GPS leído o interpolado.
     * Contiene el instante {@link #t} (double) y las coordenadas {@link #x},
     * {@link #y}.
     */
    private static class Pos {
        final double t;   // tiempo absoluto [s]
        final double x;   // coordenada X
        final double y;   // coordenada Y
        private final Label label = new Label(); // rótulo opcional

        Pos(double t, double x, double y){
            this.t = t;
            this.x = x;
            this.y = y;
        }
    }

    /* ───────── atributos ───────── */

    private final Label label = new Label();            // vista mínima
    private final List<Pos> puntos       = new ArrayList<>(); // puntos leídos
    private final List<Pos> interpolados = new ArrayList<>(); // puntos a 1 Hz
    private final String topicName;   // copia del nombre de tópico
    private int index = 0;            // índice del siguiente punto
    private Timeline tiempo;          // temporizador 1 Hz

    /* ───────── constructor ───────── */

    /**
     * Selecciona un archivo GPS, lo lee e inicia la publicación
     * de coordenadas a razón de 1 Hz.
     *
     * @param name   rótulo del publicador
     * @param broker broker central
     * @param topic  tópico destino de los mensajes
     */
    public GPSCarPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);
        this.topicName = topic;     // 'topic' es privado en Publisher

        /* Selección de archivo */
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccione archivo GPS (formato: t x y)");
        File file = fc.showOpenDialog(new Stage());
        if (file == null) return;

        /* Lectura */
        if (!leerArchivo(file)) {
            new Alert(Alert.AlertType.ERROR,
                    "Archivo vacío: " + file.getName()).showAndWait();
            return;
        }

        interpolar();  // puntos intermedios

        /* Timeline: publica cada segundo */
        tiempo = new Timeline(new KeyFrame(Duration.seconds(1), e -> publicar()));
        tiempo.setCycleCount(Timeline.INDEFINITE);
        tiempo.play();
    }

    /* ───────── lectura de archivo ───────── */

    /**
     * Lee el archivo y llena {@link #puntos}.
     *
     * @param f archivo GPS
     * @return {@code true} si se leyó al menos una línea válida
     */
    private boolean leerArchivo(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.trim().split("\\s+");
                if (p.length < 3) continue;      // línea incompleta
                try {
                    double t = Double.parseDouble(p[0]);
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
        puntos.sort((a,b) -> Double.compare(a.t, b.t));
        return !puntos.isEmpty();
    }

    /* ───────── interpolación ───────── */

    /** Genera puntos intermedios de modo que haya uno por segundo. */
    private void interpolar() {
        for (int i = 0; i < puntos.size() - 1; i++) {
            Pos p1 = puntos.get(i);
            Pos p2 = puntos.get(i + 1);
            double dt = p2.t - p1.t;
            if (dt <= 0) continue;   // ignora repetidos o mal ordenados

            for (int t = 0; t < (int) dt; t++) {
                double ratio = t / dt;
                double x = p1.x + ratio * (p2.x - p1.x);
                double y = p1.y + ratio * (p2.y - p1.y);
                interpolados.add(new Pos(p1.t + t, x, y));
            }
        }
        /* añade último punto */
        Pos ultimo = puntos.get(puntos.size() - 1);
        interpolados.add(new Pos(ultimo.t, ultimo.x, ultimo.y));
    }

    /* ───────── publicación ───────── */

    /** Publica la siguiente posición e imprime la cadena por consola (debug). */
    private void publicar() {
        if (index >= interpolados.size()) {
            tiempo.stop();
            return;
        }
        Pos p = interpolados.get(index++);
        String msg = p.t + " " + p.x + " " + p.y;
        System.out.println("Publicando: " + msg);  // depuración
        publishNewEvent(msg);
        label.setText(getName() + "->" + topicName + ":" + msg);
    }

    /* ───────── vista requerida por Component ───────── */

    /**
     * Devuelve un {@link Label} que muestra la última posición publicada.
     * Si no se desea mostrar nada en la lista de publishers, se podría
     * devolver {@code null}.
     */
    @Override
    public Parent getView() {
        return label;
    }
}


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
 * @author  Grupo 8 - Gabriela Trigo y Bianca León
 * @version 2025-05-28
 *
 * @see Publisher
 * @see CarTracker
 */
public class GPSCarPublisher extends Publisher {

    /**
     * Punto GPS leído.
     * Contiene el instante {@link #t} (double) y las coordenadas {@link #x},
     * {@link #y}.
     */
    private static class Pos {
        //posicion (tiempo, x,y)
        final double t;
        final double x;
        final double y;


        private final Label label = new Label();
        Pos(double t, double x, double y){
            this.t = t;
            this.x = x;
            this.y = y;
        }
    }

    private final Label label = new Label();  //la lista de posiciones leidas desde el archivo

    private final List<Pos> puntos       = new ArrayList<>(); //la lista de posiciones leidas desde el archivo
    private final List<Pos> interpolados = new ArrayList<>();

    private final String topicName;   //para guardar el nombre del topico
    private int index = 0;            //posicion actual al publicar
    private Timeline tiempo;          //temporizador para publicar cada segundo



    /**
     * Selecciona un archivo GPS, lo lee e inicia la publicación
     * de coordenadas a razón de 1 Hz (1 segundo).
     *
     * @param name Nombre que se mostrará en la pantalla para identificar al publicador.
     * @param broker Gestiona y coordina los diferentes tópicos del sistema.
     * @param topic tópico destino de los mensajes.
     */
    public GPSCarPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);
        this.topicName = topic;     //aqui guardamos el nombre del topico, ya que topic es privado en la clase Publisher


        FileChooser fc = new FileChooser();   //usamos lo que nos piden que es ell filechooser que contiene las posiciones de los usuarios

        fc.setTitle("Seleccione archivo GPS (formato: t x y)");
        File file = fc.showOpenDialog(new Stage());
        if (file == null) return;


        if (!leerArchivo(file)) {    //devuelve un booolean si es que no lee ningun archivo
            new Alert(Alert.AlertType.ERROR,
                    "Archivo vacío: " + file.getName()).showAndWait();
            return;     //no arranca
        }

        //se genera los puntos intermedios porque en la etapa 3 piden que el GPS publique cada segundo
        //esto aparece en las instrucciones en la parte 2.2. "El GPS del movil lee este archivo e interpola las posiciones
        //intermedias para enviar....."

        interpolar();  // puntos intermedios

        //Aqui es donde se apica lo de que se actualiza cada segundo (en verdad esta medido en Hz)
        tiempo = new Timeline(new KeyFrame(Duration.seconds(1), e -> publicar()));
        tiempo.setCycleCount(Timeline.INDEFINITE);
        tiempo.play();
    }


    /**
     * Lee el archivo y llena el parámetro{@link #puntos}.
     *
     * @param f archivo GPS
     * @return {@code true} si se leyó al menos una línea válida
     */

    //aqui hacemos la función que será la que leerá las líneas de código del texto "config.txt"
    private boolean leerArchivo(File f) {
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] p = line.trim().split("\\s+");
                if (p.length < 3) continue;      //esto ocurre cuando la línea está incompleta, es decir, solo hay uno o dos valores en vez de 3
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

        //Usamos Double.compare ya que estamos comparando números del tipo double
        puntos.sort((a,b) -> Double.compare(a.t, b.t));
        return !puntos.isEmpty();
    }

    //segun las instrucciones de la etapa 3, el GPS debe enviar una posicion cada segundo
    //aunque el archivo original de los puntos esten separados por mas tiempo. Por eso aqui se rellena con puntos
    //intermedios. Esto segun las instrucciones, como para evitar que se teletransporte de un punto a otro.

    /** Genera posiciones intermedias para asegurar que haya una por cada segundo transcurrido. */
    private void interpolar() {
        for (int i = 0; i < puntos.size() - 1; i++) {
            Pos p1 = puntos.get(i);
            Pos p2 = puntos.get(i + 1);
            double dt = p2.t - p1.t;
            if (dt <= 0)
                continue;   //si los puntos están mal o repetidos, se salta


            //se interpola por cada segundo intermedio
            for (int t = 0; t < (int) dt; t++) {
                double ratio = t / dt;
                double x = p1.x + ratio * (p2.x - p1.x);
                double y = p1.y + ratio * (p2.y - p1.y);


                //aquí se introduce el punto interpolado para ese segundo
                interpolados.add(new Pos(p1.t + t, x, y));
            }
        }
        //Agregar el último punto para cerrar
        Pos ultimo = puntos.get(puntos.size() - 1);
        interpolados.add(new Pos(ultimo.t, ultimo.x, ultimo.y));
    }


    /** Muestra la próxima posición en la consola y la envía al tópico.*/
    private void publicar() {
        if (index >= interpolados.size()) {
            tiempo.stop();
            return;
        }
        Pos p = interpolados.get(index++);

        //Mensaje por la terminal para ver si funciona
        String msg = p.t + " " + p.x + " " + p.y;
        System.out.println("Publicando: " + msg);//Es un texto que se muestra en la consola para depurar el funcionamiento

        publishNewEvent(msg);

        label.setText(getName() + "->" + topicName + ":" + msg);
    }

    //PARA QUE COMPILE POR LA HERENCIA DE COMPONENT
    /**
     * Devuelve un {@link Label} que muestra la última posición publicada.
     * Si no se desea mostrar nada en la lista de los publishers, se devuelve {@code null}.*/
    @Override
    public Parent getView() {
        return label;
    }
}


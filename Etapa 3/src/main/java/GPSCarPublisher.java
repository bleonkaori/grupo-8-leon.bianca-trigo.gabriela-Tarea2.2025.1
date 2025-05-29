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



//es el que va a actualizar las ubicaciones a razón de 1 segundo

public class GPSCarPublisher extends Publisher {
//posicion (tiempo, x,y)
    private static class Pos {
        final int t;
        final double x, y;


        private final Label label = new Label();
        Pos(int t, double x, double y){
            this.t = t;
            this.x = x;
            this.y = y;
        }
    }
    private final Label label = new Label();

    //la lista de posiciones leidas desde el archivo
    private final List<Pos> puntos = new ArrayList<>();


    private final List<Pos> interpolados = new ArrayList<>();

    //para guardar el nombre del topico
    private final String topicName;
    //posicion actual al publicar
    private int index = 0;
    //temporizador para publicar cada segundo
    private Timeline tiempo;

//este solicita archivo con datos GPS y comienza la publicacion
    public GPSCarPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);

        //aqui guardamos el nombre del topico, ya que topic es privado en la clase Publisher
        this.topicName = topic;

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

        //se genera los puntos intermedios porque en la etapa 3 piden que el GPS publique cada segundo
        //esto aparece en las instrucciones en la parte 2.2. "El GPS del movil lee este archivo e interpola las posiciones
        //intermedias para enviar....."
        interpolar();

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


    //segun las instrucciones de la etapa 3, el GPS debe enviar una posicion cada segundo
    //aunque el archivo original de los puntos esten separados por mas tiempo. Por eso aqui se rellena con puntos
    //intermedios. Esto segun las instrucciones, como para evitar que se teletransporte de un punto a otro.
    private void interpolar(){
        for(int i = 0; i<puntos.size()-1; i++){
            Pos p1 = puntos.get(i);
            Pos p2 = puntos.get(i+1);
            int dt = p2.t - p1.t;

            //si estan raros los puntos o repetidos se salta
            if(dt<=0){
                continue;
            }

            for(int t = 0; t< dt; t++){
                double ratio = (double)t/dt;
                double x = p1.x + ratio * (p2.x - p1.x);
                double y = p1.y + ratio * (p2.y - p1.y);

                //aqui se introduce el punto que toca en ese segundo
                interpolados.add(new Pos(t, x, y));
            }
        }
        //agregar el ultimo punto para cerrar.
        Pos ultimo = puntos.get(puntos.size()-1);
        interpolados.add(new Pos(ultimo.t, ultimo.x, ultimo.y));

    }


    private void publicar() {
        if (index >= interpolados.size()) {
            tiempo.stop();
            return;
        }

        Pos p = interpolados.get(index++);

        //Mensaje por la terminal para ver si funciona
        String msg = p.t+ " "+p.x+" "+p.y;
        System.out.println("Publicando: "+msg);


        publishNewEvent(msg);

        label.setText(getName() + "->"+topicName + ":"+msg);
    }


    //PARA QUE COMPILE POR LA HERENCIA DE COMPONENT
    @Override
    public Parent getView() {
        return label;  // Devolvemos una vista vacía
    }

}

import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import java.util.Optional;

public class Stage1 extends Application {

    private VBox vBoxLeft, vBoxRight;
    private Broker broker;

    @Override
    public void start(Stage primaryStage) {
        broker = new Broker();

        //Barra del menú que va en la parte de arriba a la izquierda
        MenuBar menu = new MenuBar();
        Menu menuPub  = new Menu("Publisher");
        Menu menuSub = new Menu("Subscriber");
        menu.getMenus().addAll(menuPub, menuSub); //se añaden los menus "oficiales" al menu que es el borde que se crea arriba

        //aquí creamos los items que van cuando le damos click a los menus, ya sea publisher o subscriber
        MenuItem videoPub  = new MenuItem("Video");
        MenuItem videoSub = new MenuItem("Video");
        menuPub.getItems().add(videoPub); //los añadimos
        menuSub.getItems().add(videoSub);

        //para dividir la ventana usamos borderpane
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menu);

        //Aquí usamos vBox ppara poder usar más espacio
        vBoxLeft  = new VBox(5);
        vBoxRight = new VBox(10);
        vBoxLeft.setAlignment(Pos.TOP_LEFT);//creamos las opsiciones arriba a la izquierda para ordenar los menú sub y pub
        vBoxRight.setAlignment(Pos.TOP_LEFT);
        borderPane.setLeft(vBoxLeft);
        borderPane.setRight(vBoxRight);

        Scene scene = new Scene(borderPane, 600, 400); //creamos la ventana y e añadimos el título y lo demás ara que se vea
        primaryStage.setTitle("Stage1 – Video Publisher - Subscriber");
        primaryStage.setScene(scene);
        primaryStage.show();

        videoPub.setOnAction(e  -> addVideoPub());
        videoSub.setOnAction(e -> addVideoSubs()); //añadimos los handlers
    }

    //Reusamos código del propporcionado en PubSubatternSimulation
    private String prompt(String message) {
        TextInputDialog texto  = new TextInputDialog();
        texto.setHeaderText(message);
        Optional<String> e = texto.showAndWait();
        return e.orElse(null);
    }

    private void addVideoPub() {
        String name  = prompt("Video Publisher Name");
        if (name == null) return;
        String topic = prompt("Video Publisher Topic");
        if (topic == null) return;

        new VideoPublisher(name, broker, topic);        //crea el publisher (sin usar getView)
        vBoxLeft.getChildren().add(                    // *** CAMBIO: mostramos un Label simple
                new Label(name + " → [" + topic + "]"));
    }

    private void addVideoSubs() {
        String name  = prompt("Video Subscriber Name");
        if (name == null) return;
        String topic = prompt("Video Subscriber Topic");
        if (topic == null) return;

        VideoFollower vf = new VideoFollower(name, topic);
        if (broker.subscribe(vf))
            vBoxRight.getChildren().add( 
                    new Label(name + " ← [" + topic + "]"));
        else
            new Alert(Alert.AlertType.ERROR,
                    "Topic \"" + topic + "\" does not exist").showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

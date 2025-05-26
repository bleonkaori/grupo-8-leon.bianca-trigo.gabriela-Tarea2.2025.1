import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets; //usaremos una librería nueva
import java.util.Optional;

public class Stage1 extends Application {
    private VBox vBoxLeft, vBoxRight;
    private Broker broker;

    //Aquí emepezamos a crear la ventana que se va a mostrar
    public void start(Stage primaryStage) {
        broker = new Broker();
        //Creamos los constructores y partimos con el menú
        MenuBar menuBar = new MenuBar();
        //Que contiene dos, el publisher y el subscriber
        Menu menuPublisher  = new Menu("Publisher");
        Menu menuSubscriber = new Menu("Subscriber");
        menuBar.getMenus().addAll(menuPublisher, menuSubscriber);
        //Aquí lo que va dentro de cada uno de esos dos, en este caso es e de Publisher
        MenuItem menuItemVideoPub  = new MenuItem("Video");
        MenuItem menuItemGPSPub    = new MenuItem("Car's GPS");
        menuPublisher.getItems().addAll(menuItemVideoPub, menuItemGPSPub);
        //Y aquí para Subscriber
        MenuItem menuItemVideoSubs = new MenuItem("Video");
        MenuItem menuItemGPSSubs   = new MenuItem("Car's GPS");
        menuSubscriber.getItems().addAll(menuItemVideoSubs, menuItemGPSSubs);
        //Esto se usa para dividir la ventana en partes
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setPadding(new Insets(10));
        //Dividir la fentana en lados
        vBoxLeft = new VBox(8);
        vBoxLeft.setAlignment(Pos.TOP_LEFT);
        borderPane.setLeft(vBoxLeft);
        vBoxRight = new VBox(8);
        vBoxRight.setAlignment(Pos.TOP_LEFT);
        borderPane.setRight(vBoxRight);
        //Y aquí creamos el escenario
        Scene scene = new Scene(borderPane, 800, 400);
        primaryStage.setTitle("Publisher-Subscriber Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        //Y aquí os setOnAction, tall que si presiono un botón se ejecuta otra cosa
        menuItemVideoPub.setOnAction(e -> addVideoPub());
        menuItemVideoSubs.setOnAction(e -> addVideoSubs());
    }
    //Y esto es para que se vea más bonito a ventana a vista del susuario
    private String getInputSting(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(prompt);
        dialog.setHeaderText("Please enter your " + prompt);
        dialog.setContentText(prompt + ":");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    private void addVideoPub() {
        String name  = getInputSting("Video Publisher Name");
        if (name == null) return;
        String topic = getInputSting("Video Publisher Topic");
        if (topic == null) return;

        vBoxLeft.getChildren().add(
                new VideoPublisher(name, broker, topic).getView()
        );
    }

    //esto hace que si no se escribe lo indicado, retorna nada
    private void addVideoSubs() {
        String name  = getInputSting("Video Subscriber Name");
        if (name == null)
            return;
        String topic = getInputSting("Video Subscriber Topic");
        if (topic == null)
            return;

        //Si el subscriber ingresado no es válido, el usuario es advertido
        VideoFollower videoFollower = new VideoFollower(name, topic);
        if (broker.subscribe(videoFollower))           // topic debe existir
            vBoxRight.getChildren().add(videoFollower.getView());
        else
            new Alert(Alert.AlertType.ERROR,
                    "Topic \"" + topic + "\" does not exist").showAndWait();
    }

    //Típica clase main
    public static void main(String[] args) {
        launch(args);
    }
}

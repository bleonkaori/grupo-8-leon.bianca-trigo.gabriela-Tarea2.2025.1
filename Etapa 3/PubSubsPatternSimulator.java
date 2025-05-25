import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.util.Optional;

public class PubSubsPatternSimulator extends Application {
    private VBox vBoxLeft, vBoxRight;
    private Broker broker;
    private Stage primaryStage;
    private FileChooser fileChooser;            // (mantengo la variable aunque aquí no se usa)

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        broker = new Broker();
        fileChooser = new FileChooser();

        MenuBar menuBar = new MenuBar();
        Menu menuPublisher = new Menu("Publisher");
        Menu menuSubscriber = new Menu("Subscriber");
        menuBar.getMenus().addAll(menuPublisher, menuSubscriber);

        MenuItem menuItemVideoPub = new MenuItem("Video");
        MenuItem menuItemGPSPub  = new MenuItem("Car's GPS");
        menuPublisher.getItems().addAll(menuItemVideoPub, menuItemGPSPub);

        MenuItem menuItemVideoSubs = new MenuItem("Video");
        MenuItem menuItemGPSSubs  = new MenuItem("Car's GPS");
        menuSubscriber.getItems().addAll(menuItemVideoSubs, menuItemGPSSubs);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);

        vBoxLeft = new VBox(5);
        vBoxLeft.setAlignment(Pos.CENTER);
        borderPane.setLeft(vBoxLeft);

        vBoxRight = new VBox(10);
        vBoxRight.setAlignment(Pos.CENTER);
        borderPane.setRight(vBoxRight);

        ScrollPane scrollPane = new ScrollPane(borderPane);
        Scene scene = new Scene(scrollPane, 800, 400);
        primaryStage.setTitle("Publisher-Subscriber Simulator");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();

        /* Handle menu actions */
        menuItemVideoPub.setOnAction(e -> addVideoPub());
        menuItemVideoSubs.setOnAction(e -> addVideoSubs());
        menuItemGPSPub.setOnAction(e  -> addGPSCarPub());      // crea publisher GPS
        menuItemGPSSubs.setOnAction(e -> addCarSubs());        // crea tracker GPS
    }

    /* diálogo reutilizable */
    private String getInputSting(String prompt){
        String string = "default";
        TextInputDialog dialog = new TextInputDialog(string);
        dialog.setTitle(prompt);
        dialog.setHeaderText("Please enter your " + prompt);
        dialog.setContentText(prompt + ":");
        Optional<String> result = dialog.showAndWait();
        if(result.isPresent()) return result.get();
        else return string;
    }

    /* Video Publisher */
    private void addVideoPub() {
        String name  = getInputSting("Video Publisher Name");
        String topic = getInputSting("Video Publisher Topic");
        vBoxLeft.getChildren().add(new VideoPublisher(name, broker, topic).getView());
    }

    /* GPS Publisher (archivo se elige en su propio constructor) */
    private void addGPSCarPub() {
        String name  = getInputSting("GPS Publisher Name");
        String topic = getInputSting("GPS Publisher Topic");
        new GPSCarPublisher(name, broker, topic);   // constructor con 3 args
    }

    /* Video Subscriber */
    private void addVideoSubs() {
        String name  = getInputSting("Video Subscriber Name");
        String topic = getInputSting("Video Subscriber Topic");
        VideoFollower videoFollower = new VideoFollower(name, topic);
        if (broker.subscribe(videoFollower))
            vBoxRight.getChildren().add(videoFollower.getView());
        else
            new Alert(Alert.AlertType.ERROR,
                    "Topic \"" + topic + "\" does not exist").showAndWait();
    }

    /* Car Tracker  (corregido: ahora valida la suscripción) */
    private void addCarSubs() {
        String name  = getInputSting("Car Tracker Name");
        String topic = getInputSting("Car Tracker Topic");
        CarTracker tracker = new CarTracker(name, topic);

        if (!broker.subscribe(tracker)) {                           // <-- comprobación
            new Alert(Alert.AlertType.ERROR,
                    "Topic \"" + topic + "\" does not exist").showAndWait();
        }
        /* CarTracker abre su propia ventana; no se añade al vBoxRight */
    }

    public static void main(String[] args) {
        launch(args);
    }
}

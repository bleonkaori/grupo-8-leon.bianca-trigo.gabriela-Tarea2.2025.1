import javafx.application.Application;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;      // *** CHANGED (nuevo)
import java.util.Optional;

public class PubSubsPatternSimulator extends Application {
    private VBox vBoxLeft, vBoxRight;
    private Broker broker;

    public void start(Stage primaryStage) {
        broker = new Broker();

        MenuBar menuBar = new MenuBar();
        Menu menuPublisher  = new Menu("Publisher");
        Menu menuSubscriber = new Menu("Subscriber");
        menuBar.getMenus().addAll(menuPublisher, menuSubscriber);

        MenuItem menuItemVideoPub  = new MenuItem("Video");
        MenuItem menuItemGPSPub    = new MenuItem("Car's GPS");
        menuPublisher.getItems().addAll(menuItemVideoPub, menuItemGPSPub);

        MenuItem menuItemVideoSubs = new MenuItem("Video");
        MenuItem menuItemGPSSubs   = new MenuItem("Car's GPS");
        menuSubscriber.getItems().addAll(menuItemVideoSubs, menuItemGPSSubs);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setPadding(new Insets(10));               // *** CHANGED

        vBoxLeft = new VBox(8);                              // *** CHANGED (más espacio)
        vBoxLeft.setAlignment(Pos.TOP_LEFT);                 // *** CHANGED
        borderPane.setLeft(vBoxLeft);

        vBoxRight = new VBox(8);                             // *** CHANGED
        vBoxRight.setAlignment(Pos.TOP_LEFT);                // *** CHANGED
        borderPane.setRight(vBoxRight);

        Scene scene = new Scene(borderPane, 800, 400);
        primaryStage.setTitle("Publisher-Subscriber Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        /* handlers */
        menuItemVideoPub.setOnAction(e -> addVideoPub());
        menuItemVideoSubs.setOnAction(e -> addVideoSubs());
    }

    /* diálogo reutilizable */
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

    private void addVideoSubs() {
        String name  = getInputSting("Video Subscriber Name");
        if (name == null) return;
        String topic = getInputSting("Video Subscriber Topic");
        if (topic == null) return;

        VideoFollower videoFollower = new VideoFollower(name, topic);
        if (broker.subscribe(videoFollower))           // topic debe existir
            vBoxRight.getChildren().add(videoFollower.getView());
        else
            new Alert(Alert.AlertType.ERROR,
                    "Topic \"" + topic + "\" does not exist").showAndWait();
    }

    public static void main(String[] args) { launch(args); }
}

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CarTracker extends Subscriber {

    private final Circle circle = new Circle(6, Color.RED);
    private final Label  label = new Label("t: 0, x: 0, y: 0");

    public CarTracker(String name, String topic) {
        super(name, topic);

        BorderPane root = new BorderPane();
        BorderPane canvas = new BorderPane();
        canvas.setPrefSize(600, 400);
        canvas.setCenter(circle);

        root.setCenter(canvas);
        root.setBottom(label);
        BorderPane.setAlignment(label, Pos.CENTER);
        root.setPadding(new Insets(8));

        Stage primaryStage = new Stage();
        primaryStage.setTitle("Car Tracker – " + name);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    @Override  //Llega mensaje el mensaje de las posiciones al broker
    public void update(String message) {
        String[] p = message.trim().split("\\s+");
        if (p.length < 3) return;
        try {
            double t = Double.parseDouble(p[0]);
            double x = Double.parseDouble(p[1]);
            double y = Double.parseDouble(p[2]);
            circle.setTranslateX(x);
            circle.setTranslateY(y);
            label.setText(String.format("t %.0f, x %.1f, y %.1f", t, x, y));
        } catch (NumberFormatException ignored) {}
    }
}

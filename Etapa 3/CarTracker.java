import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class CarTracker extends Subscriber {

    private final Circle car = new Circle(6, Color.RED);
    private final Label  lbl = new Label("t 0, x 0, y 0");

    public CarTracker(String name, String topic) {
        super(name, topic);

        BorderPane root = new BorderPane();
        BorderPane canvas = new BorderPane();
        canvas.setPrefSize(300, 400);
        canvas.setCenter(car);

        root.setCenter(canvas);
        root.setBottom(lbl);
        BorderPane.setAlignment(lbl, Pos.CENTER);
        root.setPadding(new Insets(8));

        Stage win = new Stage();
        win.setTitle("Car Tracker – " + name);
        win.setScene(new Scene(root));
        win.show();
    }

    /** Llega mensaje "t x y" desde el Broker */
    @Override
    public void update(String msg) {
        String[] p = msg.trim().split("\\s+");
        if (p.length < 3) return;
        try {
            double t = Double.parseDouble(p[0]);
            double x = Double.parseDouble(p[1]);
            double y = Double.parseDouble(p[2]);
            car.setTranslateX(x);
            car.setTranslateY(y);
            lbl.setText(String.format("t %.0f, x %.1f, y %.1f", t, x, y));
        } catch (NumberFormatException ignored) {}
    }
}

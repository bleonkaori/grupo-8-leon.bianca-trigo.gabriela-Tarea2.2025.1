import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class VideoPublisher extends Publisher {

    private final HBox view;

    public VideoPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);

        //flecha Unicode \u2192 →
        Label label = new Label(name + "→" + topic + ": ");

        //esto es lo que se muestra antes de escirbir en el textfield
        TextField tf = new TextField();
        tf.setPromptText("Paste URL and press ENTER");
        tf.setPrefColumnCount(34);               // ancho visible

        tf.setOnAction(e -> {
            publishNewEvent(tf.getText());       // envía al tópico
            tf.clear();
        });

        view = new HBox(6, label, tf);
    }

    public HBox getView() {
        return view;
    }
}



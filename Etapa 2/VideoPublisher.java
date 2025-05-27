import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class VideoPublisher extends Publisher {

    private final HBox view;

    public VideoPublisher(String name, Broker broker, String topic) {
        super(name, broker, topic);
        
        Label label = new Label(name + "→" + topic + ": ");

        //esto es lo que se muestra antes de escirbir en el textfield
        TextField texto = new TextField();
        texto.setPromptText("Paste URL and press ENTER");
        texto.setPrefColumnCount(34);

        texto.setOnAction(e -> { //si se escribe en ell field
            publishNewEvent(texto.getText());  //lo que se hace es que se publica lo que se escribe en el texto, por eso el getText
            texto.clear(); //limpiamos el field
        });

        view = new HBox(6, label, texto);
    }

    public HBox getView() {
        return view;
    }
}

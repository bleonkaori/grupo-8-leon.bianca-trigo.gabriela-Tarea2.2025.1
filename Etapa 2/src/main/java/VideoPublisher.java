import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.Node;

//aqui implementamos un Publisher que publica urls de video
public class VideoPublisher extends Publisher {
    private HBox layout;
    private TextField input;
    public VideoPublisher(String name, Broker broker, String topicName) {
        super(name, broker, topicName);

        //Aqui creo el campo de los textos
        input = new TextField();
        input.setPromptText("Enter Video URL and press ENTER");

        //cuando se presiona Enter, se publica el mensaje y se borra
        input.setOnAction(e -> {
            String url = input.getText();

            //Solo si no esta vacia se va a publicar
            if(!url.isEmpty()){
                publishNewEvent(url); //este es el metodo del Publisher
                input.clear();
            }
        });

        //luego crear el layout con etiqueta y campo de texto
        layout = new HBox(10, new Label(name + " -> " + topicName), input);

    }
    @Override
    public Node getView(){
        return layout; //Nodo que aparece en la interfaz
    }
    //public HBox getView(){
       // return view;
    //}
    //private HBox view;
    //private TextField message;
}

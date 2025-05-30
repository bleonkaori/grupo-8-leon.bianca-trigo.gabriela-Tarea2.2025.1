/**
 * Publicador del patrón Publisher-Subscriber.<br>
 * No posee vista propia; las subclases (VideoPublisher,
 * GPSCarPublisher, …) implementan y usan
 * {@link #publishNewEvent(String)} para enviar mensajes al {@link Topic}.
 *
 * @author  Grupo 8 – Gabriela Trigo y Bianca León
 * @version 2025-05-28
 *
 * @see Component
 * @see Subscriber
 * @see Broker
 */
public abstract class Publisher extends Component {

    /** Tópico al que se enviarán los mensajes del publicador. */
    private Topic topic;

    /** Constructor protegido sin argumentos (bloquea instancias vacías) */
    protected Publisher() {} // to ban calls to this constructor

    /**
     * Crea un publicador y registra su tópico en el broker.
     * @param name Nombre que se mostrará en la pantalla para identificar al publicador.
     * @param broker Gestiona y coordina los diferentes tópicos del sistema.
     * @param topicName nombre del tópico al que publicará los mensajes.
     */
    public Publisher(String name, Broker broker, String topicName) {
        super(name, topicName);
        topic = broker.createTopic(topicName);
    }

    /**
     * Función que envía un nuevo mensaje al tópico asociado.
     * @param message contenido del mensaje que se va a mostrar.
     */
    protected void publishNewEvent(String message) {
        topic.notify(message);
    }
}

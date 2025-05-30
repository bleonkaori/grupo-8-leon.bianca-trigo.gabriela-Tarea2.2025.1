/**
 * Publicador genérico del patrón Publisher-Subscriber.<br>
 * No posee vista propia; subclases concretas (VideoPublisher,
 * GPSCarPublisher, …) la implementan y usan
 * {@link #publishNewEvent(String)} para enviar mensajes al {@link Topic}.
 *
 * @author  Grupo 8 – León Bianca y Trigo Gabriela
 * @version 2025-05-28
 *
 * @see Component
 * @see Subscriber
 * @see Broker
 */
public abstract class Publisher extends Component {

    /** Tópico al que se enviarán los mensajes de este publicador. */
    private Topic topic;

    /** Constructor protegido sin argumentos: bloquea instancias vacías. */
    protected Publisher() {} // to ban calls to this constructor

    /**
     * Crea un publicador y registra (o recupera) su tópico en el broker.
     *
     * @param name rótulo mostrado en pantalla.
     * @param broker broker que administra los tópicos.
     * @param topicName nombre del tópico al que publicará mensajes.
     */
    public Publisher(String name, Broker broker, String topicName) {
        super(name, topicName);
        topic = broker.createTopic(topicName);
    }

    /**
     * Envía un nuevo mensaje al tópico asociado.
     *
     * @param message contenido del mensaje.
     */
    protected void publishNewEvent(String message) {
        topic.notify(message);
    }
}

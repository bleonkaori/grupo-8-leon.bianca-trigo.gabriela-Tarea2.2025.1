import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Simulador{
    private Broker broker;
    private ArrayList<Publisher> publicadores;

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Simulador <file>");
            System.exit(1);
        }

        Scanner input = null;
        try {
            input = new Scanner(new File(args[0]));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found");
            System.exit(1);
        }

        Simulador stage = new Simulador();
        stage.setupSimulator(input);
        stage.runSimulation();
    }

    public void setupSimulator(Scanner input) {
        broker = new Broker();
        publicadores = new ArrayList<Publisher>();

        while (input.hasNextLine()) {
            String line = input.nextLine();
            if (line.isEmpty()) continue;

            Scanner sc = new Scanner(line);
            String component = sc.next();

            if (component.equals("publicador")) {
                String publisherName = sc.next();
                String topicName = sc.next();
                Publisher p = new Publisher(publisherName, broker, topicName);
                publicadores.add(p);
            } else if (component.equals("suscriptor")) {
                String type = sc.next();
                String subscriberName = sc.next();
                String topicName = sc.next();
                String fileName = sc.next();

                try {
                    if (type.equals("Seguidor")) {
                        Follower follower = new Follower(subscriberName, topicName, new PrintStream(fileName));
                        broker.subscribe(follower);
                    } else if (type.equals("Registrador")) {
                        Recorder recorder = new Recorder(subscriberName, topicName, new PrintStream(fileName));
                        broker.subscribe(recorder);
                    } else if (type.equals("Monitor")) {
                        Monitor monitor = new Monitor(subscriberName, topicName, new PrintStream(fileName));
                        broker.subscribe(monitor);
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("Error al abrir archivo: " + fileName);
                    System.exit(1);
                }
            }
        }
    }

    public void runSimulation() {
        System.out.println("Ingrese los eventos (<nombrePublicador> <mensaje>) y presione ENTER para terminar:");
        Scanner input = new Scanner(System.in);

        while (input.hasNextLine()) {
            String line = input.nextLine().trim();
            if (line.isEmpty()) {
                break; // Termina si solo se da enter
            }

            Scanner sc = new Scanner(line);
            if (!sc.hasNext()) continue;

            String publisherName = sc.next();
            String message = "";
            if (sc.hasNextLine()) {
                message = sc.nextLine().trim();
            }

            Publisher publisher = findPublisher(publisherName);
            if (publisher != null) {
                publisher.publishNewEvent(message);
            } else {
                System.out.println("Unknown Publisher");
            }
        }
    }

    private Publisher findPublisher(String name) {
        for (Publisher p : publicadores) {
            if (p.getName().equals(name)) {
                return p;
            }
        }
        return null;
    }
}
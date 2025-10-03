package ar.edu.utn.dds.k3003.messaging;
import ar.edu.utn.dds.k3003.facades.dtos.*;
import ar.edu.utn.dds.k3003.app.Fachada;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class HechoWorker extends DefaultConsumer {

    private final Fachada fachada;
    private final ObjectMapper objectMapper;

    @Autowired
    public HechoWorker(Fachada fachada, ObjectMapper objectMapper) throws Exception {
        super(createChannel());
        this.fachada = fachada;
        this.objectMapper = objectMapper;

        // Declarar la cola
        String queueName = System.getenv("QUEUE_NAME");
        this.getChannel().queueDeclare(queueName, true, false, false, null);

        // Suscribirse a la cola
        this.getChannel().basicConsume(queueName, false, this);
    }

    private static Channel createChannel() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("QUEUE_USERNAME")); // en CloudAMQP vhost = user
        Connection connection = factory.newConnection();
        return connection.createChannel();
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope,
                               AMQP.BasicProperties properties, byte[] body) throws IOException {
        this.getChannel().basicAck(envelope.getDeliveryTag(), false);

        String json = new String(body, "UTF-8");
        System.out.println("📩 Mensaje recibido en Fuente: " + json);

        try {
            HechoDTO hechoDTO = objectMapper.readValue(json, HechoDTO.class);
            fachada.agregar(hechoDTO); // Reutilizamos la lógica de alta
        } catch (Exception e) {
            System.err.println("Error procesando hecho desde la cola: " + e.getMessage());
        }
    }
}
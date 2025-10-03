package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HechoSuscriber {

    private final Fachada fachada;
    private final ObjectMapper objectMapper;

    private Connection connection;
    private Channel channel;
    private String consumerTag;
    private boolean activo = false;

    @Autowired
    public HechoSuscriber(Fachada fachada, ObjectMapper objectMapper) {
        this.fachada = fachada;
        this.objectMapper = objectMapper;
    }

    public synchronized void start() throws Exception {
        if (activo) return;

        String queueName = System.getenv("QUEUE_NAME");
        if (queueName == null || queueName.isBlank()) {
            throw new IllegalStateException("QUEUE_NAME no está configurado");
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("QUEUE_USERNAME")); // tipico en CloudAMQP

        this.connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.consumerTag = channel.basicConsume(queueName, false, (tag, message) -> {
            try {
                String json = new String(message.getBody());
                System.out.println("Hecho recibido: " + json);

                HechoDTO hechoDTO = objectMapper.readValue(json, HechoDTO.class);
                fachada.agregar(hechoDTO);

                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                e.printStackTrace();
                channel.basicNack(message.getEnvelope().getDeliveryTag(), false, false);
            }
        }, tag -> {
            System.out.println("Consumer cancelado: " + tag);
        });

        this.activo = true;
        System.out.println("Suscripción iniciada con tag " + consumerTag);
    }

    public synchronized void stop() throws Exception {
        if (!activo) return;

        if (channel != null && consumerTag != null) {
            channel.basicCancel(consumerTag); // cancelar consumer
        }
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (connection != null && connection.isOpen()) {
            connection.close();
        }

        consumerTag = null;
        channel = null;
        connection = null;
        activo = false;
        System.out.println("Suscripción detenida");
    }

    public boolean isActivo() {
        return activo;
    }
}
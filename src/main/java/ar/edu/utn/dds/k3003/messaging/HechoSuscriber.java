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

    private Channel channel;
    private String consumerTag;
    private boolean activo = false;

    @Autowired
    public HechoSuscriber(Fachada fachada, ObjectMapper objectMapper) {
        this.fachada = fachada;
        this.objectMapper = objectMapper;
    }

    public void start() throws Exception {
        if (activo) return; // ya está corriendo
        String queueName = System.getenv("QUEUE_NAME");
        if (queueName == null || queueName.isBlank()) {
            throw new IllegalStateException("QUEUE_NAME no está configurado");
        }

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("QUEUE_USERNAME")); // CloudAMQP usa el user como vhost

        Connection connection = factory.newConnection();
        this.channel = connection.createChannel();

        this.consumerTag = channel.basicConsume(queueName, false, (consumerTag, message) -> {
            String json = new String(message.getBody());
            System.out.println("Hecho recibido: " + json);
            try {
                HechoDTO hechoDTO = objectMapper.readValue(json, HechoDTO.class);
                fachada.agregar(hechoDTO);
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
            } catch (Exception e) {
                System.err.println("Error procesando hecho: " + e.getMessage());
                channel.basicNack(message.getEnvelope().getDeliveryTag(), false, false);
            }
        }, consumerTag -> {});
        this.activo = true;
    }

    public void stop() throws Exception {
        if (!activo) return;
        channel.basicCancel(consumerTag);
        channel.close();
        this.activo = false;
    }

    public boolean isActivo() {
        return activo;
    }
}
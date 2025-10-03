package ar.edu.utn.dds.k3003.messaging;

import ar.edu.utn.dds.k3003.app.Fachada;
import ar.edu.utn.dds.k3003.facades.dtos.HechoDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HechoSuscriber {
    private Channel channel;
    private Connection connection;
    private String consumerTag;
    private boolean activo = false;

    private final Fachada fachada;
    private final ObjectMapper objectMapper;

    @Autowired
    public HechoSuscriber(Fachada fachada, ObjectMapper objectMapper) {
        this.fachada = fachada;
        this.objectMapper = objectMapper;
    }

    public void start() throws Exception {
        if (activo) return;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(System.getenv("QUEUE_HOST"));
        factory.setUsername(System.getenv("QUEUE_USERNAME"));
        factory.setPassword(System.getenv("QUEUE_PASSWORD"));
        factory.setVirtualHost(System.getenv("QUEUE_USERNAME"));

        connection = factory.newConnection();
        channel = connection.createChannel();
        String queueName = System.getenv("QUEUE_NAME");

        consumerTag = channel.basicConsume(queueName, false, (tag, message) -> {
            String json = new String(message.getBody());
            HechoDTO hechoDTO = objectMapper.readValue(json, HechoDTO.class);
            fachada.agregar(hechoDTO);
            channel.basicAck(message.getEnvelope().getDeliveryTag(), false);
        }, tag -> {});

        activo = true;
        System.out.println("✅ Suscripción iniciada");
    }

    public void stop() throws Exception {
        if (!activo) return;
        if (channel != null && consumerTag != null) {
            channel.basicCancel(consumerTag);
        }
        if (channel != null) channel.close();
        if (connection != null) connection.close();

        channel = null;
        connection = null;
        consumerTag = null;
        activo = false;
        System.out.println("⏹️ Suscripción detenida");
    }

    public boolean isActivo() {
        return activo;
    }
}
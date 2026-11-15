package com.example.NotesService.config;

import com.example.NotesService.event.NoteReminderEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, NoteReminderEvent> reminderProducerFactory(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> properties = new HashMap<>();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new DefaultKafkaProducerFactory<>(
                properties,
                new StringSerializer(),
                new JacksonJsonSerializer<NoteReminderEvent>().noTypeInfo());
    }

    @Bean
    public KafkaTemplate<String, NoteReminderEvent> reminderKafkaTemplate(
            ProducerFactory<String, NoteReminderEvent> reminderProducerFactory) {
        return new KafkaTemplate<>(reminderProducerFactory);
    }
}

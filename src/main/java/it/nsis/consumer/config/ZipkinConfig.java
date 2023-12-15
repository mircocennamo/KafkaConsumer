package it.nsis.consumer.config;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import zipkin2.reporter.kafka.KafkaSender;

import java.util.Map;

/**
 * @author mirco.cennamo on 12/12/2023
 * @project kafkaProducerVirtualThread
 */
@Configuration
@EnableConfigurationProperties(KafkaProperties.class)
public class ZipkinConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    String bootstrapServerKafka;

    @Value("${management.tracing.service.name}")
    String serviceName;

    @Value("${management.tracing.kafka.topic}")
    String topic;

    @Bean("zipkinSender")
    KafkaSender senderZipkin(KafkaProperties config, Environment environment) {
        Map<String, Object> properties = config.buildProducerProperties();
        properties.put("key.serializer", ByteArraySerializer.class.getName());
        properties.put("value.serializer", ByteArraySerializer.class.getName());
        properties.put(CommonClientConfigs.CLIENT_ID_CONFIG, serviceName);
        properties.put("spring.kafka.bootstrap-servers", bootstrapServerKafka);
        return KafkaSender.newBuilder().topic(topic).overrides(properties).build();
    }
}

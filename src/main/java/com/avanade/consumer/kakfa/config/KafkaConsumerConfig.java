package com.avanade.consumer.kakfa.config;



import brave.kafka.interceptor.TracingConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.tomcat.util.threads.VirtualThreadExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.FixedBackOff;

/**
 * @author mirco.cennamo on 26/11/2023
 * @project spring-boot-kafka-consumer
 */
//@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

   // @Value(value = "${kafka.backoff.interval}")
   // private Long interval;

 //   @Value(value = "${kafka.backoff.max_failure}")
  //  private Long maxAttempts;

    /**
     * The FixedBackOff class takes two arguments:
     *
     * interval: the amount of time to wait between retries in milliseconds
     * maxAttempts: the maximum number of times to retry the operation before giving up
     * @return
     */

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapServers);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                JsonDeserializer.class);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "batch");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "200");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, "1000");

        props.put(ConsumerConfig.INTERCEPTOR_CLASSES_CONFIG, TracingConsumerInterceptor.class.getName());
        props.put("zipkin.http.endpoint", "http://127.0.0.1:9411/api/v2/spans");
        props.put("zipkin.sender.type", "HTTP");
        props.put("zipkin.encoding", "JSON");
        props.put("zipkin.remote.service.name", "scntt");
        props.put("zipkin.local.service.name", "scntt");
        props.put("zipkin.trace.id.128bit.enabled", "true");
        props.put("zipkin.sampler.rate", "1.0F");



        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setBatchListener(true);

       // factory.setCommonErrorHandler(errorHandler());
        //il consumer spedisce un ACK per ogni messaggio processato -- > ContainerProperties.AckMode.RECORD
        //il consumer spedisce un ACK per ogni batch di messaggi processati -- > ContainerProperties.AckMode.BATCH


        return factory;
    }


   /* @Bean
    public DefaultErrorHandler errorHandler() {
        BackOff fixedBackOff = new FixedBackOff(interval, maxAttempts);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
            // logic to execute when all the retry attemps are exhausted
        }, fixedBackOff);
        errorHandler.addRetryableExceptions(SocketTimeoutException.class);
        errorHandler.addNotRetryableExceptions(NullPointerException.class);
        return errorHandler;
    }

    */

  /*  @Bean(name = "consumerTaskExecutor")
    public Executor productThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(30);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("consumerTaskExecutor-");
        executor.initialize();
        return executor;
    }

   */





}

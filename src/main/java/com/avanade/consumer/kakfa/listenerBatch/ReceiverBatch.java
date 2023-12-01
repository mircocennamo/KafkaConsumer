package com.avanade.consumer.kakfa.listenerBatch;

import com.avanade.model.Rilevazione;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;


@Component
@Slf4j
public class ReceiverBatch {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    //private ExecutorService executorService = Executors.newFixedThreadPool(30);

  //  @Autowired
   // @Qualifier("consumerTaskExecutor")
   // private Executor targheThreadPool;

    @Autowired
    private Executor asyncTaskExecutor;


    private static final String TARGHE = "TARGHE";

    private ConcurrentMap<String,String> retrieveTargheMap() {
        return hazelcastInstance.getMap(TARGHE);
    }


    @KafkaListener(id = "consumer-batch",topics = "${spring.kafka.consumer.topic}",
            properties = {"spring.json.value.default.type=com.avanade.model.Rilevazione"},
            groupId = "test-batch-group",
            concurrency = "4")
    public void receiveBatch(@Payload List<Rilevazione> payloads,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.debug("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        log.debug("Starting the process to recieve batch messages");
        log.debug("process to receive {} messages " , payloads.size());


        payloads.stream().forEach(payload -> {
            asyncTaskExecutor.execute(() -> loggingInfo(payload, timestamp,partition));
        });

        log.debug("all the batch messages are consumed");
    }

    //@DltHandler
    public void processMessage(Rilevazione user) {
        System.out.println("DltHandler processMessage " +  user);
    }


    @Async
    private void loggingInfo(Rilevazione payload,  long timestamp,int partition) {
         if (log.isDebugEnabled()) {
                log.debug("Thread {} received  message {} at {} on partition {} ",
                        Thread.currentThread(), payload.toString(), new Date(timestamp),partition);
            }
        String value = retrieveTargheMap().get(payload.getLicensePlate());

     }
}

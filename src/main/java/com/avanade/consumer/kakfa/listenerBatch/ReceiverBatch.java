package com.avanade.consumer.kakfa.listenerBatch;

import com.avanade.consumer.kakfa.service.MessageService;
import com.avanade.model.Rilevazione;
import com.hazelcast.core.HazelcastInstance;
import io.micrometer.observation.annotation.Observed;
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



    //private ExecutorService executorService = Executors.newFixedThreadPool(30);

  //  @Autowired
   // @Qualifier("consumerTaskExecutor")
   // private Executor targheThreadPool;

    @Autowired
    private Executor asyncTaskExecutor;

    @Autowired
    private MessageService messageService;



    @Observed(name = "SenderAsyncCallBack",
            contextualName = "receiveBatch")

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
            asyncTaskExecutor.execute(() -> messageService.loggingInfo(payload, timestamp,partition));
        });

        log.debug("all the batch messages are consumed");
    }

    //@DltHandler
    public void processMessage(Rilevazione user) {
        System.out.println("DltHandler processMessage " +  user);
    }


}

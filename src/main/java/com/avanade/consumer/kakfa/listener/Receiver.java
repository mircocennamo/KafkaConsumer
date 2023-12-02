package com.avanade.consumer.kakfa.listener;
/*
import com.avanade.model.Rilevazione;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Backoff;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;


import java.util.List;
import java.util.concurrent.Executor;


@Component
@Slf4j
public class Receiver {



    //private ExecutorService executorService = Executors.newFixedThreadPool(30);

  //  @Autowired
   // @Qualifier("consumerTaskExecutor")
   // private Executor targheThreadPool;

    @Autowired
    private Executor asyncTaskExecutor;



  //  @RetryableTopic(
    //        backoff = @Backoff(value = 3000L),
      //      attempts = "5",
        //    autoStartDltHandler = "true")
  //  @KafkaListener(id = "consumer",topics = "${spring.kafka.consumer.topic}",
  //          properties = {"spring.json.value.default.type=com.avanade.model.Rilevazione"},
  //          groupId = "test-group",
  //          concurrency = "4")
    public void receive(@Payload Rilevazione payload,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp ,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {

       asyncTaskExecutor.execute(() -> loggingInfo(payload, timestamp,partition));
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
     }
}
*/
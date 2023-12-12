package it.nsis.consumer.kakfa.listenerBatch;

import brave.Span;
import brave.Tracer;
import it.nsis.consumer.kakfa.service.MessageService;
import it.nsis.model.Rilevazione;
import io.micrometer.tracing.annotation.NewSpan;
import it.nsis.utility.TagConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Autowired
    private Tracer tracer;




    @NewSpan(name = "SCNTT-Consumer")
    @KafkaListener(id = "${spring.kafka.consumer.id}",topics = "${spring.kafka.consumer.topic}",
            properties = {"spring.json.value.default.type=it.nsis.model.Rilevazione"},
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "${spring.kafka.consumer.concurrency}",autoStartup = "${consumer.auto.startup}")
    public void receiveBatch(@Payload List<Rilevazione> payloads,
                        @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestampRiceivedMessage ,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        log.debug("- - - - - - - - - - - - - - - - - - - - - - - - - - - - - -");
        log.debug("Starting the process to receive batch messages");
        log.debug("process to receive {} messages " , payloads.size());

        Span span = this.tracer.currentSpan();
        span.tag(TagConst.NUMERO_MESSAGGI_PRELEVATI, String.valueOf(payloads.size()));
        span.tag(TagConst.PARTIZIONE_LETTA, String.valueOf(partition));


        payloads.stream().forEach(payload -> {
            asyncTaskExecutor.execute(() -> messageService.elaborazioneMessaggio(payload, timestampRiceivedMessage,partition));
        });

        log.debug("all the batch messages are consumed");
    }

    //@DltHandler
    public void processMessage(Rilevazione user) {
        System.out.println("DltHandler processMessage " +  user);
    }


}

package it.nsis.consumer.kakfa.service;

import brave.Span;
import brave.Tracer;
import io.micrometer.tracing.annotation.NewSpan;
import it.nsis.model.Rilevazione;
import com.hazelcast.core.HazelcastInstance;
import io.micrometer.tracing.annotation.ContinueSpan;
import io.micrometer.tracing.annotation.SpanTag;
import it.nsis.model.Status;
import it.nsis.utility.TagConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;

/**
 * @author mirco.cennamo on 01/12/2023
 * @project KafkaConsumer
 */
@Service
@Slf4j
public class MessageService {


    @Autowired
    private HazelcastInstance hazelcastInstance;

    private static final String TARGHE = "TARGHE";
    private static final String STATUS_TARGHE = "STATUS";

    private ConcurrentMap<String, Rilevazione> retrieveTargheMap() {
        return hazelcastInstance.getMap(TARGHE);
    }

    private ConcurrentMap<String, Status> retrieveStatusTargheMap() {
        return hazelcastInstance.getMap(STATUS_TARGHE);
    }



    @Autowired
    private Tracer tracer;


    // Example of using an annotation to observe methods
    // <user.name> will be used as a metric name
    // <getting-user-name> will be used as a span  name
    // <userType=userType2> will be set as a tag for both metric & span


    @ContinueSpan
    public void elaborazioneMessaggio(@SpanTag("consumer.payload") Rilevazione payload, long timestampRiceivedMessage, int partition) {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} received  message {} at {} on partition {} ",
                    Thread.currentThread(), payload.toString(), new Date(timestampRiceivedMessage), partition);
        }
        Span span = this.tracer.currentSpan();
        span.tag(TagConst.CORRELATION_ID, payload.getUuid().toString());
        span.tag(TagConst.MESSAGGIO, payload.toString());
        span.tag(TagConst.TARGA, payload.getLicensePlate());

        Rilevazione value = retrieveTargheMap().get(payload.getLicensePlate());
        if(value!=null){
            payload.setInsertAt(value.getInsertAt());
            if (log.isDebugEnabled()) {
                log.debug("targa recuperata invio ad NSIS payload {} " , payload);
            }
            invioNSIS(payload);
        }

    }

    @NewSpan(name = "invioNSIS")
    public void invioNSIS(Rilevazione payload) {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} invioNSIS  message {} ",
                    Thread.currentThread(), payload.toString());
        }
        Span span = this.tracer.currentSpan();
        span.tag(TagConst.CORRELATION_ID, payload.getUuid().toString());
        span.tag(TagConst.MESSAGGIO, payload.toString());
        span.tag(TagConst.TARGA, payload.getLicensePlate());
        span.tag(TagConst.INVIO_MESSAGE_NSIS, "true");
        payload.setFound(true);
        payload.setUpdateAt(new Date());
        retrieveTargheMap().put(payload.getLicensePlate(),payload);
    }


    @NewSpan(name="checkStatusCacheTarghe")
    public Status checkStatusCacheTarghe() {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} checkStatusCacheTarghe   ", Thread.currentThread());
        }

        return retrieveStatusTargheMap().get("TARGHE");
    }
}

package it.nsis.consumer.service;

import brave.Span;
import io.micrometer.tracing.annotation.ContinueSpan;
import io.micrometer.tracing.annotation.NewSpan;
import io.micrometer.tracing.annotation.SpanTag;
import it.nsis.model.Rilevazione;
import it.nsis.model.Status;
import it.nsis.utility.TagConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author mirco.cennamo on 01/12/2023
 * @project KafkaConsumer
 */
@Service
@Slf4j
public class MessageService extends BaseService {

    @Autowired
    private CallerNsis callerNsis;

    @NewSpan(name ="elaborazioneMessaggio")
    public void elaborazioneMessaggio(@SpanTag("consumer.payload") Rilevazione payload, long timestampRiceivedMessage, int partition) {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} elaborazioneMessaggio {} at {} on partition {} ",
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
            callerNsis.invioNSIS(payload);
        }

    }




    @NewSpan(name="checkStatusCacheTarghe")
    public Status checkStatusCacheTarghe() {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} checkStatusCacheTarghe   ", Thread.currentThread());
        }

        return retrieveStatusTargheMap().get("TARGHE");
    }
}

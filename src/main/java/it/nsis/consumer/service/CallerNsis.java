package it.nsis.consumer.service;

import brave.Span;
import brave.Tracer;
import com.hazelcast.core.HazelcastInstance;
import io.micrometer.tracing.annotation.ContinueSpan;
import io.micrometer.tracing.annotation.NewSpan;
import it.nsis.model.Rilevazione;
import it.nsis.utility.TagConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

/**
 * @author mirco.cennamo on 15/12/2023
 * @project KafkaConsumer
 */

@Service
@Slf4j
public class CallerNsis extends  BaseService{



    @NewSpan("invioNSIS")
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


}

package com.avanade.consumer.kakfa.service;

import com.avanade.model.Rilevazione;
import com.hazelcast.core.HazelcastInstance;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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

    private ConcurrentMap<String,String> retrieveTargheMap() {
        return hazelcastInstance.getMap(TARGHE);
    }


    // Example of using an annotation to observe methods
    // <user.name> will be used as a metric name
    // <getting-user-name> will be used as a span  name
    // <userType=userType2> will be set as a tag for both metric & span





    @Observed(name = "SenderAsyncCallBack",
            contextualName = "loggingInfo")
    @Async
    public void loggingInfo(Rilevazione payload, long timestamp, int partition) {
        if (log.isDebugEnabled()) {
            log.debug("Thread {} received  message {} at {} on partition {} ",
                    Thread.currentThread(), payload.toString(), new Date(timestamp),partition);
        }
        String value = retrieveTargheMap().get(payload.getLicensePlate());

    }
}

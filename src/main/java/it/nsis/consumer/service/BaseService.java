package it.nsis.consumer.service;

import brave.Tracer;
import com.hazelcast.core.HazelcastInstance;
import it.nsis.model.Rilevazione;
import it.nsis.model.Status;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentMap;

/**
 * @author mirco.cennamo on 15/12/2023
 * @project KafkaConsumer
 */
@Service
@Slf4j
public class BaseService {

    @Autowired
    protected Tracer tracer;
    @Autowired
    private HazelcastInstance hazelcastInstance;

    private static final String TARGHE = "TARGHE";
    private static final String STATUS_TARGHE = "STATUS";

    protected ConcurrentMap<String, Rilevazione> retrieveTargheMap() {
        return hazelcastInstance.getMap(TARGHE);
    }

    protected ConcurrentMap<String, Status> retrieveStatusTargheMap() {
        return hazelcastInstance.getMap(STATUS_TARGHE);
    }
}

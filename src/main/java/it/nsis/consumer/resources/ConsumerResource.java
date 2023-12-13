package it.nsis.consumer.resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.nsis.model.EnumStatus;
import it.nsis.model.Rilevazione;
import it.nsis.model.Status;
import it.nsis.viewmodel.RilevazioneResponseVm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ConcurrentMap;

/**
 * @author mirco.cennamo on 11/12/2023
 * @project KafkaConsumer
 */
@RestController
@RequestMapping("nsis")
@Slf4j
public class ConsumerResource {

    @Value("${spring.kafka.consumer.id}")
    String listenerId;

    @Autowired
    private it.nsis.consumer.kakfa.service.MessageService messageService;

    @Autowired
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Operation(summary = "start consumer with check status cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "consumer started",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Error in start consumer",
                    content = @Content) })
    @GetMapping(path = "scntt/consumer/start")
    public ResponseEntity<String> startConsumer(){
        log.debug("called startConsumer ");
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        Status status = messageService.checkStatusCacheTarghe();
        if( !listenerContainer.isRunning() &&
                ( status!=null  &&  status.getStatus()== EnumStatus.READY)
        ){
            listenerContainer.start();
            log.info("consumer started  :: exit");
            return new ResponseEntity<>("CONSUMER STARTED", HttpStatus.OK);

        }else {
            log.debug("stato cache targhe {}  o lo stato dei consumer isRunning {} non permette lo start dei consumer ", status.getStatus(),listenerContainer.isRunning());
            log.info("consumer already running  or status cache in incompatible mode:: exit");
            return new ResponseEntity<>("CONSUMER ALREADY STARTED OR CACHE IN INCOMPATIBLE MODE", HttpStatus.OK);
       }

    }

    @Operation(summary = "forced start consumer without check status cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "consumer started",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Error in start consumer",
                    content = @Content) })
    @GetMapping(path = "scntt/consumer/forcedStart")
    public ResponseEntity<String> forcedStartConsumer(){
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        log.debug("called forcedStartConsumer ");
        listenerContainer.start();
        log.info("consumer started in forced mode :: exit");
        return new ResponseEntity<>("CONSUMER STARTED IN FORCED MODE", HttpStatus.OK);
    }

    @Operation(summary = "stop consumer with check status cache")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "consumer stopped",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Error in stop consumer",
                    content = @Content) })
    @GetMapping(path = "scntt/consumer/stop")
    public ResponseEntity<String> stopConsumer(){
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
         if(listenerContainer.isRunning()) {
            listenerContainer.stop();
             log.info("consumer stoppeed :: exit");
             return new ResponseEntity<>("CONSUMER STOPPED", HttpStatus.OK);
        }else {
            log.info("consumer already stopped :: exit");
             return new ResponseEntity<>("CONSUMER ALREADY STOPPED", HttpStatus.OK);
        }

    }


    @Operation(summary = "retrieve status consumer ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "status consumer",
                    content = { @Content(mediaType = "application/json") }),
            @ApiResponse(responseCode = "500", description = "Error in status consumer",
                    content = @Content) })
    @GetMapping(path = "scntt/consumer/status")
    public ResponseEntity<String> statusConsumer(){
        MessageListenerContainer listenerContainer = kafkaListenerEndpointRegistry.getListenerContainer(listenerId);
        if(listenerContainer.isRunning()) {
            log.info("consumer status running :: exit");
            return new ResponseEntity<>("RUNNING", HttpStatus.OK);
        }else {
            log.info("consumer status not running :: exit");
            return new ResponseEntity<>("NOT RUNNING", HttpStatus.OK);

        }

    }
}

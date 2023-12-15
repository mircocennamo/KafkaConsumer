package it.nsis.consumer.resources;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @author mirco.cennamo on 15/12/2023
 * @project KafkaConsumer
 */
@Data
@AllArgsConstructor
public class StatusResponse {
    private String message;
    private Date date;
}

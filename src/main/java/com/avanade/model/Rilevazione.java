package com.avanade.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.util.UUID;

/**
 * @author mirco.cennamo on 20/10/2023
 * @project spring-boot-kafka-producer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Rilevazione {

    private UUID uuid;
    private Instant instant;
    private String licensePlate;

    private String latitude;
    private String longitude;



}

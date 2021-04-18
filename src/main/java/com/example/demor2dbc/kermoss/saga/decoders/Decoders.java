package com.example.demor2dbc.kermoss.saga.decoders;

import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.example.demor2dbc.kermoss.infra.DecoderRegistry;

@Configuration
public class Decoders extends DecoderRegistry {

    @PostConstruct
    public void setup() {
    	PayInvoiceServiceDecoder decoder0 = new PayInvoiceServiceDecoder();
        this.put("pay-invoice", decoder0);
     
    }
}
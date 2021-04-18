package com.example.demor2dbc.kermoss.infra;

import com.example.demor2dbc.kermoss.entities.CommandMeta;
import com.example.demor2dbc.kermoss.events.BaseTransactionEvent;

public interface BaseDecoder {
    BaseTransactionEvent decode(final CommandMeta meta);
}

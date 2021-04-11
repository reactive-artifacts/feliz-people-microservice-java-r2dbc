package com.example.demor2dbc.kermoss.events;

import com.example.demor2dbc.kermoss.domain.Message;

public abstract class BaseTransactionEvent extends Message  {
    public BaseTransactionEvent() {
    }

    public String toString() {
        return "BaseTransactionEvent()";
    }
}
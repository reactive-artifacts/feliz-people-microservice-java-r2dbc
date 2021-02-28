package com.example.demor2dbc.events;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class DomainEvent<K> {
	
	private LocalDateTime time = LocalDateTime.now();

	public LocalDateTime getTime() {
		return time;
	}

	public abstract String topic();
	public abstract K key();
	public UUID keyAsUUID() {
		return UUID.nameUUIDFromBytes(key().toString().getBytes());
	}
	
}
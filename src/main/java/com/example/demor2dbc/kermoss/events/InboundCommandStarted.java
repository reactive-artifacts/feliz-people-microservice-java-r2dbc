package com.example.demor2dbc.kermoss.events;

import com.example.demor2dbc.kermoss.entities.CommandMeta;

public class InboundCommandStarted {
	private CommandMeta meta;

	public InboundCommandStarted(CommandMeta meta) {
		this.meta = meta;
	}

	public CommandMeta getMeta() {
		return this.meta;
	}
}
package com.example.demor2dbc.kermoss.events;

import com.example.demor2dbc.kermoss.entities.CommandMeta;

public class OutboundCommandStarted {
	private CommandMeta meta;

	public OutboundCommandStarted(CommandMeta meta) {
		this.meta = meta;
	}

	public CommandMeta getMeta() {
		return this.meta;
	}
}
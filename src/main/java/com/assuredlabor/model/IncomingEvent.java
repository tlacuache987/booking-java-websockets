package com.assuredlabor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.assuredlabor.model.enums.Event;

@Data
@AllArgsConstructor
@Builder
public class IncomingEvent {
	private String event;
	private String fruta;
	private Integer cantidad;

	public Event getEventEnum() {
		return Event.valueOf(event.toUpperCase());
	}
}

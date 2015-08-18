package com.assuredlabor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Mensaje {
	private String on;
	private String msj;
	private Stock stock;
}

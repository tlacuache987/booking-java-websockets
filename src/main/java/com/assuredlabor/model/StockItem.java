package com.assuredlabor.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class StockItem {
	private String fruta;
	private Integer cantidad;
}

package com.saiket.myRetail.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PriceUpdateRequestDto 
{	
	@NotBlank(message = "priceId is mandatory")
	private String priceId;
	
	private boolean activate = false;
	
	private ProductPriceDto price;
}

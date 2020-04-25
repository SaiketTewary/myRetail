package com.saiket.myRetail.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PriceUpdateRequestDto 
{		
	private boolean activate = true;
	
	@NotBlank(message = "currency_code is mandatory")
	private String currency_code; 
	
	@NotNull(message = "Please provide a price value")
	@DecimalMin("0.00")
	private double value;
}

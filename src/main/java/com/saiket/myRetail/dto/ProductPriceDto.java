package com.saiket.myRetail.dto;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductPriceDto 
{
	@NotBlank(message = "currency_code is mandatory")
	private String currency_code; 
	
	@NotBlank(message = "Please provide a price value")
	@DecimalMin("0.00")
	private double value;
	
	public ProductPriceDto(String c, double p)
	{
		this.currency_code = c;
		this.value = p;
	}
}

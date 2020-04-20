package com.saiket.myRetail.dto;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PriceRequestDto 
{
	@NotBlank(message = "currencyCode can not be null")
	private String      currencyCode;
}

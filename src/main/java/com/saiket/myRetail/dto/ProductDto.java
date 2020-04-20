package com.saiket.myRetail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductDto 
{
	private long 			id;
	private String 			name;
	private ProductPriceDto current_price;
}

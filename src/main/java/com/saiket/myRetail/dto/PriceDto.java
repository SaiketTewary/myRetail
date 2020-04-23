package com.saiket.myRetail.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PriceDto 
{
    private long productId;
	
    private String priceId;
	
    private double price;
    
    private String currency;
    
    private boolean active;
    
    private long created;
}

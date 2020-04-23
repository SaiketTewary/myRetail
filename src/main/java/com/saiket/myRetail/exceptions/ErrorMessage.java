package com.saiket.myRetail.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ErrorMessage 
{
	String message;
	
    public ErrorMessage(String message) 
    {
        this.message = message;
    }
}

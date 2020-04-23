package com.saiket.myRetail.controller;

import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import com.saiket.myRetail.dto.PriceRequestDto;
import com.saiket.myRetail.dto.PriceUpdateRequestDto;
import com.saiket.myRetail.dto.ProductDto;
import com.saiket.myRetail.dto.ProductPriceDto;
import com.saiket.myRetail.exceptions.MyRetailException;
import com.saiket.myRetail.exceptions.RedskyException;
import com.saiket.myRetail.service.ProductPriceService;


@RestController
@RequestMapping("/products")
public class RetailController 
{	
	@Autowired
	ProductPriceService productPriceService;
	
	
    @RequestMapping(path = "/Setup", method = RequestMethod.GET)
    public String helloRetail() throws MyRetailException 
    {
    	ArrayList<ProductPriceDto> request = new ArrayList<ProductPriceDto>();
    	
    	request.add( new ProductPriceDto("USD", 12.5));
    	request.add( new ProductPriceDto("BDT", 10.5));
    	request.add( new ProductPriceDto("CAD", 20.5));
    	
    	productPriceService.createPrices(13860428, request, true);
    	
        return "Hello from Retail! Data Created";
    }  
           
    @RequestMapping(path = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<ProductDto> getProductPrice(@PathVariable("id") int productId,@Valid @RequestBody (required=false) PriceRequestDto request) throws RedskyException, MyRetailException 
    {
    	ProductDto product = null;
		try 
		{
			product = productPriceService.getPrice(productId, request);
			
		} 
		catch (RedskyException e) 
		{
			throw new RedskyException("Product Not Found!");
		}
		catch (MyRetailException e) 
		{
			throw new MyRetailException("Price Not Found!");
		}
		
		return ResponseEntity.ok(product);

    }
    
    @RequestMapping(path = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<String> updatePrice(@PathVariable("id") int productId, @Valid @RequestBody PriceUpdateRequestDto request) throws MyRetailException 
    {
    	boolean success = false;
		try 
		{
			success = productPriceService.updatePrice(productId, request);
		} 
		catch (MyRetailException e) 
		{
			throw new MyRetailException("Price Not Found!");
		}
    	
    	if(!success)
    		throw new MyRetailException("Price Not Found!");
    	
    	return ResponseEntity.ok("Price Updated");
    }
}

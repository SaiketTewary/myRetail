package com.saiket.myRetail.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.saiket.myRetail.dao.ProductPriceDao;
import com.saiket.myRetail.dto.PriceRequestDto;
import com.saiket.myRetail.dto.PriceUpdateRequestDto;
import com.saiket.myRetail.dto.ProductDto;
import com.saiket.myRetail.dto.ProductPriceDto;
import com.saiket.myRetail.exceptions.RedskyException;
import com.saiket.myRetail.repository.DynamoRepo;
import com.saiket.myRetail.repository.RedskyRepo;

@Service
public class ProductPriceService 
{
	@Autowired
	private DynamoRepo repository;
	
	@Autowired
	private RedskyRepo redskyRepo;
	
	
	public ProductDto getPrice (long id, PriceRequestDto requestDto) throws RedskyException
	{
		String curr = "USD";
		
		if(requestDto !=null)
		{
			curr =  requestDto.getCurrencyCode();
		}
		
		
		List<ProductPriceDao> daos =  repository.getCurrentPrice(id, curr);
		
		ProductDto dto = new ProductDto();
		if(!daos.isEmpty()) 
		{
			dto.setId(id);
			for (ProductPriceDao productPriceDao : daos) 
			{
				ProductPriceDto priceDto = new ProductPriceDto(productPriceDao.getCurrency(), productPriceDao.getPrice());
				dto.setCurrent_price(priceDto);
			}
			
			dto.setName(redskyRepo.getProductTitle(id));
			return dto;
		}
		return dto;
	}
	
	public boolean updatePrice (long id, PriceUpdateRequestDto request)
	{
		if(request.getPrice().getValue() < 0 || request.getPrice().getCurrency_code().isBlank())
			return false;
		
		return repository.updatePrice(id, request);
	}
	
	
	public void createPrice (long id, ProductPriceDto request)
	{
    	ArrayList<ProductPriceDto> dtos = new ArrayList<ProductPriceDto>();
    	dtos.add(request);
    	createPrices(id, dtos, false);
	}
	
	public void createPrices (long id, ArrayList<ProductPriceDto> request, boolean activate)
	{
		repository.savePrice(id, request, activate);
	}
}

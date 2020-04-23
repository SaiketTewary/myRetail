package com.saiket.myRetail.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.saiket.myRetail.dao.ProductPriceDao;
import com.saiket.myRetail.dto.PriceDto;
import com.saiket.myRetail.dto.PriceRequestDto;
import com.saiket.myRetail.dto.PriceUpdateRequestDto;
import com.saiket.myRetail.dto.ProductDto;
import com.saiket.myRetail.dto.ProductPriceDto;
import com.saiket.myRetail.exceptions.MyRetailException;
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
	
	
	public List<PriceDto> getPrices (long id) throws MyRetailException
	{
		List<ProductPriceDao> daos =  repository.getProducPrices(id);
		List<PriceDto> dtos = new ArrayList<PriceDto>();
		
		if(!daos.isEmpty()) 
		{
			daos.forEach(dao -> 
			{
				dtos.add(dao.toPriceDto());
			});
		}
		
		return dtos;
	}
	
	public ProductDto getPrice (long id, PriceRequestDto requestDto) throws RedskyException, MyRetailException
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
	
	public boolean updatePrice (long id, PriceUpdateRequestDto request) throws MyRetailException
	{
		return repository.updatePrice(id, request);
	}
	
	
	public void createPrice (long id, ProductPriceDto request) throws MyRetailException
	{
    	ArrayList<ProductPriceDto> dtos = new ArrayList<ProductPriceDto>();
    	dtos.add(request);
    	createPrices(id, dtos, false);
	}
	
	public void createPrices (long id, ArrayList<ProductPriceDto> request, boolean activate) throws MyRetailException
	{
		repository.savePrice(id, request, activate);
	}
}

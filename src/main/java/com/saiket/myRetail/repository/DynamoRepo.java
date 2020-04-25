package com.saiket.myRetail.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.saiket.myRetail.dao.ProductPriceDao;
import com.saiket.myRetail.dto.PriceUpdateRequestDto;
import com.saiket.myRetail.dto.ProductPriceDto;
import com.saiket.myRetail.exceptions.MyRetailException;

@Repository
public class DynamoRepo
{
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	
	private static Logger LOGGER = LoggerFactory.getLogger(DynamoRepo.class);
	
	public List<ProductPriceDao> getProducPrices(long id) throws MyRetailException 
	{
		ProductPriceDao dao = new ProductPriceDao();
		dao.setProductId(id);
		
		try
		{
			DynamoDBQueryExpression<ProductPriceDao> queryExpression = new DynamoDBQueryExpression<ProductPriceDao>();
			
			queryExpression.withHashKeyValues(dao);

			List<ProductPriceDao> itemList = dynamoDBMapper.query(ProductPriceDao.class, queryExpression);
			
			return itemList;
		}
        catch (Exception e) 
        { 
        	LOGGER.error(e.getMessage(), e.toString());
            throw new MyRetailException("Price Not Found!");
        } 
	}
	
	public List<ProductPriceDao> getCurrentPrice(long id, String curr) throws MyRetailException 
	{
		ProductPriceDao dao = new ProductPriceDao();
		dao.setProductId(id);
		
		try
		{
			DynamoDBQueryExpression<ProductPriceDao> queryExpression = new DynamoDBQueryExpression<ProductPriceDao>();
			
			
			Map<String, Condition> rangeKeyConditions = new HashMap<>();
			rangeKeyConditions.put("currency", new Condition()
				                 .withComparisonOperator(ComparisonOperator.EQ)
				                 .withAttributeValueList(new AttributeValue().withS(curr)));
			
			HashMap<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
			eav.put(":v_active",  new AttributeValue().withN("1"));
		
			String filterExpression = "active = :v_active";
		
		
			queryExpression.withHashKeyValues(dao)
						.withFilterExpression(filterExpression)
						.withExpressionAttributeValues(eav)
						.setRangeKeyConditions(rangeKeyConditions);

			List<ProductPriceDao> itemList = dynamoDBMapper.query(ProductPriceDao.class, queryExpression);
			
			return itemList;
		}
        catch (Exception e) 
        { 
        	LOGGER.error(e.getMessage(), e.toString());
            throw new MyRetailException("Price Not Found!");
        } 
	}
	
	public void savePrice(long id, ArrayList<ProductPriceDto> request, boolean activate) throws MyRetailException 
	{
		
		try
		{
			ArrayList<ProductPriceDao> daos =  new ArrayList<ProductPriceDao>();
			
			request.forEach( price -> {
				ProductPriceDao dao = new ProductPriceDao(id);
				dao.setActive(activate);
				dao.setCurrency(price.getCurrency_code());
				dao.setPrice(price.getValue());
				daos.add(dao);
			});
	
			dynamoDBMapper.batchSave(daos);
		}
	    catch (Exception e) 
	    { 
	    	LOGGER.error(e.getMessage(), e.toString());
	        throw new MyRetailException("Price Not Saved!");
	    } 
	}
	
	public boolean updatePrice(long id, PriceUpdateRequestDto request) throws MyRetailException
	{
		ArrayList<ProductPriceDao> daos =  new ArrayList<ProductPriceDao>();

		try
		{
			ProductPriceDao newPriceDao = new ProductPriceDao(id);
			newPriceDao.setActive(request.isActivate());
			newPriceDao.setCurrency(request.getCurrency_code());
			newPriceDao.setPrice(request.getValue());
			daos.add(newPriceDao);

			// Deactivate previous prices if the new price need to be current 
			if(request.isActivate())
			{
				List<ProductPriceDao> activeDaos =  getCurrentPrice(id, request.getCurrency_code());
				activeDaos.forEach(dao -> {
						dao.setActive(false);
						daos.add(dao);
				});
			}
			
			dynamoDBMapper.batchSave(daos);
			return true;
		}
	    catch (Exception e) 
	    { 
	    	LOGGER.error(e.getMessage(), e.toString());
	        throw new MyRetailException("Price Not Updated!");
	    } 
	}
}

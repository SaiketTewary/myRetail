package com.saiket.myRetail.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ListTablesResult;
import com.saiket.myRetail.dao.ProductPriceDao;
import com.saiket.myRetail.dto.PriceUpdateRequestDto;
import com.saiket.myRetail.dto.ProductPriceDto;

@Repository
public class DynamoRepo implements IRepository
{

	@Autowired
	private AmazonDynamoDB amazonDynamoDB;
	@Autowired
	private DynamoDBMapper dynamoDBMapper;
	
	
	public String listTable()
	{
	
		StringBuilder sb = new StringBuilder();
		
        ListTablesResult tables = amazonDynamoDB.listTables();
        List<String> iterator = tables.getTableNames();

        iterator.forEach(t ->{
        	sb.append(t);
        });
		
        return sb.toString();
	}
	
	public List<ProductPriceDao> getCurrentPrice(long id, String curr)
	{
		ProductPriceDao dao = new ProductPriceDao();
		dao.setProductId(id);
		
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
	
	public List<ProductPriceDao> getPriceDao(long productId, String priceId)
	{
		ProductPriceDao dao = new ProductPriceDao();
		dao.setProductId(productId);
		dao.setPriceId(priceId);
		
		DynamoDBQueryExpression<ProductPriceDao> queryExpression = new DynamoDBQueryExpression<ProductPriceDao>();
		
			
		Map<String, Condition> rangeKeyConditions = new HashMap<>();
		rangeKeyConditions.put("SK", new Condition()
			                 .withComparisonOperator(ComparisonOperator.EQ)
			                 .withAttributeValueList(new AttributeValue().withS(priceId)));
			
		queryExpression.withHashKeyValues(dao)
					.setRangeKeyConditions(rangeKeyConditions);

		List<ProductPriceDao> itemList = dynamoDBMapper.query(ProductPriceDao.class, queryExpression);
		
		return itemList;
	}
	
	public void savePrice(long id, ArrayList<ProductPriceDto> request, boolean activate)
	{
		
		ArrayList<ProductPriceDao> daos =  new ArrayList<ProductPriceDao>();
		
		request.forEach( price -> {
			ProductPriceDao dao = new ProductPriceDao(id);
			dao.setActive(activate);
			dao.setCurrency(price.getCurrency_code());
			dao.setPrice(price.getValue());
			daos.add(dao);
		});

		List<FailedBatch> result = dynamoDBMapper.batchSave(daos);
	}
	
	public boolean updatePrice(long id, PriceUpdateRequestDto request)
	{
		List<ProductPriceDao> daos =  getPriceDao(id, request.getPriceId());
		if(daos.isEmpty())
			return false;
		
		daos.forEach(dao -> {
			dao.setCurrency(request.getPrice().getCurrency_code().toUpperCase());
			dao.setPrice(request.getPrice().getValue());
			dao.setActive(request.isActivate());
		});
		
		if(request.isActivate())
		{
			List<ProductPriceDao> activeDaos =  getCurrentPrice(id, request.getPrice().getCurrency_code());
			activeDaos.forEach(dao -> {
				
				if(!dao.getPriceId().equals(request.getPriceId()))
				{
					dao.setActive(false);
					daos.add(dao);
				}
			});
		}
		
		List<FailedBatch> result = dynamoDBMapper.batchSave(daos);
		return true;
	}
}

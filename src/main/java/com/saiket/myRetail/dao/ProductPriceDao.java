package com.saiket.myRetail.dao;
import java.time.Instant;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.saiket.myRetail.dto.PriceDto;
import com.saiket.myRetail.dto.ProductDto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@DynamoDBTable(tableName="products_price_table")
public class ProductPriceDao 
{
	@DynamoDBHashKey(attributeName="PK")
    private long productId;
	
	@DynamoDBRangeKey(attributeName="SK")
	@DynamoDBAutoGeneratedKey
    private String priceId;
	
    private double price;
    
    @DynamoDBIndexRangeKey( localSecondaryIndexName = "LSI-ALL")
    private String currency;
    
    private boolean active = false;
    
    private long created;
    
    public ProductPriceDao()
    {
    }
    
    public ProductPriceDao(long Id)
    {
    	this.productId = Id;
    	this.created = Instant.now().toEpochMilli();
    }
    
    public static ProductPriceDao toPriceDao(ProductDto dto) 
    {
    	ProductPriceDao dao = new ProductPriceDao(dto.getId());    	
    	dao.setCreated(Instant.now().toEpochMilli());
    	
    	return dao;
    }
    
    public PriceDto toPriceDto()
    {
    	PriceDto dto = new PriceDto();
    	dto.setProductId(this.productId);
    	dto.setPriceId(this.priceId);
    	dto.setCurrency(this.currency);
    	dto.setPrice(this.price);
    	dto.setActive(this.active);
    	dto.setCreated(this.created);
    	
    	return dto;
    }
}

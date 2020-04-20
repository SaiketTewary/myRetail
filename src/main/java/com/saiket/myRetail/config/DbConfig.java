package com.saiket.myRetail.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.client.builder.AwsClientBuilder;

@Configuration
public class DbConfig 
{
    @Value("${amazon.dynamodb.endpoint}")
    private String amazonDynamoDBEndpoint;
    
    @Value("${amazon.aws.accesskey}")
    private String amazonAWSAccessKey;
 
    @Value("${amazon.aws.secretkey}")
    private String amazonAWSSecretKey;
    
   
    @Bean
    public AmazonDynamoDB amazonDynamoDB() 
    {
    	if(!amazonDynamoDBEndpoint.isEmpty())
    	{
            return AmazonDynamoDBClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(amazonDynamoDBEndpoint, "us-east-1"))
                    .withCredentials(amazonAWSCredentialsProvider())
                    .build();    		
    	}

    	return AmazonDynamoDBClientBuilder.standard().build();
    }

    
    @Bean
    public DynamoDBMapper dynamoDBMapper() 
    {
    	DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDB());
    	return mapper;
    }
    
	public AWSCredentialsProvider amazonAWSCredentialsProvider() {
	    return new AWSStaticCredentialsProvider(amazonAWSCredentials());
	}
   
    public AWSCredentials amazonAWSCredentials() {
        return new BasicAWSCredentials(
          amazonAWSAccessKey, amazonAWSSecretKey);
    }
}

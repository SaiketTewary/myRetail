package com.saiket.myRetail.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
    @Value("${amazon.dynamodb.endpoint:Unknown}")
    private String amazonDynamoDBEndpoint;
    
    @Value("${amazon.aws.accesskey:Unknown}")
    private String amazonAWSAccessKey;
 
    @Value("${amazon.aws.secretkey:Unknown}")
    private String amazonAWSSecretKey;
    
    @Autowired
    private Environment environment;
    
    
    @Bean
    public AmazonDynamoDB amazonDynamoDB() 
    {    	
    	if(Arrays.stream(environment.getActiveProfiles()).anyMatch(
    			   env -> (env.equalsIgnoreCase("dev"))) && !amazonDynamoDBEndpoint.equals("Unknown"))
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

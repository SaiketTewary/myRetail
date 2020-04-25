# myRetail
A Serverless POC for myRetail

	myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps. 
	The goal for this exercise is to create an end-to-end Proof-of-Concept for a products API, which will aggregate product data from multiple sources and return it as JSON to the caller. 
	Your goal is to create a RESTful service that can retrieve product and price details by ID. The URL structure is up to you to define, but try to follow some sort of logical convention.
	Build an application that performs the following actions: 
	•	Responds to an HTTP GET request at /products/{id} and delivers product data as JSON (where {id} will be a number. 
	•	Example product IDs: 15117729, 16483589, 16696652, 16752456, 15643793) 
	•	Example response: {"id":13860428,"name":"The Big Lebowski (Blu-ray) (Widescreen)","current_price":{"value": 13.49,"currency_code":"USD"}}
	•	Performs an HTTP GET to retrieve the product name from an external API. (For this exercise the data will come from redsky.target.com, but let’s just pretend this is an internal resource hosted by myRetail) 
	•	Example: http://redsky.target.com/v2/pdp/tcin/13860428?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics
	•	Reads pricing information from a NoSQL data store and combines it with the product id and name from the HTTP request into a single response. 
	•	BONUS: Accepts an HTTP PUT request at the same path (/products/{id}), containing a JSON request body similar to the GET response, and updates the product’s price in the data store. 
	
# Solution

Currently Hosted Solution running in AWS in below URL

	https://o3h72flfve.execute-api.us-east-1.amazonaws.com/Prod/products/

## APIs

- To setup test data
	
	GET /products/Setup
	
	Will return ProductId's created to test out the Service
	
- To get a product Price for a specific currency. Default it will return the USD price for the product and if the product id is available in Redsky. 
  If a prodcut id isn't available in Redsky , it will not return price record .
  Try product ID - 13860428
	
	GET /products/{id}
	
	Request :
	
	id - ProductId	
	Optional Body
	PriceRequestDto
	{
		String  currencyCode -  This defaults to "USD"
	}
	
	Example :
	
	{
		"currencyCode" : "USD"
	}

	Response :
	{
		id
		name
		current_price
			{
				currency_code
				value
			}
	
	}	
- To get all Price data for a specific Product.

	GET /prices/{id}
	
	Request : 
	id - ProductId
	

	Response :
	[
		{
	   	 	productId
	    	priceId		
	    	price	    
	    	currency	    
	    	active	    
	    	created
		}
	]

- To Update a product Price. By Default it will set the new price to be current 

	PUT /products/{id}
	
	Request : 
	id - ProductId
	
	Request Body :
	{	
		boolean activate
		String currency_code
		double value
	}	

	Example :
	
	{	
		"activate" : True
		"currency_code" : "USD"
		"value": 25.5
	}
	

## Getting Started

These instructions will get you a copy of the project up and running on your local machine and in AWS cloud for development and testing purposes.


### Prerequisites

1. Setup AWS CLI (AWS CLI version 2) - https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html

2. Configure aws profile - https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-configure.html#cli-quick-configuration
	
	To Configure default profile
	$ aws configure
	AWS Access Key ID [None]: <Your_Access_Key>
	AWS Secret Access Key [None]: <Your_Secret_Key>
	Default region name [None]: <Your_Region>
	Default output format [None]: json

	To Configure separate profile
	
	$ aws configure --profile <PROFILE_NAME>
	AWS Access Key ID [None]: <Your_Access_Key>
	AWS Secret Access Key [None]: <Your_Secret_Key>
	Default region name [None]: <Your_Region>
	Default output format [None]: text

3. Setup GIT - https://git-scm.com/download/win

4. Setup Maven - https://maven.apache.org/guides/getting-started/windows-prerequisites.html


### Setting up and running in Local System

1. Startup Local Dynamodb in docket
	
	docker run -p 8000:8000 amazon/dynamodb-local -jar DynamoDBLocal.jar -sharedDb

2. Create the necessary table in local Dynamodb

	aws dynamodb create-table --table-name products_price_table --attribute-definitions AttributeName=PK,AttributeType=N AttributeName=SK,AttributeType=S AttributeName=currency,AttributeType=S --key-schema AttributeName=PK,KeyType=HASH AttributeName=SK,KeyType=RANGE --local-secondary-indexes "[{\"IndexName\": \"LSI-ALL\", \"KeySchema\":[{\"AttributeName\":\"PK\",\"KeyType\":\"HASH\"},{\"AttributeName\":\"currency\",\"KeyType\":\"RANGE\"}],\"Projection\":{\"ProjectionType\":\"ALL\"}}]" --billing-mode PAY_PER_REQUEST --endpoint-url http://localhost:8000 --profile <PROFILE_NAME>
	
	to use your default aws profile ignore --profile <PROFILE_NAME>

3. Download the source code from github

	https://github.com/SaiketTewary/myRetail.git
	
4. got to the project directory and Run below commands to build , package and run the application in your local system

	mvn clean
	mvn package -P dev
	cd target
	java -Dspring.profiles.active=dev -jar myRetail-1.0.0-SNAPSHOT.jar

5. At this point you will have the service running in your local machine. Run below API to setup data

	http://localhost:8080/products/Setup
	
Not you can use any of the APIs from above to test the local system


### Setting up and running in AWS Cloud

1. Run below command to Clean , Build and package the application to deploy in AWS cloud 

	mvn clean
	mvn package

2. Create an S3 bucket

	aws s3 mb s3://<BUCKET_NAME> --profile <PROFILE_NAME>

3. Create the SAM package to deploy

	aws cloudformation package --template-file sam.yaml --output-template-file target/output-sam.yaml --profile <PROFILE_NAME> --s3-bucket <BUCKET_NAME>

4. Deploy the package 

	aws cloudformation deploy --template-file <PATH_TO_YOUR_PROJECT>\myRetail\target\output-sam.yaml --stack-name <STACK_NAME> --capabilities CAPABILITY_IAM --profile <PROFILE_NAME>
	
5. Log into AWS console and Find your stack in CouldFormation. From the output you will find the Deployed application URL


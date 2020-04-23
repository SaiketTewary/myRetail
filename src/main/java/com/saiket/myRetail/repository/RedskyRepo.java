package com.saiket.myRetail.repository;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiket.myRetail.exceptions.RedskyException;

@Repository
public class RedskyRepo
{

    private static final String REDSKY_URL = "https://redsky.target.com/v2/pdp/tcin/";
    private static final String EXCLUDES = "?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics";
    private static Logger LOGGER = LoggerFactory.getLogger(RedskyRepo.class);
    
    public String getProductTitle(long id) throws RedskyException 
    {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", id + "");

        ObjectMapper mapper = new ObjectMapper();

        String productUrl = REDSKY_URL + id + EXCLUDES;

        try 
        {
            ResponseEntity<String> response = restTemplate.getForEntity(productUrl, String.class, uriVariables);
            Map<String, Map> info = mapper.readValue(response.getBody(), Map.class);

            Map<String, Map> productMap = info.get("product");
            Map<String, Map> itemMap = productMap.get("item");
            Map<String, String> prodDescrMap = itemMap.get("product_description");
            String title = prodDescrMap.get("title");

            return title;
        } 
        catch (Exception e) 
        { 
        	LOGGER.error(e.getMessage(), e.toString());
            throw new RedskyException("Product Not Found!");
        }
    }
}

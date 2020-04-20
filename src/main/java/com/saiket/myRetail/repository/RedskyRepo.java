package com.saiket.myRetail.repository;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saiket.myRetail.exceptions.RedskyException;

@Repository
public class RedskyRepo implements IRepository
{

    @Value("${external.redsky.url}")
    private String redskyUrl;
    
    private static final String EXCLUDES = "?excludes=taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics";
    
    
    public String getProductTitle(long id) throws RedskyException 
    {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("id", id + ""); // cast id to string

        ObjectMapper mapper = new ObjectMapper();

        String productUrl = redskyUrl + id + EXCLUDES;

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
            throw new RedskyException("product not found");
        }
    }
}

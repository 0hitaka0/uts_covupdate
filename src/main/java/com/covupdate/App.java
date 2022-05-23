package com.covupdate;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONObject;

public class App {
    public static String suffix(long count ) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%." + ( exp > 1 ? 2 : 0 ) + "f%s", count / Math.pow(1000, exp), "RbJtMy".substring((exp-1)*2, exp*2));
    }
    
    public static void main(String[] args) throws IOException {
       
        // TODO code application logic here
        CloseableHttpClient httpClient = HttpClients.createDefault();
        System.out.println("Update status Covid-19 Indonesia hari ini");        
        System.out.println("=========================================");
        try {

            HttpGet request = new HttpGet("https://api.covid19api.com/dayone/country/indonesia");
            request.addHeader("accept", "application/json");

            CloseableHttpResponse response = httpClient.execute(request);
            
            try {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String json = IOUtils.toString(entity.getContent());
                    JSONArray array = new JSONArray(json);
                    JSONObject object_today = array.getJSONObject(array.length() - 1);
                    JSONObject object_yesterday = array.getJSONObject(array.length() - 2);
                    
                    System.out.println("Terkonfirmasi \t: " + (Integer.parseInt(object_today.get("Confirmed").toString()) - Integer.parseInt(object_yesterday.get("Confirmed").toString())) + " Kasus");
                    System.out.println("Tot. kasus \t: " + suffix(Integer.parseInt(object_today.get("Confirmed").toString())));
                    System.out.println("Meninggal \t: " + (Integer.parseInt(object_today.get("Deaths").toString()) - Integer.parseInt(object_yesterday.get("Deaths").toString())));
                    System.out.println("Tot. meninggal \t: " + suffix(Integer.parseInt(object_today.get("Deaths").toString())));
                    DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    OffsetDateTime dateTime = OffsetDateTime.parse(object_today.get("Date").toString());
                    System.out.println("Terakhir diperbarui " + dateTime.format(dayFormatter));

                }
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }  
}

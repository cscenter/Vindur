package ru.csc.vindur.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Andrey Kokorev
 *         Created on 11.05.2015.
 */
public class ServiceTest
{
    public static void main(String[] args) throws Exception
    {
        String toInsert = "{\"documents\": [\n" +
                "        {\"values\": {\"Int\" :[1], \"Str\": [\"Petya\", \"Lesha\"], \"Hr\" :[\"root\"]}},\n" +
                "        {\"values\": {\"Int\" :[2], \"Str\": [\"Vasya\"], \"Hr\" :[\"left\"]}},\n" +
                "        {\"values\": {\"Int\" :[3], \"Str\": [\"Alex\", \"Nikita\"], \"Hr\" :[\"right\"]}},\n" +
                "        {\"values\": {\"Str\": [\"I can not be found using another attribute\"]}}\n" +
                "    ]}";

        String toModify1 = "{\"node\":\"root\"}";
        String toModify2 = "{\"node\":\"left\",  \"parent\":\"root\"}";
        String toModify3 = "{\"node\":\"right\", \"parent\":\"root\"}";

        String toSearch1 = "{\"query\": [ {\"attribute\":\"Int\", \"from\":0, \"to\":10}, {\"attribute\":\"Str\", \"from\":\"Petya\"}]}";
        String toSearch2 = "{\"query\": [ {\"attribute\":\"Hr\", \"from\":\"root\"}]}";
        String toSearch3 = "{\"query\": [ {\"attribute\":\"Hr\", \"from\":\"left\"}]}";
        String toSearch4 = "{\"query\": [ {\"attribute\":\"Int\", \"from\":0, \"to\":100000}]}";

        String urlInsert = "http://localhost:8080/insert";
        String urlModify = "http://localhost:8080/storage/Hr/";
        String urlSearch = "http://localhost:8080/search";


        System.out.println(send(toModify1, urlModify));
        System.out.println(send(toModify2, urlModify));
        System.out.println(send(toModify3, urlModify));
        System.out.println(send(toInsert, urlInsert));
        System.out.println(send(toSearch1, urlSearch));
        System.out.println(send(toSearch2, urlSearch));
        System.out.println(send(toSearch3, urlSearch));
        System.out.println(send(toSearch4, urlSearch));
    }

    private static String send(String body, String targetURL) {
        URL url;
        HttpURLConnection connection = null;
        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(body.getBytes().length));

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (body);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }

    }
}

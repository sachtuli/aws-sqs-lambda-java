package com.st.aws.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventProcessor { // implements RequestHandler<SQSEvent, Void> {

    static String myJson;

    static {
        try {
            myJson = new Scanner(new File("src/main/resources/SQS_Event.json")).useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        handleRequest(myJson);
    }

    static final Logger log = Logger.getLogger("SQSQueueConsumer");
    static Gson gson = new Gson();

    // @Override
    public static void handleRequest(String event) { // handleRequest(SQSEvent event, Context context) {
        String geo_nums = "";
        try {
            //for (SQSEvent.SQSMessage msg : event.getRecords()) {
            // This will log the consumed message into the AWS cloudwatch log.
            log.log(Level.INFO, event);
            JsonObject Event = gson.fromJson(event, JsonObject.class);
            if (Event != null) {
                if (Event.has("Records"))
                    log.info("message parsed" + Event);
                JsonArray Records = (Event.get("Records")).getAsJsonArray();
                for (JsonElement rec : Records) {
                    JsonObject Record = rec.getAsJsonObject();
                    String body = Record.get("body").getAsString();
                    log.log(Level.INFO, "Event Body is : " + body);
                    JsonObject msgBody = gson.fromJson(body, JsonObject.class);
                    String relationshipId = ((msgBody.get("reqInfo")).getAsJsonObject().get(
                            "relationshipId")).toString();
                    String geoList = ((msgBody.get("extractInfo")).getAsJsonObject().get("geoList")).toString();
                    Pattern p = Pattern.compile("-?\\d+");
                    Matcher m = p.matcher(geoList);
                    while (m.find()) {
                        geo_nums = geo_nums.concat(m.group().concat(","));
                    }
                    geo_nums = geo_nums.substring(0, geo_nums.length() - 1);
                    System.out.println(relationshipId + " " + geo_nums);
                    boolean res = ModifyXMLDOM.XmlProcessor(relationshipId, geo_nums);
                    System.out.println(res);
                    if (!res) {
                        throw new RuntimeException("Xml Processing failed");
                    }
                }
            }
        } catch (Exception ex) {
            log.log(Level.WARNING, "Exception occurred while handling request.", ex);
        }
        log.log(Level.INFO, "Lambda Handler processing completed.");
    }
}
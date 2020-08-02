package com.intuit.interview.models;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
class FixDateSerializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectCodec oc = jp.getCodec();
        JsonNode node = oc.readTree(jp);
        //Standard formats to parse
        DateTimeFormatter parser = DateTimeFormatter.ofPattern("[M/d/yyyyHH:mm][M/dd/yyyyHH:mm][MM/dd/yyyyHH:mm]");
        String timeDate = node.asText();
        //Looking for the last slash before the year
        int lastRightSlash = node.asText().lastIndexOf("/");
        //Checking if hour will be with pattern of 3 instead of 03
        if(node.asText().length()-lastRightSlash == 9){
            //Will take the time and add leading zero
            String timeSubstring = node.asText().substring(node.asText().length()-4);
            timeDate = timeDate.replace(timeSubstring,"0"+timeSubstring);
        }
        return LocalDateTime.parse(timeDate, parser);
    }
}

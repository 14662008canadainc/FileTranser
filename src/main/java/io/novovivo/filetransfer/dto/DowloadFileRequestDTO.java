package io.novovivo.filetransfer.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

/**
 * @Author: Zuochao(Edward) Dou
 */

public class DowloadFileRequestDTO {
    @Getter
    @Setter
    private String filename;

    @Getter
    @Setter
    private String type;

    @Setter
    @Getter
    private String passcode;

    @Override
    public String toString(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}

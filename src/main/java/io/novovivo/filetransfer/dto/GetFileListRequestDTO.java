package io.novovivo.filetransfer.dto;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Zuochao(Edward) Dou
 */

public class GetFileListRequestDTO implements Serializable {
    @Getter
    @Setter
    private String type;
}

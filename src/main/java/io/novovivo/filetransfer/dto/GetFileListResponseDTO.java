package io.novovivo.filetransfer.dto;

import io.novovivo.filetransfer.model.ChunkedFile;
import java.io.Serializable;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * @Author: Zuochao(Edward) Dou
 */

public class GetFileListResponseDTO  implements Serializable {
    @Getter
    @Setter
    private List<ChunkedFile> fileList;

}

package com.sora.web.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * @author Sora5175
 * @date 2019/6/17 14:29
 */
@Data
@Component
public class FileVO {
    private String fileName;

    private InputStream inputStream;
}

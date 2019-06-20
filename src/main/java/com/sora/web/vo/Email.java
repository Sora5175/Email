package com.sora.web.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * @author Sora5175
 * @date 2019/6/15 19:21
 */
@Data
@Component
public class Email {
    String username;

    String url;

    String subject;

    String from;

    String content;

    String sendTime;

    List<File> fileList;

    List<FileVO> fileVOList;
}

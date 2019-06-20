package com.sora.web.vo;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @author Sora5175
 * @date 2019/6/15 11:02
 */
@Data
@Component
public class Status {

    private String message;

    private String location;
}

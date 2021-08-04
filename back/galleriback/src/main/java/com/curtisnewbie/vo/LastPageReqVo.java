package com.curtisnewbie.vo;

import lombok.Data;

/**
 * Next page request vo
 *
 * @author yongjie.zhuang
 */
@Data
public class LastPageReqVo {

    /**
     * Page limit
     */
    private Integer limit;

    /**
     * Last image id
     */
    private Integer lastId;

}

package com.mifu.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Auther: mifu
 * 定义了返回前端的字段
 * @Date: 2022/07/03 15:20
 * @Description: hdfs
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Hdfs {

    private String name;//文件名称

    private String date;//文件日期

    private String type;//文件类型

    private String size;//文件大小

    private String authority; //文件权限


}

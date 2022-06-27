package com.itheima.elasticsearchdemo.domain;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Goods {

    private Integer id;
    private String title;
    private Double price;
    private Integer stock;
    private Integer saleNum;
    private Date createTime;
    private String categoryName;
    private String brandName;
    private Map spec;

    @JSONField(serialize = false)//在转换JSON时，忽略该字段
    private String specStr;//接收数据库的信息 "{}"

}

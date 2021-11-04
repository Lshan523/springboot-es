package cn.vector.esdemo.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.util.List;

/**
 * Create By sSea
 * Date: 2021-11-04
 * <p>
 * 如果不使用@Field注解指定数据类型的话
 * Spring Data Elasticsearch框架会自动映射数据类型，如果es服务器中不存在索引的话会自动创建
 *
 *  Spring Data Elasticsearch框架会自动映射数据类型，如果es服务器中不存在索引的话会自动创建
 *   @Field(type=FieldType.Text, analyzer="ik_max_word")     表示该字段是一个文本，并作最大程度拆分，默认建立索引
 *   @Field(type=FieldType.Text,index=false)            表示该字段是一个文本，不建立索引
 *   @Field(type=FieldType.Date)                        表示该字段是一个文本，日期类型，默认不建立索引
 *   @Field(type=FieldType.Long)                        表示该字段是一个长整型，默认建立索引
 *   @Field(type=FieldType.Keyword)                     表示该字段内容是一个文本并作为一个整体不可分，默认建立索引
 *   @Field(type=FieldType.Float)                       表示该字段内容是一个浮点类型并作为一个整体不可分，默认建立索引
 */
@Data
@Document(indexName = "book")
public class Book {

    //@Id注解表明该字段是文档id
    @Id
    private Integer id;
    @Field(type=FieldType.Text, analyzer="ik_max_word")
    private String title;
    private BigDecimal price;
    @Field(type = FieldType.Keyword)
    private List<String> tag;
    
    public Book(Integer id, String title, BigDecimal price, List<String> tag) 
    {
        this.id = id;
        this.title = title;
        this.price = price;
        this.tag = tag;
    }

}

package cn.vector.esdemo.config;

import lombok.Data;
import lombok.ToString;

/**
 * @PACKAGE : cn.vector.esdemo.config
 * @User : Sea
 * @Date : 2021/11/4
 * @Date : 12:08
 **/
@Data
@ToString
public class MyElasticsearchClientProperties
{
    //host:port,host:port,host:port eg: 192.168.18.129:9200,192.168.18.130:9200,192.168.18.131:9200
    private String uris="192.168.18.129:9200";
    private String username;
    private String password;
    // 客户端和服务器建立连接超时时间
    private Integer connectTimeout = 10000; //10s
    // 从服务器端到客户端传输数据超时时间
    private Integer socketTimeout = 30000;
    // 从连接池中获取连接超时时间
    private Integer connectionRequestTimeout = 500;
    // 同时间正在使用的最多的连接数，默认值为 30
    private Integer maxConnTotal=50;
    // 针对一个域名同时间正在使用的最多的连接数，默认值为 10
    private Integer maxConnPerRoute =20;

}

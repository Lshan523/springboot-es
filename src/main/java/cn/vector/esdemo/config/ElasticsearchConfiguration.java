package cn.vector.esdemo.config;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @PACKAGE : cn.vector.esdemo.config
 * @User : Sea
 * @Date : 2021/11/4
 * @Date : 11:25
 **/
@Configuration
@PropertySource(value = "classpath:application.properties")
public class ElasticsearchConfiguration {

    @Bean(name = "esClientProperties")
    @Qualifier("esClientProperties")
    @ConfigurationProperties(prefix = "spring.elasticsearch")
    public MyElasticsearchClientProperties esClientProperties() {
        return new MyElasticsearchClientProperties();
    }


    /**
     * 不要调用close方法进行关闭
     * @return
     * @throws Exception
     */
    @Bean
    public RestHighLevelClient restHighLevelClient(@Qualifier("esClientProperties") MyElasticsearchClientProperties esClientProperties) throws Exception {
        if (StringUtils.isBlank(esClientProperties.getUris())) {
            throw new Exception("elasticsearch 节点信息获取失败，请在配置文件中配置");
        }
        List<HttpHost> hostList = new ArrayList<>();
        String[] endpoints = esClientProperties.getUris().split(",");
        for (String endpoint : endpoints) {
            String[] endpointSpit = endpoint.split(":");
            hostList.add(new HttpHost(endpointSpit[0], Integer.parseInt(endpointSpit[1])));
        }
        RestClientBuilder restClientBuilder = RestClient.builder(hostList.toArray(new HttpHost[0]));
        // timeout=5, max=100
        restClientBuilder.setDefaultHeaders(new Header[]{new BasicHeader("Keep-Alive", "timeout=1800, max=1000")});
        // 异步httpclient连接延时配置
        restClientBuilder.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {
            @Override
            public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
                requestConfigBuilder.setConnectTimeout(esClientProperties.getConnectTimeout());
                requestConfigBuilder.setSocketTimeout(esClientProperties.getSocketTimeout());
                requestConfigBuilder.setConnectionRequestTimeout(esClientProperties.getConnectionRequestTimeout());
                return requestConfigBuilder;
            }
        });
        // 异步httpclient连接数配置
        restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                httpClientBuilder.setMaxConnTotal(esClientProperties.getMaxConnTotal());
                httpClientBuilder.setMaxConnPerRoute(esClientProperties.getMaxConnPerRoute());
                if(StringUtils.isNotBlank(esClientProperties.getUsername())&&StringUtils.isNotBlank(esClientProperties.getPassword())){
                    httpClientBuilder.setDefaultCredentialsProvider(new PropertiesCredentialsProvider(esClientProperties));
                }
                return httpClientBuilder;
            }

        });
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(restClientBuilder);
        return restHighLevelClient;
    }



    // 添加认证 username password
    private static class PropertiesCredentialsProvider extends BasicCredentialsProvider {

        PropertiesCredentialsProvider(MyElasticsearchClientProperties properties) {
            if (org.springframework.util.StringUtils.hasText(properties.getUsername())) {
                Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(),
                        properties.getPassword());
                setCredentials(AuthScope.ANY, credentials);
            }
            Arrays.asList(properties.getUris().split(","))
                    .stream().map(this::toUri).filter(this::hasUserInfo)
                    .forEach(this::addUserInfoCredentials);
        }

        private URI toUri(String uri) {
            try {
                return URI.create(uri);
            }
            catch (IllegalArgumentException ex) {
                return null;
            }
        }

        private boolean hasUserInfo(URI uri) {
            return uri != null && org.springframework.util.StringUtils.hasLength(uri.getUserInfo());
        }

        private void addUserInfoCredentials(URI uri) {
            AuthScope authScope = new AuthScope(uri.getHost(), uri.getPort());
            Credentials credentials = createUserInfoCredentials(uri.getUserInfo());
            setCredentials(authScope, credentials);
        }

        private Credentials createUserInfoCredentials(String userInfo) {
            int delimiter = userInfo.indexOf(":");
            if (delimiter == -1) {
                return new UsernamePasswordCredentials(userInfo, null);
            }
            String username = userInfo.substring(0, delimiter);
            String password = userInfo.substring(delimiter + 1);
            return new UsernamePasswordCredentials(username, password);
        }
    }


}

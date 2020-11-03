## 使用HttpClient访问已启动的HttpServer，返回响应信息

pom.xml引入依赖

```
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
    <version>5.0.3</version>
</dependency>
```

```java
package java0.nio01;

import org.apache.hc.client5.http.ClientProtocolException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;

/**
 * HttpClient访问HttpServer，并返回响应信息
 */
public class HttpClient {
    public static void main(String[] args) throws Exception {

        try(final CloseableHttpClient httpClient = HttpClients.createDefault()){
            final HttpGet httpGet = new HttpGet("http://localhost:8801");
            System.out.println("Executing request: " + httpGet.getMethod() + ", " + httpGet.getUri());

            final HttpClientResponseHandler<String> responseHandler = new HttpClientResponseHandler<String>() {
                @Override
                public String handleResponse(final ClassicHttpResponse response) throws HttpException, IOException {
                    final int status = response.getCode();
                    if(status >= HttpStatus.SC_SUCCESS && status < HttpStatus.SC_REDIRECTION){
                        System.out.println("响应状态码: " + status);
                        final HttpEntity entity = response.getEntity();
                        try {
                            return entity != null ? EntityUtils.toString(entity) : null;
                        } catch (final ParseException e){
                            throw new ClientProtocolException(e);
                        }
                    } else {
                        throw new ClientProtocolException("非期待响应状态码: " + status);
                    }
                }
            };
            final String responseBody = httpClient.execute(httpGet, responseHandler);
            System.out.println("------------------------");
            System.out.println("响应结果: " + responseBody);
        }
    }
}

```


作为我自己的OutboundHandler，被浏览器请求localhost:8888端口访问后，去访问gateway-server-0.0.1-SNAPSHOT.jar的8088端口，然后返回响应结果。但是不知道如何将HttpClient获取到的响应结果返回给页面。

```
package io.github.kimmking.gateway.outbound.myhttpclient;


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
public class HttpClientOutboundHandler {

    private static String backendUrl;


    public HttpClientOutboundHandler(String backendUrl){
        this.backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;

        try(final CloseableHttpClient httpClient = HttpClients.createDefault()){
            //8888是HttpSever的端口(也就是网关的端口)，网关会去调用真实服务的地址,端口为8088
            final HttpGet httpGet = new HttpGet(backendUrl + "/api/hello");
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

        }catch(Exception e){
            e.printStackTrace();
        }


    }
}
```
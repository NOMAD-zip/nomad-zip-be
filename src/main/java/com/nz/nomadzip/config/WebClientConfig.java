package com.nz.nomadzip.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Map;

@Configuration
public class WebClientConfig {
    @Value("${open-api.api-key}")
    private String openApiKey;

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder
                .baseUrl("https://apis.data.go.kr/B551011/KorService2")
                .defaultHeaders(h -> h.add("Accept", "application/json"))
                .filter((request, next) -> {
                    // 1. 기존 request url 가져옴
                    URI old = request.url();

                    // 2. URI를 편집할 수 있는 빌더 객체 생성
                    UriComponentsBuilder ub  = UriComponentsBuilder.fromUri(old);

                    // 3. QueryString 가져와서 URI 편집하기
                    MultiValueMap<String, String> qs = ub.build(true).getQueryParams();
                    if (!qs.containsKey("serviceKey")) ub.queryParam("serviceKey", openApiKey);
                    if (!qs.containsKey("_type"))      ub.queryParam("_type", "json");
                    if (!qs.containsKey("MobileOS"))   ub.queryParam("MobileOS", "WEB");
                    if (!qs.containsKey("MobileApp"))  ub.queryParam("MobileApp", "NOMAD.zip");

                    // 4. 새로운 uri build 하고 인코딩하기
                    URI newUrl = ub.build(true).toUri();

                    // 5. 원래의 url을 수정한 url로 교체
                    ClientRequest newReq = ClientRequest.from(request).url(newUrl).build();

                    return next.exchange(newReq);
                })
                .build();
    }
}

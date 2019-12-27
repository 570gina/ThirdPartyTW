package com.gerrnbutton.controller;

import com.gerrnbutton.model.User;
import com.gerrnbutton.service.AuthorizationService;
import com.gerrnbutton.service.UserService;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

@Controller
public class WebController {
    @Autowired
    UserService userService;

    @Autowired
    AuthorizationService authorizationService;

    @GetMapping("/")
    public String index() {
       return "index";
    }
    @GetMapping("login")
    public String login_page() {
        return "login";
    }
    @GetMapping("register")
    public String register_page() {
        return "register";
    }

    @GetMapping("/redirect")
    public String redirect(String code, Principal principal) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, JSONException {
        String client = "THIRD_PARTY:secret";
        Base64 base64 = new Base64();
        String b64_basic = base64.encodeToString(client.getBytes());
        String url = "https://140.96.170.47:60107/oauth/token";
        RestTemplate restTemplate = getRestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + b64_basic);
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("code", code);
        params.add("redirect_uri", "http://localhost:8090/redirect");
        params.add("grant_type", "authorization_code");
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        authorizationService.insertAuthorization(response.getBody(), principal.getName());
        return "index";
    }

    @PostMapping("perform_register")
    public String perform_register(User user){
        userService.insertUser(user);
        return "redirect:/login?registered";
    }

    //SSL closed
    public RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                return true;
            }
        };
        SSLContext sslContext = org.apache.http.ssl.SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return restTemplate;
    }
}

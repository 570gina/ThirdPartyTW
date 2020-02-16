package com.gerrnbutton.service;

import com.gerrnbutton.entity.Authorization;
import com.gerrnbutton.entity.User;
import com.gerrnbutton.repository.AuthorizationRepository;
import com.gerrnbutton.repository.UserRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@Service
public class AuthorizationService {

    @Autowired
    private AuthorizationRepository authorizationRepository;
    @Autowired
    private UserRepository userRepository;

    public Authorization auth(String code, Principal principal) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
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
        Map map = response.getBody();
        map.put("username", getAuthUsername(restTemplate, map.get("access_token").toString()));
        return insertAuthorization(map, principal.getName());
    }

    public String getAuthUsername( RestTemplate restTemplate, String accessToken) {
        String url = "https://140.96.170.47:60107/checkToken";
        MultiValueMap<String, String> params= new LinkedMultiValueMap<String, String>();
        params.add("accessToken", accessToken);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, new HttpHeaders());
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        return response.getBody().get("username").toString();
    }

    public Authorization insertAuthorization(Map<String, Object> map, String username) {
        Authorization authorization = new Authorization();
        authorization.setAccessToken(map.get("access_token").toString());
        authorization.setNumber(map.get("username").toString());
        authorization.setRefreshToken(map.get("refresh_token").toString());
        String formattedDate = calculateTime((int)map.get("expires_in"));
        authorization.setExpiresIn(formattedDate);
        authorization.setTokenType(map.get("token_type").toString());
        User appUser = userRepository.findByUsername(username);
        authorization.setUser(appUser);
        authorizationRepository.save(authorization);
        return authorization;
    };

    public Authorization searchAuthByUser(User user){
        return authorizationRepository.findByUser(user);
    }

    //SSL closed
    private RestTemplate getRestTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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

    private String calculateTime(int datatime){
        Date date = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, datatime);
        date = c.getTime();
        String strDateFormat = "yyyy-MM-dd HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }
}

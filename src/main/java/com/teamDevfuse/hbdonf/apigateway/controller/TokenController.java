package com.teamDevfuse.hbdonf.apigateway.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.teamDevfuse.hbdonf.apigateway.domain.TokenDto;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("token")
public class TokenController {
    private final static Logger LOGGER = LoggerFactory.getLogger(TokenController.class);

    @Value("${firebase.key}")
    private String FIREBASE_KEY;

    @GetMapping("{uid}")
    public TokenDto getFirebaseUid(@PathVariable(name = "uid") String uid) throws FirebaseAuthException, ParseException {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithCustomToken?key=" + FIREBASE_KEY;
        String token = FirebaseAuth.getInstance().createCustomToken(uid);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = new HashMap<>();
        body.put("token", token);
        body.put("returnSecureToken", true);

        HttpEntity<?> entity = new HttpEntity<>(body, httpHeaders);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(response.getBody());
        JSONObject jsonObj = (JSONObject) obj;

        TokenDto tokenDto = new TokenDto();
        tokenDto.setIdToken((String) jsonObj.get("idToken"));

        return tokenDto;
    }
}

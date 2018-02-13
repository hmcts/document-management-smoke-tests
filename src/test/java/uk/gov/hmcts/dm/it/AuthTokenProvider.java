package uk.gov.hmcts.dm.it;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Map;

@Service
public class AuthTokenProvider {

//    private final String idamS2SBaseUri;
    private final String idamUserBaseUrl;

    private final String token;

    @Autowired
    public AuthTokenProvider(@Value("${base-urls.idam-s2s}") String idamS2SBaseUri,
                             @Value("${base-urls.idam-user}") String idamUserBaseUri,
                             @Value("${login.token}")String token
    ) {
//        this.idamS2SBaseUri = idamS2SBaseUri;
        this.idamUserBaseUrl = idamUserBaseUri;

        this.token = token;
        System.out.println("IDAM User URL - " + idamUserBaseUri);
        System.out.println("IDAM S2S URL - " + idamS2SBaseUri);
        System.out.println("JWT token - " + token);
    }

    public AuthTokens getTokens() {
//        String userToken = findUserToken(username, password);
        return new AuthTokens(token, "");
    }

//    private String findServiceToken() {
//        return RestAssured
//                .given().baseUri(idamS2SBaseUri)
//                .body("microservice=sscs")
//                .post("testing-support/lease?role=test&id=1&microservice=sscs")
//                .andReturn().asString();
//    }

    private String findUserToken(String username, String password) {
        final String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
        final Response authorization = RestAssured.given().baseUri(idamUserBaseUrl)
                .header("Authorization", "Basic " + encoded)
                .post("oauth2/authorize");
        authorization.then().statusCode(200);
        final Map<String, String> userResponse = authorization.andReturn().as(Map.class);
        return userResponse.get("access-token");
    }


    public class AuthTokens {
        private final String userToken;
        private final String serviceToken;

        public AuthTokens(String userToken, String serviceToken) {
            this.userToken = userToken;
            this.serviceToken = serviceToken;
        }

        public String getUserToken() {
            return userToken;
        }

//        public String getServiceToken() {
//            return serviceToken;
//        }

        @Override
        public String toString() {
            return "AuthTokens{" +
                    "userToken='" + userToken + '\'' +
                    ", serviceToken='" + serviceToken + '\'' +
                    '}';
        }
    }
}

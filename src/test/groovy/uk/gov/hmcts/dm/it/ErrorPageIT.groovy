package uk.gov.hmcts.dm.it

import io.restassured.http.ContentType
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import static org.hamcrest.Matchers.equalTo

/**
 * Created by matej on 27/10/2017.
 */
@RunWith(SpringRunner.class)
class ErrorPageIT extends BaseIT {

    @Before
    void setup() throws Exception {

    }

    @Test
    void "EP1 As an unauthenticated web user trying to access a document, receive HTML error page with 401"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
                .accept(ContentType.HTML)
                .expect()
                    .contentType(ContentType.HTML)
                    .body("html.head.title", equalTo("401 Error"))
                    .statusCode(401)
                .when()
                    .get(documentUrl)

    }

    @Test
    void "EP2 As an authenticated web user trying to access an unknown document, receive HTML error page with 404"() {

        givenRequest()
                .accept(ContentType.HTML)
                .expect()
                    .body("html.head.title", equalTo("404 Error"))
                    .statusCode(404)
                .when()
                    .get('documents/XXX')

    }

    @Test
    void "EP3 As an authenticated web user trying to access document/, receive HTML error page with 405"() {

        givenRequest()
                .accept(ContentType.HTML)
                .expect()
                .body("html.head.title", equalTo("405 Error"))
                .statusCode(405)
                .when()
                .get('documents/')
    }

    @Test
    void "EP4 As an authenticated web user trying to post no document, receive HTML error page with 415"() {

        givenRequest()
                .accept(ContentType.HTML)
                .expect()
                .body("html.head.title", equalTo("415 Error"))
                .statusCode(415)
                .when()
                .post('documents/')
    }

    @Test
    void "EP5 As an authenticated web user trying to post bad attachment, receive HTML error page with 415"() {

        givenRequest()
                .accept(ContentType.HTML)
                .multiPart("file", file(BAD_ATTACHMENT_1), MediaType.ALL_VALUE)
                .expect()
                .body("html.head.title", equalTo("422 Error"))
                .statusCode(422)
                .when()
                .post('documents/')
    }

    @Test
    void "EP6 As an authenticated web user but not the owner of the file, post the newer version of the file, receive HTML error page with 403"() {

        def url = createDocumentAndGetUrlAs()



        givenRequest()
                .accept(ContentType.HTML)
                .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
                .expect()
                .body("html.head.title", equalTo("403 Error"))
                .statusCode(403)
                .when()
                .post(url)
    }

}

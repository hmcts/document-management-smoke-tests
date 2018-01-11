package uk.gov.hmcts.dm.it

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import uk.gov.hmcts.dm.it.utilities.V1MediaTypes

import static org.hamcrest.Matchers.equalTo

/**
 * Created by pawel on 13/10/2017.
 */
@RunWith(SpringRunner.class)
class ReadContentVersionIT extends BaseIT {

    String documentUrl

    def documentVersion

    String documentVersionUrl

    String documentVersionBinaryUrl

    @Before
    public void setup() throws Exception {
        documentUrl = createDocumentAndGetUrlAs()
        documentVersion = createDocumentContentVersion documentUrl, ATTACHMENT_2
        documentVersionUrl = documentVersion.path('_links.self.href')
        documentVersionBinaryUrl = documentVersion.path('_links.binary.href')
    }

    @Test
    void "RCV1 As creator I read content version by URL"() {
        givenRequest()
            .expect()
                .statusCode(200)
                .contentType(V1MediaTypes.V1_HAL_DOCUMENT_CONTENT_VERSION_MEDIA_TYPE_VALUE)
                .body("originalDocumentName", equalTo(ATTACHMENT_2))
                .body("mimeType", equalTo(MediaType.TEXT_PLAIN_VALUE))
            .when()
                .get(documentVersionUrl)

    }


    @Test
    void "RCV2 As creator I read content version binary by URL"() {
        givenRequest()
            .expect()
                .statusCode(200)
                .contentType(MediaType.TEXT_PLAIN_VALUE)
                .body(equalTo("Attachment File 2 for test"))
            .when()
                .get(documentVersionBinaryUrl)

    }

    @Test
    void "RCV3 As a case worker I read content version by URL"() {
        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(documentVersionUrl)

    }

    @Test
    void "RCV4 As a probate case-worker I read content version binary by URL"() {
        givenRequest()
                .expect()
                .statusCode(200)
                .body(equalTo("Attachment File 2 for test"))
                .when()
                .get(documentVersionBinaryUrl)
    }

    @Test
    void "RCV5 As a cmc case-worker I can read content version binary by URL"() {
        givenRequest()
                .expect()
                .statusCode(200)
                .body(equalTo("Attachment File 2 for test"))
                .when()
                .get(documentVersionBinaryUrl)
    }

    @Test
    void "RCV6 As a sscs case-worker I can read content version binary by URL"() {
        givenRequest()
                .expect()
                .statusCode(200)
                .body(equalTo("Attachment File 2 for test"))
                .when()
                .get(documentVersionBinaryUrl)
    }

    @Test
    void "RCV7 As a divorce case-worker I can read content version binary by URL"() {
        givenRequest()
                .expect()
                .statusCode(200)
                .body(equalTo("Attachment File 2 for test"))
                .when()
                .get(documentVersionBinaryUrl)
    }

}

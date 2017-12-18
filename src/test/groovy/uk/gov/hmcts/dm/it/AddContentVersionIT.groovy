package uk.gov.hmcts.dm.it

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import uk.gov.hmcts.dm.it.utilities.V1MediaTypes

import static org.hamcrest.Matchers.equalTo

@RunWith(SpringRunner.class)
class AddContentVersionIT extends BaseIT {

    @Test
    void "ACV1 As authenticated user who is an owner POST a new version of the content to an existing document then expect 201"() {

        def documentURL = createDocumentAndGetUrlAs()

        def response = givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
        .expect().log().all()
            .statusCode(201)
            .contentType(V1MediaTypes.V1_HAL_DOCUMENT_CONTENT_VERSION_MEDIA_TYPE_VALUE)
            .body("originalDocumentName", equalTo(ATTACHMENT_1))
            .body("mimeType", equalTo(MediaType.TEXT_PLAIN_VALUE))
        .when()
            .post(documentURL)
            .thenReturn()

        def newVersionUrl = response.getHeader 'Location'

        givenRequest()
            .expect()
                .statusCode(200)
            .when()
                .get(newVersionUrl)

    }


    @Test
    void "ACV2 As authenticated user POST a new version of the content to a not existing document"() {

        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(404)
            .when()
                .post('/documents' + UUID.randomUUID())

    }

    @Test
    void "ACV3 As unauthenticated user POST a new version of the content to a not existing document"() {

        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(401)
            .when()
                .post('/documents' + UUID.randomUUID())

    }

    @Test
    void "ACV4 As unauthenticated user POST a new version of the content to an existing document"() {

        def url = createDocumentAndGetUrlAs()

        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(401)
            .when()
                .post(url)

    }

    @Test
    void "ACV5 As authenticated user who is an not an owner POST a new version of the content to an existing document"() {

        def url = createDocumentAndGetUrlAs()



        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(403)
            .when()
                .post(url)

    }

    @Test
    void "ACV6 As authenticated user who is not an owner and is a case worker"() {

        def url = createDocumentAndGetUrlAs()

        createCaseWorker

        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(403)
            .when()
                .post(url)

    }

    @Test
    void "ACV7 As authenticated user who is a case worker POST a new version of the content to a not existing document and expect 404"() {

        createCaseWorker

        givenRequest()
            .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
            .expect()
                .statusCode(404)
            .when()
                .post("documents/${UUID.randomUUID()}")

    }

    @Test
    void "ACV8 As an authenticated user and the owner I should not be able to upload multiple new content versions then expect 201"() {

        def documentURL = createDocumentAndGetUrlAs()
        def response = givenRequest()
                .multiPart("file", file(ATTACHMENT_1), MediaType.TEXT_PLAIN_VALUE)
                .multiPart("file", file(ATTACHMENT_2), MediaType.TEXT_PLAIN_VALUE)
                .multiPart("file", file(ATTACHMENT_3), MediaType.TEXT_PLAIN_VALUE)
                .expect().log().all()
                .statusCode(201)
                .contentType(V1MediaTypes.V1_HAL_DOCUMENT_CONTENT_VERSION_MEDIA_TYPE_VALUE)
                .body("originalDocumentName", equalTo(ATTACHMENT_1))
                .body("mimeType", equalTo(MediaType.TEXT_PLAIN_VALUE))
                .when()
                .post(documentURL)
                .thenReturn()

        def newVersionUrl = response.getHeader 'Location'

        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(newVersionUrl)

    }

    @Test
    void "ACV9 As an authenticated user and the owner I should be able to upload new version of different format"() {

        def documentURL = createDocumentAndGetUrlAs()
        def response = givenRequest()
                .multiPart("file", file(ATTACHMENT_4), MediaType.APPLICATION_PDF_VALUE)
                .expect().log().all()
                .statusCode(201)
                .contentType(V1MediaTypes.V1_HAL_DOCUMENT_CONTENT_VERSION_MEDIA_TYPE_VALUE)
                .body("originalDocumentName", equalTo(ATTACHMENT_4))
                .body("mimeType", equalTo(MediaType.APPLICATION_PDF_VALUE))
                .when()
                .post(documentURL)
                .thenReturn()

        def newVersionUrl = response.getHeader 'Location'

        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(newVersionUrl)

    }

    @Test
    void "ACV10 As an authenticated user and the owner I should not be able to upload exes"() {

        def documentURL = createDocumentAndGetUrlAs()
        givenRequest()
                .multiPart("file", file(BAD_ATTACHMENT_1), MediaType.ALL_VALUE)
                .expect().log().all()
                .statusCode(422)
                .when()
                .post(documentURL)
    }

    @Test
    void "ACV11 As an authenticated user and the owner I should not be able to upload zip"() {

        def documentURL = createDocumentAndGetUrlAs()
        givenRequest()
                .multiPart("file", file(BAD_ATTACHMENT_2), MediaType.ALL_VALUE)
                .expect().log().all()
                .statusCode(422)
                .when()
                .post(documentURL)
    }
}

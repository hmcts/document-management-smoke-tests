package uk.gov.hmcts.dm.it

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by pawel on 13/10/2017.
 */
@RunWith(SpringRunner.class)
class ReadDocumentIT extends BaseIT {

    @Test
    void "R1 As authenticated user who is an owner, can read owned documents"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(documentUrl)

    }

    @Test
    void "R2 As authenticated user who is an owner, but Accept Header is application/vnd.uk.gov.hmcts.dm.document.v10000+hal+json"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
            .header('Accept','application/vnd.uk.gov.hmcts.dm.document.v10000+hal+json')
            .expect()
                .statusCode(406)
            .when()
                .get(documentUrl)

    }

    @Test
    void "R3 As unauthenticated user I try getting an existing document and get 401"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
            .expect()
                .statusCode(401)
            .when()
                .get(documentUrl)

    }


    @Test
    void "R4 As unauthenticated user GET existing document and receive 401"() {

        givenRequest()
            .expect()
                .statusCode(401)
            .when()
                .get('/documents/XXX')

    }

    @Test
    void "R5 As authenticated user who is not an owner and not a case worker I can't access a document"() {
        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
            .expect()
                .statusCode(403)
            .when()
                .get(documentUrl)

    }


    @Test
    void "R6 As authenticated user who is not an owner and not a case worker GET existing document binary and see 403"() {

        def binaryUrl = createDocumentAndGetBinaryUrlAs()

        givenRequest()
            .expect()
                .statusCode(403)
            .when()
                .get(binaryUrl)

    }

    @Test
    void "R7 As authenticated user who is not an owner and is a case worker I can read not owned documents"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
            .expect()
                .statusCode(200)
            .when()
                .get(documentUrl)

    }

    @Test
    void "R8 As authenticated user GET document/xxx where xxx is not UUID"() {

        givenRequest()
            .expect()
                .statusCode(404)
            .when()
                .get('documents/xxx')
    }

    @Test
    void "R9 As authenticated user GET document/111 where 111 is not UUID"() {

        givenRequest()
                .expect()
                .statusCode(404)
                .when()
                .get('documents/111')
    }

    @Test
    void "R10 As authenticated user GET document/ where 111 is not UUID"() {

        givenRequest()
                .expect()
                .statusCode(405)
                .when()
                .get('documents/')
    }

    @Test
    void "R11 As authenticated user GET document/xxx where xxx is UUID but it doesn't exist in our BD"() {

        givenRequest()
            .expect()
                .statusCode(404)
            .when()
                .get('documents/' + UUID.randomUUID())

    }

    @Test
    void "R12 As unauthenticated user GET document that exists with jwt parameter appended to the document URL"() {

        def documentUrl = createDocumentAndGetUrlAs()

        def jwt = authToken()

        def response = givenRequest()
            .param("jwt", jwt)
            .redirects().follow(false)
            .expect()
                .statusCode(302)
            .when()
                .get(documentUrl).andReturn()

        def authToken = response.cookie('__auth-token')
        def newLocation = response.header('Location')

        givenRequest()
            .header('Authorization', authToken)
            .expect()
                .statusCode(200)
            .when()
                .get(newLocation)

    }


    @Test
    void "R13 As unauthenticated user GET document that does not exists with jwt parameter appended to the document URL"() {

        def jwt = authToken()

        def response = givenRequest()
                .param("jwt", jwt)
                .redirects().follow(false)
                .expect()
                .statusCode(302)
                .when()
                .get('/documents/xxx').andReturn()

        def authToken = response.cookie('__auth-token')
        def newLocation = response.header('Location')

        givenRequest()
                .header('Authorization', authToken)
                .expect()
                .statusCode(404)
                .when()
                .get(newLocation)

    }

    @Test
    void "R14 As authenticated user with a specific role I can access a document if its CLASSIFICATION is restricted and roles match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'RESTRICTED', ['caseworker']

        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(documentUrl)

    }

    @Test
    void "R15 As authenticated user with a specific role I can't access a document if its CLASSIFICATION is PRIVATE and roles match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PRIVATE', ['caseworker']

        givenRequest()
                .expect()
                .statusCode(403)
                .when()
                .get(documentUrl)

    }

    @Test
    void "R16 As authenticated user with a specific role I can access a document if its CLASSIFICATION is public and roles match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)

    }

    @Test
    void "R17 As authenticated user with a specific role I can access a document if its CLASSIFICATION is public and matches role"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['citizen', 'caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R18 As authenticated user with no role I cannot access a document if its CLASSIFICATION is public with no role"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', [null]

        givenRequest()
            .expect()
            .statusCode(403)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R19 As authenticated user with some role I can access a document if its CLASSIFICATION is public and roles does not match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', [null]

        givenRequest()
            .expect()
            .statusCode(403)
            .when()
            .get(documentUrl)

    }

    @Test
    void "R20 As authenticated user with no role (Tests by default sets role as citizen) I can access a document if its CLASSIFICATION is public and roles is citizen"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['citizen']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R21 As an owner I can access a document even if its CLASSIFICATION is private with no roles"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PRIVATE', [null]

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R22 As authenticated user with a specific role I can't access a document if its CLASSIFICATION is restricted and roles don't match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'RESTRICTED', ['caseworker']

        givenRequest()
                .expect()
                .statusCode(403)
                .when()
                .get(documentUrl)
    }

    @Test
    void "R23 As an Owner with no role I can access a document even if its CLASSIFICATION is private and role as caseworker"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PRIVATE', ['caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }
}

package uk.gov.hmcts.dm.it

import org.junit.Before
import org.junit.Ignore
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

        givenUnauthenticatedUsersRequest()
            .expect()
                .statusCode(401)
            .when()
                .get(documentUrl)

    }


    @Test
    void "R4 As unauthenticated user GET existing document and receive 401"() {

        givenUnauthenticatedUsersRequest()
            .expect()
                .statusCode(401)
            .when()
                .get('/documents/XXX')

    }

    @Test
    void "R5 As authenticated user who is not an owner and is a case worker I can read not owned documents"() {

        def documentUrl = createDocumentAndGetUrlAs()

        givenRequest()
            .expect()
                .statusCode(200)
            .when()
                .get(documentUrl)

    }

    @Test
    void "R6 As authenticated user GET document/xxx where xxx is not UUID"() {

        givenRequest()
            .expect()
                .statusCode(404)
            .when()
                .get('documents/xxx')
    }

    @Test
    void "R7 As authenticated user GET document/111 where 111 is not UUID"() {

        givenRequest()
                .expect()
                .statusCode(404)
                .when()
                .get('documents/111')
    }

    @Test
    void "R8 As authenticated user GET document/ where 111 is not UUID"() {

        givenRequest()
                .expect()
                .statusCode(405)
                .when()
                .get('documents/')
    }

    @Test
    void "R9 As authenticated user GET document/xxx where xxx is UUID but it doesn't exist in our BD"() {

        givenRequest()
            .expect()
                .statusCode(404)
            .when()
                .get('documents/' + UUID.randomUUID())

    }

    @Ignore ("This is not considered as a smoke test")
    @Test
    void "R10 As unauthenticated user GET document that exists with jwt parameter appended to the document URL"() {

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


    @Ignore ("This is not considered as a smoke test")
    @Test
    void "R11 As unauthenticated user GET document that does not exists with jwt parameter appended to the document URL"() {

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
    void "R12 As authenticated user with a specific role I can access a document if its CLASSIFICATION is restricted and roles match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'RESTRICTED', ['caseworker']

        givenRequest()
                .expect()
                .statusCode(200)
                .when()
                .get(documentUrl)

    }

    @Test
    void "R13 As authenticated user with a specific role I can access a document if its CLASSIFICATION is public and roles match"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)

    }

    @Test
    void "R14 As authenticated user with a specific role I can access a document if its CLASSIFICATION is public and matches role"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['citizen', 'caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R15 As authenticated user with no role (Tests by default sets role as citizen) I can access a document if its CLASSIFICATION is public and roles is citizen"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PUBLIC', ['citizen']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R16 As an owner I can access a document even if its CLASSIFICATION is private with no roles"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PRIVATE', [null]

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }

    @Test
    void "R17 As an Owner with no role I can access a document even if its CLASSIFICATION is private and role as caseworker"() {

        def documentUrl = createDocumentAndGetUrlAs ATTACHMENT_1, 'PRIVATE', ['caseworker']

        givenRequest()
            .expect()
            .statusCode(200)
            .when()
            .get(documentUrl)
    }
}

package uk.gov.hmcts.dm.it

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.junit4.SpringRunner

/**
 * Created by pawel on 13/10/2017.
 */
@RunWith(SpringRunner.class)
@Ignore("Not in release R1.1.0")
class DeleteDocumentIT extends BaseIT {

    @Test
    void "D1 For all users delete is not allowed"() {

        def documentUrl = createDocumentAndGetUrlAs()

        def documentUrl1 = createDocumentAndGetUrlAs()

        givenRequest()
            .expect()
                .statusCode(204)
            .when()
                .delete(documentUrl)

        givenRequest()
                .expect()
                .statusCode(204)
                .when()
                .delete(documentUrl)

        givenRequest()
                .expect()
                .statusCode(204)
                .when()
                .delete(documentUrl)

        givenRequest()
                .expect()
                .statusCode(204)
                .when()
                .delete(documentUrl1)

    }


}

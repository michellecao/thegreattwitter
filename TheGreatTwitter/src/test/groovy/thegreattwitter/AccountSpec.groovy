package thegreattwitter

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Account)
class AccountSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

//    void "test something"() {
//        expect:"fix me"
//            true == false
//    }

    void "test account"() {
        def account = new Account(accountName: "michelle", email: "caoxx521@umn.edu", password: "Password1234")

        expect:"true"
        account.save() != null
    }
}

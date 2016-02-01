package thegreattwitter

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Message)
class MessageSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "M1. Saving a message with a valid account and message text will succeed"() {
        Account account = new Account(accountName: 'Test', email: 'test@test.com', password: 'Test12345');
        def message = new Message (messageText: 'Message Text', account: account)
        expect:"Message saved"
        message.save() != null
    }


}

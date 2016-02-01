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


    def "test adding messages"() {
        when:
        //def account1 = new Account(accountName: "michelle", email: "caoxx521@umn.edu", password: "Password1234")
        def message = new Message(account: account, messageText : messageText)

        def result = message.save();

        then:
        result == expected

        where:

        description            | account | messageText | expected
        'account empty'        | null | 'first message on twitter ' | null
        'messageText empty'    | new Account(accountName: "michelle", email: "caoxx521@umn.edu", password: "Password1234")| ' ' | null
        'message longer than 40 characters' | new Account(accountName: "michelle", email: "caoxx521@umn.edu", password: "Password1234")| 'This is my fist twitter message ever, this is to test that if it will fail when the length of the message exceeds the limit specified' | null
    }

}

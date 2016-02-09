package thegreattwitter

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Message)
@Mock(Account)
class MessageSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    def "Saving a message with a valid account and message text will succeed"() {
        given:
        Account account = new Account(accountName: 'Test', email: 'test@test.com',
                password: 'Test12345', handle: '@Test');
        def message = new Message(messageText: 'Message Text', account: account)
        when:
        account.save()
        if (account.id) {
            message.save()
        }
        then: "Message saved"
        account.id
        !account.errors.hasErrors()
        message.id
        !message.errors.hasErrors()
    }

    def "Fails to save a message when required message text field is missing"() {
        given:
        Account account = new Account(accountName: 'Test', email: 'test@test.com', password: 'Test12345',
                handle: "@Test");
        def message = new Message(account: account)

        when:
        account.save()
        if (account.id) {
            message.save()
        }

        then:
        account.id
        !account.errors.hasErrors()
        !message.id
        message.hasErrors()
        message.errors.getFieldError('messageText')
    }

    def "Fails to save a message when account is missing"() {
        given:

        def message = new Message(messageText: 'Text')

        when:
        message.save()

        then:
        !message.id
        message.hasErrors()
        message.errors.getFieldError('account')
    }

    def "Message text is required to be non-blank and 40 characters or less "() {
        when:
        Account testAccount = new Account(accountName: 'Test', email: 'test@test.com', password: 'Test12345',
                handle: "@Test");
        testAccount.save()
        def result = null;
        if (testAccount.id) {
            def message = new Message(account: testAccount, messageText: messageText)
            result = message.save();

        }

        then:
        result == expected

        where:

        description                              | messageText                                                                                                                             | expected
        'empty message text'                     | null                                                                                                                                    | null
        'message text longer than 40 characters' | 'This is my fist twitter message ever, this is to test that if it will fail when the length of the message exceeds the limit specified' | null
        'blank message text'                     | '   '                                                                                                                                   | null

    }

}

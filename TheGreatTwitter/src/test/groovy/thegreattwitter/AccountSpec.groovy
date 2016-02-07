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

    void "A1. Saving an account with a valid handle, email, password and name will succeed"() {
        def account = new Account(accountName: 'michelle', email: 'caoxx521@umn.edu', password: 'Test12345', handle: 'accountHandle');
        expect: "saving occurs"
        account.save(flush: true) != null
        account.errors.errorCount == 0
        account.id != null
        account.getAccountName() == 'michelle'
        account.get(account.id).email == 'caoxx521@umn.edu'
        account.getPassword() == 'Test12345'
        account.getHandle() == 'accountHandle'
        account.getDateCreated() != null

    }

    def 'A2. Saving an account missing any of the required values of handle email, password and name will fail'() {
        when:
        def account = new Account(accountName: accountName, email: email, password: password, handle: handle,)
        def result = account.save()

        then:
        result == expected

        where:
        description           | accountName   | email           | password   | handle          | expected
        'AccountName missing' | null          | 'test@test.com' | 'Test1234' | 'accountHandel' | null
        'Email missing'       | 'TestAccount' | null            | 'Test1234' | 'accountHandel' | null
        'Invalid email'       | 'TestAccount' | 'testemail'     | 'Test1234' | 'accountHandel' | null
        'Password missing'    | 'TestAccount' | 'test@test.com' | ''         | 'accountHandel' | null
        'handle missing'      | 'TestAccount' | 'test@test.com' | 'Test1234' | ''              | null

    }
    //Passwords must be 8-16 characters and have
    // at least 1 number,
    // at least one lower-case letter,
    // at least 1 upper-case letter
    def 'A3. Saving an account with an invalid password will fail'() {
        when:
        def account = new Account(accountName: 'Test', email: 'test@test.com', password: password)
        def result = account.save()

        then:
        result == expected

        where:
        description                          | password             | expected
        'Password is less than 8 chars'      | 'Test123'            | null
        'Password is more than 16 chars'     | 'Test12345678912345' | null
        'Password without a digit'           | 'TestTestTest'       | null
        'Password without upper case letter' | 'test1234556777'     | null
        'Password without lower case letter' | 'TEST1234556777'     | null
        'Password without letters'           | '1234556777'         | null
        'Password with only special chars'   | '%^&*&*'             | null
        'Password is blank'                  | ''                   | null
        'Password is null'                   | null                 | null
    }

    def "test adding account with null values"() {
        when:
        def account = new Account(accountName: accountName, email: email, password: password, handle: handle)
        def result = account.save();

        then:
        result == expected

        where:
        description           | accountName | email              | password       | handle          | expected
        'accountName missing' | ' '         | 'caoxx521@umn.edu' | 'Password1234' | 'accountHandel' | null
        'email missing'       | 'michelle'  | ' '                | 'Password1234' | 'accountHandel' | null
        'password missing'    | 'michelle'  | ' '                | ' '            | 'accountHandel' | null
    }
}

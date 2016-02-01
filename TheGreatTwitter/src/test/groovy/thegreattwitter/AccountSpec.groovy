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
        def account = new Account(accountName: 'Test', email: 'test@test.com', password: 'Test12345');
        expect:"saving occurs"
        account.save() != null
    }
    def 'A2. Saving an account missing any of the required values of handle email, password and name will fail'() {
        when:
        def account = new Account (accountName: accountName, email: email, password: password)
        def result = account.save()

        then:
        result == expected

        where:
        description            | accountName  | email | password | expected
        'AccountName missing'  | null | 'test@test.com' | 'Test1234' | null
        'Email missing'        | 'TestAccount' | null | 'Test1234' | null
        'Invalid email'        | 'TestAccount' | 'testemail' | 'Test1234' | null
        'Password missing'     | 'TestAccount' | 'test@test.com' | '' | null

    }

    def 'A3. Saving an account with an invalid password will fail'() {
        when:
        def account = new Account (accountName: 'Test', email: 'test@test.com', password: password)
        def result = account.save()

        then:
        result == expected

        where:
        description                         | password | expected
        'Password is less than 8 chars'      | 'Test123' | null
        'Password is more than 16 chars'    | 'Test12345678912345' | null
        'Password without a digit'          | 'TestTestTest'| null
        'Password without upper case letter'| 'test1234556777'| null
        'Password without lower case letter'| 'TEST1234556777'| null
        'Password without letters'          | '1234556777'| null
        'Password with only special chars'  | '%^&*&*'| null
        'Password is blank'                 |''| null
        'Password is null'                  | null| null
    }

    def 'A4. Saving account with a non-unique email or handle address must fail '() {
        setup:
        def account1 = new Account (accountName: 'Test1', email: 'test@test.com', password: 'Test1234')
        account1.save()
        def account2 = new Account (accountName: 'Test2', email: 'test@test.com', password: 'Test1234')
        when:
        account2.save()

        then:
        account1.errors.errorCount == 0
        account1.id
        account1.getAccountName() == 'Test1'
        account1.getEmail() == 'test@test.com'
      // TODO: figure out uniqueness
        //  account2.errors.errorCount > 0
    }

    def "test adding account with null values"() {
        when:
        def account = new Account(accountName: accountName, email: email, password: password)
        def result = account.save();

        then:
        result == expected

        where:
        description            | accountName | email | password | expected
        'accountName missing'  | ' '  | 'caoxx521@umn.edu' | 'Password1234' | null
        'email missing'        | 'michelle' | ' ' | 'Password1234' | null
        'password missing'       | 'michelle' | ' ' | ' ' | null
    }

    def 'saving a new account'() {
        setup:
        def account1 = new Account(accountName: 'michelle', email: 'caoxx521@umn.edu', password: 'Password1234')
        def account2 = new Account(accountName: 'michelle', email: 'caoxx521@umn.edu', password: 'Password1234')

        when:
        account1.save()
        account2.save()

        then:
        account1.errors.errorCount == 0
        account1.id
        account1.getAccountName() == 'michelle'
        account1.get(account1.id).email == 'caoxx521@umn.edu'
        account2.id
        account1.get(account2.id).email == 'caoxx521@umn.edu'

    }

}

package thegreattwitter

import grails.test.mixin.TestFor
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
@TestFor(Account)
class AccountSpec extends Specification {

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

    def 'A2. Saving an account missing any of the required values of handle email, password and name will fail: #description'() {
        when:
        def numberOfAccountsBeforeSave = Account.count()
        def account = new Account(accountName: accountName, email: email, password: password, handle: handle,)
        account.save()

        then:
        account.hasErrors()
        !account.id
        Account.count() == numberOfAccountsBeforeSave

        where:
        description           | accountName   | email           | password   | handle
        'AccountName missing' | null          | 'test@test.com' | 'Test1234' | 'accountHandel'
        'Email missing'       | 'TestAccount' | null            | 'Test1234' | 'accountHandel'
        'Invalid email'       | 'TestAccount' | 'testemail'     | 'Test1234' | 'accountHandel'
        'Password missing'    | 'TestAccount' | 'test@test.com' | ''         | 'accountHandel'
        'handle missing'      | 'TestAccount' | 'test@test.com' | 'Test1234' | ''

    }

    def 'A3. Saving an account with an invalid password will fail: #description'() {
        when:
        def numberOfAccountsBeforeSave = Account.count()
        def account = new Account(accountName: 'Test', email: 'test@test.com', password: password)
        account.save()

        then:
        account.hasErrors()
        !account.id
        Account.count() == numberOfAccountsBeforeSave

        where:
        description                          | password
        'Password is less than 8 chars'      | 'Test123'
        'Password is more than 16 chars'     | 'Test12345678912345'
        'Password without a digit'           | 'TestTestTest'
        'Password without upper case letter' | 'test1234556777'
        'Password without lower case letter' | 'TEST1234556777'
        'Password without letters'           | '1234556777'
        'Password with only special chars'   | '%^&*&*'
        'Password is blank'                  | ''
        'Password is null'                   | null
    }

    def "Test adding account with null values"() {
        when:
        def numberOfAccountsBeforeSave = Account.count()
        def account = new Account(accountName: accountName, email: email, password: password, handle: handle)
        account.save();

        then:
        account.hasErrors()
        !account.id
        Account.count() == numberOfAccountsBeforeSave

        where:
        description           | accountName | email              | password       | handle
        'accountName missing' | ' '         | 'caoxx521@umn.edu' | 'Password1234' | 'accountHandel'
        'email missing'       | 'michelle'  | ' '                | 'Password1234' | 'accountHandel'
        'password missing'    | 'michelle'  | ' '                | ' '            | 'accountHandel'
    }
}

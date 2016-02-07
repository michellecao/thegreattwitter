package thegreattwitter


import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class AccountIntegrationSpec extends Specification {

    def setup() {
        def account1 = new Account(accountName: 'Test1', email: 'test@test.com', password: 'Test1234')

        def account2 = new Account(accountName: 'Test2', email: 'test@test.com', password: 'Test1234')

    }

    def cleanup() {
    }

//    def 'A4. Saving account with a non-unique email or handle address must fail '() {
//        setup:
//        def account1 = new Account(accountName: 'Test1', email: 'test@test.com', password: 'Test1234')
//        account1.save(flush: true)
//        def account2 = new Account(accountName: 'Test2', email: 'test@test.com', password: 'Test1234')
//        when:
//        account2.save(flush: true)
//
//        then:
//        account1.errors.errorCount == 0
//        account1.id
//        account1.getAccountName() == 'Test1'
//        account1.getEmail() == 'test@test.com'
//        account2.errors.errorCount > 0
//    }

    def 'F1. An account may have multiple followers '() {
        setup:
        def account1 = new Account(accountName: 'Test1', email: 'test@test.com', password: 'Test1234')
        account1.save(flush: true)
        def follower = new Account(accountName: 'Test2', email: 'test@test.com', password: 'Test1234')
        follower.save(flush: true)
        when:
        follower.addToFollowing(account1)
        follower.save(flush: true)
        account1.addToFollowers(follower)
        account1.save(flush: true)
        then:
        account1.getFollowers() == follower
        follower.getFollowing() == account1
    }
}

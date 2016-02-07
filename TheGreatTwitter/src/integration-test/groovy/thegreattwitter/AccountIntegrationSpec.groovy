package thegreattwitter


import grails.test.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Rollback
class AccountIntegrationSpec extends Specification {


    def 'Saving an account with a non-unique email must fail '() {
        setup:
        def coldPlay = new Account(accountName: 'ColdPlay', email: 'coldPlay@test.com', password: 'superBowl123',
                handle: '@Coldplay')
        coldPlay.save(flush: true)
        def fakeColdPlay = new Account(accountName: 'coldPlay1', email: 'coldPlay@test.com', password: 'Test1234',
                handle: '@Coldplay1')
        when:
        fakeColdPlay.save(flush: true)

        then:
        coldPlay.errors.errorCount == 0
        coldPlay.id
        coldPlay.getAccountName() == 'ColdPlay'
        coldPlay.getEmail() == 'coldPlay@test.com'
        fakeColdPlay.errors.errorCount > 0

        cleanup:
        coldPlay.delete(flush: true)

    }

    def 'Saving an account with a non-unique handle must fail '() {
        setup:
        def coldPlay = new Account(accountName: 'ColdPlay', email: 'coldPlay@test.com', password: 'superBowl123',
                handle: '@Coldplay')
        coldPlay.save(flush: true)
        def fakeColdPlay = new Account(accountName: 'coldPlay1', email: 'coldPlay1@test.com', password: 'Test1234',
                handle: '@Coldplay')
        when:
        fakeColdPlay.save(flush: true)

        then:
        coldPlay.errors.errorCount == 0
        coldPlay.id
        coldPlay.getAccountName() == 'ColdPlay'
        coldPlay.getEmail() == 'coldPlay@test.com'
        fakeColdPlay.errors.errorCount > 0

        cleanup:
        coldPlay.delete(flush: true)
    }


    def 'An account may have multiple followers'() {
        setup:
        def bobMarley = new Account(accountName: 'Bob Marley', email: 'bobMarley@marley.com', password: 'Password123'
                , handle: 'Bob Marley')
        bobMarley.save(flush: true)
        def coldPlay = new Account(accountName: 'ColdPlay', email: 'coldPlay@test.com', password: 'superBowl123',
                handle: 'Music Group')
        coldPlay.save(flush: true)
        when:
        bobMarley.addToFollowers(coldPlay);
        coldPlay.addToFollowing(bobMarley);
        then:
        bobMarley.getFollowers().size() == 1
        bobMarley.getFollowing() == null
        bobMarley.getFollowers().getAt(0) == coldPlay
        coldPlay.getFollowing().size() == 1
        coldPlay.getFollowing().getAt(0) == bobMarley
        cleanup:
        bobMarley.setFollowers(null)
        coldPlay.setFollowing(null)
        bobMarley.delete(flush: true)
        coldPlay.delete(flush: true)

    }


}

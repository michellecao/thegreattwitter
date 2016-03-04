package thegreattwitter

import grails.test.mixin.integration.Integration
import grails.transaction.Rollback
import spock.lang.Specification

@Integration
@Rollback
class AccountIntegrationSpec extends Specification {


    def 'Saving an account with a non-unique email must fail '() {
        setup:
        def accountsBeforeSave = Account.count()
        def coldPlay = TestUtil.createAndSaveAccount('coldPlay@test.com', '@Coldplay')
        def fakeColdPlay = new Account(accountName: 'coldPlay1', email: 'coldPlay@test.com', password: 'Test1234',
                handle: '@Coldplay1')
        when:
        fakeColdPlay.save(flush: true)

        then:
        coldPlay.errors.errorCount == 0
        coldPlay.id
        coldPlay.getAccountName() == 'TestAccount'
        coldPlay.getEmail() == 'coldPlay@test.com'
        fakeColdPlay.errors.errorCount > 0
        Account.count() == accountsBeforeSave + 1

        cleanup:
        coldPlay.delete(flush: true)

    }

    def 'Saving an account with a non-unique handle must fail '() {
        setup:
        def accountsBeforeSave = Account.count()
        def coldPlay = TestUtil.createAndSaveAccount('coldPlay@test.com', '@Coldplay')
        def fakeColdPlay = new Account(accountName: 'coldPlay1', email: 'coldPlay1@test.com', password: 'Test1234',
                handle: '@Coldplay')
        when:
        fakeColdPlay.save(flush: true)

        then:
        coldPlay.errors.errorCount == 0
        coldPlay.id
        coldPlay.getAccountName() == 'TestAccount'
        coldPlay.getEmail() == 'coldPlay@test.com'
        fakeColdPlay.errors.errorCount > 0
        Account.count() == accountsBeforeSave + 1

        cleanup:
        coldPlay.delete(flush: true)
    }

    def 'An account may have one follower'() {
        setup:
        def bobMarley = TestUtil.createAndSaveAccount('bobMarley@marley.com', '@BobMarley')
        def coldPlay = TestUtil.createAndSaveAccount('coldPlay@test.com', '@Coldplay')

        when:
        TestUtil.addToFollowers(bobMarley, coldPlay)

        then:
        bobMarley.getFollowers().size() == 1
        !bobMarley.getFollowing()
        bobMarley.getFollowers().getAt(0) == coldPlay
        coldPlay.getFollowing().size() == 1
        coldPlay.getFollowing().getAt(0) == bobMarley
        !coldPlay.getFollowers()

        cleanup:
        TestUtil.deleteAccount(bobMarley)
        TestUtil.deleteAccount(coldPlay)
    }


    def 'An account may have multiple followers'() {
        setup:
        def bobMarley = TestUtil.createAndSaveAccount('bobMarley@marley.com', '@BobMarley')
        def coldPlay = TestUtil.createAndSaveAccount('coldPlay@test.com', '@Coldplay')
        def beyonce = TestUtil.createAndSaveAccount('beyonce@test.com', '@beyonce')

        when:
        TestUtil.addToFollowers(bobMarley, coldPlay)
        TestUtil.addToFollowers(bobMarley, beyonce)

        then:
        bobMarley.getFollowers().size() == 2
        !bobMarley.getFollowing()
        bobMarley.getFollowers().contains(coldPlay)
        bobMarley.getFollowers().contains(beyonce)
        coldPlay.getFollowing().size() == 1
        coldPlay.getFollowing().getAt(0) == bobMarley
        !coldPlay.getFollowers()
        beyonce.getFollowing().size() == 1
        beyonce.getFollowing().getAt(0) == bobMarley
        !beyonce.getFollowers()

        cleanup:
        TestUtil.deleteAccount(bobMarley)
        TestUtil.deleteAccount(beyonce)
        TestUtil.deleteAccount(coldPlay)
    }

    def 'Two accounts may follow each others'() {
        setup:
        def bobMarley = new Account(accountName: 'Bob Marley', email: 'bobMarley@marley.com', password: 'Password123'
                , handle: 'Bob Marley')
        bobMarley.save(flush: true)
        def coldPlay = new Account(accountName: 'ColdPlay', email: 'coldPlay@test.com', password: 'superBowl123',
                handle: 'Music Group')
        coldPlay.save(flush: true)
        when:
        //ColdPlay following Bob Marley
        TestUtil.addToFollowers(bobMarley, coldPlay)
        //Bob Marley following ColdPlay
        TestUtil.addToFollowers(coldPlay, bobMarley)
        then:
        bobMarley.getFollowers().size() == 1
        bobMarley.getFollowing().getAt(0) == coldPlay
        bobMarley.getFollowers().getAt(0) == coldPlay
        coldPlay.getFollowing().size() == 1
        coldPlay.getFollowing().getAt(0) == bobMarley
        coldPlay.getFollowers().getAt(0) == bobMarley

        cleanup:
        TestUtil.deleteAccount(bobMarley)
        TestUtil.deleteAccount(coldPlay)
    }


}

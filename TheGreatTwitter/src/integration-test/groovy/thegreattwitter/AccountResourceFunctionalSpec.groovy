package thegreattwitter

import geb.spock.GebSpec
import grails.converters.JSON
import grails.test.mixin.integration.Integration
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Stepwise

@Integration
@Stepwise
class AccountResourceFunctionalSpec extends GebSpec {

    RESTClient restClient
    @Shared
    def accountId
    @Shared
    def accountHandle

    @Shared
    def bobAccount
    @Shared
    def bobAccountId
    @Shared
    def bobAccountHandle

    def setup() {
        restClient = new RESTClient(baseUrl)
    }

    def 'Get all accounts resources'() {
        when:
        def resp = restClient.get(path: '/accounts')

        then:
        resp.status == 200
        resp.data.size() == 3

    }

    def 'Save an account'() {
        when:
        def account = new Account(accountName: "Prince", email: "Prince@prince.com",
                password: "Minneapolis1234", handle: "@Prince")
        def json = account as JSON
        def resp = restClient.post(path: '/accounts', body: json as String, requestContentType: 'application/json')

        then:
        resp.status == 201
        resp.data.size() > 0
        !!(accountId = resp.data.id)
        !!(accountHandle = resp.data.handle)
    }

    def 'Save an invalid account should fail'() {
        when:
        def account = new Account(accountName: "Test", email: "Test@prince.com",
                password: "minneapolis1234", handle: "@Test")
        def json = account as JSON
        restClient.post(path: '/accounts', body: json as String, requestContentType: 'application/json')

        then:
        HttpResponseException problem = thrown(HttpResponseException)
        problem.statusCode == 400
        problem.message.contains('Bad Request')

    }

    def 'Get the created account by id'() {
        when:
        def path = '/accounts/' + accountId
        def resp = restClient.get(path: path as String)
        then:
        resp.status == 200
        resp.data.id == accountId
        resp.data.accountName == 'Prince'
        resp.data.email == 'Prince@prince.com'
        resp.data.handle == '@Prince'
        resp.data.followerCount == 0
        resp.data.followingCount == 0
    }

    def 'Get the created account by handle'() {
        when:
        def path = '/accounts/' + accountHandle
        def resp = restClient.get(path: path as String)
        then:
        resp.status == 200
        resp.data.id == accountId
        resp.data.accountName == 'Prince'
        resp.data.email == 'Prince@prince.com'
        resp.data.handle == '@Prince'
    }

    def 'Follow an account'() {
        when:
        def bobMarley = new Account(accountName: 'Bob Marley', email: 'bobMarley@marley.com', password: 'Password123'
                , handle: 'Bob Marley')
        def json = bobMarley as JSON
        def resp = restClient.post(path: '/accounts', body: json as String, requestContentType: 'application/json')


        then:
        resp.status == 201
        resp.data.size() > 0
        !!(bobAccount = resp.data)
        !!(bobAccountId = resp.data.id)
        !!(bobAccountHandle = resp.data.handle)

        when:
        def path = '/accounts/' + accountId + '/follow'
        bobAccount.id = bobAccountId
        def bobJson = bobAccount as JSON
        def updateResp = restClient.post(path: path as String, body: bobJson as String,
                requestContentType: 'application/json')

        then:
        updateResp.status == 200
        updateResp.data.properties.account.followers.size() == 1
        updateResp.data.properties.account.followers[0].id == bobAccountId
        updateResp.data.properties.account.following.size() == 0
        updateResp.data.properties.follower.followers == null
        updateResp.data.properties.follower.following.size() == 1
        updateResp.data.properties.follower.following[0].id == accountId

    }

    def 'Get Followers for account'() {
        when:
        def accountsResp = restClient.get(path: '/accounts')

        then:
        accountsResp.status == 200
        accountsResp.data.size() > 0

        when:
        def mikeId;

        for (account in accountsResp.data) {
            if (account.handle == "@mike") {
                mikeId = account.id
                break
            }
        }
        def path = '/accounts/' + mikeId + '/followers'
        def resp = restClient.get(path: path as String)
        then:
        resp.status == 200
        resp.data[0].id == 1
        resp.data[0].handle == "@michelle"
    }

}

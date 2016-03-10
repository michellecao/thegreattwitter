package thegreattwitter

import geb.spock.GebSpec
import grails.converters.JSON
import grails.test.mixin.integration.Integration
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Stepwise

@Integration
@Stepwise
class MessageResourceFunctionalSpec extends GebSpec {

    RESTClient restClient
    @Shared
    def accountId
    @Shared
    def accountHandle
    @Shared
    def messageId
    @Shared
    Account savedAccount

    def setup() {
        restClient = new RESTClient(baseUrl)
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
        resp.data.id == 4
        resp.data.handle == "@Prince"
        !!(savedAccount = resp.data)
        !!(accountId = resp.data.id)
        !!(accountHandle = resp.data.handle)

    }

    def 'Save a message using account id'() {

        when:
        def path = '/accounts/' + accountId
        def message = new Message(messageText: 'Message Functional Text', account: savedAccount)
        message.account.id = accountId
        def messageAsJson = message as JSON
        def messagePath = "/accounts/" + accountId + "/messages" as String
        def messageResp = restClient.post(path: messagePath,
                body: messageAsJson as String, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0
        messageResp.data.account.id == accountId
        !!(messageId = messageResp.data.id)

    }


    def 'Save a message using account handle'() {
        when:
        def message = new Message(messageText: 'Message Text with handle', account: savedAccount)
        message.account.id = accountId
        def messageAsJson = message as JSON
        def path = "/accounts/" + accountHandle + "/messages"
        def messageResp = restClient.post(path: path as String,
                body: messageAsJson as String, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0
        messageResp.data.account.id == accountId
    }

    def 'Get recent messages'() {
        when:
        def message = new Message(messageText: 'Message Text', account: savedAccount)
        message.account.id = accountId
        def messageAsJson = message as JSON
        def messagePath = "/accounts/" + accountId + "/messages" as String
        for(int i = 0; i < 14; i++) {
            restClient.post(path: messagePath,
                    body: messageAsJson as String, requestContentType: 'application/json')
        }
        def path = '/accounts/' + accountId + '/messages/recent'
        def messageResp = restClient.get(path: path,
                requestContentType: 'application/json')

        then:
        messageResp.status == 200
        messageResp.data.size() == 10
    }

    def 'Search for Keyword'() {
        when:
        String path = "/accounts/messages/search"
        def messageResp = restClient.get(path: path,
                requestContentType: 'application/json', query: [keyword: 'functional'])
        then:
        messageResp.status == 200
        messageResp.data.size() == 1
        messageResp.data[0].account.id == accountId
        messageResp.data[0].account.handle == accountHandle
        messageResp.data[0].messageText.toLowerCase().contains("functional")
        messageResp.data[0].dateCreated != null

    }

    def getFeed() {
        when:
        def accountsResp = restClient.get(path: '/accounts')
        then:
        accountsResp.status == 200
        accountsResp.data.size() > 0

        when:
        def michelleId;
        def michelleAccount;
        for (account in accountsResp.data) {
            if (account.handle == "@michelle") {
                michelleId = account.id
                michelleAccount = account
                break
            }
        }
        def followPath = '/accounts/' + accountId + '/follow'
        def michelleJson = michelleAccount as JSON

        def updateResp = restClient.post(path: followPath as String, body: michelleJson as String,
                requestContentType: 'application/json')
        then:
        updateResp.status == 200
        updateResp.data.size() > 0
        when:
        String path = "/accounts/"+ michelleId + "/messages/feed"
        def resp = restClient.get(path: path,
                requestContentType: 'application/json', query:[dateCreated: "2016-03-09"])
        then:
        resp.status == 200
        resp.data.size() == 10
    }

}

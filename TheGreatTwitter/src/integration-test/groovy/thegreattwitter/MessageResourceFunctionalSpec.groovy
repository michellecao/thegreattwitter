package thegreattwitter

import geb.spock.GebSpec
import grails.converters.JSON
import grails.test.mixin.integration.Integration
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.grails.web.json.JSONObject
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
        def account = new Account(accountName: "newUser", email: "newUser@newUser.com",
                password: "Minneapolis1234", handle: "@newUser")
        def json = account as JSON
        def resp = restClient.post(path: '/accounts', body: json as String, requestContentType: 'application/json')

        then:
        resp.status == 201
        resp.data.size() > 0
        resp.data.handle == "@newUser"
        !!(savedAccount = resp.data)
        !!(accountId = resp.data.id)
        !!(accountHandle = resp.data.handle)

    }

    def 'Save a message using account id'() {

        when:
        def message = new JSONObject().put("messageText", "Message Functional Text");
        def body = message.toString()
        def messagePath = "/accounts/" + accountId + "/messages" as String
        def messageResp = restClient.post(path: messagePath,
                body: body, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0
        messageResp.data.account.id == accountId
        !!(messageId = messageResp.data.id)

    }

    def 'Save a message using account handle'() {

        when:
        def message = new JSONObject().put("messageText", "Message Functional Text");
        def body = message.toString()
        def messagePath = "/accounts/" + accountHandle + "/messages" as String
        def messageResp = restClient.post(path: messagePath,
                body: body, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0
        messageResp.data.account.id == accountId
        !!(messageId = messageResp.data.id)

    }


    def 'Save a message using invalid account should fail'() {
        when:
        def message = new JSONObject().put("messageText", "Message Functional Text");
        def body = message.toString()
        def messagePath = "/accounts/" + accountIdentifier + "/messages" as String
        restClient.post(path: messagePath,
                body: body, requestContentType: 'application/json')
        then:
        HttpResponseException problem = thrown(HttpResponseException)
        problem.statusCode == 404
        problem.message.contains('Not Found')

        where:
        description              | accountIdentifier
        "Invalid account id"     | 2000
        "Invalid account handle" | 'fake'

    }

    def 'Get recent messages'() {
        when:
        def message = new Message(messageText: 'Message Text', account: savedAccount)
        message.account.id = accountId
        def messageAsJson = message as JSON
        def messagePath = "/accounts/" + accountId + "/messages" as String
        for (int i = 0; i < 14; i++) {
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

    def 'Get recent messages with a set max and offset'() {
        when:
        def path = '/accounts/' + accountId + '/messages/recent'
        def resp = restClient.get(path: path,
                requestContentType: 'application/json', params: [max: 100, offset: 0])

        then:
        resp.status == 200
        resp.data.size() == 16

        when:
        def messageResp = restClient.get(path: path,
                requestContentType: 'application/json', params: [max: 3, offset: 1])

        then:
        messageResp.status == 200
        messageResp.data.size() == 3

    }

    def 'Search for Keyword'() {
        when:
        String path = "/accounts/messages/search"
        def messageResp = restClient.get(path: path,
                requestContentType: 'application/json', query: [keyword: 'functional'])
        then:
        messageResp.status == 200
        messageResp.data.size() == 2
        messageResp.data[0].account.id == accountId
        messageResp.data[0].account.handle == accountHandle
        messageResp.data[0].messageText.toLowerCase().contains("functional")
        messageResp.data[0].dateCreated != null

    }

    def 'Search for Keyword that doesnt exist' () {
        when:
        String path = "/accounts/messages/search"
        def messageResp = restClient.get(path: path,
                requestContentType: 'application/json', query: [keyword: 'tree'])
        then:
        messageResp.status == 200
        messageResp.data.size() == 0
    }

    def 'Get Feed for a specific account'() {
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
        String path = "/accounts/" + michelleId + "/messages/feed"
        def resp = restClient.get(path: path,
                requestContentType: 'application/json', query: [dateCreated: "2016-03-09"])
        then:
        resp.status == 200
        resp.data.size() == 10
    }

}

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
    def savedAccount

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
        !!(savedAccount = resp.data)
        !!(accountId = resp.data.id)
        !!(accountHandle = resp.data.handle)
    }

    def 'Save a message using account id'() {

        when:

        def message = new Message(messageText: 'Message Text', account: savedAccount)
        def messageAsJson = message as JSON
        def messageResp = restClient.post(path:'/accounts/${accountId}/messages',
                body: messageAsJson as String, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0
        !!(messageId = messageResp.data.id)

    }


    def 'Save a message using account handle'() {

        when:
        def message = new Message(messageText: 'Message Text with handle', account: savedAccount)
        def messageAsJson = message as JSON
        def messageResp = restClient.post(path: '/accounts/${accountHandle}/messages',
                body: messageAsJson as String, requestContentType: 'application/json')
        then:
        messageResp.status == 201
        messageResp.data.size() > 0

    }

    def 'Get recent messages'() {

        when:
        def messageResp = restClient.get(path: '/accounts/${accountHandle}/messages/recent',
                requestContentType: 'application/json')

        then:
        messageResp.status == 200
        messageResp.data.size() > 0

    }


}

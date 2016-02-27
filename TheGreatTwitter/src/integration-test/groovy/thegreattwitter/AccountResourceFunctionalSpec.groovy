package thegreattwitter

import geb.spock.GebSpec
import grails.test.mixin.integration.Integration
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Stepwise

@Integration
@Stepwise
class AccountResourceFunctionalSpec  extends GebSpec {

    RESTClient restClient
    @Shared
    accountId

    def setup() {
        restClient = new RESTClient (baseUrl)
    }

    def 'get all accounts resources' () {
        when :
        def resp = restClient.get(path: '/accounts')

        then:
        resp.status == 200

    }
    //def account = new Account (....)
    //def json - account as JSON
    // restClient.post (path: '/accounts', body: json as String, requestContentType:' application/json')
    //
    // !!(punchId = resp.data.id)

    
}

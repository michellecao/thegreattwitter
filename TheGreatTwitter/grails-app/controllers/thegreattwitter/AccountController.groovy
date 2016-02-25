package thegreattwitter

import grails.rest.RestfulController
import grails.transaction.Transactional

class AccountController extends RestfulController {
    static responseFormats = ['json', 'xml']
    static allowedMethods = [getAccount: "GET", save: "POST", update: "PUT"]
    AccountController() {
        super(Account)
    }

    @Transactional
    def save(Account account) {

        if (!account) {
            response.status = 404
            respond new Expando(success: false, message: 'auctionUser not provided')
            return
        }

        if (account.hasErrors()) {
            response.status = 500
            respond new Expando(success: false, message: 'has errors', errors: account.errors)
            return
        }

        account.save flush: true, failOnError: true
        respond new Expando(success: true, message: 'Account created', account : account)
    }

}

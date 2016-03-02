package thegreattwitter

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional

class AccountController extends RestfulController<Account> {
    static responseFormats = ['json', 'xml']
    static allowedMethods = [show: "GET", save: "POST", update: "PUT"]

    AccountController() {
        super(Account)
    }

    @Override
    @Transactional
    def show() {
        if (params.id) {
            def byHandle = Account.findByHandle(params.id)
            if (byHandle != null)
                render byHandle as JSON
            else {
                render Account.findById(params.id) as JSON
            }
        } else {
            render Account.list()
        }
    }

    @Transactional
    def save(Account account) {

        if (!account) {
            response.status = 404
        respond new Expando(success: false, message: 'Account not provided')
            return
        }

        if (account.hasErrors()) {
            response.status = 500
            respond new Expando(success: false, message: 'Account has errors', errors: account.errors)
            return
        }

        account.save(flush: true, failOnError: true)
        response.status = 201
        render account as JSON
        respond new Expando(success: true, message: 'Account created', account : account)
    }

}

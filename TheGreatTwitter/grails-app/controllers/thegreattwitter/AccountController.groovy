package thegreattwitter

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional

class AccountController extends RestfulController<Account> {
    static responseFormats = ['json', 'xml']
    static allowedMethods = [show: "GET", save: "POST", follow: "POST"]

    AccountController() {
        super(Account)
    }

    @Override
    def show() {
        if (params.id) {
            def byHandle = Account.findByHandle(params.id)
            if (byHandle != null)
                render byHandle as JSON
            else {
                render Account.findById(params.id) as JSON
            }
        } else {
            render Account.list() as JSON
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
        respond new Expando(success: true, message: 'Account created', account: account)
    }

    @Transactional
    def follow(Account follower) {
        if (!follower) {
            response.status = 204
        }
        if (follower.getErrors().errorCount > 0) {
            response.status = 500
        }
        def accountId = params.accountId
        Account account = Account.findById(accountId)
        if (!account) {
            response.status = 404
            respond new Expando(success: false, message: 'Account not provided')
            return
        }
        account.addToFollowers(follower)
        follower.addToFollowing(account)
        response.status = 200
        respond new Expando(success: true, account: account, follower: follower)
    }

}

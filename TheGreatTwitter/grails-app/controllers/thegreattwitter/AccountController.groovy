package thegreattwitter

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional

class AccountController extends RestfulController<Account> {
    static responseFormats = ['json', 'xml']
    static allowedMethods = [show: "GET", getFollowers: "GET", save: "POST", follow: "POST"]

    AccountController() {
        super(Account)
    }

    def index(Integer max) {
        params.max = max ?: 10
        respond Account.hasMany.followers.list(params), model: [offset: Account.hasMany.followers.list(params).size()]
    }

    @Override
    def show() {

        def account;
        if (params.accountId) {
            def byHandle = Account.findByHandle(params.accountId)
            if (byHandle != null) {
                account = byHandle
            } else if ((params.accountId as String).isNumber()) {
                account = Account.findById(params.accountId)
            }
            if (!account) {
                response.status = 404
                render(contentType: 'application/json') {
                    error = response.status
                    message = 'Account Not Found'
                }
                return
            }
            int followerCount = account.getFollowers().size()
            int followingCount = account.getFollowing().size()
            def jsonAccount = account as JSON
            def jsonObject = JSON.parse(jsonAccount.toString())
            jsonObject.put("followerCount", followerCount)
            jsonObject.put("followingCount", followingCount)
            render jsonObject as JSON
        } else {
            render Account.list() as JSON
        }
    }

    @Transactional
    def save(Account account) {

        if (!account) {
            response.status = 404
            respond new Expando(success: false, message: 'Account not provided')
        }

        if (account.hasErrors() ||
                account?.accountName == null ||
                account?.handle == null ||
                account?.email == null ||
                account?.password == null) {
            response.status = 400
            render(contentType: 'application/json') {
                error = response.status
                message = 'Bad request'
            }
            return
        }
        account.save(flush: true)
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
        def account;
        if (params.accountId) {
            def byHandle = Account.findByHandle(params.accountId)
            if (byHandle != null) {
                account = byHandle
            } else if ((params.accountId as String).isNumber()) {
                account = Account.findById(params.accountId)
            }
            if (!account) {
                response.status = 404
                render(contentType: 'application/json') {
                    error = response.status
                    message = 'Account Not Found'
                }
                return
            }
        }
        account.addToFollowers(follower)
        follower.addToFollowing(account)
        response.status = 200
        respond new Expando(success: true, account: account, follower: follower)
    }

    def getFollowers() {
        if (params.accountId) {
            def byHandle = Account.findByHandle(params.accountId)
            if (byHandle != null) {
                Account account = Account.findByHandle(params.accountId)
                response.status = 200
                render account.followers as JSON
            } else if ((params.accountId as String).isNumber()) {
                Account account = Account.findById(params.accountId)
                response.status = 200
                def followers = account.followers
                render followers as JSON
            }
        }
    }


}

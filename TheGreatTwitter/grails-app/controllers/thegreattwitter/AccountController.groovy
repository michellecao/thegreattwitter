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
        int followerCount = 0
        int followingCount = 0
        if (params.id) {
            def byHandle = Account.findByHandle(params.id)
            if (byHandle != null) {
                Account account = Account.findByHandle(params.id)
                followerCount = account.getFollowers().size()
                followingCount = account.getFollowing().size()
                def jsonByHandle = account as JSON
                def jsonObject = JSON.parse(jsonByHandle.toString())
                jsonObject.put("followerCount", followerCount)
                jsonObject.put("followingCount", followingCount)

                render jsonObject as JSON
            } else {
                Account account = Account.findById(params.id)
                followerCount = account.getFollowers().size()
                followingCount = account.getFollowing().size()
                def jsonAccount = account as JSON
                def jsonObject = JSON.parse(jsonAccount.toString())
                jsonObject.put("followerCount", followerCount)
                jsonObject.put("followingCount", followingCount)

                render jsonObject as JSON
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

    def getFollowers() {
        if (params.accountId) {
            def byHandle = Account.findByHandle(params.accountId)
            if (byHandle != null) {
                Account account = Account.findByHandle(params.accountId)
                response.status = 200
                render account.followers as JSON
            } else {
                Account account = Account.findById(params.accountId)
                response.status = 200
                render account.followers as JSON
            }
        }
    }
    

}

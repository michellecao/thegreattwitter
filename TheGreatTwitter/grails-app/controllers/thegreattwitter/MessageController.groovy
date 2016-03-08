package thegreattwitter

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional
import org.grails.web.json.JSONArray

class MessageController extends RestfulController<Message> {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: 'POST', getRecent: 'GET', search: 'GET', getFeed: 'GET']

    MessageController() {
        super(Message)
    }


    protected Message queryForResource(Serializable id) {
        def accountId = params.accountId
        Message.where {
            id == id && account.id == accountId
        }.find()
    }


    def index(Integer max) {
        params.max = max ?: 10
        respond Message.list(params), model: [messagesCount: Message.count()]
    }

    @Transactional
    def save(Message message) {
        //TODO handle errors better
        if (!message) {
            response.status = 404
            respond new Expando(success: false, notification: 'Message not provided')
            return
        }

        if (params.accountId) {
            def byHandle = Account.findByHandle(params.accountId)
            if (byHandle != null) {
                message.account = byHandle
            } else {
                message.account = Account.findById(params.accountId)
            }
        }

        if (message.hasErrors()) {
            response.status = 500
            respond new Expando(success: false, notification: 'has errors', errors: message.errors)
            return
        }

        message.save(flush: true, failOnError: true)
        response.status = 201
        render message as JSON
        respond new Expando(success: true, notification: 'Message created', message: message)
    }

    def getRecent() {
        render Message.list(max: params.max ? params.max : 10, offset: params.offset ? params.offset : 0,
                sort: "dateCreated", order: "desc") as JSON

    }

    @Transactional
    def search() {
        if (!params.keyword) {
            response.status = 500
            respond new Expando(success: false, notification: 'Missing Keyword Parameter', errors: message.errors)
            return
        }
        def keyword = params.keyword
        def query = Message.where {
            messageText =~ "%${keyword}%"
        }
        def results = query.list()
        JSONArray jArray = new JSONArray();
        for (result in results) {
            def found = Account.findById(result.account.id)
            def json = result as JSON
            def jsonMessageObject = JSON.parse(json.toString())
            def jsonAccountObject = JSON.parse(json.toString()).account
            jsonAccountObject.put("handle", found.handle)
            jsonMessageObject.putAt("account", jsonAccountObject)
            jArray.put(jsonMessageObject)

        }

        render jArray as JSON


    }

    def getFeed() {
        if (params.accountId) {
            def accountId = params.accountId
            def follower = Account.findById(accountId)
            def accounts = follower.following
            def max = params.max ? params.max : 10
            def offset = params.offset ? params.offset : 0
            def results = Message.createCriteria().list(max: max, offset: offset) {
                "in"("account", accounts)
                order('dateCreated', 'desc')
            }

            render results as JSON
        }

    }
}



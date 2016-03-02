package thegreattwitter

import grails.converters.JSON
import grails.rest.RestfulController
import grails.transaction.Transactional

class MessageController extends RestfulController<Message> {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: 'POST']
    MessageController() {
        super(Message)
    }


    @Override
    protected Message queryForResource(Serializable id) {
        def accountId = params.accountId
        Message.where {
            id == id && account.id == accountId
        }.find()
    }



    @Transactional
    def save(Message message) {
        //TODO handle errors better
        if (!message) {
            response.status = 404
            respond new Expando(success: false, notification: 'Message not provided')
            return
        }

        if (message.hasErrors()) {
            response.status = 500
            respond new Expando(success: false, notification: 'has errors', errors: message.errors)
            return
        }

        message.save(flush: true, failOnError: true)
        response.status = 201
        render message as JSON
        respond new Expando(success: true, notification: 'Message created', message : message)
    }
}

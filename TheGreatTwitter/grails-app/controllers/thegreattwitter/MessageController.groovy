package thegreattwitter

import grails.rest.RestfulController
import grails.transaction.Transactional

class MessageController extends RestfulController {

    static responseFormats = ['json', 'xml']
    static allowedMethods = [save: 'POST']
    MessageController() {
        super(Message)
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

        message.save flush: true, failOnError: true
        respond new Expando(success: true, notification: 'Message created', message: message)
    }
}

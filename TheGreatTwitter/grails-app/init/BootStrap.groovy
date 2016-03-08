import thegreattwitter.Account
import thegreattwitter.Message

class BootStrap {

    def init = { servletContext ->
        def michelle = new Account (accountName: "michelle", email: "michelle@test.com",
                password: "Minneapolis1234", handle: "@michelle")
        michelle.save(flush: true)
        def mike = new Account (accountName: "mike", email: "mike@test.com",
                password: "Minneapolis1234", handle: "@mike")
        mike.save(flush: true)
        def fatima = new Account (accountName: "fatima", email: "fatima@test.com",
                password: "Minneapolis1234", handle: "@fatima")
        fatima.save(flush: true)
        michelle.addToFollowing(mike)
        mike.addToFollowers(michelle)
        michelle.addToFollowing(fatima)
        fatima.addToFollowers(michelle)
        Message mikeMessage = new Message(messageText: 'Message Text from Mike', account: mike).save(flush: true)
        Message fatimaMessage = new Message(messageText: 'Message Text from Fatima', account: fatima).save(flush: true)
    }
    def destroy = {
    }
}

package thegreattwitter

class Message {

    String messageText
    Account account
    Date dateCreated

    static belongsTo = [account: Account]
    static constraints = {
        //Message text is required to be non-blank and 40 characters or less
        messageText blank: false,
                nullable: false,
                maxSize: 40
        account nullable: false
    }
}

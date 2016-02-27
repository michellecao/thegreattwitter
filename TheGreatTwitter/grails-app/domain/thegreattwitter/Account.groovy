package thegreattwitter

import grails.rest.Resource

@Resource (uri='/accounts', formats=['json', 'xml'])
class Account {

    String accountName
    String handle
    String email
    String password
    Date dateCreated


    static hasMany = [followers: Account, following: Account]

    static mapping = {
        autoTimestamp true
    }

    static constraints = {
        accountName blank: false,
                nullable: false
        email unique: true,
                blank: false,
                email: true,
                nullable: false
        handle unique: true,
                blank: false,
                nullable: false
        password blank: false,
                size: 8..16,
                nullable: false,
                validator: { String password ->
                    boolean hasUpperCase = false
                    boolean hasLowerCase = false
                    boolean hasDigit = false
                    char[] passwordArray = password.toCharArray();
                    for (int i = 0; i < passwordArray.length; i++) {
                        if (passwordArray[i].isUpperCase()) {
                            hasUpperCase = true
                            continue
                        }
                        if (passwordArray[i].isLowerCase()) {
                            hasLowerCase = true
                            continue
                        }
                        if (passwordArray[i].isDigit()) {
                            hasDigit = true
                        }
                    }
                    return hasUpperCase && hasLowerCase && hasDigit
                }
    }
}

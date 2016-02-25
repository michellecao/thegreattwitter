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
        //unique and valid email
        email unique: true,
                blank: false,
                email: true,
                nullable: false
        //unique handle
        handle unique: true,
                blank: false,
                nullable: false
        //Passwords must be 8-16 characters
        //and have at least 1 number,
        // at least one lower-case letter,
        // at least 1 upper-case letter 
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

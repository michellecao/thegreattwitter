package thegreattwitter


class TestUtil {
    static createAndSaveAccount(email, handle) {
        def account = new Account(accountName: "TestAccount", email: email, password: "superBowl123",
                handle: handle)
        account.save(flush: true)
    }

    static addToFollowers(account, follower) {
        account.addToFollowers(follower)
        follower.addToFollowing(account)
    }

    static deleteAccount(account) {
        for (follower in account.followers) {
            if (follower.following.contains(account)) {
                follower.following.remove(account)
            }
        }
        for (following in account.following) {
            if (following.followers.contains(account)) {
                following.followers.remove(account)
            }
        }
        account.delete(flush: true)
    }

}

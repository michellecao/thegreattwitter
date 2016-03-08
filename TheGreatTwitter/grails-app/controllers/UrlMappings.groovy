class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(view: "/index")
        "500"(view: '/error')
        "404"(view: '/notFound')

        "/accounts"(controller: 'account') {
         //   "/follow"(controller: 'account', action: "follow", method: "POST")
           // "/followers"(controller: 'account', action: "getFollowers", method: "GET")
            "/messages"(controller: 'message')
            "/messages/recent"(controller: 'message', action: "getRecent", method: "GET")
            "/messages/search"(controller: 'message', action: "search", method: "GET")
        }

        "/accounts"(controller: 'account', action: "show", method: "GET")
        "/accounts"(controller: 'account', action: "save", method: "POST")
        "/accounts/$accountId"(controller: 'account', action: "show", method: "GET")
        "/accounts/$accountId/follow"(controller: 'account', action: "follow", method: "POST")
        "/accounts/$accountId/followers"(controller: 'account', action: "getFollowers", method: "GET")

        "/accounts/$accountId/messages"(controller: 'message', action: "show", method: "GET")
        "/accounts/$accountId/messages/$id"(controller: 'message', action: "show", method: "GET")
        "/accounts/$accountId/messages"(controller: 'message', action: "save", method: "POST")
        "/accounts/$accountId/messages/recent"(controller: 'message', action: "getRecent", method: "GET")
        "/accounts/messages/search?(.$format)?"(controller: 'message', action: "search", method: "GET")

    }
}

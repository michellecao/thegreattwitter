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

        "/accounts"(resources: 'account') {
            "/follow"(controller: 'account', action: "follow", method: "POST")
            "/followers"(controller: 'account', action: "getFollowers", method: "GET")
            "/messages"(resources: 'message')
            "/messages/recent"(controller: 'message', action: "getRecent", method: "GET")
            "/messages/search"(controller: 'message', action: "search", method: "GET")
        }

    }
}

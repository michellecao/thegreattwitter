class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
//        "/accounts"(resources: 'account')
//        "/messages"(resources: 'message')
        "/accounts"(resources: 'account') {
            "/messages"(resources: 'message')
            "/messages/recent"(resources: 'message', action: "getRecent", method: "GET")
            "/messages/search"(resources: 'message', action: "search", method: "GET")
        }

    }
}

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }
        //"/restaurants/$id/reservations" (resource: 'reservation')
        //reservations (resource: 'reservation')
        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
        "/accounts" (resources: 'account')
        "/messages" (resources: 'message')
    }
}

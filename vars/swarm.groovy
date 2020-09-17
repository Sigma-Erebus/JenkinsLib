def swarmInfo = null

def init(user, ticket, url){
    swarmInfo = [user:null, ticket:null, url:null]
    swarmInfo.user = user
    swarmInfo.ticket = ticket
    swarmInfo.url = url
}

def upVote(id) {
   bat(script: "curl -u '${swarmInfo.user}:${swarmInfo.ticket}' -X POST '${swarmInfo.url}/reviews/${id}/vote/up'")
}
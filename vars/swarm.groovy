import groovy.json.JsonOutput
def swarmInfo = null

def init(user, ticket, url)
{
    swarmInfo = [user:null, ticket:null, url:null]
    swarmInfo.user = user
    swarmInfo.ticket = ticket
    swarmInfo.url = url
}

def clear() 
{
    swarmInfo.clear()
}

def upVote(id) 
{
   bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST \"${swarmInfo.url}/reviews/${id}/vote/up\"")
}

def downVote(id)
{
   bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST \"${swarmInfo.url}/reviews/${id}/vote/down\"")
}

def comment(id, comment)
{
   bat(script: "curl -u \"${swarm_object.user}:${swarm_object.ticket}\" -X POST -d \"topic=reviews/${id}&body=${comment}\" \"${swarmInfo.url}/api/v9/comments/\"")
}

def needsReview(id)
{
    setState(id,"needsReview")
}

def needsRevision(id)
{
    setState(id,"needsRevision")
}

def approve(id)
{
    setState(id,"approved")
}

def archive(id)
{
    setState(id,"archived")
}

def reject(id)
{
    setState(id,"rejected")
}

def setState(id, state)
{
    script: "curl -u \"${swarm_object.user}:${swarm_object.ticket}\" -X PATCH -d \"state=${state}\" \"${swarmInfo.url}/api/v9/reviews/${id}/state/\""
}
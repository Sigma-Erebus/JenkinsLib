import groovy.json.JsonOutput
def swarmInfo = null

// Must be called first before calling other functions
def init(swarmUser, p4ticket, swarmUrl)
{
   swarmInfo = [user: swarmUser, ticket: p4ticket, url: swarmUrl]
}

def clear() 
{
   swarmInfo.clear()
}

def createReview(id, participants = null, desc = null)
{
   bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST -d \"change=${id}\" \"${swarmInfo.url}/api/v9/reviews/\"")
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
   bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST -d \"topic=reviews/${id}&body=${comment}\" \"${swarmInfo.url}/api/v9/comments/\"")
}

def needsReview(id)
{
   setState(id, "needsReview")
}

def needsRevision(id)
{
   setState(id, "needsRevision")
}

def approve(id)
{
   setState(id, "approved")
}

def archive(id)
{
   setState(id, "archived")
}

def reject(id)
{
   setState(id, "rejected")
}

def setState(id, state)
{
   bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X PATCH -d \"state=${state}\" \"${swarmInfo.url}/api/v9/reviews/${id}/state/\"")
}
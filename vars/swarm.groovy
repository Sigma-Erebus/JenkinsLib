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
   bat(script: """
      curl -u \"${swarm_object.user}:${swarm_object.ticket}\" -X POST 
      -H \"Content-Type: application/x-www-form-urlencoded\" 
      -d \"topic=reviews/${id}&body=${comment}\" \"${swarmInfo.url}/api/v9/comments\"
   """)
}

def vote(id, vote, version = null)
{
   def body = JsonOutput.toJson([vote: vote, version: version])

   bat(script: "curl -X POST -H \"Content-Type: application/json\" -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -d \"${body}\" \"${swarmInfo.url}/api/v10/reviews/${id}/vote\"")
}
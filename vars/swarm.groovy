import groovy.json.JsonSlurper
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

def getParticipantsOfGroup(groupName, groups)
{
   def participants = []
   def groupsParsed = new JsonSlurper().parseText(groups)

   groupsParsed.groups.each { group ->
      
      if (group.name == groupName)
      {
         participants = group.swarmID
      }
   }

   return participants
}

def getParticipantsOfGroups(groupNames, groups)
{
   def participantsArray = []

   groupNames.each {
      def participants = getParticipantsOfGroup(it, groups)
      participants.each {
         participantsArray.add(it)
      }
   }

   return participantsArray
}

def createReview(id, participants = null)
{
   def reviewers = ""
   if (participants)
   {
      participants.each {
         reviewers = reviewers + "-d \"reviewers[]=${it} \""
      }
   }

   def output = bat(script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST -d \"change=${id}\" \"${reviewers}\" \"${swarmInfo.url}/api/v9/reviews/\"", returnStdout: true)
   def responseArray = output.split('\\n')
   return responseArray[2].trim()
}

def getReviewID(curlResponse)
{
   def reviewInfo = new JsonSlurper().parseText(curlResponse)
   return reviewInfo.review.id
}

def getReviewAuthor(curlResponse)
{
   def reviewInfo = new JsonSlurper().parseText(curlResponse)
   return reviewInfo.review.author
}

def upVote(id) 
{
   bat(label: "Upvote Swarm review", script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST \"${swarmInfo.url}/reviews/${id}/vote/up\"")
}

def downVote(id)
{
   bat(label: "Downvote Swarm review", script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST \"${swarmInfo.url}/reviews/${id}/vote/down\"")
}

def comment(id, comment)
{
   bat(label: "Comment on Swarm review", script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X POST -d \"topic=reviews/${id}&body=${comment}\" \"${swarmInfo.url}/api/v9/comments/\"")
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
   bat(label: "Set state of Swarm review", script: "curl -u \"${swarmInfo.user}:${swarmInfo.ticket}\" -X PATCH -d \"state=${state}\" \"${swarmInfo.url}/api/v9/reviews/${id}/state/\"")
}
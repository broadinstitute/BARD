def crowd = config.CbipCrowd
def crowdFactory = ctx.crowdFactory

def crowdClient = crowdFactory.newInstance(crowd.application.url, crowd.application.username, crowd.application.password)

crowdClient.authenticateUser('username', 'password')


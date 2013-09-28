package bard.util

import grails.events.Listener

//TODO: Add ACL Only ADMINS should call this class
class DownTimeSchedulerService {

    //will receive client events from 'saveTodo' topic
    @Listener(namespace = 'browser')
    def downTimeScheduler(String message) {
        log.info(message)

        event(topic: 'downTime', data: message)
    }
}

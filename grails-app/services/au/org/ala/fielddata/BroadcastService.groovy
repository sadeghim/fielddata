package au.org.ala.fielddata

import grails.converters.JSON
import org.springframework.jms.core.JmsTemplate

import javax.jms.TextMessage
import javax.jms.JMSException
import javax.jms.Session
import javax.jms.Message
import org.springframework.jms.core.MessageCreator

class BroadcastService {

    def serviceMethod() {}

    def grailsApplication

    def mediaService

    def failedRecordService

    JmsTemplate jmsTemplate

    def userService

    def destination

    def sendMessage(method, json){
        jmsTemplate.send(destination, new MessageCreator() {
            Message createMessage(Session session) throws JMSException {
                TextMessage message = session.createTextMessage(json)
                message.setStringProperty("messageMethod", method)
                return message
            }
        })
    }

    def resyncAll(){
        if(!grailsApplication.config.enableJMS) return 0
        def max = 100
        def offset = 0
        def synced = 0
        def finished = false
        while(!finished){
            def results = Record.list([offset:offset,max:max])
            finished = results.isEmpty()
            results.each { sendCreate(it); synced++ }
            offset += results.size()
        }
        synced
    }

    def resubmit(method,recordid){
        Record r = Record.get(recordid)
        log.debug("Resubmitting : " + r.properties)
        boolean ok = false
        switch(method){
            case "DELETE":
                ok= sendDelete(recordid)
                break
            case "UPDATE":
                ok= sendUpdate(r)
                break
            case "CREATE" :
                ok = sendCreate(r)
                break
        }
        if(ok)
            failedRecordService.removeFailed(r)
    }

    def sendCreate(record){
        if(grailsApplication.config.enableJMS){
            try{
                def mapOfProperties = toMap(record)
                def json = mapOfProperties as JSON
                log.info("sending create: " + record["id"])
                sendMessage("CREATE", json.toString(true))
                true
            }
            catch(Exception e){
                failedRecordService.markAsFailed("CREATE" ,record)
                log.error(e.getMessage(), e)
                false
            }
        } else {
            log.info "JMS currently disabled....not sending CREATE"
            true
        }
    }

    def sendUpdate(record){
        if(grailsApplication.config.enableJMS){
            try{
                log.debug("Record to update " + record.properties)
                def mapOfProperties = toMap(record)
                def json = mapOfProperties as JSON
                log.info("sending update: " + record["id"])
                sendMessage("UPDATE", json.toString(true))
                return true
            }
            catch(Exception e){
                failedRecordService.markAsFailed("UPDATE", record)
                log.error(e.getMessage(), e)
                return false
            }
        } else {
            log.info "JMS currently disabled....not sending UPDATE"
            return true
        }
    }

    def sendDelete(occurrenceID){
        if(grailsApplication.config.enableJMS){
            try{
                def map = [occurrenceID:occurrenceID]
                log.debug("sending delete: " + occurrenceID)
                sendMessage("DELETE", (map as JSON).toString(true))
                true
            }
            catch(Exception e){
                failedRecordService.markAsFailed("DELETE", Record.get(occurrenceID))
                log.error(e.getMessage(), e)
                false
            }
        } else {
            log.info "JMS currently disabled....not sending DELETE"
            true
        }
    }

    def toMap(record){
        def dbo = record.getProperty("dbo")
        def mapOfProperties = dbo.toMap()
        def id = mapOfProperties["_id"].toString()
        mapOfProperties["id"] = id
        mapOfProperties.remove("_id")
        if(mapOfProperties["userId"]){
            def userMap = userService.getUserNamesForIdsMap()
            def userDisplayName = userMap.get(mapOfProperties["userId"])
            if(userDisplayName){
                 mapOfProperties["recordedBy"] = userDisplayName
            }
        }
        mediaService.setupMediaUrlsForAssociatedMedia(mapOfProperties)
        mapOfProperties
    }
}
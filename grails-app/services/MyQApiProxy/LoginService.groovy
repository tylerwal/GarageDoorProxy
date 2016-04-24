package MyQApiProxy

import Constants.*
import grails.transaction.Transactional
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

@Transactional
class LoginService {
	
	def grailsApplication

    def getSecurityToken() {
		def config = grailsApplication.config
		
		def securityToken = null
		def http = new HTTPBuilder(UrlConstants.LOGIN_URL)
				
		http.request(GET, JSON){ req ->
			uri.query = [
				appId: MyQConstants.APPLICATION_ID,
				securityToken: null,
				username: config.myQ.username,
				password: config.myQ.password,
				culture: MyQConstants.CULTURE
			]
	
			response.success = { resp, reader ->
				if(reader.ErrorMessage) {
					throw new Exception("There was an error: ${reader.ErrorMessage}")
				}
				securityToken = reader.SecurityToken
			}
	
			response.'404' = { resp ->
				throw new Exception('A 404 error occured while attempting to login.')
			}
		}
		
		return securityToken
    }
}

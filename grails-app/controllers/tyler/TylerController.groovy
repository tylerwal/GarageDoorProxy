package tyler

import grails.converters.JSON
import lookups.*

class TylerController {
	
	def loginService
	def deviceService
	def grailsApplication
	
	private String securityToken
	
	def beforeInterceptor = {
		securityToken = loginService.getSecurityToken()
		
		/*
		def apiCookie = request.cookies.find{ 'api' == it.name }?.value
		def isValidAccess =	apiCookie == "qwerty12345asdfg"
		*/
		
		def isValidAccess =	params.apiKey == grailsApplication.config.apiKey		
		
		if (isValidAccess) {
			return true
		} else {
			//log.error("Bad Access Token - token: $apiCookie")
			println("Incorrect Api Key: " + params.apiKey)
			return false
		}
	}
	
    def getStatus(String deviceName) {		
		Map device = deviceService.getGarageDoorOpener(securityToken, deviceName)
		
		def state = deviceService.getGarageDoorState(device)
		
		def text = DoorState.values().find{ ds ->
			ds.getValue() == Integer.parseInt(state.Value)
		}?.name()
		
		render text
	}
	
	def setStatus(String deviceName, String state) {
		Map device = deviceService.getGarageDoorOpener(securityToken, deviceName)
				
		def doorState = DoorState.valueOf(state)
		
		deviceService.setGarageDoorState(securityToken, deviceService.getGarageDoorId(device), doorState.getValue())
				
		render 'Done'
	}
}

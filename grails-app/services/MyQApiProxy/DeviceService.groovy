package myQApiProxy

import constants.*
import grails.transaction.Transactional
import groovyx.net.http.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

@Transactional
class DeviceService {

    public List getGarageDoorOpeners(String securityToken) {
		def foundDoors = []
		
		def http = new HTTPBuilder(UrlConstants.USER_DEVICES_URL)
		
		http.request(GET, JSON){ req ->
			uri.query = [
				appId: MyQConstants.APPLICATION_ID,
				securityToken: securityToken,
			]
	
			response.success = { resp, reader ->
				reader.Devices.each{
					if(it.MyQDeviceTypeName == 'GarageDoorOpener') {
						foundDoors += it
					}
				}
			}
	
			response.'404' = { resp ->
				throw new Exception('A 404 error occured while attempting to retrieve user devices.')
			}
		}
		
		return foundDoors
    }
	
	public Map getGarageDoorOpener(String securityToken, String deviceName) {
		def allDoors = getGarageDoorOpeners(securityToken)
		
		return allDoors.find { door ->
			def deviceNameAttribute = getAttributeByName(door, 'desc')
			return deviceNameAttribute.Value == deviceName
		}
	}
	
	public Map getGarageDoorState(Map device) {
		return getAttributeByName(device, 'doorstate')
	}
	
	public String getGarageDoorId(Map device) {
		return device.ConnectServerDeviceId
	}
	
	public void setGarageDoorState(String securityToken, String deviceId, int state) {
		def http = new HTTPBuilder(UrlConstants.SET_DEVICE_ATTRIBUTE_URL)
		
		http.request(PUT, JSON){ req ->
			body = [
				ApplicationId: MyQConstants.APPLICATION_ID,
				SecurityToken: securityToken,
				AttributeName: 'desireddoorstate',
				AttributeValue: state,
				//MyQDeviceId: deviceId, // does not appear to be needed
				DeviceId: deviceId
			]
			
			headers."Content-Type" = "application/json"
			
			response.success = { resp, reader ->
				println reader
				println resp
			}
	
			response.'404' = { resp ->
				throw new Exception('There was a 404 while setting the door state.')
			}
		}
	}
		
	/* ##### PRIVATE FUNCTIONS ##### */
	
	private getAttributeByName(Map device, String attributeName) {
		return device.Attributes.find { attr ->
			return attr.AttributeDisplayName == attributeName
		}
	}
}

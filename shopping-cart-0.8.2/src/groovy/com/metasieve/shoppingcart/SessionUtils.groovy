package com.metasieve.shoppingcart

import javax.servlet.http.HttpSession
import org.springframework.web.context.request.RequestContextHolder

class SessionUtils {
	public static session
	
	// method for setting session during tests
	public static void setSession(def mockSession) {
		session = mockSession
	}
	
	public static def getSession() {
		if (session) {
			return session
		}
		
		return RequestContextHolder.currentRequestAttributes().getSession()
	}
}
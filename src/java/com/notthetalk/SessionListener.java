/*
 * This file is part of the NOTtheTalk distribution (https://github.com/jdudmesh/notthetalk).
 * Copyright (c) 2011-2021 John Dudmesh.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.notthetalk;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener {

	private static Logger log = Logger.getLogger(SessionListener.class);

		private boolean isEnabled() {
			return false;
		}

	    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
	        final HttpSession session = request.getSession(false);
	        if (session == null) {
	            log.warn("-- No session here...");
	        } else {
	            final StringBuffer msg = new StringBuffer("-- Session found").append("\n");
	            dumpSession(session, msg);
	            log.warn(msg.toString());
	        }
	        chain.doFilter(request, response);
	    }

	    public void sessionCreated(HttpSessionEvent se) {
	    	System.out.println(log.getName());
	        logSessionEvent(se, "Session created");
	    }

	    public void sessionDestroyed(HttpSessionEvent se) {
	        logSessionEvent(se, "Session destroyed");
	    }

	    public void attributeAdded(HttpSessionBindingEvent event) {
	        logSessionEvent(event, "Session attribute added");
	    }

	    public void attributeRemoved(HttpSessionBindingEvent event) {
	        logSessionEvent(event, "Session attribute removed");
	    }

	    public void attributeReplaced(HttpSessionBindingEvent event) {
	        logSessionEvent(event, "Session attribute replaced");
	    }

	    protected void logSessionEvent(HttpSessionEvent event, String s) {
	        if(!isEnabled()){
	            return;
	        }
	        final StringBuffer msg = new StringBuffer("-- ");
	        msg.append(s).append("\n");
	        dumpStacktrace(msg);
	        dumpSession(event.getSession(), msg);
	        if (event instanceof HttpSessionBindingEvent) {
	            dumpSessionBindingEvent((HttpSessionBindingEvent) event, msg);
	        }
	        msg.append("------------------------\n");
	        log.warn(msg.toString());
	    }

	    protected void dumpStacktrace(StringBuffer sb) {
	        sb.append("-- Stack trace :").append("\n");
	        final Throwable fakeException = new Throwable();
	        final StackTraceElement[] elements = fakeException.getStackTrace();
	        // we start at 2 since we don't need the latest elements (ie the calls to logSessionEvent() and dumpStacktrace())
	        for (int i = 2; i < elements.length; i++) {
	            final StackTraceElement el = elements[i];
	            sb.append("    ").append(el).append("\n");
	        }
	        sb.append("----------").append("\n");
	    }

	    protected void dumpSession(HttpSession session, StringBuffer sb) {
	        try {
	            sb.append("-- Session attributes :").append("\n");
	            final Enumeration sessAttrNames = session.getAttributeNames();
	            while (sessAttrNames.hasMoreElements()) {
	                final String attr = (String) sessAttrNames.nextElement();
	                sb.append("    ").append(attr).append(" = ").append(session.getAttribute(attr)).append("\n");
	            }
	            sb.append("-- Session is new : ");
	            sb.append(session.isNew()).append("\n");
	        } catch (IllegalStateException e) {
	            sb.append("-- ").append(e.getMessage());
	        }
	        sb.append("----------").append("\n");
	    }

	    protected void dumpSessionBindingEvent(HttpSessionBindingEvent event, StringBuffer sb) {
	        sb.append("-- Session event :").append("\n");
	        sb.append("  Event: attribute name: ").append(event.getName()).append("\n");
	        sb.append("  Event: attribute value: ").append(event.getValue()).append("\n");
	    }

}

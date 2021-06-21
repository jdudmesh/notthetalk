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


/**
 * Application configuration file for WebXml plugin.
 */
webxml {
    //========================================
    // Delegating Filter Chain
    //========================================
    //
    // Add a 'filter chain proxy' delegater as a Filter.  This will allow the application
    // to define a FilterChainProxy bean that can add additional filters, such as
    // an instance of org.acegisecurity.util.FilterChainProxy.

    // Set to true to add a filter chain delegator.
    //filterChainProxyDelegator.add = true

    // The name of the delegate FilterChainProxy bean.  You must ensure you have added a bean
    // witht his name that implements FilterChainProxy to
    // YOUR-APP/grails-app/conf/spring/resources.groovy.
    //filterChainProxyDelegator.targetBeanName = "filterChainProxyDelegate"

    // The URL pattern to which the filter will apply.  Usually set to '/*' to cover all URLs.
    //filterChainProxyDelegator.urlPattern = "/*"

    // Set to true to add Listeners
    //listener.add = true
    //listener.classNames = ["org.springframework.web.context.request.RequestContextListener"]

    //-------------------------------------------------
    // These settings usually do not need to be changed
    //-------------------------------------------------

    // The name of the delegating filter.
    //filterChainProxyDelegator.filterName = "filterChainProxyDelegator"

    // The delegating filter proxy class.
    //filterChainProxyDelegator.className = "org.springframework.web.filter.DelegatingFilterProxy"

    // ------------------------------------------------
    // Example for context aparameters
    // ------------------------------------------------
    // this example will create the following XML part
    // contextparams = [port: '6001']
    //
    //  <context-param>
    //    <param-name>port</param-name>
    //    <param-value>6001</param-value>
    //  </context-param>
	distributable = []

}

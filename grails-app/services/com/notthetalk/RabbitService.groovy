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

package com.notthetalk

import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.DisposableBean


import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.AMQP

class RabbitService implements InitializingBean, DisposableBean {

    static transactional = true

	Connection _connection;
	Channel _channel;

    def serviceMethod() {

    }
	@Override
	public void destroy() throws Exception {
		_channel.close()

	}
	@Override
	public void afterPropertiesSet() throws Exception {

		ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("rabbitmq");
        _connection = factory.newConnection();

        _channel = _connection.createChannel();
        _channel.exchangeDeclare("notthetalk", "topic", true);


	}

	def send(message, routingKey) {
		_channel.basicPublish("notthetalk", routingKey, new AMQP.BasicProperties.Builder().contentType("application/json").build(), message.getBytes())
	}

}

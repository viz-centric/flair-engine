package com.fbi.engine.service;

import org.springframework.stereotype.Service;

import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import com.fbi.engine.service.mapper.ConnectionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionHelperService {

	private final ConnectionTypeService connectionTypeService;
	private final ConnectionDetailsMapper connectionDetailsMapper;
	private final ConnectionMapper connectionMapper;

	public com.fbi.engine.domain.Connection toConnectionEntity(com.flair.bi.messages.Connection connection) {
		if (connection == null) {
			return null;
		}
		ConnectionDTO c = new ConnectionDTO();
		c.setId(connection.getId());
		c.setConnectionPassword(connection.getConnectionPassword());
		c.setConnectionUsername(connection.getConnectionUsername());
		c.setName(connection.getName());
		c.setLinkId(connection.getLinkId());

		connectionTypeService.findOne(connection.getConnectionType()).ifPresent(c::setConnectionType);
		c.setDetails(connectionDetailsMapper.mapToEntity(connection.getDetailsMap()));

		return connectionMapper.toEntity(c);
	}

}

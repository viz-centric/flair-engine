package com.fbi.engine.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fbi.engine.domain.ConnectionParameter;
import com.fbi.engine.repository.ConnectionParameterRepository;
import com.fbi.engine.service.dto.ConnectionParameters;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConnectionParameterService {

	private final ConnectionParameterRepository connectionParameterRepository;

	@Transactional
	public void save(String linkId, Map<String, String> parameters) {
		List<ConnectionParameter> existingParameters = connectionParameterRepository.findAllByLinkId(linkId);

		List<ConnectionParameter> removeParameters = existingParameters.stream()
				.filter(ep -> !parameters.containsKey(ep.getName())).collect(Collectors.toList());

		connectionParameterRepository.deleteAll(removeParameters);

		List<ConnectionParameter> newAndModifiedParameters = parameters.keySet().stream()
				.map(k -> existingParameters.stream().filter(v -> v.getName().equals(k)).findFirst()
						.map(m -> m.value(parameters.get(k)))
						.orElseGet(() -> new ConnectionParameter().name(k).value(parameters.get(k)).linkId(linkId)))
				.collect(Collectors.toList());

		connectionParameterRepository.saveAll(newAndModifiedParameters);
	}

	@Transactional(readOnly = true)
	public Map<String, String> getParametersByLinkId(String linkId) {
		List<ConnectionParameter> parameters = connectionParameterRepository.findAllByLinkId(linkId);
		return parameters.stream()
				.collect(Collectors.toMap(item -> item.getName(), item -> item.getValue(), (o, o2) -> o));
	}

	@Transactional(readOnly = true)
	public ConnectionParameters getParameters(String linkId) {
		List<ConnectionParameter> parameters = connectionParameterRepository.findAllByLinkId(linkId);
		Map<Object, Object> map = parameters.stream()
				.collect(Collectors.toMap(item -> item.getName(), item -> item.getValue(), (o, o2) -> o));
		return new ConnectionParameters(map);
	}

	public void deleteByLinkId(String linkId) {
		connectionParameterRepository.deleteByLinkId(linkId);
	}
}

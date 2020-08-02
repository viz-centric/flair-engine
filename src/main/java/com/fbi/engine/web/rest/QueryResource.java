package com.fbi.engine.web.rest;

import com.fbi.engine.domain.Connection;
import com.fbi.engine.query.QueryServiceImpl;
import com.fbi.engine.service.ConnectionService;
import com.fbi.engine.service.cache.QueryParams;
import com.project.bi.query.FlairQuery;
import com.project.bi.query.dto.QueryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.sql.SQLException;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api")
public class QueryResource {

    private final ConnectionService connectionService;

    private final QueryServiceImpl queryService;

    @PostMapping(value = "/queries/{connectionLinkId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> executeQuery(@PathVariable String connectionLinkId, @Valid @RequestBody QueryDTO queryDTO) throws JSONException, SQLException {
        log.info(" Connection name with out metadata : " + connectionLinkId);
        Connection connection = connectionService.findByConnectionLinkId(connectionLinkId);
        if (connection == null) {
            return ResponseEntity.badRequest().body(null);
        }
        FlairQuery query = new FlairQuery(queryDTO.interpret(), queryDTO.isMetaRetrieved());
        return ResponseEntity.ok(queryService.executeQuery(QueryParams.builder()
                .connection(connection)
                .flairQuery(query)
                .build()).getResult());
    }

}

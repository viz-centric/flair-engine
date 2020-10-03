package com.fbi.engine.service;

import com.fbi.engine.domain.QConnection;
import com.fbi.engine.domain.schema.ConnectionProperty;
import com.fbi.engine.service.dto.ConnectionDTO;
import com.fbi.engine.service.dto.ConnectionTypeDTO;
import com.fbi.engine.service.dto.UpdateConnectionDTO;
import com.fbi.engine.service.mapper.ConnectionDetailsMapper;
import com.flair.bi.messages.ConnectionServiceGrpc;
import com.flair.bi.messages.ConnectionType;
import com.flair.bi.messages.ConnectionTypesResponses;
import com.flair.bi.messages.DeleteConnectionRequest;
import com.flair.bi.messages.DeleteConnectionResponse;
import com.flair.bi.messages.GetAllConnectionTypesRequest;
import com.flair.bi.messages.GetAllConnectionsRequest;
import com.flair.bi.messages.GetAllConnectionsResponse;
import com.flair.bi.messages.GetConnectionRequest;
import com.flair.bi.messages.GetConnectionResponse;
import com.flair.bi.messages.ListTablesRequest;
import com.flair.bi.messages.ListTablesResponse;
import com.flair.bi.messages.SaveConnectionRequest;
import com.flair.bi.messages.SaveConnectionResponse;
import com.flair.bi.messages.TestConnectionRequest;
import com.flair.bi.messages.TestConnectionResponse;
import com.flair.bi.messages.UpdateConnectionRequest;
import com.flair.bi.messages.UpdateConnectionResponse;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.fbi.engine.service.constant.GrpcConstants.CONNECTION_EXISTS;
import static com.fbi.engine.service.constant.GrpcConstants.CONNECTION_NOT_FOUND;
import static java.util.stream.Collectors.toList;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractConnectionGrpcService extends ConnectionServiceGrpc.ConnectionServiceImplBase {

    private final ConnectionService connectionService;
    private final ConnectionParameterService connectionParameterService;
    private final ConnectionTypeService connectionTypeService;
    private final TestConnectionService connectionTestService;
    private final ConnectionDetailsMapper connectionDetailsMapper;
    private final ListTablesService listTablesService;
    private final ConnectionHelperService connectionHelperService;
    @Override
    public void getConnection(GetConnectionRequest request, StreamObserver<GetConnectionResponse> responseObserver) {
        log.info("Get connection request: {}", request);
        ConnectionDTO connection = null;
        if (StringUtils.isNotEmpty(request.getLinkId())) {
            connection = connectionService.findByConnectionLinkIdAsDto(request.getLinkId());
        } else if (request.getId() != 0) {
            connection = connectionService.findOne(request.getId());
        }

        if (connection == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
        } else {
            Map<String, String> connectionParameters = connectionParameterService.getParametersByLinkId(connection.getLinkId());
            responseObserver.onNext(GetConnectionResponse.newBuilder()
                .setConnection(toConnectionProto(connection, connectionParameters))
                .build());
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteConnection(DeleteConnectionRequest request, StreamObserver<DeleteConnectionResponse> responseObserver) {
        log.info("Delete connection {}", request);

        ConnectionDTO connectionDTO = connectionService.findById(request.getConnectionId());

        connectionService.delete(request.getConnectionId());
        connectionParameterService.deleteByLinkId(connectionDTO.getLinkId());

        log.debug("Connection deleted {}", request.getConnectionId());

        responseObserver.onNext(DeleteConnectionResponse.newBuilder()
            .setConnectionId(request.getConnectionId())
            .setSuccess(true)
            .build());
        responseObserver.onCompleted();
    }


    @Override
    public void getAllConnections(GetAllConnectionsRequest request, StreamObserver<GetAllConnectionsResponse> responseObserver) {
        log.debug("Getting all connections");
        BooleanExpression where = QConnection.connection.realmId.eq(request.getRealmId());
        if (!StringUtils.isEmpty(request.getLinkId())) {
            where.and(QConnection.connection.linkId.eq(request.getLinkId()));
        }
        if (request.getConnectionType() > 0) {
            where.and(QConnection.connection.connectionType.id.eq(request.getConnectionType()));
        }

        List<ConnectionDTO> connections = connectionService.findAllByRealm(where);

        GetAllConnectionsResponse responses = GetAllConnectionsResponse.newBuilder()
            .addAllConnection(connections
                .stream()
                .map(connection -> {
                    Map<String, String> connectionParameters = connectionParameterService.getParametersByLinkId(connection.getLinkId());
                    return toConnectionProto(connection, connectionParameters);
                })
                .collect(toList()))
            .build();
        responseObserver.onNext(responses);
        responseObserver.onCompleted();
    }

    @Override
    public void getConnectionTypes(GetAllConnectionTypesRequest request, StreamObserver<ConnectionTypesResponses> responseObserver) {
        log.debug("Get connection types");
        Page<ConnectionTypeDTO> all = connectionTypeService.findAll(new PageRequest(0, Integer.MAX_VALUE));
        List<ConnectionTypeDTO> connectionTypeDTOList = all.getContent();

        responseObserver.onNext(ConnectionTypesResponses.
            newBuilder()
            .addAllConnectionTypes(connectionTypeDTOList.stream().map(this::toConnectionTypeProto).collect(toList()))
            .build());
        responseObserver.onCompleted();
    }

    @Override
    public void testConnection(TestConnectionRequest request, StreamObserver<TestConnectionResponse> responseObserver) {
        log.debug("Test connection connection {}", request.getConnection());

        String result = connectionTestService.testConnection(connectionHelperService.toConnectionEntity(request.getConnection()));

        TestConnectionResponse.Builder builder = TestConnectionResponse.newBuilder();

        if (result != null) {
            builder.setResult(result);
        }

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void saveConnection(SaveConnectionRequest request, StreamObserver<SaveConnectionResponse> responseObserver) {
        if (request.getConnection().getId() != 0) {
            log.info("Cannot create a connection if ID is already present {}", request.getConnection().getId());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_EXISTS).asRuntimeException());
            return;
        }

        ConnectionDTO dto = new ConnectionDTO();
        dto.setName(request.getConnection().getName());
        dto.setRealmId(request.getConnection().getRealmId());
        dto.setConnectionUsername(request.getConnection().getConnectionUsername());
        dto.setConnectionPassword(request.getConnection().getConnectionPassword());
        dto.setLinkId(request.getConnection().getLinkId());
        dto.setConnectionType(connectionTypeService.findOne(request.getConnection().getConnectionType()));
        dto.setDetails(connectionDetailsMapper.mapToEntity(request.getConnection().getDetailsMap()));

        log.info("Saving connection {}", dto);

        ConnectionDTO createdConnection = connectionService.save(dto);

        log.debug("Saved connection {}", createdConnection);

        connectionParameterService.save(createdConnection.getLinkId(), request.getConnection().getConnectionParametersMap());
        Map<String, String> connectionParameters = connectionParameterService.getParametersByLinkId(createdConnection.getLinkId());

        log.debug("Saved connection parameters {}", connectionParameters);

        responseObserver.onNext(SaveConnectionResponse.newBuilder()
            .setConnection(toConnectionProto(createdConnection, connectionParameters))
            .build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateConnection(UpdateConnectionRequest request, StreamObserver<UpdateConnectionResponse> responseObserver) {
        if (request.getConnection().getId() == 0) {
            log.info("Cannot update a connection if ID is not present {}", request.getConnection().getId());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        UpdateConnectionDTO dto = new UpdateConnectionDTO();
        dto.setId(request.getConnection().getId());
        dto.setName(request.getConnection().getName());
        dto.setConnectionUsername(request.getConnection().getConnectionUsername());
        dto.setConnectionPassword(request.getConnection().getConnectionPassword());
        dto.setLinkId(request.getConnection().getLinkId());
        dto.setConnectionType(connectionTypeService.findOne(request.getConnection().getConnectionType()));
        dto.setDetails(connectionDetailsMapper.mapToEntity(request.getConnection().getDetailsMap()));

        log.info("Updating connection {}", dto);

        ConnectionDTO createdConnection = connectionService.updateConnection(dto);

        log.debug("Updated connection {}", createdConnection);

        connectionParameterService.save(createdConnection.getLinkId(), request.getConnection().getConnectionParametersMap());
        Map<String, String> connectionParameters = connectionParameterService.getParametersByLinkId(createdConnection.getLinkId());

        log.debug("Updated connection parameters {}", connectionParameters);

        responseObserver.onNext(UpdateConnectionResponse.newBuilder()
            .setConnection(toConnectionProto(createdConnection, connectionParameters))
            .build());
        responseObserver.onCompleted();
    }

    @Override
    public void listTables(ListTablesRequest request, StreamObserver<ListTablesResponse> responseObserver) {
        log.info("Listing tables for connection link id {}", request.getConnectionLinkId());
        Set<String> tables = listTablesService.listTables(request.getConnectionLinkId(),
            request.getTableNameLike(),
            request.getMaxEntries(),
            connectionHelperService.toConnectionEntity(request.hasConnection() ? request.getConnection() : null));

        if (tables == null) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(CONNECTION_NOT_FOUND).asRuntimeException());
            return;
        }

        responseObserver.onNext(ListTablesResponse.newBuilder()
            .addAllTables(tables.stream()
                .map(table -> ListTablesResponse.Table.newBuilder()
                    .setTableName(table)
                    .build())
                .collect(Collectors.toList()))
            .build());
        responseObserver.onCompleted();
    }

    private ConnectionType toConnectionTypeProto(ConnectionTypeDTO connType) {
        return ConnectionType.newBuilder()
            .setBundleClass(connType.getBundleClass())
            .setConnectionPropertiesSchema(createConnectionPropertiesSchema(connType))
            .setId(connType.getId())
            .setName(connType.getName())
            .build();
    }

    private ConnectionType.ConnectionPropertiesSchema createConnectionPropertiesSchema(ConnectionTypeDTO connType) {
        return ConnectionType.ConnectionPropertiesSchema.newBuilder()
            .setConnectionDetailsClass(connType.getConnectionPropertiesSchema().getConnectionDetailsClass())
            .setConnectionDetailsType(connType.getConnectionPropertiesSchema().getConnectionDetailsType())
            .setImagePath(connType.getConnectionPropertiesSchema().getImagePath())
            .addAllConnectionProperties(connType.getConnectionPropertiesSchema()
                .getConnectionProperties()
                .stream()
                .map(connProperty -> createConnectionProperty(connProperty))
                .collect(toList()))
            .build();
    }

    private ConnectionType.ConnectionPropertiesSchema.ConnectionProperty createConnectionProperty(ConnectionProperty connProperty) {
        return ConnectionType.ConnectionPropertiesSchema.ConnectionProperty.newBuilder()
            .setDisplayName(connProperty.getDisplayName())
            .setFieldName(connProperty.getFieldName())
            .setOrder(connProperty.getOrder())
            .setFieldType(connProperty.getFieldType())
            .setDefaultValue(connProperty.getDefaultValue() != null ? connProperty.getDefaultValue() : "")
            .setRequired(connProperty.isRequired())
            .build();
    }

    private com.flair.bi.messages.Connection toConnectionProto(ConnectionDTO connection, Map<String, String> connectionParameters) {
        return com.flair.bi.messages.Connection.newBuilder()
            .setId(connection.getId())
            .setName(connection.getName())
            .setConnectionUsername(connection.getConnectionUsername())
            .setConnectionPassword(connection.getConnectionPassword())
            .setConnectionType(connection.getConnectionType().getId())
            .setLinkId(connection.getLinkId())
            .setRealmId(connection.getRealmId())
            .putAllDetails(connectionDetailsMapper.entityToMap(connection.getDetails()))
            .putAllConnectionParameters(connectionParameters)
            .build();
    }
}

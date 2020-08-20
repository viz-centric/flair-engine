package com.fbi.engine.config.grpc;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class JwtServerInterceptor implements ServerInterceptor {

    private static final Set<String> SKIP_AUTH = new HashSet<>(Arrays.asList("grpc.health.v1.Health"));
    private final JwtParser parser;

    public JwtServerInterceptor(String key) {
        this.parser = Jwts.parser().setSigningKey(key.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
        String value = metadata.get(Constant.AUTHORIZATION_METADATA_KEY);

        log.info("Server interceptor {}", serverCall.getMethodDescriptor().getServiceName());

        Status status;
        if (skipAuth(serverCall)) {
            return serverCallHandler.startCall(serverCall, metadata);
        } else if (value == null) {
            status = Status.UNAUTHENTICATED.withDescription("Authorization token is missing");
        } else if (!value.startsWith(Constant.BEARER_TYPE)) {
            status = Status.UNAUTHENTICATED.withDescription("Unknown authorization type");
        } else {
            Jws<Claims> claims = null;
            // remove authorization type prefix
            String token = value.substring(Constant.BEARER_TYPE.length()).trim();
            try {
                // verify token signature and parse claims
                claims = parser.parseClaimsJws(token);
                status = Status.OK;
            } catch (JwtException e) {
                status = Status.UNAUTHENTICATED.withDescription(e.getMessage()).withCause(e);
            }
            if (claims != null) {
                // set client id into current context
                Claims body = claims.getBody();
                String subject = body.getSubject();
                Context ctx = Context.current()
                        .withValue(Constant.USERNAME_CONTEXT_KEY, subject);
                return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
            }
        }

        serverCall.close(status, new Metadata());
        return new ServerCall.Listener<ReqT>() {
            // noop
        };
    }

    private <ReqT, RespT> boolean skipAuth(ServerCall<ReqT, RespT> serverCall) {
        return SKIP_AUTH.contains(serverCall.getMethodDescriptor().getServiceName());
    }

}

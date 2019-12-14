
// stringToExpression is hidden because we have $ in GrpcDsl
import java.io.File

import com.flair.bi.messages.{Query, QueryServiceGrpc}
import io.gatling.core.Predef.{stringToExpression => _}
import io.grpc.netty.shaded.io.grpc.netty.{GrpcSslContexts, NettyChannelBuilder}
/**
 * Performance test for the Connection entity.
 */
class UniaryQueryTest extends Simulation {

  
   val grpcConf = grpc(NettyChannelBuilder.forAddress("localhost", 6565).sslContext(GrpcSslContexts.forClient().trustManager(new File("src/main/resources/ssl/grpc/server.crt")).build()));
  
   val  query: Query = Query.newBuilder()
                .setQueryId("1")
                .setUserId("34fsdfgd")
                .setSourceId("1715917d-fff8-44a1-af02-ee2cd41a3609")
                .setSource("Transactions local")
                .addFields("state")
                .addFields("city")
                .setLimit(10)
                .setOffset(53)
                .build();
   
  
  val s = scenario("Query Test")
    .exec(setUpGrpc)
    .exec(
      grpc("QueryGrpcService")
        .rpc(QueryServiceGrpc.getGetDataStreamMethod)
        .payload(query)
    )
    .exitHereIfFailed
    

  setUp(
    s.inject(atOnceUsers(56))
  ).protocols(grpcConf)
}

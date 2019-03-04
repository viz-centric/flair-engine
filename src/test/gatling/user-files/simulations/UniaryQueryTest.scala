import _root_.io.gatling.core.scenario.Simulation
import ch.qos.logback.classic.{Level, LoggerContext}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory
import com.github.phisgr.gatling.grpc.Predef._
import com.github.phisgr.gatling.pb._
import com.github.phisgr.gatling.util._
// stringToExpression is hidden because we have $ in GrpcDsl
import io.gatling.core.Predef.{stringToExpression => _, _}
import io.gatling.core.session.Expression
import io.grpc.{ManagedChannelBuilder, Status}
import io.grpc.ManagedChannel

import javax.net.ssl.SSLException;
import java.io.File;
import com.flair.bi.messages.QueryServiceGrpc;

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import com.flair.bi.messages.Query;
import com.flair.bi.messages.QueryResponse;

import scala.concurrent.duration._
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

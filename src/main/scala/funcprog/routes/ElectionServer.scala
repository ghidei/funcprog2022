package funcprog.routes

import funcprog.ElectionService
import funcprog.ElectionService.*
import funcprog.model.*

import zio.*

import sttp.apispec.openapi.circe.yaml.*
import sttp.model.StatusCode
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.json.zio.*
import sttp.tapir.server.ziohttp.ZioHttpInterpreter
import sttp.tapir.swagger.SwaggerUI
import sttp.tapir.ztapir.*
import zhttp.http.HttpApp

trait ElectionServer {
  def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]]
}

object ElectionServer {

  lazy val live: ZLayer[ElectionService, Nothing, ElectionServer] = ZLayer {
    for {
      electionService <- ZIO.service[ElectionService]
    } yield ElectionServerLive(electionService)
  }

  def httpApp: ZIO[ElectionServer, Nothing, HttpApp[Any, Throwable]] =
    ZIO.serviceWithZIO[ElectionServer](_.httpApp)

}

final case class ElectionServerLive(service: ElectionService) extends ElectionServer {

  private val baseEndpoint = endpoint.in("election")

  private val getVoteErrorOut = oneOf[Error](
    oneOfVariant(StatusCode.NotFound, jsonBody[Error.NotFound].description("Vote was not found."))
  )

  private val postVoteErrorOut = oneOf[Error](
    oneOfVariant(StatusCode.BadRequest, jsonBody[Error.InvalidInput].description("Invalid vote."))
  )

  private val examplePerson = Person(Firstname("Erik"), Lastname("Eriksson"), Nin("199001010000"))

  private val exampleParty = Party(PartyName("FuncProg2022"))

  private val exampleVote = Vote(examplePerson, exampleParty)

  private val partyBody = jsonBody[Party].example(exampleParty)

  private val voteBody = jsonBody[Vote].example(exampleVote)

  private val getVoteEndpoint =
    baseEndpoint.get
      .in("vote")
      .in(path[Nin]("national_identification_number"))
      .out(voteBody)
      .errorOut(getVoteErrorOut)

  private val putPartyEndpoint =
    baseEndpoint.put
      .in("party")
      .in(partyBody)
      .out(partyBody)

  private val postVoteEndpoint =
    baseEndpoint.post
      .in("vote")
      .in(voteBody)
      .errorOut(postVoteErrorOut)

  private val getVoteRoute =
    getVoteEndpoint.zServerLogic { case nationalIdNumber =>
      service.getVote(nationalIdNumber)
    }

  private val putPartyRoute =
    putPartyEndpoint.zServerLogic { case party =>
      service.createParty(party)
    }

  private val postVoteRoute =
    postVoteEndpoint.zServerLogic { case vote =>
      service.registerVote(vote)
    }

  private val allRoutes = List(
    getVoteRoute,
    putPartyRoute,
    postVoteRoute
  )

  private val endpoints = {
    val endpoints = List(
      getVoteEndpoint,
      putPartyEndpoint,
      postVoteEndpoint
    )
    endpoints.map(_.tags(List("Election Endpoints")))
  }

  override def httpApp: ZIO[Any, Nothing, HttpApp[Any, Throwable]] =
    for {
      openApi       <- ZIO.succeed(OpenAPIDocsInterpreter().toOpenAPI(endpoints, "Election Service", "0.1"))
      routesHttp    <- ZIO.succeed(ZioHttpInterpreter().toHttp(allRoutes))
      endPointsHttp <- ZIO.succeed(ZioHttpInterpreter().toHttp(SwaggerUI[Task](openApi.toYaml)))
    } yield routesHttp ++ endPointsHttp

}

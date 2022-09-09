package funcprog.validation

sealed trait ValidationResponse

object ValidationResponse {
  case object Valid   extends ValidationResponse
  case object Invalid extends ValidationResponse
}

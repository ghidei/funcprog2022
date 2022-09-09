package funcprog.validation

sealed trait ValidationError extends Throwable

object ValidationError {
  case object Temporary                  extends ValidationError
  case class ResponseError(code: String) extends ValidationError
}

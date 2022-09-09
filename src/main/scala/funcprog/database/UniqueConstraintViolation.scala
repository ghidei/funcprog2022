package funcprog.database

case class UniqueConstraintViolation(message: String) extends Throwable

package funcprog.database

import java.time.Instant

import funcprog.model.*

final case class VoteRecord(
  person: Person,
  party: Party,
  createdAt: Instant
) {
  def toVote: Vote = Vote(person, party)
}

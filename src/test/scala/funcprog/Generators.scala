package funcprog

import funcprog.model.*

import zio.test.*
import zio.test.magnolia.*

object Generators {

  implicit lazy val genFirstname: Gen[Sized, Firstname]      = Gen.string.map(Firstname(_))
  implicit lazy val deriveGenFirstname: DeriveGen[Firstname] = DeriveGen.instance(genFirstname)

  implicit lazy val genLastname: Gen[Sized, Lastname]      = Gen.string.map(Lastname(_))
  implicit lazy val deriveGenLastname: DeriveGen[Lastname] = DeriveGen.instance(genLastname)

  implicit lazy val genNin: Gen[Sized, Nin]      = Gen.string.map(Nin(_))
  implicit lazy val deriveGenNin: DeriveGen[Nin] = DeriveGen.instance(genNin)

  implicit lazy val genPartyName: Gen[Sized, PartyName]      = Gen.string.map(PartyName(_))
  implicit lazy val deriveGenPartyName: DeriveGen[PartyName] = DeriveGen.instance(genPartyName)

  lazy val genParty: Gen[Sized, Party] = DeriveGen[Party]

  lazy val genVote: Gen[Sized, Vote] = DeriveGen[Vote]

}

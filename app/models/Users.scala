package models

import play.api.db.slick.Config.driver.simple._

object UserDomainRepresentations {

  // User Class
  case class User(name: String, nickname: String)
  case class UserView(name: String, nickname: String, emails: List[String])

  /* Table mapping
   */
  class UsersTable(tag: Tag) extends Table[User](tag, "user") {

    def name = column[String]("name", O.PrimaryKey)

    def nickname = column[String]("nickname")

    def * = (name, nickname) <> (User.tupled, User.unapply)
  }

  // Email Class
  case class Email(address: String, name: String)

  /* Table mapping
   */
  class EmailsTable(tag: Tag) extends Table[Email](tag, "email") {

    def address = column[String]("address", O.PrimaryKey)

    def name = column[String]("name")

    def * = (address, name) <> (Email.tupled, Email.unapply)
  }

  //create an instance of the table
  val users = TableQuery[UsersTable]
  val emails = TableQuery[EmailsTable]

}

package models

import play.api.db.slick.Config.driver.simple._

object GroupDomainRepresentations {

  // Group Class - NOTE: Slick requires multi-column representations
  // req is a placeholder for the second column
  case class Group(name: String, req: Boolean)
  case class GroupView(name: String, members: List[String])
  case class GroupMember(name: String, operation: String)
  case class GroupRequest(members: List[GroupMember])

  /* Table mapping
   */
  class GroupsTable(tag: Tag) extends Table[Group](tag, "group") {

    def name = column[String]("name", O.PrimaryKey)

    def req = column[Boolean]("req")

    def * = (name, req) <> (Group.tupled, Group.unapply)
  }

  val groups = TableQuery[GroupsTable]
}

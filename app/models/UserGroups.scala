package models

import play.api.db.slick.Config.driver.simple._

object UserGroupDomainRepresentations {

  // Group Class - NOTE: Slick requires multi-column representations
  // req is a placeholder for the second column

  /* Table mapping
   */
  class UserGroupsJoinTable(tag: Tag) extends Table[(String, String)](tag, "user_group") {

    def userName = column[String]("user_name")

    def groupName = column[String]("group_name")

    def * = (userName, groupName)
  }

  val userGroups = TableQuery[UserGroupsJoinTable]
}
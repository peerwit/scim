package controllers

import play.api.db.slick._
import play.api.db.slick.Config.driver.simple._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc._
import play.api.Play.current
import play.api.libs.json.Json

object Application extends Controller{
  import models.UserDomainRepresentations._
  import models.GroupDomainRepresentations._
  import models.UserGroupDomainRepresentations._

  //JSON read/write macro
  implicit val userFormat = Json.format[UserView]
  implicit val groupFormat = Json.format[GroupView]
  implicit val groupMemberFormat = Json.format[GroupMember]
  implicit val groupRequestFormat = Json.format[GroupRequest]


  val userForm = Form(
    mapping(
      "name" -> text,
      "nickname" -> text,
      "emails" -> list(text)
    )(UserView.apply)(UserView.unapply)
  )

  val groupForm = Form(
    mapping(
      "name" -> text,
      "members" -> list(text)
    )(GroupView.apply)(GroupView.unapply)
  )

  val groupMemberForm = Form(
    mapping(
      "operation" -> text,
      "name" -> text
    )(GroupMember.apply)(GroupMember.unapply)
  )

  val groupRequestForm = Form(
    mapping(
      "members" -> list(groupMemberForm.mapping)
    )(GroupRequest.apply)(GroupRequest.unapply)
  )

  def getUsers = DBAction { implicit rs =>
    val allEmails = emails.list.groupBy(_.name).mapValues(x => x.map(_.address))
    val userViews = users.list.map { u =>
      UserView(u.name, u.nickname, allEmails.getOrElse(u.name, List()))
    }
    Ok(Json.toJson(userViews))
  }

  def getUser(uid: String) = DBAction { implicit rs =>
    val userView = (for {
      (u, e) <- users filter (_.name === uid) join emails on (_.name === _.name)
    } yield (u,e)).list.groupBy(_._1).toList.map { x =>
      UserView(x._1.name, x._1.nickname, x._2.map(_._2.address))
    }
    Ok(Json.toJson(userView))
  }

  def postUsers = DBAction { implicit rs =>
    val userView = Json.fromJson[UserView](rs.request.body.asJson.get).map { userView =>
      val name = userView.name
      val user = User(name, userView.nickname)
      users.insert(user)
      userView.emails.map(x => Email(x, name)).foreach( emails.insertOrUpdate(_) )
      userView
    }.get
    Ok(Json.toJson(userView))
  }

  def putUsers(uid: String) = DBAction { implicit rs =>
    val userView = Json.fromJson[UserView](rs.request.body.asJson.get).map { userView =>
      val name = userView.name
      val user = User(name, userView.nickname)
      users.filter(_.name === uid).update(user)
      val updatedEmails = userView.emails.map(e => Email(e, name)).toSet
      val currentEmails = emails.list.toSet
      val addEmails = updatedEmails diff currentEmails
      val delEmails = (currentEmails diff updatedEmails).map(_.address)
      addEmails.foreach(email => emails.insert(email))
      emails.filter(_.address inSet(delEmails)).delete
      userView
    }.get
    Ok(Json.toJson(userView))
  }

  def delUsers(uid: String) = DBAction { implicit rs =>
    users.filter(_.name === uid).delete
    Ok(s"$uid deleted")
  }


  def getGroups = DBAction { implicit rs =>
    val userGroupsMap = userGroups.list.groupBy(_._2).mapValues(_.map(x => x._1))
    val groupViews = groups.list.map { g =>
      GroupView(g.name, userGroupsMap.getOrElse(g.name, List()))
    }
    Ok(Json.toJson(groupViews))
  }

  def patchGroups(uid: String) = DBAction { implicit rs =>
    val userView = Json.fromJson[GroupRequest](rs.request.body.asJson.get).foreach { userReq =>
      val usersMap = userReq.members.groupBy(_.operation)
      val adds = usersMap.getOrElse("add", List()).map(u => (u.name, uid))
      val deletes = usersMap.getOrElse("delete", List()).map(_.name)

      adds.foreach(userGroups.insert(_))
      userGroups.filter(_.userName inSet(deletes)).delete

    }
    Ok(userView.toString)
  }
}

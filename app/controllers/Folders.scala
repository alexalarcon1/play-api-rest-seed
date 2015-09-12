package controllers

import api._
import api.ApiError._
import api.JsonCombinators._
import models.Folder
import play.api.mvc._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import javax.inject.Inject
import play.api.i18n.{ MessagesApi, I18nSupport }

class Folders @Inject() (val messagesApi: MessagesApi) extends api.ApiController with I18nSupport {

  def list(sort: Option[String], p: Int, s: Int) = SecuredGetAction { implicit request =>
    sortedPage(sort, Folder.sortingFields, default = "order") { sortingFields =>
      Folder.page(request.userId, sortingFields, p, s)
    }
  }

  def insert = SecuredPostAction { implicit request =>
    readFromRequest[Folder] { folder =>
      Folder.insert(request.userId, folder.name).flatMap {
        case (id, newFolder) => ok(newFolder)
      }
    }
  }

  def info(id: Long) = SecuredGetAction { implicit request =>
    maybeItem(Folder.findById(id))
  }

  def update(id: Long) = SecuredPutAction { implicit request =>
    readFromRequest[Folder] { folder =>
      Folder.basicUpdate(id, folder.name).flatMap { isOk =>
        if (isOk) noContent() else errorInternal
      }
    }
  }

  def updateOrder(id: Long, newOrder: Int) = SecuredPutAction { implicit request =>
    Folder.updateOrder(id, newOrder).flatMap { isOk =>
      if (isOk) noContent() else errorInternal
    }
  }

  def delete(id: Long) = SecuredDeleteAction { implicit request =>
    Folder.delete(id).flatMap { _ =>
      noContent()
    }
  }

}
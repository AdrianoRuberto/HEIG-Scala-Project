package controllers

import javax.inject._
import play.api.mvc._

@Singleton
class HomeController @Inject() extends Controller {
	def index = Action { implicit request =>
		Ok(views.html.main())
	}

	def notFound(path: String) = Action { NotFound }
}

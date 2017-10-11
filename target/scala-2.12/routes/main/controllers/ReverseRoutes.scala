
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/jorge/IdeaProjects/WebDPWManager/conf/routes
// @DATE:Tue Sep 19 12:02:10 PYT 2017

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers {

  // @LINE:16
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:16
    def versioned(file:Asset): Call = {
      implicit val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public")))
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }

  // @LINE:9
  class ReverseCountController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def count(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "count")
    }
  
  }

  // @LINE:13
  class ReverseAgentExecutionController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:13
    def listAllAgentExecutions(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/execs")
    }
  
  }

  // @LINE:7
  class ReverseHomeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def index(): Call = {
      
      Call("GET", _prefix)
    }
  
  }

  // @LINE:11
  class ReverseAsyncController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:11
    def message(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "message")
    }
  
  }


}

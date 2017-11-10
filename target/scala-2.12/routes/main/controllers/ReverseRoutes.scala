
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/jorge/IdeaProjects/WebDPWManager/conf/routes
// @DATE:Thu Nov 09 09:35:47 PYST 2017

import play.api.mvc.Call


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers {

  // @LINE:46
  class ReverseAssets(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:46
    def versioned(file:Asset): Call = {
      implicit val _rrc = new play.core.routing.ReverseRouteContext(Map(("path", "/public")))
      Call("GET", _prefix + { _defaultPrefix } + "assets/" + implicitly[play.api.mvc.PathBindable[Asset]].unbind("file", file))
    }
  
  }

  // @LINE:37
  class ReverseMasterTypeController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:37
    def getMasterType(masterTypeId:Short): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/master/type/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Short]].unbind("masterTypeId", masterTypeId)))
    }
  
    // @LINE:41
    def getFieldById(fieldId:Int): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/master/field/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Int]].unbind("fieldId", fieldId)))
    }
  
    // @LINE:43
    def getFieldsForMaster(masterTypeId:Short): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/master/fields/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Short]].unbind("masterTypeId", masterTypeId)))
    }
  
    // @LINE:39
    def getMasterTypes(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/master/types")
    }
  
  }

  // @LINE:15
  class ReverseHostController(_prefix: => String) {
    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:27
    def getAllHosts(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/all")
    }
  
    // @LINE:23
    def deployNewRole(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/hosts/deployments/add")
    }
  
    // @LINE:17
    def getRoleById(roleId:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/role/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("roleId", roleId)))
    }
  
    // @LINE:21
    def getSingleDeploymentForRoleById(deployId:Int): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/deployments/details/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Int]].unbind("deployId", deployId)))
    }
  
    // @LINE:29
    def getAllHostsInActorSystemCluster(actorSystemName:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/cluster/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("actorSystemName", actorSystemName)))
    }
  
    // @LINE:25
    def getDeploymentsForHost(hostId:Short): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/roles/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Short]].unbind("hostId", hostId)))
    }
  
    // @LINE:19
    def addHost(): Call = {
      
      Call("POST", _prefix + { _defaultPrefix } + "api/hosts/add")
    }
  
    // @LINE:31
    def getAllActorSystems(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/clusters")
    }
  
    // @LINE:33
    def getAllHostsWhereRoleDeployed(roleId:String): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/role/deployments/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[String]].unbind("roleId", roleId)))
    }
  
    // @LINE:15
    def getAllRoles(): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/hosts/roles")
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
    def listAllAgentExecutions(deployId:Int): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/execs/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Int]].unbind("deployId", deployId)))
    }
  
    // @LINE:35
    def getAgentExecutionDetails(agentExecId:Int): Call = {
      
      Call("GET", _prefix + { _defaultPrefix } + "api/execs/details/" + play.core.routing.dynamicString(implicitly[play.api.mvc.PathBindable[Int]].unbind("agentExecId", agentExecId)))
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

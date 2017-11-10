
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/jorge/IdeaProjects/WebDPWManager/conf/routes
// @DATE:Thu Nov 09 09:35:47 PYST 2017

import play.api.routing.JavaScriptReverseRoute


import _root_.controllers.Assets.Asset

// @LINE:7
package controllers.javascript {

  // @LINE:46
  class ReverseAssets(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:46
    def versioned: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.Assets.versioned",
      """
        function(file1) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "assets/" + (""" + implicitly[play.api.mvc.PathBindable[Asset]].javascriptUnbind + """)("file", file1)})
        }
      """
    )
  
  }

  // @LINE:37
  class ReverseMasterTypeController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:37
    def getMasterType: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MasterTypeController.getMasterType",
      """
        function(masterTypeId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/master/type/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Short]].javascriptUnbind + """)("masterTypeId", masterTypeId0))})
        }
      """
    )
  
    // @LINE:41
    def getFieldById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MasterTypeController.getFieldById",
      """
        function(fieldId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/master/field/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Int]].javascriptUnbind + """)("fieldId", fieldId0))})
        }
      """
    )
  
    // @LINE:43
    def getFieldsForMaster: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MasterTypeController.getFieldsForMaster",
      """
        function(masterTypeId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/master/fields/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Short]].javascriptUnbind + """)("masterTypeId", masterTypeId0))})
        }
      """
    )
  
    // @LINE:39
    def getMasterTypes: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.MasterTypeController.getMasterTypes",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/master/types"})
        }
      """
    )
  
  }

  // @LINE:15
  class ReverseHostController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:27
    def getAllHosts: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getAllHosts",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/all"})
        }
      """
    )
  
    // @LINE:23
    def deployNewRole: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.deployNewRole",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/deployments/add"})
        }
      """
    )
  
    // @LINE:17
    def getRoleById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getRoleById",
      """
        function(roleId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/role/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("roleId", roleId0))})
        }
      """
    )
  
    // @LINE:21
    def getSingleDeploymentForRoleById: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getSingleDeploymentForRoleById",
      """
        function(deployId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/deployments/details/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Int]].javascriptUnbind + """)("deployId", deployId0))})
        }
      """
    )
  
    // @LINE:29
    def getAllHostsInActorSystemCluster: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getAllHostsInActorSystemCluster",
      """
        function(actorSystemName0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/cluster/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("actorSystemName", actorSystemName0))})
        }
      """
    )
  
    // @LINE:25
    def getDeploymentsForHost: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getDeploymentsForHost",
      """
        function(hostId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/roles/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Short]].javascriptUnbind + """)("hostId", hostId0))})
        }
      """
    )
  
    // @LINE:19
    def addHost: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.addHost",
      """
        function() {
          return _wA({method:"POST", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/add"})
        }
      """
    )
  
    // @LINE:31
    def getAllActorSystems: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getAllActorSystems",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/clusters"})
        }
      """
    )
  
    // @LINE:33
    def getAllHostsWhereRoleDeployed: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getAllHostsWhereRoleDeployed",
      """
        function(roleId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/role/deployments/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[String]].javascriptUnbind + """)("roleId", roleId0))})
        }
      """
    )
  
    // @LINE:15
    def getAllRoles: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HostController.getAllRoles",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/hosts/roles"})
        }
      """
    )
  
  }

  // @LINE:9
  class ReverseCountController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:9
    def count: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.CountController.count",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "count"})
        }
      """
    )
  
  }

  // @LINE:13
  class ReverseAgentExecutionController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:13
    def listAllAgentExecutions: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AgentExecutionController.listAllAgentExecutions",
      """
        function(deployId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/execs/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Int]].javascriptUnbind + """)("deployId", deployId0))})
        }
      """
    )
  
    // @LINE:35
    def getAgentExecutionDetails: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AgentExecutionController.getAgentExecutionDetails",
      """
        function(agentExecId0) {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "api/execs/details/" + encodeURIComponent((""" + implicitly[play.api.mvc.PathBindable[Int]].javascriptUnbind + """)("agentExecId", agentExecId0))})
        }
      """
    )
  
  }

  // @LINE:7
  class ReverseHomeController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:7
    def index: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.HomeController.index",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + """"})
        }
      """
    )
  
  }

  // @LINE:11
  class ReverseAsyncController(_prefix: => String) {

    def _defaultPrefix: String = {
      if (_prefix.endsWith("/")) "" else "/"
    }

  
    // @LINE:11
    def message: JavaScriptReverseRoute = JavaScriptReverseRoute(
      "controllers.AsyncController.message",
      """
        function() {
          return _wA({method:"GET", url:"""" + _prefix + { _defaultPrefix } + """" + "message"})
        }
      """
    )
  
  }


}

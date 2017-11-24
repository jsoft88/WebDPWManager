
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/jorge/IdeaProjects/WebDPWManager/conf/routes
// @DATE:Mon Nov 20 16:08:27 PYST 2017

package router

import play.core.routing._
import play.core.routing.HandlerInvokerFactory._

import play.api.mvc._

import _root_.controllers.Assets.Asset

class Routes(
  override val errorHandler: play.api.http.HttpErrorHandler, 
  // @LINE:7
  HomeController_1: controllers.HomeController,
  // @LINE:9
  CountController_0: controllers.CountController,
  // @LINE:11
  AsyncController_3: controllers.AsyncController,
  // @LINE:13
  AgentExecutionController_5: controllers.AgentExecutionController,
  // @LINE:15
  HostController_4: controllers.HostController,
  // @LINE:37
  MasterTypeController_2: controllers.MasterTypeController,
  // @LINE:48
  Assets_6: controllers.Assets,
  val prefix: String
) extends GeneratedRouter {

   @javax.inject.Inject()
   def this(errorHandler: play.api.http.HttpErrorHandler,
    // @LINE:7
    HomeController_1: controllers.HomeController,
    // @LINE:9
    CountController_0: controllers.CountController,
    // @LINE:11
    AsyncController_3: controllers.AsyncController,
    // @LINE:13
    AgentExecutionController_5: controllers.AgentExecutionController,
    // @LINE:15
    HostController_4: controllers.HostController,
    // @LINE:37
    MasterTypeController_2: controllers.MasterTypeController,
    // @LINE:48
    Assets_6: controllers.Assets
  ) = this(errorHandler, HomeController_1, CountController_0, AsyncController_3, AgentExecutionController_5, HostController_4, MasterTypeController_2, Assets_6, "/")

  def withPrefix(prefix: String): Routes = {
    router.RoutesPrefix.setPrefix(prefix)
    new Routes(errorHandler, HomeController_1, CountController_0, AsyncController_3, AgentExecutionController_5, HostController_4, MasterTypeController_2, Assets_6, prefix)
  }

  private[this] val defaultPrefix: String = {
    if (this.prefix.endsWith("/")) "" else "/"
  }

  def documentation = List(
    ("""GET""", this.prefix, """controllers.HomeController.index"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """count""", """controllers.CountController.count"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """message""", """controllers.AsyncController.message"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/execs/""" + "$" + """deployId<[^/]+>""", """controllers.AgentExecutionController.listAllAgentExecutions(deployId:Int)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/roles""", """controllers.HostController.getAllRoles"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/role/""" + "$" + """roleId<[^/]+>""", """controllers.HostController.getRoleById(roleId:String)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/add""", """controllers.HostController.addHost"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/deployments/details/""" + "$" + """deployId<[^/]+>""", """controllers.HostController.getSingleDeploymentForRoleById(deployId:Int)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/deployments/add""", """controllers.HostController.deployNewRole"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/roles/""" + "$" + """hostId<[^/]+>""", """controllers.HostController.getDeploymentsForHost(hostId:Int)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/all""", """controllers.HostController.getAllHosts"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/cluster/""" + "$" + """actorSystemName<[^/]+>""", """controllers.HostController.getAllHostsInActorSystemCluster(actorSystemName:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/clusters""", """controllers.HostController.getAllActorSystems"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/hosts/role/deployments/""" + "$" + """roleId<[^/]+>""", """controllers.HostController.getAllHostsWhereRoleDeployed(roleId:String)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/execs/details/""" + "$" + """agentExecId<[^/]+>""", """controllers.AgentExecutionController.getAgentExecutionDetails(agentExecId:Int)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/master/type/""" + "$" + """masterTypeId<[^/]+>""", """controllers.MasterTypeController.getMasterType(masterTypeId:Int)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/master/types""", """controllers.MasterTypeController.getMasterTypes"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/master/field/""" + "$" + """fieldId<[^/]+>""", """controllers.MasterTypeController.getFieldById(fieldId:Int)"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/master/fields/""" + "$" + """masterTypeId<[^/]+>""", """controllers.MasterTypeController.getFieldsForMaster(masterTypeId:Int)"""),
    ("""POST""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """api/master/execute""", """controllers.AgentExecutionController.addNewExecution"""),
    ("""GET""", this.prefix + (if(this.prefix.endsWith("/")) "" else "/") + """assets/""" + "$" + """file<.+>""", """controllers.Assets.versioned(path:String = "/public", file:Asset)"""),
    Nil
  ).foldLeft(List.empty[(String,String,String)]) { (s,e) => e.asInstanceOf[Any] match {
    case r @ (_,_,_) => s :+ r.asInstanceOf[(String,String,String)]
    case l => s ++ l.asInstanceOf[List[(String,String,String)]]
  }}


  // @LINE:7
  private[this] lazy val controllers_HomeController_index0_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix)))
  )
  private[this] lazy val controllers_HomeController_index0_invoker = createInvoker(
    HomeController_1.index,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HomeController",
      "index",
      Nil,
      "GET",
      this.prefix + """""",
      """ An example controller showing a sample home page""",
      Seq()
    )
  )

  // @LINE:9
  private[this] lazy val controllers_CountController_count1_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("count")))
  )
  private[this] lazy val controllers_CountController_count1_invoker = createInvoker(
    CountController_0.count,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.CountController",
      "count",
      Nil,
      "GET",
      this.prefix + """count""",
      """ An example controller showing how to use dependency injection""",
      Seq()
    )
  )

  // @LINE:11
  private[this] lazy val controllers_AsyncController_message2_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("message")))
  )
  private[this] lazy val controllers_AsyncController_message2_invoker = createInvoker(
    AsyncController_3.message,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AsyncController",
      "message",
      Nil,
      "GET",
      this.prefix + """message""",
      """ An example controller showing how to write asynchronous code""",
      Seq()
    )
  )

  // @LINE:13
  private[this] lazy val controllers_AgentExecutionController_listAllAgentExecutions3_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/execs/"), DynamicPart("deployId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_AgentExecutionController_listAllAgentExecutions3_invoker = createInvoker(
    AgentExecutionController_5.listAllAgentExecutions(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AgentExecutionController",
      "listAllAgentExecutions",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/execs/""" + "$" + """deployId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:15
  private[this] lazy val controllers_HostController_getAllRoles4_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/roles")))
  )
  private[this] lazy val controllers_HostController_getAllRoles4_invoker = createInvoker(
    HostController_4.getAllRoles,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getAllRoles",
      Nil,
      "GET",
      this.prefix + """api/hosts/roles""",
      """""",
      Seq()
    )
  )

  // @LINE:17
  private[this] lazy val controllers_HostController_getRoleById5_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/role/"), DynamicPart("roleId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_HostController_getRoleById5_invoker = createInvoker(
    HostController_4.getRoleById(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getRoleById",
      Seq(classOf[String]),
      "GET",
      this.prefix + """api/hosts/role/""" + "$" + """roleId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:19
  private[this] lazy val controllers_HostController_addHost6_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/add")))
  )
  private[this] lazy val controllers_HostController_addHost6_invoker = createInvoker(
    HostController_4.addHost,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "addHost",
      Nil,
      "POST",
      this.prefix + """api/hosts/add""",
      """""",
      Seq()
    )
  )

  // @LINE:21
  private[this] lazy val controllers_HostController_getSingleDeploymentForRoleById7_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/deployments/details/"), DynamicPart("deployId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_HostController_getSingleDeploymentForRoleById7_invoker = createInvoker(
    HostController_4.getSingleDeploymentForRoleById(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getSingleDeploymentForRoleById",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/hosts/deployments/details/""" + "$" + """deployId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:23
  private[this] lazy val controllers_HostController_deployNewRole8_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/deployments/add")))
  )
  private[this] lazy val controllers_HostController_deployNewRole8_invoker = createInvoker(
    HostController_4.deployNewRole,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "deployNewRole",
      Nil,
      "POST",
      this.prefix + """api/hosts/deployments/add""",
      """""",
      Seq()
    )
  )

  // @LINE:25
  private[this] lazy val controllers_HostController_getDeploymentsForHost9_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/roles/"), DynamicPart("hostId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_HostController_getDeploymentsForHost9_invoker = createInvoker(
    HostController_4.getDeploymentsForHost(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getDeploymentsForHost",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/hosts/roles/""" + "$" + """hostId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:27
  private[this] lazy val controllers_HostController_getAllHosts10_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/all")))
  )
  private[this] lazy val controllers_HostController_getAllHosts10_invoker = createInvoker(
    HostController_4.getAllHosts,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getAllHosts",
      Nil,
      "GET",
      this.prefix + """api/hosts/all""",
      """""",
      Seq()
    )
  )

  // @LINE:29
  private[this] lazy val controllers_HostController_getAllHostsInActorSystemCluster11_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/cluster/"), DynamicPart("actorSystemName", """[^/]+""",true)))
  )
  private[this] lazy val controllers_HostController_getAllHostsInActorSystemCluster11_invoker = createInvoker(
    HostController_4.getAllHostsInActorSystemCluster(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getAllHostsInActorSystemCluster",
      Seq(classOf[String]),
      "GET",
      this.prefix + """api/hosts/cluster/""" + "$" + """actorSystemName<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:31
  private[this] lazy val controllers_HostController_getAllActorSystems12_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/clusters")))
  )
  private[this] lazy val controllers_HostController_getAllActorSystems12_invoker = createInvoker(
    HostController_4.getAllActorSystems,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getAllActorSystems",
      Nil,
      "GET",
      this.prefix + """api/hosts/clusters""",
      """""",
      Seq()
    )
  )

  // @LINE:33
  private[this] lazy val controllers_HostController_getAllHostsWhereRoleDeployed13_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/hosts/role/deployments/"), DynamicPart("roleId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_HostController_getAllHostsWhereRoleDeployed13_invoker = createInvoker(
    HostController_4.getAllHostsWhereRoleDeployed(fakeValue[String]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.HostController",
      "getAllHostsWhereRoleDeployed",
      Seq(classOf[String]),
      "GET",
      this.prefix + """api/hosts/role/deployments/""" + "$" + """roleId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:35
  private[this] lazy val controllers_AgentExecutionController_getAgentExecutionDetails14_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/execs/details/"), DynamicPart("agentExecId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_AgentExecutionController_getAgentExecutionDetails14_invoker = createInvoker(
    AgentExecutionController_5.getAgentExecutionDetails(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AgentExecutionController",
      "getAgentExecutionDetails",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/execs/details/""" + "$" + """agentExecId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:37
  private[this] lazy val controllers_MasterTypeController_getMasterType15_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/master/type/"), DynamicPart("masterTypeId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_MasterTypeController_getMasterType15_invoker = createInvoker(
    MasterTypeController_2.getMasterType(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MasterTypeController",
      "getMasterType",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/master/type/""" + "$" + """masterTypeId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:39
  private[this] lazy val controllers_MasterTypeController_getMasterTypes16_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/master/types")))
  )
  private[this] lazy val controllers_MasterTypeController_getMasterTypes16_invoker = createInvoker(
    MasterTypeController_2.getMasterTypes,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MasterTypeController",
      "getMasterTypes",
      Nil,
      "GET",
      this.prefix + """api/master/types""",
      """""",
      Seq()
    )
  )

  // @LINE:41
  private[this] lazy val controllers_MasterTypeController_getFieldById17_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/master/field/"), DynamicPart("fieldId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_MasterTypeController_getFieldById17_invoker = createInvoker(
    MasterTypeController_2.getFieldById(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MasterTypeController",
      "getFieldById",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/master/field/""" + "$" + """fieldId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:43
  private[this] lazy val controllers_MasterTypeController_getFieldsForMaster18_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/master/fields/"), DynamicPart("masterTypeId", """[^/]+""",true)))
  )
  private[this] lazy val controllers_MasterTypeController_getFieldsForMaster18_invoker = createInvoker(
    MasterTypeController_2.getFieldsForMaster(fakeValue[Int]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.MasterTypeController",
      "getFieldsForMaster",
      Seq(classOf[Int]),
      "GET",
      this.prefix + """api/master/fields/""" + "$" + """masterTypeId<[^/]+>""",
      """""",
      Seq()
    )
  )

  // @LINE:45
  private[this] lazy val controllers_AgentExecutionController_addNewExecution19_route = Route("POST",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("api/master/execute")))
  )
  private[this] lazy val controllers_AgentExecutionController_addNewExecution19_invoker = createInvoker(
    AgentExecutionController_5.addNewExecution,
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.AgentExecutionController",
      "addNewExecution",
      Nil,
      "POST",
      this.prefix + """api/master/execute""",
      """""",
      Seq()
    )
  )

  // @LINE:48
  private[this] lazy val controllers_Assets_versioned20_route = Route("GET",
    PathPattern(List(StaticPart(this.prefix), StaticPart(this.defaultPrefix), StaticPart("assets/"), DynamicPart("file", """.+""",false)))
  )
  private[this] lazy val controllers_Assets_versioned20_invoker = createInvoker(
    Assets_6.versioned(fakeValue[String], fakeValue[Asset]),
    play.api.routing.HandlerDef(this.getClass.getClassLoader,
      "router",
      "controllers.Assets",
      "versioned",
      Seq(classOf[String], classOf[Asset]),
      "GET",
      this.prefix + """assets/""" + "$" + """file<.+>""",
      """ Map static resources from the /public folder to the /assets URL path""",
      Seq()
    )
  )


  def routes: PartialFunction[RequestHeader, Handler] = {
  
    // @LINE:7
    case controllers_HomeController_index0_route(params) =>
      call { 
        controllers_HomeController_index0_invoker.call(HomeController_1.index)
      }
  
    // @LINE:9
    case controllers_CountController_count1_route(params) =>
      call { 
        controllers_CountController_count1_invoker.call(CountController_0.count)
      }
  
    // @LINE:11
    case controllers_AsyncController_message2_route(params) =>
      call { 
        controllers_AsyncController_message2_invoker.call(AsyncController_3.message)
      }
  
    // @LINE:13
    case controllers_AgentExecutionController_listAllAgentExecutions3_route(params) =>
      call(params.fromPath[Int]("deployId", None)) { (deployId) =>
        controllers_AgentExecutionController_listAllAgentExecutions3_invoker.call(AgentExecutionController_5.listAllAgentExecutions(deployId))
      }
  
    // @LINE:15
    case controllers_HostController_getAllRoles4_route(params) =>
      call { 
        controllers_HostController_getAllRoles4_invoker.call(HostController_4.getAllRoles)
      }
  
    // @LINE:17
    case controllers_HostController_getRoleById5_route(params) =>
      call(params.fromPath[String]("roleId", None)) { (roleId) =>
        controllers_HostController_getRoleById5_invoker.call(HostController_4.getRoleById(roleId))
      }
  
    // @LINE:19
    case controllers_HostController_addHost6_route(params) =>
      call { 
        controllers_HostController_addHost6_invoker.call(HostController_4.addHost)
      }
  
    // @LINE:21
    case controllers_HostController_getSingleDeploymentForRoleById7_route(params) =>
      call(params.fromPath[Int]("deployId", None)) { (deployId) =>
        controllers_HostController_getSingleDeploymentForRoleById7_invoker.call(HostController_4.getSingleDeploymentForRoleById(deployId))
      }
  
    // @LINE:23
    case controllers_HostController_deployNewRole8_route(params) =>
      call { 
        controllers_HostController_deployNewRole8_invoker.call(HostController_4.deployNewRole)
      }
  
    // @LINE:25
    case controllers_HostController_getDeploymentsForHost9_route(params) =>
      call(params.fromPath[Int]("hostId", None)) { (hostId) =>
        controllers_HostController_getDeploymentsForHost9_invoker.call(HostController_4.getDeploymentsForHost(hostId))
      }
  
    // @LINE:27
    case controllers_HostController_getAllHosts10_route(params) =>
      call { 
        controllers_HostController_getAllHosts10_invoker.call(HostController_4.getAllHosts)
      }
  
    // @LINE:29
    case controllers_HostController_getAllHostsInActorSystemCluster11_route(params) =>
      call(params.fromPath[String]("actorSystemName", None)) { (actorSystemName) =>
        controllers_HostController_getAllHostsInActorSystemCluster11_invoker.call(HostController_4.getAllHostsInActorSystemCluster(actorSystemName))
      }
  
    // @LINE:31
    case controllers_HostController_getAllActorSystems12_route(params) =>
      call { 
        controllers_HostController_getAllActorSystems12_invoker.call(HostController_4.getAllActorSystems)
      }
  
    // @LINE:33
    case controllers_HostController_getAllHostsWhereRoleDeployed13_route(params) =>
      call(params.fromPath[String]("roleId", None)) { (roleId) =>
        controllers_HostController_getAllHostsWhereRoleDeployed13_invoker.call(HostController_4.getAllHostsWhereRoleDeployed(roleId))
      }
  
    // @LINE:35
    case controllers_AgentExecutionController_getAgentExecutionDetails14_route(params) =>
      call(params.fromPath[Int]("agentExecId", None)) { (agentExecId) =>
        controllers_AgentExecutionController_getAgentExecutionDetails14_invoker.call(AgentExecutionController_5.getAgentExecutionDetails(agentExecId))
      }
  
    // @LINE:37
    case controllers_MasterTypeController_getMasterType15_route(params) =>
      call(params.fromPath[Int]("masterTypeId", None)) { (masterTypeId) =>
        controllers_MasterTypeController_getMasterType15_invoker.call(MasterTypeController_2.getMasterType(masterTypeId))
      }
  
    // @LINE:39
    case controllers_MasterTypeController_getMasterTypes16_route(params) =>
      call { 
        controllers_MasterTypeController_getMasterTypes16_invoker.call(MasterTypeController_2.getMasterTypes)
      }
  
    // @LINE:41
    case controllers_MasterTypeController_getFieldById17_route(params) =>
      call(params.fromPath[Int]("fieldId", None)) { (fieldId) =>
        controllers_MasterTypeController_getFieldById17_invoker.call(MasterTypeController_2.getFieldById(fieldId))
      }
  
    // @LINE:43
    case controllers_MasterTypeController_getFieldsForMaster18_route(params) =>
      call(params.fromPath[Int]("masterTypeId", None)) { (masterTypeId) =>
        controllers_MasterTypeController_getFieldsForMaster18_invoker.call(MasterTypeController_2.getFieldsForMaster(masterTypeId))
      }
  
    // @LINE:45
    case controllers_AgentExecutionController_addNewExecution19_route(params) =>
      call { 
        controllers_AgentExecutionController_addNewExecution19_invoker.call(AgentExecutionController_5.addNewExecution)
      }
  
    // @LINE:48
    case controllers_Assets_versioned20_route(params) =>
      call(Param[String]("path", Right("/public")), params.fromPath[Asset]("file", None)) { (path, file) =>
        controllers_Assets_versioned20_invoker.call(Assets_6.versioned(path, file))
      }
  }
}

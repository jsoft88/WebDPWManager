
// @GENERATOR:play-routes-compiler
// @SOURCE:C:/Users/jorge/IdeaProjects/WebDPWManager/conf/routes
// @DATE:Thu Nov 09 09:35:47 PYST 2017


package router {
  object RoutesPrefix {
    private var _prefix: String = "/"
    def setPrefix(p: String): Unit = {
      _prefix = p
    }
    def prefix: String = _prefix
    val byNamePrefix: Function0[String] = { () => prefix }
  }
}

package com.dean.pr_po

class GlobalConfig {

    companion object {
        private val SERVER = "http://192.168.1.8/GlobalInc/"
        var pGlobalPath = "/storage/emulated/0/init/"
        val urlLogin = SERVER+"loginService.php"
        val urlVersion = SERVER+"verService.php"
        val pId_app = "R210303003"
        var pInitAppl: String = pGlobalPath + "initappl.txt"
        var baseURL: String? = null
        var pAppname: String? = null
        var pVer: String? = null
        var pDev: String? = null

        // global variable untuk login
        var username = "username"
        var password = "password"
        /*var pVer: String? = null
        var pDev: String? = null*/

    }
}
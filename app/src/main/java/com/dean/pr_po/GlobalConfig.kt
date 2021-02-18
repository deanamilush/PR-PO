package com.dean.pr_po

class GlobalConfig {

    companion object {

        private val SERVER = "http://192.168.1.8/GlobalInc/"
        val urlLogin = SERVER+"loginService.php"
        val urlVersion = SERVER+"verService.php"
        val pId_app = "A200706002"
        var username = "username"
        var password = "password"
    }
}
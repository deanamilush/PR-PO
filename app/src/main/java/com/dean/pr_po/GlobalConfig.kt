package com.dean.pr_po

class GlobalConfig {

    companion object {
        private val SERVER = "http://36.91.208.115/GlobalInc/"
        val urlLogin = SERVER+"loginService.php"
        val urlVersion = SERVER+"verService.php"
        val urlValPrPo = SERVER+"valPrPO.php"
        val urlVerifLog = SERVER+"verifLog.php"
        val pId_app = "R210303003"
    }
}
package com.dean.pr_po

class GlobalConfig {
    var pId = 0
    var pId_user: String? = null
    var pId_conn: String? = null
    var pImei: String? = null
    var pIp_webser: String? = null
    var pLast_in: String? = null
    var pAppname: String? = null
    var pVer: String? = null
    var pDev: String? = null
    var pPlant: String? = null
    var pAshost: String? = null
    var pSysnr: String? = null
    var pClient: String? = null

    companion object {
        private val SERVER = "http://192.168.1.8/GlobalInc/"
        var pGlobalPath = "/storage/emulated/0/init/"
        val urlLogin = SERVER+"loginService.php"
        val urlVersion = SERVER+"verService.php"
        val pId_app = "A200706002"
        var username = "username"
        var password = "password"
        var pInitAppl: String = pGlobalPath + "initappl.txt"



        private var baseURL: String? = null
        var urlCekVer: String? = null
        var urlVerifLog: String? = null
        var urlChangePas: String? = null
        var urlScanBarang: String? = null
        var urlVerMatnr: String? = null
        var urlRfcRes: String? = null
        private val pDirGlobal = "GlobalInc"
    }


}
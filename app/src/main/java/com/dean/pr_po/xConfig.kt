package com.dean.pr_po

class xConfig {

    var pGlobalPath = "/storage/emulated/0/init/"
    var pInitAppl = pGlobalPath + "initappl.txt"
    var pDBName = "dashappdb"
    var pDBApp = "stockopdb"
    var pId_app = "A200706002" //id_app dari t_app dashboard A200706002


    private var baseURL: String? = null
    var urlLogin: String? = null
    var urlCekVer: String? = null
    var urlVerifLog: String? = null
    var urlChangePas: String? = null
    var urlScanBarang: String? = null
    var urlVerMatnr: String? = null
    var urlRfcRes: String? = null
    private val pDirGlobal = "GlobalInc"

    // global T_LOG
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

    // global user SAP
    var pUser_sap: String? = null
    var pPass_sap: String? = null

    // global variable untuk login
    var username = "username"
    var password = "password"

    //global Variabel Aplikasi Stock Opname
    var pDate: String? = null
    var pLgnum: String? = null
    var pLgpla: String? = null
    var pPic: String? = null
    var pMenu: Int? = null

/*    public Config() {
    }*/

    /*    public Config() {
    }*/
    fun setBaseURL(baseURL: String) {
        // Set URL for PHP WebService
        this.baseURL = baseURL
        urlLogin = "$baseURL$pDirGlobal/loginService.php"
        urlCekVer = "$baseURL$pDirGlobal/verService.php"
        urlVerifLog = "$baseURL$pDirGlobal/verifLog.php"
        urlChangePas = "$baseURL$pDirGlobal/changePass.php"
        urlScanBarang = "$baseURL$pDirGlobal/valBin.php"
        urlVerMatnr = "$baseURL$pDirGlobal/valMatnr.php"
        urlRfcRes = "$baseURL$pDirGlobal/valRfcRes.php"
//        this.urlRfcRes = baseURL+pDirGlobal+"/valBin.php";
    }
}
package com.dean.pr_po

class GlobalConfig {

    companion object {
        val pId_app = "R211105001"
        var PRIMARY = "http://developer.gsg.co.id/prpo/api"
        var DEV = "http://dev.gsg.co.id/prpo/api"
        val TEST = "http://192.168.1.184:81/prpo/api"
        val urlVersion = TEST+"/log/verserv"
        val urlLogin = TEST+"/log/logserv"
        val urlValPrPo = TEST+"/rpt/valprpo"
        val urlVerifLog = TEST+"/log/verifLog"
    }
}
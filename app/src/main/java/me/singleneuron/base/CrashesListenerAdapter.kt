package me.singleneuron.base

import com.microsoft.appcenter.crashes.CrashesListener
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog
import com.microsoft.appcenter.crashes.model.ErrorReport

class CrashesListenerAdapter : CrashesListener {
    override fun shouldProcess(report: ErrorReport?): Boolean {
        if (report==null) return false
        if (report.stackTrace.contains("me.",true)) return true
        if (report.stackTrace.contains("nil.nadph",true)) return true
        return false
    }

    override fun shouldAwaitUserConfirmation(): Boolean {
        return false
    }

    override fun getErrorAttachments(report: ErrorReport?): MutableIterable<ErrorAttachmentLog>? {
        return null
    }
    override fun onBeforeSending(report: ErrorReport?) {}
    override fun onSendingFailed(report: ErrorReport?, e: Exception?) {}
    override fun onSendingSucceeded(report: ErrorReport?) {}
}
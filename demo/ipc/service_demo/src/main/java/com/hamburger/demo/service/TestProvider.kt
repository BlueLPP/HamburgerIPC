package com.hamburger.demo.service

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.hamburger.common.TestInterface
import com.hamburger.ipc.log.Logger
import com.hamburger.ipc.server.IpcContentProvider

class TestProvider : IpcContentProvider() {

    init {
        Logger.setInterfaceLog {
            Log.i("[Hamburger]", "[service][interface] $it")
        }
        Logger.setInternalLog {
            Log.i("[Hamburger]", "[service][internal] $it")
        }
        Logger.setIpcLog {
            Log.i("[Hamburger]", "[service][IPC] $it")
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? {
        throw UnsupportedOperationException()
    }

    override fun getType(uri: Uri): String? {
        return "$uri"
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException()
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        throw UnsupportedOperationException()
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int {
        throw UnsupportedOperationException()
    }

    override fun register(): List<IpcObjectMap> {
        return listOf(IpcObjectMap(TestInterface::class.java, TestImpl()))
    }
}
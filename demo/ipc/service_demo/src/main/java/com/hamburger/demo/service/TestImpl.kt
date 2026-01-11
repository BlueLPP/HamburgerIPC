package com.hamburger.demo.service

import android.util.Log
import com.hamburger.common.Data1
import com.hamburger.common.TestInterface
import com.hamburger.ipc.server.HamburgerBinder

private const val TAG = "Hamburger.Service"

class TestImpl : TestInterface {

    private var idx = 1
    private var data: Data1? = null

    override fun get(): String {
        printFrom()
        Log.i(TAG, "TestImpl.get")
        return "abc"
    }

    override fun set(s: String) {
        printFrom()
        Log.i(TAG, "TestImpl.set: $s")
    }

    override fun getId(): Int {
        printFrom()
        val id = idx++
        Log.i(TAG, "TestImpl.getId: $id")
        return id
    }

    override fun onlyGetId(): Int {
        printFrom()
        Log.i(TAG, "TestImpl.onlyGetId")
        return idx
    }

    override fun getData(): Data1? {
        printFrom()
        Log.i(TAG, "TestImpl.getData")
        return data
    }

    override fun setData(data: Data1?) {
        printFrom()
        Log.i(TAG, "TestImpl.setData")
        this.data = data
    }

    override fun setData(s: String, data: Data1?) {
        printFrom()
        Log.i(TAG, "TestImpl.setData: $s")
        this.data = data
    }

    override fun test(args: IntArray) {
        printFrom()
        Log.i(TAG, "TestImpl.test: ${args.contentToString()}")
    }

    override fun test(args: List<Int>) {
        printFrom()
        Log.i(TAG, "TestImpl.test: $args")
    }

    override fun test(args: Set<Int>) {
        printFrom()
        Log.i(TAG, "TestImpl.test: $args")
    }

    override fun test(args: Map<String, String>) {
        printFrom()
        Log.i(TAG, "TestImpl.test: $args")
    }

    private fun printFrom() {
        Log.i(
            TAG,
            "TestImpl.printFrom: ${HamburgerBinder.getCallPackage()}, ${HamburgerBinder.getCallingPid()}, ${HamburgerBinder.getCallingUid()}"
        )
    }
}
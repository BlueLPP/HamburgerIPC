package com.hamburger.common

import com.hamburger.ipc.Hamburger

@Hamburger("TestInterface")
interface TestInterface {

    //    @Hamburger(name = "getMethod")
    fun get(): String

    //    @Hamburger(name = "setMethod")
    fun set(s: String)

    //    @Hamburger(name = "getIdMethod")
    fun getId(): Int

    fun onlyGetId(): Int

    fun getData(): Data1?

    @Hamburger("setData")
    fun setData(data: Data1?)

    @Hamburger("setData2")
    fun setData(s: String, data: Data1?)

    @Hamburger("testIntArray")
    fun test(args: IntArray)

    @Hamburger("testList")
    fun test(args: List<Int>)

    @Hamburger("testSet")
    fun test(args: Set<Int>)

    @Hamburger("testMap")
    fun test(args: Map<String, String>)
}

data class Data1(
    var param1: String? = null,
//    @Hamburger(name = "TestInt")
    var param2: Int? = null,
    var param3: Data2? = null,
)

data class Data2(
    var param1: String = "",
    var param2: Int = 0,
)
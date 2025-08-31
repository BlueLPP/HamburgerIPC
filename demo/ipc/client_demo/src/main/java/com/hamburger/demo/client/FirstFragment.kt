package com.hamburger.demo.client

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hamburger.demo.client.databinding.FragmentFirstBinding
import com.hamburger.common.Data1
import com.hamburger.common.Data2
import com.hamburger.common.TestInterface
import com.hamburger.common.TestInterface2
import com.hamburger.ipc.HamburgerIPC

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
private const val TAG = "Hamburger.Client"

class FirstFragment : Fragment() {

    private val ipc = HamburgerIPC.Builder().uri("com.hamburger.IPCProvider").build()

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            try {
                ipc.create(TestInterface2::class.java).test()
            } catch (e: Exception) {
                Log.e(TAG, "click TestInterface2", e)
            }
            try {
                val test = ipc.create(TestInterface::class.java)
                test.set(System.currentTimeMillis().toString())
                Log.i(TAG, "set done")
                Log.i(TAG, "get: ${test.get()}")
                Log.i(TAG, "onlyGetId: ${test.onlyGetId()}")
                Log.i(TAG, "getId: ${test.getId()}")
                Log.i(TAG, "onlyGetId: ${test.onlyGetId()}")
                Log.i(TAG, "getData 1: ${test.getData()}")
                test.setData(Data1().apply {
                    param1 = "test param1"
                    param2 = 2
                    param3 = Data2("data 2", 3)
                })
                Log.i(TAG, "setData done")
                Log.i(TAG, "getData 2: ${test.getData()}")
                test.setData("test multi param", Data1().apply {
                    param1 = "test param1+"
                    param2 = 3
                    param3 = Data2("data 2+", 4)
                })
                Log.i(TAG, "setData done")
                Log.i(TAG, "getData 3: ${test.getData()}")
                test.test(intArrayOf(1, 2, 3))
                Log.i(TAG, "test array done")
                test.test(listOf(4, 5, 6))
                Log.i(TAG, "test list done")
                test.test(setOf(7, 8, 9))
                Log.i(TAG, "test set done")
                test.test(mapOf("a" to "1", "b" to "2"))
                Log.i(TAG, "test map done")
            } catch (e: Exception) {
                Log.e(TAG, "click TestInterface", e)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
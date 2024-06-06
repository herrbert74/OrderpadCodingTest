package com.justeat.orderpadcodingtest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Before
import org.junit.Test

abstract class Response {
    data class Success(val output: String) : Response()
    data class Failure(val error: String? = null) : Response()
}

interface Repository {
    suspend fun getProductData(input: String): Response
}


class MyViewModel(private val repository: Repository) : ViewModel() {

    internal val state = MutableStateFlow(MyState("", false))

    fun refreshData() {
        viewModelScope.launch {
            val data1Deferred = async { repository.getProductData("Hello") }
            val data2Deferred = async { repository.getProductData("Hi") }

            val data1 = data1Deferred.await()
            val data2 = data2Deferred.await()

            when {
                data1 is Response.Failure && data2 is Response.Failure ->
                    state.update { state.value.copy(showError = true) }

                data1 is Response.Failure && data2 is Response.Success ->
                    state.update { state.value.copy(output = data2.output, showError = false) }

                data1 is Response.Success && data2 is Response.Failure ->
                    state.update { state.value.copy(output = data1.output, showError = false) }

                data1 is Response.Success && data2 is Response.Success ->
                    state.update {
                        state.value.copy(
                            output = data1.output + data2.output,
                            showError = false
                        )
                    }
            }

        }
    }

    data class MyState(val output: String, val showError: Boolean)
}

@ExperimentalCoroutinesApi
class TestViewModel {

    val repo :Repository= mockk()

    val vm = MyViewModel(repo)

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @Test
    fun writeTestHere() = runBlocking {
        coEvery { repo.getProductData(any()) } returns Response.Success("Hi")

        vm.refreshData()

        Assert.assertEquals("HiHi", vm.state.first().output, )
    }

}

package com.getir.patika.coroutinework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URL

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //PAGE 6
        CoroutineScope(IO).launch {
            delay(5000)
            println("Waited 5 sec")
        }

        //PAGE 7
        val deferred = GlobalScope.async {
            "Merhaba"
        }
        runBlocking {
            val mesaj = deferred.await()
            println(mesaj)
        }

        //PAGE 8
        runBlocking {
            delay(5000)
            println("Main thread waited for 5 seconds")
        }

        //PAGE 9
        CoroutineScope(Dispatchers.Main).launch { //Dispatchers.Main --> UI process
            val text = "UI Updated!"
            println(text)
        }

        //PAGE 10
        CoroutineScope(IO).launch {  //Dispatchers.IO --> Web requests, file read/write
            val url = "https://example.com/api/data"
            val response = URL(url).readText()
            println(response)
        }

        //PAGE 11
        CoroutineScope(Dispatchers.Default).launch { //Dispatchers.Default --> CPU process
            var sum = 0
            for (i in 0..100000) {
                sum += i
            }
            println("Toplam: $sum")
        }

        //PAGE 12
        val url = "https://example.com/api/data"
        CoroutineScope(IO).launch {
            withContext(Dispatchers.IO) {
                val response = URL(url).readText()
                println("Veri: $response")
            }
        }
        val scope = CoroutineScope(IO)
        fun myCoroutine(job: Job) {
            scope.launch {
                withContext(job) {
                    //Process code here
                }
            }
        }

        //PAGE 13
        val scope2 = CoroutineScope(IO)
        val job = Job()
        scope.launch(job) {
            //Code here
        }
        job.cancel()

        //PAGE 15
        fun getNumbers(delay: Long): List<Int> {
            val numbers = mutableListOf<Int>()
            for (i in 1..10) {
                Thread.sleep(delay)
                numbers.add(i)
            }
            return numbers
        }

        fun main2() {
            val numbers = getNumbers(100)
            for (nmber in numbers) {
                println(nmber)
            }
        }

        //PAGE 16
        fun getNumbersFlow(delay: Long): Flow<Int> {
            return flow {
                for (i in 1..10) {
                    emit(i)
                    delay(delay)
                }
            }
        }

        fun main3() {
            val flow = getNumbersFlow(100)
            launch {
                for (number in flow) {
                    println(number)
                }
            }
        }

        //PAGE 17
        val scope3 = CoroutineScope(IO)
        scope.launch {
            //Code here
        }

        //PAGE 18
        val supervisorJob = SupervisorJob()
        val scope4 = CoroutineScope(supervisorJob)
        scope4.launch { //1. Coroutine
            //Code here
        }
        scope4.launch { // 2.Coroutine
            //Code here
        }

        //PAGE 19
        val unsupervisedJob = SupervisorJob()
        val scope5 = CoroutineScope(unsupervisedJob)
        scope5.launch {
            //Code here
        }
        scope5.launch {
            //Code here
        }

        //PAGE 20
        val uiScope = MainCoroutineScope()
        uiScope.launch {
            //Code here
        }

        //PAGE 21
        val supervisorScope = SupervisorScope()
        supervisorScope.launch {
            //Code here
        }

        //PAGE 23
        val flow = flow {
            emit(1)
            emit(2)
            emit(3)
        }
        flow.collect { value -> println(value) } //print emitted values

        //PAGE 24
        val sayilar = listOf(1, 2, 3)
        val flow = sayilar.asFlow()
        flow.collect { valCol -> println(valCol) }

        //PAGE 25
        val sayilarFlow = flow {
            emit(1)
            emit(2)
            emit(3)
        }
        val karelerFlow = sayilarFlow.map { sayi -> sayi * sayi }
        karelerFlow.collect { kare -> println(kare) }

        //PAGE 26
        val sayilarFlow2 = flow {
            emit(1)
            emit(2)
            emit(3)
            emit(4)
            emit(5)
        }
        val teklerFlow = sayilarFlow2.filter { sayi -> sayi % 2 == 1 }
        teklerFlow.collect { tek -> println(tek) }

        //PAGE 27
        suspend fun performRequest(request: Int): String {
            delay(1000)
            return "response $request"
        }

        fun main4() = runBlocking<Unit> {
            (1..3).asFlow()
                .transform { request ->
                    emit("Making request $request")
                    emit(performRequest(request))
                }
                .collect { response -> println(response) }
        }

        //PAGE 28
        val sayilarFlow3 = flow {
            emit(1)
            emit(2)
            emit(3)
            emit(4)
            emit(5)
        }
        sayilarFlow3.flowOn(Dispatchers.IO)
            .collect { sayi -> println(sayi) }

        //PAGE 31
        fun main5() = runBlocking {
            val kanal = Channel<Int>()
            launch {
                println("Sender started")
                kanal.send(10)
                println("Val sent: 10")
            }
            launch {
                println("Getter started")
                val deger = kanal.receive()
                println("Got Value: $deger")
            }
        }

        //PAGE 32
        fun main6() = runBlocking {
            val kanal = Channel<String>()
            launch {
                println("Sender started")
                for (i in 1..5) {
                    kanal.send("Mesaj $i")
                    println("Message sent: Mesaj $i")
                }
            }
            launch {
                println("Getter started")
                while (true) {
                    val mesaj = kanal.receive()
                    println("Got Message: $mesaj")
                }
            }
        }

        //PAGE 33
        fun main7() = runBlocking {
            val kanal = ticker(1000, 0)
            launch {
                println("Getter started")
                while (true) {
                    kanal.receive()
                    println("Got click")
                }
            }
            delay(5000)
        }
    }
}
package com.example.modul_4_pract_1_4

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.modul_4_pract_1_4.ui.theme.Modul_4_pract_14Theme
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import java.io.File
import kotlin.system.measureTimeMillis
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            Modul_4_pract_14Theme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    Modul_4_pract_14Theme {
//        Greeting("Android")
//    }
//}


class MainActivity : ComponentActivity() {

    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Modul_4_pract_14Theme {
            }
        }

        loadAllData()
    }

    fun loadAllData() {
        val time = measureTimeMillis {
            runBlocking {

                val usersDeferred = async {
                    try {
                        LoadUsers()
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка загрузки пользователей: ${e.message}")
                        emptyList<String>()
                    }
                }
                val salesDeferred = async {
                    try {
                        LoadSales()
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка загрузки продаж: ${e.message}")
                        emptyMap<String, Int>()
                    }
                }

                val weatherDeferred = async {
                    try {
                        LoadWeather()
                    } catch (e: Exception) {
                        Log.e(TAG, "Ошибка загрузки погоды: ${e.message}")
                        emptyList<String>()
                    }
                }

                val users = usersDeferred.await()
                val sales = salesDeferred.await()
                val weather = weatherDeferred.await()

                Log.d(TAG, "Пользователи: $users")
                Log.d(TAG, "Продажи: $sales")
                Log.d(TAG, "Погода: $weather")

            }
        }
        Log.d(TAG, "Общее время выполнения: ${time / 1000.0} секунд")


    }
    suspend fun LoadUsers():List<String>{
        delay(1800)

        if (Random.nextFloat() < 0.7f) {
            throw Exception("Ошибка соединения при загрузке пользователей")
        }

        val jsonString = loadJsonFromAssets("users.json")
        val listType = object : TypeToken<List<User>>() {}.type
        val users: List<User> = Gson().fromJson(jsonString, listType)
        val names = users.map { it.name }
        return names
    }

    suspend fun LoadSales():Map<String, Int>{
        delay(1200)

        if (Random.nextFloat() < 0.2f) {
            throw Exception("Ошибка сервера при загрузке продаж")
        }

        val jsonString = loadJsonFromAssets("sales.json")
        val salesData: SalesData = Gson().fromJson(jsonString, SalesData::class.java)
        val salesMap = salesData.items.associate { it.product to it.qty }
        return salesMap

    }

    suspend fun LoadWeather():List<String>{
        delay(2500)

        if (Random.nextFloat() < 0.2f) {
            throw Exception("Таймаут при загрузке погоды")
        }

        val jsonString = loadJsonFromAssets("weather.json")
        val listType = object : TypeToken<List<Weather>>() {}.type
        val weatherList: List<Weather> = Gson().fromJson(jsonString, listType)
        val weatherStrings = weatherList.map { "${it.city}: ${it.temp}°C" }
        return weatherStrings
    }


    private fun loadJsonFromAssets(filename: String): String {
        return assets.open(filename).bufferedReader().use { it.readText() }
    }


    data class User(
        val id: Int,
        val name: String
    )
    data class SaleItem(
        val product: String,
        val qty: Int,
        val revenue: Int
    )

    data class SalesData(
        val today: String,
        val items: List<SaleItem>
    )
    data class Weather(
        val city: String,
        val temp: Int,
        val condition: String
    )

}





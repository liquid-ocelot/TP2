package com.e.tp2

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.annotation.RequiresApi
import com.e.tp2.databinding.ActivityAuthenticationBinding
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

interface IAuthentication

class Authentication : AppCompatActivity(), IAuthentication {

    private lateinit var binding: ActivityAuthenticationBinding
    private var result:String? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.authenticateButton.setOnClickListener { v -> Thread(//lorsqu'on clique sur le bouton on lance l'authentification pour ne pas bloquer le thread UI
            Runnable() {
                lateinit var url: URL
                val username = binding.loginBox.text.toString()
                val password = binding.passwordBox.text
                val cred = "$username:$password"
                val basicAuth: String = "Basic " + Base64.encodeToString(cred.toByteArray(), Base64.NO_WRAP)


                try {
                    url = URL("https://httpbin.org/basic-auth/bob/sympa")
                    val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
                    urlConnection.setRequestProperty("Authorization", basicAuth)
                    try {
                        val input:InputStream = BufferedInputStream(urlConnection.inputStream)
                        val s: String = readStream(input)
                        Log.i("JFL", s)
                        val json = JSONObject(s)
                        val res = json["authenticated"].toString()
                        result = res

                        binding.resultView.post(Runnable { binding.resultView.text = result


                        })


                    }finally {
                        urlConnection.disconnect()
                    }
                }catch (e:MalformedURLException){
                    e.printStackTrace()
                }catch (e:IOException){
                    e.printStackTrace()
                }

            }).start()
        }

    }


    @RequiresApi(Build.VERSION_CODES.N)
    private fun readStream(input:InputStream): String{
        return BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
            .collect(Collectors.joining("\n"))
    }


}
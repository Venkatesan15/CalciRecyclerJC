package com.example.calculatorjc

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat

class MainActivity : AppCompatActivity() {


    private val fragmentTwo = FragmentTwo()
    private val fragmentOne = FragmentOne()


    companion object {

        var containerOne by mutableStateOf(ViewCompat.generateViewId())
        var containerTwo by mutableStateOf(ViewCompat.generateViewId())

        const val fragmentOneTag = "FragmentOne"
        const val fragTwoArg = "fragTwoArg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            Container(savedInstanceState)
        }
    }

    @Composable
    fun Container(savedInstanceState: Bundle?) {


                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

                    SetFragmentOne(Modifier.fillMaxSize(), savedInstanceState)
                }
                else if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)  {


                    Row {

                        SetFragmentOne(
                            Modifier
                                .weight(1f)
                                .fillMaxSize(), savedInstanceState
                        )


                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                        ) {

                            AndroidView(factory = { context ->
                                FrameLayout(context).apply {
                                    id = containerTwo
                                }
                            },
                            update = {
                                if(savedInstanceState?.getBundle(fragTwoArg) != null) {

                                    fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)
                                    supportFragmentManager.beginTransaction().replace(it.id, fragmentTwo, FragmentOne.frgBTag).commit()
                                }
                            })
                        }
                    }
                }

    }

    @Composable
    fun SetFragmentOne(modifier: Modifier, savedInstanceState: Bundle?) {

        val containerOneR by rememberSaveable { mutableStateOf(View.generateViewId()) }
        val containerTwoR by rememberSaveable { mutableStateOf(View.generateViewId()) }

        containerOne = containerOneR
        containerTwo = containerTwoR

        Box(modifier = modifier) {

            AndroidView( factory = { context ->

                FrameLayout(context).apply {
                    id = containerOne
                }
            },
                update = {
                    if (savedInstanceState?.getBundle(fragTwoArg) != null) {

                        setFragmentTwo(savedInstanceState, it)
                    }
                    else {
                        supportFragmentManager.beginTransaction()
                            .replace(it.id, FragmentOne(), fragmentOneTag).commit()
                    }
                })

        }

    }
    private fun setFragmentTwo(savedInstanceState: Bundle, frameLayout: FrameLayout) {

        fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //When we start from landscape
            val frgA = supportFragmentManager.findFragmentById(frameLayout.id)
            if(frgA == null) {
                supportFragmentManager.beginTransaction().replace(frameLayout.id, fragmentOne, fragmentOneTag).commit()
            }

            val frgB = supportFragmentManager.findFragmentByTag(FragmentOne.frgBTag)
            if(frgB  != null) supportFragmentManager.beginTransaction().remove(frgB).commit()

            supportFragmentManager.beginTransaction().apply {
                println("Add frgOne to stack")
                addToBackStack(fragmentOneTag)
                replace(frameLayout.id, fragmentTwo, FragmentOne.frgBTag).commit()
            }

        } else {
            supportFragmentManager.popBackStack()
            supportFragmentManager.beginTransaction().replace( frameLayout.id, fragmentOne, fragmentOneTag ).commit()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {

        val frgTwo = supportFragmentManager.findFragmentByTag(FragmentOne.frgBTag)
        outState.putBundle( fragTwoArg, frgTwo?.arguments)

        super.onSaveInstanceState(outState)

    }

    override fun onBackPressed() {
        val frgB = supportFragmentManager.findFragmentByTag(FragmentOne.frgBTag)

        FragmentTwo.inputOne = ""
        FragmentTwo.inputTwo = ""
        if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && frgB != null) {
            supportFragmentManager.beginTransaction().remove(frgB).commit()
        }
        else super.onBackPressed()
    }

}
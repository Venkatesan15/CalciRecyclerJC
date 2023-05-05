package com.example.calculatorjc

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentOnAttachListener
import com.example.calculatorjc.ui.theme.CalculatorJCTheme


class MainActivity : AppCompatActivity(), FragmentOne.Action,
    FragmentTwo.Result {

    private lateinit var fragmentTwo: FragmentTwo
    private lateinit var fragmentOne: FragmentOne
    private var containerOne = 0
    private var containerTwo = 1

    var isBackBtnVisible by mutableStateOf(false)

    companion object {
        const val fragmentOneTag = "FragmentOne"
        const val frgBTag = "FragmentB"
        const val fragTwoArg = "fragTwoArg"
        const val fragmentOneArg = "FragmentOneArg"
        const val isBackBtnVisibleTag ="IsBackBtnVisible"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!this::fragmentOne.isInitialized) fragmentOne = FragmentOne()
        if (!this::fragmentTwo.isInitialized) fragmentTwo = FragmentTwo()

        val listener = FragmentOnAttachListener { _, fragment ->
            onAttachFragments(fragment = fragment)
        }
        supportFragmentManager.addFragmentOnAttachListener(listener)

        if(savedInstanceState != null && savedInstanceState.getBoolean(isBackBtnVisibleTag)) isBackBtnVisible = true
        setContent {

            InitializeContainers(savedInstanceState)

            CalculatorJCTheme() {
                Scaffold(
                    topBar = { if(isBackBtnVisible) AddTopAppBar() },
                    content = { paddingValues -> InflateFragments(paddingValues) }
                )
            }

        }
        super.onCreate(savedInstanceState)
    }

    @Composable
    private fun InflateFragments(paddingValues: PaddingValues) {

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(MaterialTheme.colors.onBackground)
            ) {
                LoadInputFragment()
                LoadActionFragment()
            }

        } else {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LoadActionFragment(
                    modifier = Modifier
                        .width(0.dp)
                        .weight(1f))
                Divider(modifier = Modifier.fillMaxHeight().width(5.dp), color = MaterialTheme.colors.onPrimary)

                LoadInputFragment( modifier = Modifier.weight(1f) )
            }
        }
    }
    @Composable
    private fun InitializeContainers(savedInstanceState: Bundle?) {

        val containerOne by rememberSaveable {
            mutableStateOf(ViewCompat.generateViewId())
        }
        val containerTwo by rememberSaveable {
            mutableStateOf(ViewCompat.generateViewId())
        }

        this.containerOne = containerOne
        this.containerTwo = containerTwo

        if (savedInstanceState?.getBundle(fragTwoArg) != null) {
            fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)
        }
        if (savedInstanceState?.getBundle(fragmentOneArg) != null) {
            fragmentOne.arguments = savedInstanceState.getBundle(fragmentOneArg)
        }
    }

    @Composable
    private fun LoadInputFragment() {
        AndroidView(
            factory = {
            FrameLayout(it).apply {
                id = containerTwo

                supportFragmentManager.beginTransaction()
                    .replace(containerTwo, fragmentTwo, frgBTag)
                    .hide(fragmentTwo)
                    .commit()
                if (fragmentTwo.arguments != null) {
                    supportFragmentManager.beginTransaction()
                        .show(fragmentTwo).commit()
                }
            }
        })
    }

    @Composable
    private fun LoadActionFragment() {
        AndroidView(
            factory = {
                FrameLayout(it).apply {
                    id = containerOne

                    supportFragmentManager.beginTransaction()
                        .replace(containerOne, fragmentOne, fragmentOneTag)
                        .commit()
                    if (fragmentTwo.arguments != null) {
                        supportFragmentManager.beginTransaction().hide(fragmentOne)
                            .commit()


                    }
                }
            },
        )
    }

    @Composable
    private fun LoadActionFragment(modifier: Modifier) {
        AndroidView(
            factory = {
                FrameLayout(it).apply {
                    id = containerOne

                    supportFragmentManager.beginTransaction()
                        .replace(id, fragmentOne, fragmentOneTag).commit()

                }
            }, modifier = modifier
        )
    }
    @Composable
    private fun LoadInputFragment(modifier: Modifier) {

        AndroidView(
            factory = {

                FrameLayout(it).apply {
                    id = containerTwo

                    supportFragmentManager.beginTransaction()
                        .add(id, fragmentTwo, frgBTag)
                        .hide(fragmentTwo)
                        .commit()

                    if (fragmentTwo.arguments != null) {

                        supportFragmentManager.beginTransaction().show(fragmentTwo)
                            .commit()

                    }
                }

            }, modifier = modifier
        )
    }


    override fun onSaveInstanceState(outState: Bundle) {

        outState.putBundle(fragmentOneArg, fragmentOne.arguments)
        outState.putBundle(fragTwoArg, fragmentTwo.arguments)

        outState.putBoolean(isBackBtnVisibleTag, isBackBtnVisible)
        super.onSaveInstanceState(outState)

    }


    //This function is used for send the Action(Add, Subtract,..) from FragmentOne to FragmentTwo
    override fun sendActionText(text: String): Boolean {

        isBackBtnVisible = true

        val bundle = Bundle()

        bundle.putString(FragmentTwo.buttonText, text)

        fragmentTwo.arguments = bundle

        supportFragmentManager.beginTransaction().show(fragmentTwo).commit()
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            supportFragmentManager.beginTransaction().hide(fragmentOne).commit()
        }
        fragmentTwo.updateActionBtnText(text)

        return false
    }

    //This function is used for get Result from FragmentTwo and send it to FragmentOne
    override fun sendResult(result: String) {

        fragmentOne.arguments = Bundle()
        fragmentTwo.arguments = null
        fragmentOne.addResultIntoAdapter(result)
        supportFragmentManager.beginTransaction().hide(fragmentTwo).show(fragmentOne).commit()

    }

    private fun onAttachFragments(fragment: Fragment) {
        if(fragment is FragmentOne) {
            fragment.setOnActionSender(this)
        } else if(fragment is FragmentTwo) {
            fragment.setOnResultSender(this)
        }
    }



    @Composable
    fun AddTopAppBar() {

        TopAppBar(
            title = {
         },

            navigationIcon = {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            contentColor = MaterialTheme.colors.onPrimary,

            backgroundColor = MaterialTheme.colors.onBackground,
            elevation = 0.dp
        )

    }


    private fun onBackClick() {

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

            if (imm != null) {
                if(imm.hideSoftInputFromWindow(view.windowToken, 0)) return
            }
        }

        if (fragmentTwo.arguments != null) {
            isBackBtnVisible = false
            supportFragmentManager.beginTransaction().hide(fragmentTwo).show(fragmentOne).commit()
            fragmentTwo.resetInputs()
            fragmentTwo.arguments = null
        } else if(fragmentOne.arguments != null) {
            isBackBtnVisible = false
            fragmentOne.addActionsIntoAdapter()
            fragmentOne.arguments = null
        }
    }


    override fun onBackPressed() {

        if (fragmentTwo.arguments != null) {
            supportFragmentManager.beginTransaction().hide(fragmentTwo).show(fragmentOne).commit()
            fragmentTwo.resetInputs()
            fragmentTwo.arguments = null
        } else super.onBackPressed()
    }
}
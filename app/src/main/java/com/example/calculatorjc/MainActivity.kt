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
import androidx.compose.foundation.layout.width
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

    var isNavBtnVisible by mutableStateOf(false)

    companion object {
        const val fragmentOneTag = "FragmentOne"
        const val frgBTag = "FragmentB"
        const val fragTwoArg = "fragTwoArg"
        const val fragmentOneArg = "FragmentOneArg"
        const val isBackBtnVisibleTag ="IsBackBtnVisible"

        const val isFrgBVisibleNow = "IsFrgBVisibleNow"
    }


    private var isFrgBVisible by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        if (!this::fragmentOne.isInitialized) fragmentOne = FragmentOne()
        if (!this::fragmentTwo.isInitialized) fragmentTwo = FragmentTwo()

        val listener = FragmentOnAttachListener { _, fragment ->
            onAttachFragments(fragment = fragment)
        }
        supportFragmentManager.addFragmentOnAttachListener(listener)

        if(savedInstanceState != null && savedInstanceState.getBoolean(isBackBtnVisibleTag)) isNavBtnVisible = true
        setContent {

            InitializeContainers(savedInstanceState)

            CalculatorJCTheme() {
                Scaffold(
                    topBar = { if(isNavBtnVisible) AddTopAppBar() },
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
                if(!isFrgBVisible) {
                    LoadActionFragment()
                }
                else {
                    LoadInputFragment(frgBVisible = isFrgBVisible)
                }
            }

        } else {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                if(!isFrgBVisible) {
                    LoadActionFragment(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(5.dp), color = MaterialTheme.colors.onPrimary
                    )

                    Row(modifier = Modifier.weight(1f)) {}
                }

                if(isFrgBVisible) {
                    LoadActionFragment(
                        modifier = Modifier
                            .width(0.dp)
                            .weight(1f),
                    )

                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(5.dp), color = MaterialTheme.colors.onPrimary
                    )
                    LoadInputFragment(
                        modifier = Modifier
                            .weight(1f)
                            .width(0.dp),
                        isFrgBVisible
                    )
                }
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

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

        if (savedInstanceState?.getBundle(fragTwoArg) != null) {
            fragmentTwo.arguments = savedInstanceState.getBundle(fragTwoArg)
            frgB?.arguments = savedInstanceState.getBundle(fragTwoArg)
        }
        if (savedInstanceState?.getBundle(fragmentOneArg) != null) {
            fragmentOne.arguments = savedInstanceState.getBundle(fragmentOneArg)
        }
        if(savedInstanceState != null && savedInstanceState.getBoolean(isFrgBVisibleNow)) {
            isFrgBVisible = savedInstanceState.getBoolean(isFrgBVisibleNow)
        }
    }

    @Composable
    private fun LoadInputFragment(modifier: Modifier = Modifier, frgBVisible: Boolean) {

        AndroidView(
            factory = {

                FrameLayout(it).apply {
                    id = containerTwo

                    if(frgBVisible) {

                        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
                        if (frgB != null) {

                            val frg = FragmentTwo()
                            if(fragmentTwo.arguments != null) frg.arguments = fragmentTwo.arguments
                            else frg.arguments = frgB.arguments
                            supportFragmentManager.beginTransaction()
                                .replace(containerTwo, frg, frgBTag)
                                .commit()
                        } else {

                            supportFragmentManager.beginTransaction()
                                .replace(containerTwo, fragmentTwo, frgBTag)
                                .commit()
                        }
                    }
                }
            }, modifier = modifier)

    }

    @Composable
    private fun LoadActionFragment(modifier: Modifier = Modifier) {

        AndroidView(
            factory = {
                FrameLayout(it).apply {
                    id = containerOne
                    supportFragmentManager.beginTransaction()
                        .replace(containerOne, fragmentOne, fragmentOneTag)
                        .commit()

                    val frg: Fragment? = supportFragmentManager.findFragmentByTag(fragmentOneTag)

                    if(frg != null) {

                        val rep = FragmentOne()
                        rep.arguments = frg.arguments
                        supportFragmentManager.beginTransaction()
                            .replace(containerOne, rep, fragmentOneTag)
                            .commit()
                    } else {
                        supportFragmentManager.beginTransaction()
                            .replace(containerOne, fragmentOne, fragmentOneTag)
                            .commit()
                    }
                }
            }, modifier = modifier,
        )
    }



    override fun onSaveInstanceState(outState: Bundle) {

        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

        if (frgA != null ) outState.putBundle(fragmentOneArg, frgA.arguments)
        if (frgB != null) {
            outState.putBundle(fragTwoArg, frgB.arguments)}

        outState.putBoolean(isBackBtnVisibleTag, isNavBtnVisible)
        outState.putBoolean(isFrgBVisibleNow, isFrgBVisible)
        super.onSaveInstanceState(outState)

    }


    //This function is used for send the Action(Add, Subtract,..) from FragmentOne to FragmentTwo
    override fun sendActionText(text: String): Boolean {

        isNavBtnVisible = true
        isFrgBVisible = true

        val bundle = Bundle()

        bundle.putString(FragmentTwo.buttonText, text)

       fragmentTwo.arguments = bundle

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

        if(frgB != null) {
            frgB.arguments = bundle
            (frgB as FragmentTwo).updateActionBtnText(text)

        }

        fragmentTwo.updateActionBtnText(text)

        return false
    }

    //This function is used for get Result from FragmentTwo and send it to FragmentOne
    override fun sendResult(result: String) {

        isFrgBVisible = false

        val bundle = Bundle()
        bundle.putString(FragmentOne.resultAvailable, result)
        //fragmentOne.arguments = bundle

        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
        if(frgA != null) frgA.arguments = bundle
        fragmentTwo.arguments = null
        fragmentOne.addResultIntoAdapter(result)
//        supportFragmentManager.beginTransaction().hide(fragmentTwo).show(fragmentOne).commit()

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

        if (isFrgBVisible) {
            isNavBtnVisible = false

            val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
            (frgB as FragmentTwo).resetInputs()
            isFrgBVisible = false
            frgB.arguments = null

        } else  {

            val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
            if(frgA != null) {
                isNavBtnVisible = false
                (frgA as FragmentOne).addActionsIntoAdapter()
                frgA.arguments = null
            }
        }
    }


    override fun onBackPressed() {

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)

        if(frgA?.arguments == null) isNavBtnVisible = false
        if (isFrgBVisible) {
//            supportFragmentManager.beginTransaction().hide(fragmentTwo).show(fragmentOne).commit()
            (frgB as FragmentTwo).resetInputs()
            isFrgBVisible = false
            frgB.arguments = null
        } else super.onBackPressed()
    }
}
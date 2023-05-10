package com.example.calculatorjc


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.calculatorjc.ui.theme.black
import com.example.calculatorjc.ui.theme.pink


class FragmentOne : Fragment() {

    private var resultText = ""

    private val buttonItem = 1
    private val resultItem = 2

    private lateinit var addObj: ActionOrResItem
    private lateinit var subObj: ActionOrResItem
    private lateinit var mulObj: ActionOrResItem
    private lateinit var divObj: ActionOrResItem

    private lateinit var reset: String

    private val actionOrResItems = mutableStateListOf<ActionOrResItem>()

    private lateinit var callBack: Action


    companion object {

        const val resultAvailable = "ResultAvailable"
    }

    interface Action {
        fun sendActionText (text: String): Boolean
    }

    fun setOnActionSender(callBack: Action) {
        this.callBack = callBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialize()

        return ComposeView(requireContext()).apply {

            setContent {
                InflateActions(actionOrResItems)
            }

        }
    }

    private fun initialize() {

        addObj = ActionOrResItem(buttonItem, ResourcesClass.addBtn)
        subObj = ActionOrResItem(buttonItem, ResourcesClass.subtractBtn)
        mulObj = ActionOrResItem(buttonItem, ResourcesClass.multiply)
        divObj = ActionOrResItem(buttonItem, ResourcesClass.division)

        reset = ResourcesClass.reset

        //This function will add result(if result available) and reset button into the adapter when orientation change
        addResult()

        if (actionOrResItems.size == 0) addActionsIntoAdapter()

    }

    private fun addResult() {

        if (arguments?.getString(resultAvailable) != null) {

            actionOrResItems.add(
                ActionOrResItem(
                    resultItem,
                    arguments?.getString(resultAvailable)!!
                )
            )
            actionOrResItems.add(
                ActionOrResItem(
                    buttonItem,
                    ResourcesClass.reset
                )
            )
        }
    }
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun InflateActions(actionOrResItems: SnapshotStateList<ActionOrResItem>) {

        val isDarkTheme = isSystemInDarkTheme()
        val backGround = if(isDarkTheme) black else pink

        var visible by remember {
            mutableStateOf(false)
        }
        LaunchedEffect (key1 = Unit, block = {
            visible = true
        })

        AnimatedVisibility(visible = visible,
        enter = slideInVertically(
            tween(
                durationMillis = 400,
            ),initialOffsetY = {it},

        )
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize().background(backGround),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                items(actionOrResItems.size, key = {it}) { index ->

                    if (actionOrResItems[index].itemType == buttonItem ) {
                        ButtonItem(index = index, Modifier.width(100.dp).animateItemPlacement())
                    } else {
                        Text(
                            text = actionOrResItems[index].text,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(20.dp).animateItemPlacement(),
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun ButtonItem(index: Int, modifier: Modifier) {

        var onClick by remember {
            mutableStateOf(false)
        }
        var actionText by remember {
            mutableStateOf("")
        }

        if (onClick) onClick = callBack.sendActionText(actionText)

        Button(modifier = modifier,
            onClick = {
                actionText = actionOrResItems[index].text
                if (actionText == reset) {
                    (activity as MainActivity).isNavBtnVisible = false
                    resultText = ""
                    arguments = null
                    actionOrResItems.clear()
                    addActionsIntoAdapter()
                } else onClick = true
            })
        {
            Text(text = actionOrResItems[index].text)
        }
    }

     fun addActionsIntoAdapter() {

        actionOrResItems.clear()
        actionOrResItems.add(addObj)
        actionOrResItems.add(subObj)
        actionOrResItems.add(mulObj)
        actionOrResItems.add(divObj)
    }
    override fun onSaveInstanceState(outState: Bundle) {

        if (resultText.isNotEmpty()) {
            arguments?.putString(resultAvailable, resultText)
        }
        super.onSaveInstanceState(outState)
    }

    fun addResultIntoAdapter(result: String) {

        resultText = result
        actionOrResItems.clear()
        actionOrResItems.add(ActionOrResItem(resultItem, result))
        actionOrResItems.add(ActionOrResItem(buttonItem, ResourcesClass.reset))
    }

}
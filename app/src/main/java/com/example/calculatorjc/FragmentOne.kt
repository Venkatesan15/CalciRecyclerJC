package com.example.calculatorjc


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
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

        addObj = ActionOrResItem(buttonItem, resources.getString(R.string.add_btn))
        subObj = ActionOrResItem(buttonItem, resources.getString(R.string.sub_btn))
        mulObj = ActionOrResItem(buttonItem, resources.getString(R.string.mul_btn))
        divObj = ActionOrResItem(buttonItem, resources.getString(R.string.div_btn))

        reset = resources.getString(R.string.reset_btn)

        //This function will add result(if result available) and reset button when orientation change
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
                    resources.getString(R.string.reset_btn)
                )
            )
        }
    }
    @Composable
    fun InflateActions(actionOrResItems: SnapshotStateList<ActionOrResItem>) {

        var visible by remember {
            mutableStateOf(false)
        }
        LaunchedEffect(key1 = Unit, block = {

                visible = true

        })

        AnimatedVisibility(visible = visible,
        enter = slideInVertically(
            tween(
                durationMillis = 400,
                easing = FastOutSlowInEasing
            ),initialOffsetY = {it},

        )
        ) {


            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(actionOrResItems.size) { index ->

                    if (actionOrResItems[index].itemType == buttonItem ) {
                        ButtonItem(index = index)
                    } else {
                        Text(
                            text = actionOrResItems[index].text,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(20.dp),
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun ButtonItem(index: Int) {

        var onClick by remember {
            mutableStateOf(false)
        }
        var actionText by remember {
            mutableStateOf("")
        }

        if (onClick) onClick = callBack.sendActionText(actionText)

        Button(modifier = Modifier.width(100.dp),
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
        actionOrResItems.add(ActionOrResItem(buttonItem, "reset"))
    }

}
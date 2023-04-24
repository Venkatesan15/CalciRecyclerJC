package com.example.calculatorjc

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment


class FragmentOne : Fragment() {

    private val fragmentTwo = FragmentTwo()

    private val actionItem = 1

    private val addObj = ActionOrResItem(actionItem, "Add")
    private val subObj = ActionOrResItem(actionItem, "Subtract")
    private val mulObj = ActionOrResItem(actionItem, "Multiply")
    private val divObj = ActionOrResItem(actionItem, "Division")


    companion object {

        val actionOrResItems = mutableStateListOf<ActionOrResItem>()

        var resetButtonVisible by mutableStateOf(false)
        const val frgBTag = "FragmentB"
        const val action = "Action"
        var actionPage by mutableStateOf(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return ComposeView(requireContext()).apply {
            setContent {

                if(actionOrResItems.size == 0) addIntoViewsList()
                SetActions()
            }
        }
    }

    @Composable
    fun SetActions() {

        var onClick by remember {
            mutableStateOf(false)
        }
        val bundle by remember {
            mutableStateOf(Bundle())
        }

        if (onClick) onClick = createFragmentTwo(bundle = bundle)

        Column(modifier = Modifier.fillMaxSize().background(Color.Cyan)) {

            LazyColumn(
                modifier = Modifier
                    .weight(1F)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                items(actionOrResItems.size) { index ->
                    if (actionOrResItems[index].itemType == actionItem) {
                        Button(modifier = Modifier.width(100.dp),
                            onClick = {
                                bundle.putString(action, actionOrResItems[index].text)
                                onClick = true
                            })
                        {
                            Text(text = actionOrResItems[index].text)
                        }
                    } else {
                        Text(text = actionOrResItems[index].text, fontSize = 30.sp, modifier = Modifier.padding(20.dp))
                    }
                }
            }

            if (resetButtonVisible) {
                Button(onClick = {
                    actionPage = true
                    actionOrResItems.clear()
                    addIntoViewsList()
                    resetButtonVisible = false

                },
                    Modifier
                        .wrapContentWidth()
                        .wrapContentHeight()
                        .align(Alignment.CenterHorizontally)) {
                    Text(text = "Reset")
                }
                
                Spacer(modifier = Modifier.height(40.dp))
            }
        }

    }

    @Composable
    fun createFragmentTwo(bundle: Bundle) : Boolean {

        FragmentTwo.btnText = bundle.getString(action).toString()


        fragmentTwo.arguments = bundle

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {

            parentFragmentManager.beginTransaction().apply {
                addToBackStack(MainActivity.fragmentOneTag)
                replace(MainActivity.containerOne, fragmentTwo, frgBTag).commit()
            }
        }
        else {

            val frgB = parentFragmentManager.findFragmentByTag(frgBTag)
            if(frgB != null) {
                FragmentTwo.btnText = bundle.getString(action).toString()
            }
            else {
                parentFragmentManager.beginTransaction().apply {
                    replace(MainActivity.containerTwo, fragmentTwo, frgBTag).commit()
                }
            }
        }
        return false
    }

    private fun addIntoViewsList() {
        actionOrResItems.add(addObj)
        actionOrResItems.add(subObj)
        actionOrResItems.add(mulObj)
        actionOrResItems.add(divObj)
    }

}
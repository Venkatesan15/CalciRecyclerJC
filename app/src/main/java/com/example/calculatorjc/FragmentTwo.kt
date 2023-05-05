package com.example.calculatorjc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldColors
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.lang.Exception
import java.text.DecimalFormat


class FragmentTwo : Fragment() {


    private var btnText by mutableStateOf("")
    private var inputOne by mutableStateOf("")
    private var inputTwo by mutableStateOf("")

    companion object {
        const val inputOneKey = "InputOne"
        const val inputTwoKey = "InputTwo"
        const val buttonText = "ButtonText"

    }

    lateinit var callBack: Result
    interface Result {
        fun sendResult(result: String)
    }

    fun setOnResultSender(callBack: Result) {
        this.callBack = callBack
    }

    fun updateActionBtnText(text: String) {
        btnText = text
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        if (arguments?.getString(inputOneKey) != null) inputOne = requireArguments().getString(
            inputOneKey
        )!!

        if (arguments?.getString(inputTwoKey) != null) inputTwo = requireArguments().getString(
            inputTwoKey
        )!!

        return ComposeView(requireContext()).apply {
            setContent {
                InflateContent()
            }
        }
    }

    @Composable
    fun InflateContent() {

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
        ) {
            InputOne(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 30.dp)
                    )

            InputTwo( modifier = Modifier.align(Alignment.CenterHorizontally), focusManager )

            ActionButton(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 40.dp), focusManager)
        }
    }

    @Composable
    private fun InputOne(modifier: Modifier) {


        TextField (
            value = inputOne, onValueChange = {
                if (!it.contains(',') && !it.contains(' ') && !it.contains('-')) {
                    inputOne = it
                }
            },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            singleLine = true,
            label = { Text(resources.getString(R.string.number_One),  color = MaterialTheme.colors.onPrimary) },
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onPrimary)

        )
    }

    @Composable
    private fun InputTwo(modifier: Modifier, focusManager: FocusManager) {

        TextField (
            value = inputTwo,
            onValueChange = {
                if (!it.contains(',')) {
                    inputTwo = it
                }
            },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            singleLine = true,
            label = { Text(resources.getString(R.string.number_Two), color = MaterialTheme.colors.onPrimary)},
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onPrimary),


        )
    }

    @Composable
    private fun ActionButton(modifier: Modifier, focusManager: FocusManager) {

        Button (onClick = {
            focusManager.clearFocus()
            onClick()
        }, modifier = modifier) {
            if (arguments?.getString(buttonText) != null) {
                btnText = arguments?.getString(buttonText)!!
            }
            Text(text = btnText)
        }
    }

    private fun onClick() {

        if (inputTwo == "0" && btnText == "Division") {
            Toast.makeText(context, "Divided by 0 Always infinite", Toast.LENGTH_SHORT).show()
        } else if (inputOne.isNotEmpty() && inputTwo.isNotEmpty() && inputOne != "." && inputTwo != ".") {

            generateResult(
                inputOne,
                inputTwo,
                btnText
            )

            inputOne = ""
            inputTwo = ""
            arguments = null

        } else {
            Toast.makeText(context, "Please Enter Valid Input", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateResult(input1: String, input2: String, action: String) {

        try {
            val num1 = input1.toFloat()
            val num2 = input2.toFloat()

            val ans = when (action) {
                "Add" -> (num1 + num2)
                "Subtract" -> (num1 - num2)
                "Multiply" -> (num1 * num2)
                "Division" -> (num1 / num2)
                else -> null!!
            }

            val format = DecimalFormat("0.#")

            val resultText =
                "Your Result is ${format.format(ans)} for inputs $input1 and $input2 with action $action"

            callBack.sendResult(resultText)
        }
        catch (e: Exception) {
            Toast.makeText(context, "Something went Wrong", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {
        super.onPause()
        if(inputOne.isNotEmpty()) arguments?.putString(inputOneKey, inputOne)

        if(inputTwo.isNotEmpty()) arguments?.putString(inputTwoKey, inputTwo)
    }

    fun resetInputs() {
        inputTwo = ""
        inputOne = ""
    }
}


package com.example.calculatorxml

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import kotlin.math.abs


class MainActivity : AppCompatActivity() {

    private var canAddOperation = false
    private var canAddDecimal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn0 = findViewById<Button>(R.id.btn0)
        val btn1 = findViewById<Button>(R.id.btn1)
        val btn2 = findViewById<Button>(R.id.btn2)
        val btn3 = findViewById<Button>(R.id.btn3)
        val btn4 = findViewById<Button>(R.id.btn4)
        val btn5 = findViewById<Button>(R.id.btn5)
        val btn6 = findViewById<Button>(R.id.btn6)
        val btn7 = findViewById<Button>(R.id.btn7)
        val btn8 = findViewById<Button>(R.id.btn8)
        val btn9 = findViewById<Button>(R.id.btn9)
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val btnMult = findViewById<Button>(R.id.btnMult)
        val btnDiv = findViewById<Button>(R.id.btnDiv)
        val btnAC = findViewById<Button>(R.id.btnAC)
        val btnErase = findViewById<ImageButton>(R.id.btnErase)
        val btnDot = findViewById<Button>(R.id.btnDot)
        val btnEqual = findViewById<Button>(R.id.btnEqual)
        val tvOperation = findViewById<TextView>(R.id.tvOperation)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        btn0.setOnClickListener {
            numberAction(btn0)
        }
        btn1.setOnClickListener {
            numberAction(btn1)
        }
        btn2.setOnClickListener {
            numberAction(btn2)
        }
        btn3.setOnClickListener {
            numberAction(btn3)
        }
        btn4.setOnClickListener {
            numberAction(btn4)
        }
        btn5.setOnClickListener {
            numberAction(btn5)
        }
        btn6.setOnClickListener {
            numberAction(btn6)
        }
        btn7.setOnClickListener {
            numberAction(btn7)
        }
        btn8.setOnClickListener {
            numberAction(btn8)
        }
        btn9.setOnClickListener {
            numberAction(btn9)
        }
        btnDot.setOnClickListener {
            numberAction(btnDot)
        }
        btnPlus.setOnClickListener {
            operationAction(btnPlus)
        }
        btnMinus.setOnClickListener {
            operationAction(btnMinus)
        }
        btnMult.setOnClickListener {
            operationAction(btnMult)
        }
        btnDiv.setOnClickListener {
            operationAction(btnDiv)
        }

        btnAC.setOnClickListener {
            tvOperation.text = ""
            tvResult.text = ""
            canAddDecimal = false
            canAddOperation = false
        }

        btnErase.setOnClickListener {
            val length = tvOperation.length()

            if (length > 0) {

                val lastSymbol = tvOperation.text[length-1].toString()
                var preLastSymbol = ""
                if(length > 1){
                    preLastSymbol = tvOperation.text[length-2].toString()
                }

                val regNum = Regex("\\d")
                val regOper = Regex("[\\+\\–*/]")
                val regDot = Regex("\\.")

                if(regNum.matches(lastSymbol)){
                    canAddOperation = false
                    canAddDecimal = false
                }
                else if(regOper.matches(lastSymbol)){
                    canAddOperation = true
                    canAddDecimal = true
                }
                else if (regDot.matches(lastSymbol)){
                    canAddDecimal = true
                    canAddOperation = true
                }
                else if(regNum.matches(lastSymbol) && regOper.matches(preLastSymbol)){
                    canAddOperation = false
                }
                else if(regNum.matches(lastSymbol) && regDot.matches(preLastSymbol)){
                    canAddOperation = false
                    canAddDecimal = false
                }
                tvOperation.text = tvOperation.text.substring(0, length - 1)
            }
        }

        btnEqual.setOnClickListener {
            equationAction(tvResult)
        }

    }

    fun numberAction(view: View) {
        val tvOperation = findViewById<TextView>(R.id.tvOperation)
        val regDot = Regex(".*\\.\\d*")
        if (view is Button) {
            if (view.text == ".") {
                if(canAddDecimal) {
                    if (tvOperation.text.matches(regDot)) {
                        tvOperation.append("")
                    }
                    else{
                        tvOperation.append(view.text)
                        canAddOperation = false
                        canAddDecimal = false
                    }
                }
            }
            else {
                tvOperation.append(view.text)
                canAddOperation = true
                canAddDecimal = true
            }

        }
    }

    fun operationAction(view: View){
        val tvOperation = findViewById<TextView>(R.id.tvOperation)
        if(view is Button && canAddOperation){
            tvOperation.append(view.text)
            canAddOperation=false
            canAddDecimal = false
        }
    }

    fun equationAction(tvRes: TextView){
        tvRes.text = calculateResult()

    }

    private fun calculateResult() : String {
        val digitsOperators = digitsOperators()
        if(digitsOperators.isEmpty()) return ""
        Log.d("digitsOperators", "$digitsOperators")

        val timeDivision = timeDivisionCalculate(digitsOperators)
        if(timeDivision.isEmpty()) return ""
        Log.d("timeDivision", "$timeDivision")

        val result = addSubCalc(timeDivision)

        return result.toString()
    }

    private fun digitsOperators(): MutableList<Any> {
        val tvOperation = findViewById<TextView>(R.id.tvOperation)
        val list = mutableListOf<Any>()
        var currentDigit = ""
        for(char in tvOperation.text){
            if(char.isDigit() || char == '.'){
                currentDigit += char
            }
            else{
                list.add(currentDigit.toFloat())
                currentDigit = ""
                list.add(char)
            }
        }

        if(currentDigit != "") {
            list.add(currentDigit.toFloat())
        }

        return list
    }

    private fun timeDivisionCalculate(passedList: MutableList<Any>): MutableList<Any> {
        var list = passedList
        while (list.contains('*') || list.contains('/')){
            list = calcTimesDiv(list)
        }

        return list
    }

    private fun calcTimesDiv(passedList: MutableList<Any>): MutableList<Any> {
        val newList = mutableListOf<Any>()
        var restartIndex = passedList.size

        for (i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex && i < restartIndex){
                val operator = passedList[i]
                val prevDigit = passedList[i - 1] as Float
                val nextDigit = passedList[i + 1] as Float
                Log.d("calcTimesDiv", "operator - $operator")
                Log.d("calcTimesDivt", "prevDigit - $prevDigit")
                Log.d("calcTimesDiv", "nextDigit - $nextDigit")
                when(operator){
                    '*' -> {
                        newList.add(prevDigit * nextDigit)
                        Log.d("calcTimesDiv", "newList * - $newList")
                        restartIndex = i + 1
                    }
                    '/' -> {
                        newList.add(prevDigit / nextDigit)
                        Log.d("calcTimesDiv", "newList / - $newList")
                        restartIndex = i + 1
                    }
                    else -> {
                        newList.add(prevDigit)
                        newList.add(operator)
//                        newList.add(nextDigit)
                        Log.d("calcTimesDiv", "newList else - $newList")
                    }
                }
            }
            if(i > restartIndex) {
                newList.add(passedList[i])
                Log.d("calcTimesDiv", "newList If - $newList")
            }
        }

        Log.d("calcTimesDiv", "End newList - $newList")

        return newList
    }

    private fun addSubCalc(passedList: MutableList<Any>): Float {
        var result = passedList[0] as Float

        for (i in passedList.indices){
            if(passedList[i] is Char && i != passedList.lastIndex){
                val operator = passedList[i]
                val nextDigit = passedList[i + 1] as Float
                Log.d("addSubCalc", "operator - $operator")
                Log.d("addSubCalc", "nextDigit - $nextDigit")
                when(operator){
                    '+' -> {
                        result += nextDigit
                        Log.d("addSubCalc", "result + $result")
                    }
                    '–' -> {
                        result -= nextDigit
                        Log.d("addSubCalc", "result - $result")
                    }
                }

                Log.d("addSubCalc", "result when $result")
            }
            Log.d("addSubCalc", "for - $result")
        }

        Log.d("addSubCalc", "result End - $result")

        return result
    }




}

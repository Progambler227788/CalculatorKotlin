package com.example.calculator3

import android.health.connect.datatypes.units.Percentage
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.renderscript.Sampler
import com.example.calculator3.databinding.ActivityMainBinding
import kotlin.math.sqrt

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var inputValue1: Double? = 0.0
    private var inputValue2: Double? = null
    private var currentOperator: Operator? = null
    private var result: Double? = null
    private var equation: StringBuilder = StringBuilder().append(ZERO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setListeners()
    }

    private fun setListeners(){
        for(button in getNumericButtons()){
            button.setOnClickListener{onNUmberClicked(button.text.toString())}
        }
        with(binding){
            buttonZero.setOnClickListener{onZeroClicked()}
            buttonDecimalPoint.setOnClickListener{onDecimal()}
            buttonAddition.setOnClickListener{onOperatorClicked(Operator.ADDITION)}
            buttonSub.setOnClickListener{onOperatorClicked(Operator.SUBTRACTION)}
            buttonMulti.setOnClickListener{onOperatorClicked(Operator.MULTIPLICATION)}
            buttonDivision.setOnClickListener{onOperatorClicked(Operator.DIVISION)}
            buttonEquals.setOnClickListener{onEqualsClicked()}
            buttonAllClear.setOnClickListener{onAllClearClicked()}
            buttonPlusMinus.setOnClickListener{onPlusMinus()}
            buttonPercentage.setOnClickListener{onPercentageClicked()}
            ClearButton.setOnClickListener{clearButton()}
            buttonSquareRoot.setOnClickListener{onSquareRoot()}
            //add the square root and delete the cuurrent thiing
        }
    }

    private fun onPercentageClicked(){
        if(inputValue2 == null){
            val percentage = getInputValue1() / 100
            inputValue1 = percentage
            equation.clear().append(percentage)
            updateInputOnDisplay()
        }else{
            val percentageOfValue1 = (getInputValue1()* getInputValue2()) /100
            val percentageOfValue2 = (getInputValue2()/100)
            result = when(requireNotNull(currentOperator)){
                Operator.ADDITION -> getInputValue1() + getInputValue2()
                Operator.SUBTRACTION -> getInputValue1() - getInputValue2()
                Operator.MULTIPLICATION -> getInputValue1() * getInputValue2()
                Operator.DIVISION -> getInputValue1() / getInputValue2()
              //  Operator.MODULUS -> getInputValue1() % getInputValue2()
            }
            equation.clear().append(ZERO)
            updateResultOnDisplay(isPercentage = true)
            inputValue1 = result
            result = null
            inputValue2 = null
            currentOperator = null
        }
    }
    private fun onSquareRoot(){
        if(equation.isNotEmpty()){
            result= sqrt(equation.toString().toDouble())
            equation.clear().append(result)
            updateInputOnDisplay()
        }
    }

    private fun onPlusMinus(){
        if(equation.startsWith(MINUS)){
            equation.deleteCharAt(0)
        }else{
            equation.insert(0, MINUS)
        }
        setInput()
        clearDisplay()

    }
    private fun onAllClearClicked() {
        inputValue1 = 0.0
        inputValue2 = null
        currentOperator = null
        result = null
        equation.clear().append(ZERO)
        clearDisplay()
    }
    private fun clearButton(){
        equation.clear()
        clearDisplay()
    }
    private fun onOperatorClicked(operator: Operator){
        onEqualsClicked()
        currentOperator = operator
    }
    private fun onEqualsClicked(){
        if(inputValue2 != null){
            result = calculate()
            equation.clear().append(ZERO)
            updateResultOnDisplay()
            inputValue1 = result
            result = null
            inputValue2 = null
            currentOperator = null
        }else{
            equation.clear().append(ZERO)
        }
    }
    private fun calculate(): Double {
        return when(requireNotNull(currentOperator)) {
            Operator.ADDITION -> getInputValue1() + getInputValue2()
            Operator.SUBTRACTION -> getInputValue1() - getInputValue2()
            Operator.MULTIPLICATION -> getInputValue1() * getInputValue2()
            Operator.DIVISION -> getInputValue1() / getInputValue2()
          //  Operator.MODULUS -> getInputValue1() % getInputValue2()
        }
    }
    private fun onDecimal(){
        if(equation.contains(DECIMAL_POINT))return
        equation.append(DECIMAL_POINT)
        setInput()
        updateInputOnDisplay()
    }
    private fun onZeroClicked(){
        if(equation.startsWith(ZERO))return
        onNUmberClicked(ZERO)
    }

    private fun onDoubleZeroClicked(){
        if(equation.startsWith(ZERO))return
        onNUmberClicked(Double_ZERO)
    }
    private fun getNumericButtons() = with(binding){
        arrayOf(
            buttonOne,
            buttonTwo,
            buttonThree,
            buttonFour,
            buttonFive,
            buttonSix,
            buttonSeven,
            buttonEight,
            buttonNine
        )
    }
    private fun onNUmberClicked(numberText: String){
        if (equation.startsWith(ZERO)) {
            equation.deleteCharAt(0)
        }else if(equation.startsWith("$MINUS$ZERO")){
            equation.deleteCharAt(1)
        }
        equation.append(numberText)
        setInput()
        updateInputOnDisplay()
    }
    private fun setInput(){
        if(currentOperator == null){
            inputValue1 = equation.toString().toDouble()
        }else{
            inputValue2 = equation.toString().toDouble()
        }
    }
    private fun clearDisplay(){
        with(binding){
            textInput.text = getFormattedDisplayValue(value = getInputValue1())
            textEquation.text= null
        }
    }

    private fun updateResultOnDisplay(isPercentage: Boolean =false){
        binding.textInput.text = getFormattedDisplayValue(value = result)
        var input2text = getFormattedDisplayValue(value = getInputValue2())
        if(isPercentage) input2text = "$input2text${getString(R.string.percentage)}"
        binding.textEquation.text = String.format(
            "%s %s %s",
            getFormattedDisplayValue( value = getInputValue1()),
            getOperatorSymbol(),
            input2text
        )

    }
    private fun updateInputOnDisplay(){
        if(result == null){
            binding.textEquation.text= null
        }
        binding.textInput.text = equation
    }

    private fun getInputValue1() = inputValue1 ?: 0.0
    private fun getInputValue2() = inputValue2 ?: 0.0

    private fun getOperatorSymbol(): String{
        return when(requireNotNull(currentOperator)){
            Operator.ADDITION -> getString(R.string.addition)
            Operator.SUBTRACTION ->getString(R.string.subtraction)
            Operator.MULTIPLICATION ->getString(R.string.multiplication)
            Operator.DIVISION ->getString(R.string.division)
           // Operator.MODULUS ->getString(R.string.percentage)
        }
    }

    private fun getFormattedDisplayValue(value: Double?): String?{
        val originalValue = value ?: return null
        return if (originalValue % 1 == 0.0){
            originalValue.toInt().toString()
        }else{
            originalValue.toString()
        }
    }

    enum class Operator {
        ADDITION, SUBTRACTION, MULTIPLICATION, DIVISION//, MODULUS
    }

    private companion object {
        const val DECIMAL_POINT= "."
        const val ZERO = "0"
        const val Double_ZERO = "00"
        const val MINUS = "-"
    }
}
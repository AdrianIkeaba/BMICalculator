package com.example.bmicalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import kotlinx.parcelize.Parcelize
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.xml.KonfettiView
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit


@Parcelize
class MainActivity : AppCompatActivity(), Parcelable {

    // Late initialised variables for Views in MainActivity
    lateinit var calculateBMI: Button
    lateinit var weightEdit: EditText
    lateinit var heightEdit: EditText
    lateinit var switchWeight: Switch
    lateinit var switchHeight: Switch
    lateinit var inchesEdit: EditText
    lateinit var reset: ImageView
    lateinit var layout: LinearLayout
    lateinit var viewKonfetti: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //IDs for views in MainActivity
        calculateBMI = findViewById(R.id.button)
        weightEdit = findViewById(R.id.weight)
        heightEdit = findViewById(R.id.heightText)
        switchHeight = findViewById(R.id.switchHeight)
        switchWeight = findViewById(R.id.switchWeight)
        inchesEdit = findViewById(R.id.inches)
        layout = findViewById(R.id.main_layout)
        viewKonfetti = findViewById(R.id.konfettiView)

        inchesEdit.visibility = View.INVISIBLE //Set Inches EditText Invisible onCreate(Default unit is Centimeters)

        //Calculate BMI Listener
        calculateBMI.setOnClickListener {
            val heightHold: Int //Switch variable for height... 1 means yes, 0 means no
            val weightHold: Int //Switch variable for weight... 1 means yes, 0 means no
            val bmi: Float //BMI value
            val df = DecimalFormat("#.##") //Decimal format for BMI to decimal places
            df.roundingMode = RoundingMode.DOWN

            if (TextUtils.isEmpty(heightEdit.text.toString()) || TextUtils.isEmpty(weightEdit.text.toString())) { //Check if edittext fields are empty or not
                Snackbar.make(findViewById(R.id.button), "Fill all fields", Snackbar.LENGTH_SHORT).show()
            } else {
                heightHold = if (switchHeight.isChecked) {
                    1 //yes is checked..height
                } else {
                    0 //no is not checked..height
                }

                weightHold = if (switchWeight.isChecked) {
                    1 //yes is checked..weight
                } else {
                    0 //no is not checked..weight
                }
                if (heightHold == 0 && weightHold == 0) { //both are not checked
                    val weight: Float = weightEdit.text.toString().toFloat() //Convert edittext value to a float
                    val height: Float = (heightEdit.text.toString().toFloat() * 0.01).toFloat() //Convert edittext value to a float and convert centimeters to meters

                    bmi = df.format(weight / (height * height)).toFloat()
                    setBack(bmi) //Color Change for background..Function setBack is found below
                    Snackbar.make(findViewById(R.id.button), "Your bmi is $bmi", Snackbar.LENGTH_INDEFINITE).show()

                } else if (heightHold == 1 && weightHold == 1) {
                    if (TextUtils.isEmpty(inchesEdit.text)) { //Since inches is available check if its empty
                        Snackbar.make(findViewById(R.id.button), "Fill all fields", Snackbar.LENGTH_SHORT).show()
                    } else {
                        val weight: Float = (weightEdit.text.toString().toFloat() / 2.2).toFloat() //convert to from pounds to kilos
                        var height: Float = (heightEdit.text.toString().toFloat() * 0.3048).toFloat() // convert from feet to meters
                        val inches: Float = (inchesEdit.text.toString().toFloat() * 0.0254).toFloat()//convert from inches to meters
                        height += inches // add together

                        bmi = df.format(weight / (height * height)).toFloat()
                        setBack(bmi) //Color Change for background..Function setBack is found below
                        Snackbar.make(findViewById(R.id.button), "Your bmi is $bmi", Snackbar.LENGTH_INDEFINITE).show()
                    }

                } else if (heightHold == 0 && weightHold == 1) {

                    val weight: Float = (weightEdit.text.toString().toFloat()/ 2.2).toFloat()
                    val height: Float = (heightEdit.text.toString().toFloat()* 0.01).toFloat()

                    bmi = df.format(weight / (height * height)).toFloat()
                    setBack(bmi) //Color Change for background..Function setBack is found below
                    Snackbar.make(findViewById(R.id.button), "Your bmi is $bmi", Snackbar.LENGTH_INDEFINITE).show()

                } else if (heightHold == 1 && weightHold == 0) {

                    if (TextUtils.isEmpty(inchesEdit.text)) {
                        Snackbar.make(findViewById(R.id.button), "Fill all fields", Snackbar.LENGTH_SHORT).show()
                    } else {

                        var weight: Float = weightEdit.text.toString().toFloat()
                        var height: Float = (heightEdit.text.toString().toFloat() * 0.3048).toFloat()
                        var inches: Float = (inchesEdit.text.toString().toFloat()* 0.0254).toFloat()
                        height += inches

                        bmi = df.format(weight / (height * height)).toFloat()
                        setBack(bmi) //Color Change for background..Function setBack is found below
                        Snackbar.make(findViewById(R.id.button), "Your bmi is $bmi", Snackbar.LENGTH_INDEFINITE).show()
                    }
                }
            }
        }
        //If switch for height is checked or not
        switchHeight.setOnClickListener {
            if (switchHeight.isChecked) {
                heightEdit.hint = "Feet"
                inchesEdit.visibility = View.VISIBLE
                switchHeight.text = "Centimeters"
            } else {
                heightEdit.hint = "Centimeters"
                inchesEdit.visibility = View.INVISIBLE
                switchHeight.text = "Feet and Inches"
            }
        }
        //if switch for weight is checked or not
        switchWeight.setOnClickListener {
            if (switchWeight.isChecked) {
                weightEdit.hint = "Pounds"
                switchWeight.text = "Kilograms"
            } else {
                weightEdit.hint = "Kilograms"
                switchWeight.text = "Pounds"
            }
        }
    }
//Color change for gradient background
    private fun setBack(bmi: Float) {
        if (bmi <= 18.5) {
            layout.background = ContextCompat.getDrawable(this, R.drawable.main_header_selector2)
        }
        if (bmi > 18.5 && bmi <= 25.0) {
            layout.background = ContextCompat.getDrawable(this, R.drawable.main_header_selector3)
            viewKonfetti.start(rain()) //Call rain confetti function
        }
        if (bmi >25.0 && bmi <= 30.0) {
            layout.background = ContextCompat.getDrawable(this, R.drawable.main_header_selector4)
        }
        if (bmi > 30.0) {
            layout.background = ContextCompat.getDrawable(this, R.drawable.main_header_selector5)
        }
    }

/*
Function for rain confetti effect
Dependencies have been added in BUILD.GRADLE(MODULE)
 */
    private fun rain(): List<Party> {
        return listOf(
            Party(
                speed = 0f,
                maxSpeed = 15f,
                damping = 0.9f,
                angle = Angle.BOTTOM,
                spread = Spread.ROUND,
                colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
                emitter = Emitter(duration = 3, TimeUnit.SECONDS).perSecond(100),
                position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0))
            )
        )
    }
}

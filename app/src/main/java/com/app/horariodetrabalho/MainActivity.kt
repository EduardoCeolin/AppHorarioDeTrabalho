package com.app.horariodetrabalho

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import com.kizitonwose.time.Interval
import com.kizitonwose.time.Second
import com.kizitonwose.time.hours
import com.kizitonwose.time.minutes
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textHorarioEntrada.addTextChangedListener(MaskEditUtil.mask(textHorarioEntrada, MaskEditUtil.FORMAT_HOUR))

        textHorarioEntrada.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (textHorarioEntrada.length() == 5) {
                    var textHoraEntrada = textHorarioEntrada.text.toString().substring(0, 2).toInt()
                    var textMinutoEntrada = textHorarioEntrada.text.toString().substring(3, 5).toInt()
                    var horaEmSegundos = textHoraEntrada.hours.inSeconds
                    var minutosEmSegundos = textMinutoEntrada.minutes.inSeconds

                    if(textHoraEntrada > 23 || textMinutoEntrada > 59){
                        Toast.makeText(this@MainActivity, "Horário informado está incorreto !", Toast.LENGTH_LONG).show()
                    }else{
                    calculoHorarioSaida(horaEmSegundos + minutosEmSegundos)
                    }
                }
                else{
                    textHorarioSaida.text = ""
                }
            }
        })
    }

    private fun calculoHorarioSaida(horarioEntrada: Interval<Second>) {
        val horarioSaidaEmSegundos = ((horarioEntrada.plus(9.hours.inSeconds)).plus(48.minutes.inSeconds))
        var horaSaida = horarioSaidaEmSegundos.inHours.toString().substring(0, 2)
        var minutoSaida = (round(((horarioSaidaEmSegundos.inHours.minus(horaSaida.toInt().hours)) * 60).value)).toString().substring(0, 2)

        if(horaSaida.toInt() > 23){
            horaSaida = (horaSaida.toInt() - 24).toString()
        }

        if (horaSaida.toInt() < 10) {
            horaSaida = "0${horaSaida.substring(0,1)}"
        }

        if (minutoSaida.substring(1, 2) == "." && minutoSaida.substring(0,1).toInt() < 10) {
            minutoSaida = "0${minutoSaida.substring(0,1)}"
        }

        textHorarioSaida.text = "Horário previsto de saída: $horaSaida:$minutoSaida"
    }
}

object MaskEditUtil {

    const val FORMAT_HOUR = "##:##"

    fun mask(ediTxt: EditText, mask: String): TextWatcher {
        return object : TextWatcher {
            internal var isUpdating: Boolean = false
            internal var old = ""

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val str = MaskEditUtil.unmask(s.toString())
                var mascara = ""
                if (isUpdating) {
                    old = str
                    isUpdating = false
                    return
                }
                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#' && str.length > old.length) {
                        mascara += m
                        continue
                    }
                    try {
                        mascara += str[i]
                    } catch (e: Exception) {
                        break
                    }

                    i++
                }
                isUpdating = true
                ediTxt.setText(mascara)
                ediTxt.setSelection(mascara.length)
            }
        }
    }

    fun unmask(s: String): String {
        return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "").replace("[/]".toRegex(), "")
            .replace("[(]".toRegex(), "").replace("[ ]".toRegex(), "").replace("[:]".toRegex(), "")
            .replace("[)]".toRegex(), "")
    }
}

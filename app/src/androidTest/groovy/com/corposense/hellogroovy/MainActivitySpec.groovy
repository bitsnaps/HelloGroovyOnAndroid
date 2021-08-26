package com.corposense.hellogroovy

import android.content.Context
import android.widget.TextView;
import com.andrewreitz.spock.android.UseActivity
import com.andrewreitz.spock.android.WithContext
import spock.lang.Shared
import spock.lang.Specification

class MainActivitySpec extends Specification {

    @WithContext
    Context context

    @UseActivity(MainActivity)
    MainActivity mainActivity

    @Shared
    TextView tvHello

    def "test activity setup"() {
        expect:
        mainActivity != null
        mainActivity instanceof MainActivity
    }

    def "should find text view and compare text value"() {
        given:
            tvHello = mainActivity.findViewById(R.id.tvHello) as TextView
        when:
            def text = tvHello.text
        then:
            text == 'Hello World!'
    }

    def "should check numbers"() {
        given:
            int total; int x = 2; int y = 1
        when:
            total = x + y
        then:
            total == 3
    }

}

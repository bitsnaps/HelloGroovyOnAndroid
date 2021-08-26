package com.corposense.hellogroovy

import android.widget.Button
import com.andrewreitz.spock.android.AndroidSpecification
import groovy.transform.CompileDynamic

@CompileDynamic
class MockTestSpec extends AndroidSpecification {

    def "I'm mocking on Android!"() {
        def mocked = Mock(Button)

        when:
        mocked.setOnClickListener(null)

        then:
        1 * mocked.setOnClickListener(null)
    }
}
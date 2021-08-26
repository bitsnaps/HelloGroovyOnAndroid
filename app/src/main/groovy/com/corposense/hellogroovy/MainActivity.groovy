package com.corposense.hellogroovy

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.error.VolleyError
import com.android.volley.request.StringRequest
import com.arasthel.swissknife.SwissKnife
import com.arasthel.swissknife.annotations.InjectView
import com.arasthel.swissknife.annotations.OnClick
import groovy.json.JsonSlurper
//import groovy.transform.CompileStatic

//@CompileStatic
class MainActivity extends AppCompatActivity {

    final int NOTIFICATION_ID = 10

    @InjectView(R.id.btnHello)
    Button btnHello

    @InjectView(R.id.tvHello)
    TextView tvHello


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SwissKnife.inject(this)

        btnHello.onClick {

            toast "hello all!" show()

//            startActivity( new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")) )

            ContextMethods.alert(this){
                title = 'Request API'
                message = 'Do you want really to make a request?'
                cancelable = true
                setPositiveButton('Yes', { DialogInterface dialog, int which ->
                    //toast "Hello Android from Groovy!" show()
                    def url = "https://api.github.com"

                        // VolleyPlus
                        VolleyPlus.getInstance(this,true).addToRequestQueue(
                                new StringRequest(Request.Method.GET, "https://api.github.com",
                                        {String response ->
                                            toast response show()
                                        }, { VolleyError error -> log error.message}){
                                    @Override // additional headers
                                    Map getHeaders(){
                                        ["content-type":"application/json", "Accept":"application/vnd.github.v3+json"]
                                    }
//                        @Override // additional params
//                        Map getParams(){ [ : ] }
                                }
                                ,false)

                    // Regular Volley
                    /*Volley.newRequestQueue(this).add(new StringRequest(Request.Method.GET, url,
                            { String response ->
                                def json = (Map) new JsonSlurper().parseText(response)
                                toast ("User URL: ${json['current_user_url']}").show()
                                log response
                            }, {
                        VolleyError error -> log error.message; toast"That didn't work!" show() } ){
                        @Override // additional headers
                        Map getHeaders(){
                            ["content-type":"application/json", "Accept":"application/vnd.github.v3+json"]
                        }
                    })*/
                    log "done."
                })
                setNegativeButton('No', { DialogInterface dialog, int which ->

                    ContextMethods.notify(this, NOTIFICATION_ID){
                        smallIcon = R.drawable.notification_icon_background
//            largeIcon = cachedBitmap // lazly loaded
                        contentTitle = "Info"
                        contentText = "Request canceled"
//            contentIntent = pendingActivityIntent(0, intent(WelcomeActivity), 0)
                        ongoing = false
                    }
                })
            }


        } // onClick

    }

    @OnClick(R.id.btnAsync)
    void asyncRequest(View view){
        def task = Async.background {
            // This closure runs in a background thread, all other closures run in UI thread
            Thread.sleep(1_000)
            def json = new JsonSlurper().parse([:], new URL('https://www.bitstamp.net/api/ticker/'), 'utf-8')
            json as Map
        } first {
            // this runs before the background task in the UI thread
            (view as Button).enabled = false
            tvHello.text = 'Loading...'
        } then { Map result ->
            // This runs in the UI thread after the background task
            (view as Button).enabled = true
            log "running in the UI thread: ${result}"
            tvHello.setText("BTC-USD: \$${result['last']} at: ${new Date().format('dd/MM/yyyy HH:mm')}")
        } onError { error ->
            // This runs if an error occurs in any closure
            log "ERROR: ${error.class.name}: ${error.message}"
        } execute()
    }

}

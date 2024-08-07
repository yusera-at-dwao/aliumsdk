package com.dwao.alium.survey;

import static com.dwao.alium.utils.Util.generateCustomerId;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dwao.alium.frequencyManager.FrequencyManagerFactory;
import com.dwao.alium.frequencyManager.SurveyFrequencyManager;
import com.dwao.alium.listeners.VolleyResponseListener;
import com.dwao.alium.network.VolleyService;
import com.dwao.alium.utils.preferences.AliumPreferences;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.Iterator;

public class AliumSurveyLoader {
    private boolean activityInstanceCreated=false;
    AliumPreferences aliumPreferences;
    private static JSONObject surveyConfigJSON;
    //        private static Map<String, SurveyConfig> surveyConfigMap;

    private  Gson gson;
    private  Context context;

    private static VolleyService volleyService;

    private SurveyParameters surveyParameters;

    private  AliumSurveyLoader(){}
    public AliumSurveyLoader(Context context,SurveyParameters surveyParameters, JSONObject surveyConf){
        this.surveyParameters=surveyParameters;
        surveyConfigJSON=surveyConf;
        volleyService=new VolleyService();
        this.context=context;
        gson=new Gson();
        aliumPreferences= AliumPreferences.getInstance(context);
        if(aliumPreferences.getCustomerId().isEmpty()){
            aliumPreferences.setCustomerId(generateCustomerId());
        }
    }

    public SurveyParameters getSurveyParameters() {
        return surveyParameters;
    }

    public void showSurvey( ){
        Log.d("Alium", "showing survey on :"+surveyParameters.screenName);
        findAndLoadSurveyForCurrentScr();
    }


    private void findAndLoadSurveyForCurrentScr() {
//        Iterator<String> keys = surveyConfigMap.keySet().iterator();
        Iterator<String> keys = surveyConfigJSON.keys();
        while(keys.hasNext()) {
            String key = keys.next();
            try {
                JSONObject jsonObject = surveyConfigJSON.getJSONObject(key);
                JSONObject ppupsrvObject = jsonObject.getJSONObject("appsrv");
                String screenName = ppupsrvObject.getString("url");
                if (surveyParameters.screenName.equals(screenName)){
                    loadSurveyIfShouldBeLoaded(jsonObject, key);
                }
            } catch (Exception e) {
                Log.i("error", "inside catch block");
                e.printStackTrace();
            }
        }
    }

    private void loadSurveyIfShouldBeLoaded(JSONObject currentSurveyJson, String key)  {
       try{
           JSONObject ppupsrvObject = currentSurveyJson.getJSONObject("appsrv");
           Uri spath=Uri.parse(currentSurveyJson.getString("spath"));
           Log.d("URI", spath.toString());
           String srvshowfrq=ppupsrvObject.getString("srvshowfrq");
           CustomFreqSurveyData customFreqSurveyData=null;
           if(ppupsrvObject.has("customSurveyDetails")){
               JSONObject customSurveyDetails=ppupsrvObject.getJSONObject("customSurveyDetails");

               customFreqSurveyData=new CustomFreqSurveyData(
                       customSurveyDetails.getString("freq"),
                       customSurveyDetails.getString("startOn"),
                       customSurveyDetails.getString("endOn")
               );
           }
//            srvshowfrq="custom";
           customFreqSurveyData=new CustomFreqSurveyData(
                  "2-d",
                  "2024-07-07",
                  "2024-07-15"
          );

           String thankyouObj = ppupsrvObject.getString("thnkMsg");
           if(   FrequencyManagerFactory
                   .getFrequencyManager(aliumPreferences,key, srvshowfrq,
                           customFreqSurveyData)
                   .shouldSurveyLoad()){
               loadSurvey( new LoadableSurveySpecs(
                       key, srvshowfrq, spath.toString(), thankyouObj,
                       customFreqSurveyData
               ));
           }
       }catch (Exception e){
           Log.e("loadSurveyIfShouldLoad", e.toString());
       }
    }
    private void loadSurvey(LoadableSurveySpecs loadableSurveySpecs) {
        String surURL=loadableSurveySpecs.uri.toString();
        volleyService.callVolley(context, surURL,new LoadSurveyFromAPI(loadableSurveySpecs) );
    }

    class LoadSurveyFromAPI implements VolleyResponseListener{
        LoadableSurveySpecs loadableSurveySpecs;
        private LoadSurveyFromAPI(){}
        public LoadSurveyFromAPI(LoadableSurveySpecs loadableSurveySpecs) {
            this.loadableSurveySpecs=loadableSurveySpecs;
        }

        @Override
        public void onResponseReceived(JSONObject json) {
            Log.d("Alium-survey loaded", json.toString());
            ExecutableSurveySpecs executableSurveySpecs=new ExecutableSurveySpecs(json
                    , loadableSurveySpecs);

            if(!AliumSurveyActivity.isActivityRunning  || !activityInstanceCreated) {
                Intent intent = new Intent(context, AliumSurveyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                );
                context.startActivity(intent);
                activityInstanceCreated = true;
            }

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent=new Intent("survey_content_fetched");
                        intent.putExtra("surveyJson", json.toString());
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        );
                        intent.putExtra("loadableSurveySpecs", loadableSurveySpecs);
                        intent.putExtra("surveyParameters", surveyParameters);
                        intent.putExtra("canonicalClassName", ((Activity)context).getClass().getCanonicalName());
//                        context.sendBroadcast(intent);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        Log.d("alium-activity", "is running");
                    }
                }, 500);

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {

//                    new SurveyDialog(context, executableSurveySpecs,
//                            surveyParameters)
//                            .show();
                }
            });


        }
    }



}
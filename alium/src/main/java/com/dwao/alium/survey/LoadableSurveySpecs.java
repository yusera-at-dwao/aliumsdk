package com.dwao.alium.survey;


import android.net.Uri;

public class LoadableSurveySpecs {
    String key, surveyFreq, thankYouMsg;
    Uri uri;

    CustomFreqSurveyData customSurveyData;

    public LoadableSurveySpecs(String key, String surveyFreq, Uri uri, String thankYouMsg, CustomFreqSurveyData customSurveyData) {
        this.key = key;
        this.surveyFreq = surveyFreq;
        this.uri = uri;
        this.thankYouMsg=thankYouMsg;
        this.customSurveyData=customSurveyData;
    }

}

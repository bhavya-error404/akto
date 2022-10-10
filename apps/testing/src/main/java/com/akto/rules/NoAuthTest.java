package com.akto.rules;


import com.akto.dto.*;
import com.akto.dto.testing.*;
import com.akto.dto.type.SingleTypeInfo;
import com.akto.store.SampleMessageStore;
import com.akto.testing.ApiExecutor;
import com.akto.testing.StatusCodeAnalyser;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class NoAuthTest extends TestPlugin {

    @Override
    public TestResult start(ApiInfo.ApiInfoKey apiInfoKey, AuthMechanism authMechanism, List<RawApi> messages, Map<String, SingleTypeInfo> singleTypeInfoMap) {
        List<RawApi> filteredMessages = SampleMessageStore.filterMessagesWithAuthToken(messages, authMechanism);
        if (filteredMessages.isEmpty()) return addWithoutRequestError(null, TestResult.TestError.NO_PATH);

        RawApi rawApi = filteredMessages.get(0);

        OriginalHttpRequest testRequest = rawApi.getRequest().copy();
        OriginalHttpResponse originalHttpResponse = rawApi.getResponse().copy();

        authMechanism.removeAuthFromRequest(testRequest);

        OriginalHttpResponse testResponse = null;
        try {
            testResponse = ApiExecutor.sendRequest(testRequest, true);
        } catch (Exception e) {
            return addWithRequestError( rawApi.getOriginalMessage(), TestResult.TestError.API_REQUEST_FAILED, testRequest);
        }

        int statusCode = StatusCodeAnalyser.getStatusCode(testResponse.getBody(), testResponse.getStatusCode());
        boolean vulnerable = isStatusGood(statusCode);

        double percentageMatch = compareWithOriginalResponse(originalHttpResponse.getBody(), testResponse.getBody());

        return addTestSuccessResult(testRequest, testResponse, rawApi.getOriginalMessage(),
                vulnerable, percentageMatch, new ArrayList<>(), TestResult.Confidence.HIGH);

    }

    @Override
    public String testName() {
        return "NO_AUTH";
    }
}

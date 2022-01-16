//package com.st.aws.lambda.test;
//
//import com.amazonaws.services.lambda.runtime.events.SQSEvent;
//import com.amazonaws.services.lambda.runtime.tests.annotations.Event;
//import com.amazonaws.services.lambda.runtime.LambdaLogger;
//import org.junit.jupiter.params.ParameterizedTest;
//import com.st.aws.lambda.EventProcessor;
//import com.amazonaws.services.lambda.runtime.Context;
//import org.mockito.Mock;
//import static org.assertj.core.api.Assertions.*;
//
//public class EventTest {
//    private EventProcessor handler;
//    @Mock
//    Context context;
//    @Mock
//    LambdaLogger loggerMock;
//
//    @ParameterizedTest
//    @Event(value = "C:\\aws-java\\aws-sqs-lambda-java\\event.json", type = SQSEvent.class)
//    public void testInjectEvent(SQSEvent event, Context context) {
//        Void response = handler.handleRequest(event, context);
//        System.out.println(response);
//        //assertThat(response).isNotNull();
//        //assertThat(event.getRecords()).hasSize(1);
//    }
//}

package com.erarslan.billing_service.grpc;

import org.slf4j.Logger;
import org.springframework.grpc.server.service.GrpcService;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase{
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(BillingGrpcService.class);

    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver) {
        logger.info("Received createBillingAccount request for account: {}", billingRequest.toString());

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId(billingRequest.getPatientId())
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response); 
        responseObserver.onCompleted();
    }   
}

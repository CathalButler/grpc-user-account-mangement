package ie.gmit.ds;

import io.grpc.stub.StreamObserver;

public class PasswordServiceImpl extends PasswordServiceGrpc.PasswordServiceImplBase {

    public PasswordServiceImpl() {

    }

    @Override
    public void hash(UserHashRequest request, StreamObserver<HashResponse> responseObserver) {
        super.hash(request, responseObserver);
    }

    @Override
    public void validate(ValidateRequest request, StreamObserver<ValidateResponse> responseObserver) {
        super.validate(request, responseObserver);
    }
}

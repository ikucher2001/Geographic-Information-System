package at.tugraz.oop2.errors;

import io.grpc.Status;

public class GrpcResponseException extends RuntimeException {
    public GrpcResponseException(Status.Code code, long id) {
        switch (code){
            case NOT_FOUND:
                throw new ResourceNotFoundException("Resource not found with id: " + id);
            default:
                throw new InternalServerErrorException("Internal Server Exception");
        }
    }
}

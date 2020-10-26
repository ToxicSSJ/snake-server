package me.itoxic.snake.server;

public enum Response {

    BUCKET_CREATED(200),
    BUCKET_DELETED(210),
    FILE_DELETED(220),
    BUCKETS(225),
    FILES(230),
    UPTIME(235),
    RECEIVING_FILE(240),
    FILE_FOUND(245),
    CLOSE(250),

    BUCKET_NOT_EXISTS(805),
    BUCKET_ALREADY_EXISTS(810),
    FILE_ALREADY_EXISTS(815),
    FILE_NOT_EXISTS(820),

    INTERNAL_ERROR(900),
    BAD_CMD(980)

    ;

    private int code;

    Response(int code) {
        this.code = code;
    }

    public String getLiteral() {
        return this.name();
    }

    public int getCode() {
        return this.code;
    }

}

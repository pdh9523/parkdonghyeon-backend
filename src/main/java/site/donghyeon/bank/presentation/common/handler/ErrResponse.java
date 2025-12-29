package site.donghyeon.bank.presentation.common.handler;

public record ErrResponse(
        String code,
        String message
) {
    public static ErrResponse from(RuntimeException e) {
        return new ErrResponse(
                e.getClass().getSimpleName(),
                e.getMessage()
        );
    }

    public static ErrResponse internal() {
        return new ErrResponse(
                "INTERNAL_SERVER_ERROR",
                "Unexpected error occurred"
        );
    }
}

package payment;

public enum TicketProduct {
    DAILY_3H("일일권 3시간"),
    DAILY_6H("일일권 6시간"),
    DAILY_12H("일일권 12시간"),
    DAILY_24H("일일권 24시간"),

    DURATION_1W("기간권 1주"),
    DURATION_2W("기간권 2주"),
    DURATION_1M("기간권 1달"),
    DURATION_3M("기간권 3달"),

    TIME_50H("시간권 50시간"),
    TIME_100H("시간권 100시간"),
    TIME_200H("시간권 200시간");

    private final String koreanName;

    TicketProduct(String koreanName) {
        this.koreanName = koreanName;
    }

    @Override
    public String toString() {
        return koreanName;
    }
}

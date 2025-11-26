package Seat;

// 좌석 1개의 상태를 관리
public class Seat {
    public enum Status { AVAILABLE, IN_USE }
    public enum AirType { NONE, WEAK, STRONG }
    public enum ZoneType { LAPTOP, FOCUS }
    public enum SeatType {
        FLOW,       // 플로우석 (개방형 1인석)
        WINDOW,     // 창가석
        TABLE,      // 테이블석
        BOOTH,      // 칸막이석
        PREMIUM     // 프리미엄 집중석
    }

    private final String seatNumber;
    private Status status;
    private String occupantId;
    private AirType airType;
    private ZoneType zoneType;
    private SeatType seatType;

    // 기존 로직 호환용: 메타데이터가 없던 호출을 그대로 지원
    public Seat(String seatNumber) {
        this(seatNumber, AirType.NONE, ZoneType.LAPTOP, SeatType.FLOW);
    }

    public Seat(String seatNumber, AirType airType, ZoneType zoneType, SeatType seatType) {
        if (seatNumber == null || seatNumber.isBlank()) {
            throw new IllegalArgumentException("좌석 번호를 반드시 입력해야 합니다.");
        }
        this.seatNumber = seatNumber;
        this.status = Status.AVAILABLE;
        this.occupantId = null;
        // null 방어: 주어진 값이 없으면 기본값으로 채운다.
        this.airType = (airType != null) ? airType : AirType.NONE;
        this.zoneType = (zoneType != null) ? zoneType : ZoneType.LAPTOP;
        this.seatType = (seatType != null) ? seatType : SeatType.FLOW;
    }

    // 좌석을 '사용 중'으로 변경 (입실)
    public void occupy(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("이용자 ID를 반드시 입력해야 합니다.");
        }
        if (status == Status.IN_USE) {
            throw new IllegalStateException("이미 사용 중인 좌석입니다.: " + seatNumber);
        }
        this.status = Status.IN_USE;
        this.occupantId = memberId;
    }

    // 좌석을 '사용 가능'으로 변경 (퇴실, 외출)
    public void vacate() {
        this.status = Status.AVAILABLE;
        this.occupantId = null;
    }

    // 현재 좌석이 사용 가능한지 확인
    public boolean isAvailable() {
        return status == Status.AVAILABLE;
    }

    // getter
    public String getSeatNumber() {
        return seatNumber;
    }

    public String getOccupantId() {
        return occupantId;
    }

    public AirType getAirType() {
        return airType;
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public SeatType getSeatType() {
        return seatType;
    }
}

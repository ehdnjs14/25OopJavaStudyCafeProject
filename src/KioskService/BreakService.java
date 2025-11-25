package KioskService;

import Seat.Seat;
import Seat.UsageSession;
import SeatManager.SeatManager;

public class BreakService {

    private SeatManager seatManager;
    private SessionManager sessionManager;

    public BreakService(SeatManager seatManager,
                        SessionManager sessionManager) {
        this.seatManager = seatManager;
        this.sessionManager = sessionManager;
    }

    /*
     * 외출 처리
     * 외출해도 좌석과 세션은 그대로 유지
     * 외출 상태만 나중에 쓰고 싶으면
     * SessionManager나 UsageSession에 별도 플래그를 추가
     */
    public boolean goOut(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return false;
        }

        // 입실 상태인지(좌석 사용 중인지)만 확인
        Seat seat = seatManager.findSeatByMember(memberId);
        if (seat == null) {
            // 사용 중인 좌석이 없으면 외출 불가
            return false;
        }

        // 진행 중인 세션이 있는지도 확인 (선택적이지만 안전하게 체크)
        UsageSession session = sessionManager.getActiveSession(memberId);
        if (session == null) {
            // 세션이 없으면 비정상 상태
            return false;
        }

        // 좌석 비우지 않고, 세션도 그대로 둔다.
        // 필요하면 여기서 "외출 상태" 플래그를 세션에 기록
        return true;
    }

    /*
     * 복귀 처리
     * 세션과 좌석이 여전히 유효한지만 확인.
     */
    public boolean returnToSeat(String memberId) {
        if (memberId == null || memberId.isBlank()) {
            return false;
        }

        // 진행 중인 세션이 있는지 확인
        UsageSession session = sessionManager.getActiveSession(memberId);
        if (session == null) {
            // 세션이 없으면 복귀할 수 없음
            return false;
        }

        // 여전히 좌석을 점유 중인지 확인
        Seat seat = seatManager.findSeatByMember(memberId);
        if (seat == null) {
            String originalSeatNumber = session.getSeatNumber();
            Seat originalSeat = seatManager.findSeatByNumber(originalSeatNumber);
            if (originalSeat == null || !originalSeat.isAvailable()) {
                return false;
            }
            return seatManager.assignSeat(memberId, originalSeatNumber);
        }

        return true;
    }
}

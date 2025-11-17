package KioskService;

import Seat.Seat;
import Seat.UsageSession;
import SeatManager.SeatManager;
import payment.ILogManager;


public class CheckOutService {

    private SeatManager seatManager;
    private SessionManager sessionManager;

    public CheckOutService(SeatManager seatManager,
                           SessionManager sessionManager, ILogManager logManager) {
        this.seatManager = seatManager;
        this.sessionManager = sessionManager;
    }

    /*
     * 퇴실 처리 
     */
    public boolean checkOut(String memberId) {
        // 현재 사용 중인 좌석 확인
        Seat seat = seatManager.findSeatByMember(memberId);
        if (seat == null) {
            // 사용 중인 좌석이 없으면
            return false;
        }

        // UsageSession 정보 가져오기
        UsageSession session = sessionManager.endSession(memberId);
        if (session == null) {
            // 진행 중인 세션이 없는 경우
            return false;
        }

        // 이용 시간 계산
        //long durationMinutes = session.getDurationMinutes();

        // 좌석 비우기
        seatManager.vacateSeat(memberId);

        return true;
    }
}

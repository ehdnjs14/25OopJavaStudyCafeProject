package Ticket;

import Seat.UsageSession; // 파일에서 import Seat.UsageSession; 확인됨.
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * [Ticket] 패키지에 속하며, 입실한 시간에 따라 선물을 증정하는 클래스 (2시간마다 아메리카노 증정).
 */
public class Present {

    private static final int PRESENT_INTERVAL_HOURS = 2; // 선물 증정 기준 시간 (2시간)

    /**
     * 현재 이용 세션을 확인하고, 2시간 이용 시마다 선물을 증정합니다.
     * 선물의 중복 증정을 막기 위해 UsageSession 내부에 증정 횟수를 추적합니다.
     * @param session 현재 진행 중인 이용 세션
     * @return 새로 증정된 선물 개수
     */
    public int checkAndGivePresent(UsageSession session) {
        if (session == null) {
            System.out.println("⚠️ 오류: 유효한 이용 세션이 존재하지 않습니다.");
            return 0;
        }

        // 1. 현재 이용 시간 계산 (현재 시간과 입실 시간의 차이)
        // 실제로는 SessionManager가 주기적으로 호출하거나 세션 종료 시 호출
        Duration elapsedDuration = Duration.between(session.getCheckInTime(), LocalDateTime.now());
        long elapsedHours = elapsedDuration.toHours(); // 시간 단위

        // 2. 증정해야 할 '총' 선물 횟수 계산 (2시간마다)
        long totalPresentsDue = elapsedHours / PRESENT_INTERVAL_HOURS;

        // 3. 이미 증정된 선물 횟수 가져오기 (UsageSession에 저장된 값)
        int presentsGiven = session.getPresentsGivenCount();

        // 4. 새로 증정할 선물 개수 계산
        int newPresentsCount = (int) (totalPresentsDue - presentsGiven);

        if (newPresentsCount > 0) {
            // 선물 증정 처리
            System.out.printf("🎁 축하합니다! [회원: %s] %d시간 이용으로 아메리카노 %d잔을 드립니다.\n",
                    session.getMemberId(), elapsedHours, newPresentsCount);

            // 세션에 증정 횟수 업데이트 (중복 증정 방지)
            session.incrementPresentsGivenCount(newPresentsCount);

            return newPresentsCount;
        } else if (elapsedHours > 0) {
            // 다음 선물까지 남은 시간 안내
            long nextPresentHours = PRESENT_INTERVAL_HOURS * (presentsGiven + 1);
            System.out.printf("⏰ 현재 이용 시간: %d시간. 다음 아메리카노는 총 %d시간 이용 시 증정됩니다.\n",
                    elapsedHours, nextPresentHours);
        } else {
            System.out.printf("⏰ [회원: %s] 이용 세션 시작 후 경과 시간이 2시간 미만입니다.\n", session.getMemberId());
        }

        return 0;
    }
}

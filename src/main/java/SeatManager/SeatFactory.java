package SeatManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatFactory {

    // 모든 좌석 객체
    private List<Seat> seatList;

    /*
     * 외부에서 좌석 리스트를 주입
     * 파일 입출력으로 생성한 좌석 리스트를 넘겨주기
     */
    public SeatFactory(List<Seat> seatList) {
        this.seatList = new ArrayList<>(seatList);
    }

    /*
     * 좌석 개수와 기본 정보를 받아 내부에서 좌석을 생성
     */
    public SeatFactory(int seatCount) {
        this.seatList = new ArrayList<>();
        for (int i = 1; i <= seatCount; i++) {
            String seatNumber = String.valueOf(i);
            seatList.add(new Seat(seatNumber));
        }
    }

    /**
     * 현재 모든 좌석의 상태 목록 반환
     * unmodifiableList >> 외부에서 수정 x
     */
    public List<Seat> getSeatMap() {
        return Collections.unmodifiableList(seatList);
    }

    // SeatManager 쪽에서 동일 리스트를 쓰기 위한 내부용 getter
    public List<Seat> getSeatListInternal() {
        return seatList;
    }
}

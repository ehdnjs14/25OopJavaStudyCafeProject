package SeatManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Seat.Seat;

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

        // 강냉방 좌석 리스트 (필터용 메타데이터)
        List<Integer> strongList = List.of(
            3,4,5,
            11,12,13,14,15,16,17,18,
            21,22,23,
            27,28,
            31,32,
            35,36,37,38,
            40,41,
            46,47,48
        );

        for (int i = 1; i <= seatCount; i++) {
            String seatNumber = String.valueOf(i);

            // 1) ZONE 결정 (대분류: 노트북 / 집중)
            Seat.ZoneType zone = (i >= 1 && i <= 23) ? Seat.ZoneType.LAPTOP : Seat.ZoneType.FOCUS;

            // 2) 좌석 타입(SeatType) 결정
            Seat.SeatType type;
            if (i >= 1 && i <= 5) {
                type = Seat.SeatType.FLOW;
            } else if (i >= 6 && i <= 10) {
                type = Seat.SeatType.WINDOW;
            } else if (i >= 11 && i <= 18) {
                type = Seat.SeatType.TABLE;
            } else if (i >= 19 && i <= 23) {
                type = Seat.SeatType.FLOW;
            } else if (i >= 24 && i <= 28) {
                type = Seat.SeatType.BOOTH;
            } else if (i >= 29 && i <= 36) {
                type = Seat.SeatType.FLOW;
            } else if (i >= 37 && i <= 42) {
                type = Seat.SeatType.FLOW;
            } else {
                type = Seat.SeatType.PREMIUM;
            }

            // 3) 냉난방 결정
            Seat.AirType air = strongList.contains(i) ? Seat.AirType.STRONG : Seat.AirType.WEAK;

            seatList.add(new Seat(seatNumber, air, zone, type));
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

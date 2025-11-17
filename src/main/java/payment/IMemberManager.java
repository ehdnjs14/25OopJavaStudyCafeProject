package payment;

import ReadingRoomLogin.Member;
import Ticket.Ticket;

public interface IMemberManager {
    Member findMemberById(String memberID);
    Member login(String id, String password);
    void register(String id, String password, String name);
    void setTicket(String memberID, Ticket newTicket);
    void saveMembersToFile();
}

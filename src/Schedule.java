import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // data field
    private GregorianCalendar startDate, dueDate;
    private String title, note;
    private char category; // 학교('U'niversity), 개인('P'ersonal), 가족('F'amily), 공부('S'tudy)
    private int priority; // 1 ~ 5 사이로 지정
    
    // constructor
    Schedule(int priority, char category, String title, String note, GregorianCalendar startDate, GregorianCalendar dueDate) {
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.category = category;
        this.title = title;
        this.note = note;
        this.priority = priority;
    }
    
    // method
    GregorianCalendar getStartDate() {
        return startDate;
    }
    GregorianCalendar getDueDate() {
        return dueDate;
    }
    char getCategory() {
        return category;
    }
    String getTitle() {
        return title;
    }
    String getNote() {
        return note;
    }
    int getPriority() {
        return priority;
    }
    
    void setTitle(String newTitle) {
        title = newTitle;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Schedule) {
            Schedule other = (Schedule) obj;
            return this.title.equals(other.title);
        }
        return false;
    }
    
    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return String.format("우선순위: %d, 카테고리: %c, 제목: '%s', 내용: '%s', 시작 시간: %s, 종료 시간: %s",
            priority,
            category,
            title,
            note,
            sdf.format(startDate.getTime()),
            sdf.format(dueDate.getTime()));
    }
}

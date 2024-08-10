// 일정 매니저 class
import java.util.GregorianCalendar;
import java.io.*;

public class ScheduleManager{
	// date field
	private final int MAX_SCHEDULE_SIZE = 100; // 일정 최대 개수
	private Schedule[] scheduleList;
	private int scheduleNum; // 일정 개수
	
	
	// constructor
	ScheduleManager(){
		scheduleList = new Schedule[MAX_SCHEDULE_SIZE];
		scheduleNum = 0;
	}
	ScheduleManager(int i) {
		scheduleList = new Schedule[i];
		scheduleNum = 0;
	}
	
	// method
	// get 함수
	Schedule[] getScheduleList() { // 전체 일정 반환
		if(scheduleList == null || scheduleNum == 0) {
			return null;
		}
		Schedule allSchedule[] = new Schedule[scheduleNum];
		for(int i = 0; i < scheduleNum ; i++) {
			allSchedule[i] = scheduleList[i];
		}
		return allSchedule;
	}
	int getScheduleNum() {
		return scheduleNum;
	}
	
	// 일정 추가 - 중복될 경우 예외 발생, 인터페이스에서 처리
	void addSchedule(Schedule s) throws Exception {
	    // 일정 중복 검사 : 이름이 동일한 경우 중복 처리 -> 사용자 확답 후 일정 추가(인터페이스)
	    for (int i = 0; i < scheduleNum; i++) {
	        if (s.getTitle().equals(scheduleList[i].getTitle())) {
	            throw new Exception("Exist");
	        }
	    }
	    // 중복 아닐 경우
	    if (scheduleNum < scheduleList.length) { // 사용자가 입력한 스케줄 리스트의 크기로 수정
	        scheduleList[scheduleNum] = s;
	        scheduleNum++;
	    } else {
	        //일정이 가득 찬 경우 
	        throw new Exception("Full");
	    }
	}
	
	// 중복 검사 없이 일정을 추가하는 메서드
	void forceAddSchedule(Schedule s) throws Exception {
	    if (scheduleNum < scheduleList.length) { // 사용자가 입력한 스케줄 리스트의 크기로 수정
	        scheduleList[scheduleNum] = s;
	        scheduleNum++;
	    } else {
	        // 일정이 가득 찬 경우 
	        throw new Exception("Full");
	    }
	}



	
	// 일정 검색(특정 날짜 이후 일정 검색)
	Schedule[] search(GregorianCalendar g) {
		Schedule searchList[] = new Schedule[scheduleNum]; // 검색된 일정만을 저장하는 리스트 생성
		int searchNum = 0; // 검색 일정 개수
		
		for(int i = 0; i < scheduleNum; i++) { // 일정 검색
			GregorianCalendar dueDate = scheduleList[i].getDueDate();
			if(dueDate.after(g)) {
				searchList[searchNum] = scheduleList[i];
				searchNum++;
			}
		}
		// 검색된 일정 없을 경우
		return searchNum > 0? searchList : null;
	}
	// 일정 검색(by 기간 사이)
	Schedule[] search(GregorianCalendar s, GregorianCalendar d) {
		Schedule searchList[] = new Schedule[scheduleNum]; // 검색된 일정만을 저장하는 리스트 생성
		int searchNum = 0; // 검색 일정 개수
		// s, d 반대로 입력된 경우
		if(s.after(d)) {
			GregorianCalendar t;
			t = s;
			s = d;
			d = t;
		}

		// s 부터 d 기간 사이에
		if (s.before(d) || s.equals(d)) {
			for (int i = 0; i < scheduleNum; i++) {
				GregorianCalendar startDate = scheduleList[i].getStartDate();
				GregorianCalendar dueDate = scheduleList[i].getDueDate();
				
				// 시작일 혹은 마감일이 기간 내 있을 경우
				if((startDate.equals(s) || (startDate.after(s) && startDate.before(d))) ||
					(dueDate.equals(d) || (dueDate.after(s) && dueDate.before(d)))) {
					searchList[searchNum] = scheduleList[i];
					searchNum++;
				}
			}
		}
		// 검색된 일정이 없을 경우
		return searchNum > 0? searchList : null;
	}
	// 일정 검색(by 키워드)
	Schedule[] search(String keyword) {
		Schedule searchList[] = new Schedule[scheduleNum]; // 검색된 일정만을 저장하는 리스트 생성
		int searchNum = 0; // 검색 일정 개수
		
		for(int i = 0; i < scheduleNum; i++) {
			String title = scheduleList[i].getTitle();
			String note = scheduleList[i].getNote();
			
			if(title.contains(keyword) || note.contains(keyword)) {
				searchList[searchNum] = scheduleList[i];
				searchNum++;
			}
		}
		// 검색된 일정이 없을 경우
		return searchNum > 0? searchList : null;
	}
	// 일정 검색(by 우선순위 : 해당 우선순위의 일정들만 검색)
	Schedule[] search(int priority) {
		Schedule searchList[] = new Schedule[scheduleNum]; // 검색된 일정만을 저장하는 리스트 생성
		int searchNum = 0; // 검색 일정 개수
		
		for (int i = 0; i < scheduleNum ; i++) {
			int importance = scheduleList[i].getPriority();
			if(importance == priority) {
				searchList[searchNum] = scheduleList[i];
				searchNum++;
			}
		}
		// 검색된 일정이 없을 경우
		return searchNum > 0? searchList : null;
	}
	// 일정 검색(by 카테고리)
	Schedule[] search(char category) {
		Schedule searchList[] = new Schedule[scheduleNum]; // 검색된 일정만을 저장하는 리스트 생성
		int searchNum = 0; // 검색 일정 개수
		
		for (int i = 0; i < scheduleNum; i++) {
			char cat = scheduleList[i].getCategory();
			if(cat == category) {
				searchList[searchNum] = scheduleList[i];
				searchNum++;
			}
		}
		// 검색된 일정이 없을 경우
		return searchNum > 0? searchList : null;
	}
	

	
// 일정 삭제를 위한 스케쥴 배열의 인덱스 리턴
 // 인덱스에 따라 일정 삭제
	public boolean delete(int index) {
	    if (index >= 0 && index < scheduleNum) {
	        for (int i = index; i < scheduleNum - 1; i++) {
	            scheduleList[i] = scheduleList[i + 1];
	        }
	        scheduleList[scheduleNum - 1] = null;
	        scheduleNum--;
	        return true;  // 성공적으로 삭제되었을 의미 
	    }
	    return false;  // 인덱스가 유효하지 않은 경우
	}
	
	   // 날짜에 따른 인덱스 검색
    int[] searchIndexes(GregorianCalendar g) {
        int[] tempIndexes = new int[scheduleNum];
        int count = 0;
        for (int i = 0; i < scheduleNum; i++) {
            if (scheduleList[i].getDueDate().after(g)) {
                tempIndexes[count++] = i;
            }
        }
        int[] indexes = new int[count];
        System.arraycopy(tempIndexes, 0, indexes, 0, count);
        return indexes;
    }

    // 기간에 따른 인덱스 검색
    int[] searchIndexes(GregorianCalendar start, GregorianCalendar end) {
        int[] tempIndexes = new int[scheduleNum];
        int count = 0;
        if (start.after(end)) {
            GregorianCalendar temp = start;
            start = end;
            end = temp;
        }
        for (int i = 0; i < scheduleNum; i++) {
            GregorianCalendar startDate = scheduleList[i].getStartDate();
            GregorianCalendar dueDate = scheduleList[i].getDueDate();
            if ((startDate.after(start) && startDate.before(end)) || startDate.equals(start) ||
                (dueDate.after(start) && dueDate.before(end)) || dueDate.equals(end)) {
                tempIndexes[count++] = i;
            }
        }
        int[] indexes = new int[count];
        System.arraycopy(tempIndexes, 0, indexes, 0, count);
        return indexes;
    }

    // 키워드에 따른 인덱스 검색
    int[] searchIndexes(String keyword) {
        int[] tempIndexes = new int[scheduleNum];
        int count = 0;
        for (int i = 0; i < scheduleNum; i++) {
            if (scheduleList[i].getTitle().contains(keyword) || scheduleList[i].getNote().contains(keyword)) {
                tempIndexes[count++] = i;
            }
        }
        int[] indexes = new int[count];
        System.arraycopy(tempIndexes, 0, indexes, 0, count);
        return indexes;
    }

    // 우선순위에 따른 인덱스 검색
    int[] searchIndexes(int priority) {
        int[] tempIndexes = new int[scheduleNum];
        int count = 0;
        for (int i = 0; i < scheduleNum; i++) {
            if (scheduleList[i].getPriority() == priority) {
                tempIndexes[count++] = i;
            }
        }
        int[] indexes = new int[count];
        System.arraycopy(tempIndexes, 0, indexes, 0, count);
        return indexes;
    }

    // 카테고리에 따른 인덱스 검색
    int[] searchIndexes(char category) {
        int[] tempIndexes = new int[scheduleNum];
        int count = 0;
        for (int i = 0; i < scheduleNum; i++) {
            if (scheduleList[i].getCategory() == category) {
                tempIndexes[count++] = i;
            }
        }
        int[] indexes = new int[count];
        System.arraycopy(tempIndexes, 0, indexes, 0, count);
        return indexes;
    }
    
    //일정 수정 메소드 
    public boolean update(int index, Schedule newSchedule) {
        if (index >= 0 && index < scheduleNum) {
            scheduleList[index] = newSchedule;
            return true;
        }
        return false;
    }
    
    // 일정 저장
    public void writeSchedules(File file) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            for (int i = 0; i < scheduleNum; i++) {
                out.writeObject(scheduleList[i]);
            }
        } catch (IOException e) {
            throw new IOException("일정을 파일로 저장하는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

 // 일정 읽기
    public void readSchedules(File file) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            while (true) {
                try {
                    Schedule schedule = (Schedule) in.readObject();
                    try {
                        addSchedule(schedule);
                    } catch (Exception e) {
                        if (!e.getMessage().equals("Exist")) {
                            throw e;
                        }
                        // "Exist" 예외는 무시하고 넘어감
                    }
                } catch (EOFException eof) {
                    break; // 파일 끝에 도달했을 때의 예외는 무시하고 종료
                }
            }
        } catch (FileNotFoundException fnfe) {
            throw new FileNotFoundException("파일이 존재하지 않습니다: " + fnfe.getMessage());
        } catch (IOException | ClassNotFoundException e) {
            throw new IOException("일정을 파일에서 읽는 도중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
	
}
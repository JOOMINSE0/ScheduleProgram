// 사용자 인터페이스 class
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.InputMismatchException;

public class UserInterface{
	Scanner scan = new Scanner(System.in);
	
	public static void main(String args[]) throws Exception{
		Scanner scan = new Scanner(System.in);
		Schedule newSchedule = null;
		ScheduleManager manager = new ScheduleManager();
		UserInterface ui = new UserInterface(); // UserInterface의 인스턴스 생성
		
		
	    // 일정 파일 읽기
	    File scheduleFile = new File("schedules.dat");
	    try {
	        manager.readSchedules(scheduleFile);
	    } catch (Exception e) {
	        System.out.println( e.getMessage());
	    }
        
		
		while(true) {
			Menu.mainMenu(); // 초기 메뉴 제공
			int scheduleMenu = scan.nextInt(); // 사용자의 선택
			
			switch(scheduleMenu) {
			
			// 일정 추가
			case 1:
				while (true) {
					
					System.out.println("일정 정보를 입력하세요 (우선순위, 카데고리, 제목, 내용, 시작 시간, 마감 시간)");
					String title, note, category = null;
					int priority = 1;
					try {
						System.out.print("우선순위(1~5) : ");
						priority = scan.nextInt();
						// 우선순위 범위 : 1 ~ 5
						if(priority < 1 || priority > 5) {
							throw new Exception("Wrong Priority");
						}
						
						System.out.println("카테고리 - U, P, F, S 중 택 1");
						System.out.print("학교('U'niversity), 개인('P'ersonal), 가족('F'amily), 공부('S'tudy)) : ");
						category = scan.next();
						// 주어진 문자 이외의 문자 입력시 에러 발생
						if(!"UPFS".contains(category)) {
							throw new Exception("Wrong Category");
						}
						
						scan.nextLine(); // 버퍼 내 \'n' 제거
					}catch(InputMismatchException e) {
						scan = new Scanner(System.in);
						System.out.println("올바르게 입력해주세요.");
						scan.close();
						continue;
					}
					
					
					catch(Exception e) {
						if(e.getMessage().equals("Wrong Priority")) {
							System.out.println("우선순위는 1에서 5 사이의 값으로 입력하세요.");
						}
						if(e.getMessage().equals("Wrong Category")) {
							System.out.println("주어진 보기에서 선택하여 입력하세요.(U, P, F, S 중 택 1)");
						}
						continue; // 에러 발생 시 처음부터 다시 입력
					}
					try {
					    System.out.print("일정 제목 : ");
					    title = scan.nextLine();
					    System.out.print("일정 내용 : ");
					    note = scan.nextLine();
					    System.out.println("시작 시간");
					    GregorianCalendar startDate = ui.inputDateTime();
					    System.out.println("마감 시간");
					    GregorianCalendar dueDate = ui.inputDateTime();

					    // 시작 시간과 마감 시간이 반대로 입력된 경우 -> 두 시간을 바꿔서 저장
					    if (startDate.after(dueDate)) {
					        GregorianCalendar time;
					        time = startDate;
					        startDate = dueDate;
					        dueDate = time;
					    }

					    newSchedule = new Schedule(priority, category.charAt(0), title, note, startDate, dueDate);

					    // 중복 일정 추가 시도
	                    boolean isAdded = false;
	                    while (!isAdded) {
	                        try {
	                            manager.addSchedule(newSchedule);
	                            isAdded = true;
	                        } catch (Exception e) {
	                            if (e.getMessage().equals("Exist")) {
	                                System.out.print("동일한 이름의 일정이 존재합니다. 같은 이름으로 일정을 추가하시겠습니까? Y : N > ");
	                                char check = scan.nextLine().charAt(0);
	                                if (check == 'Y' || check == 'y') {
	                                    manager.forceAddSchedule(newSchedule); // 예외를 발생시키지 않고 일정을 추가하는 메서드
	                                    System.out.println("같은 이름으로 일정을 추가했습니다.");
	                                    isAdded = true;
	                                } else if (check == 'N' || check == 'n') {
	                                    System.out.println("이름 변경 후 일정을 추가하겠습니다.");
	                                    System.out.print("변경할 이름을 입력하세요 : ");
	                                    String newTitle = scan.nextLine();
	                                    newSchedule.setTitle(newTitle);
	                                }
	                            } else if (e.getMessage().equals("Full")) {
	                                System.out.println("일정 목록이 가득 찼습니다. 완료된 일정 삭제 후 다시 시도하세요.");
	                                break;
	                            } else {
	                                System.out.println("일정을 추가하는 도중 오류가 발생했습니다: " + e.getMessage());
	                                break;
	                            }
	                        }
	                    }
	                } catch (InputMismatchException e) {
	                    scan = new Scanner(System.in);
	                    System.out.println("잘못된 형식의 입력입니다. 다시 입력하세요.");
	                } catch (Exception e) {
	                    System.out.println("일정을 추가하는 도중 오류가 발생했습니다: " + e.getMessage());
	                }
	                break;
				}
				break; // 메인 메뉴 case 1 종료
			// 일정 검색
			case 2:
			    
			    while(true) {
			        Menu.searchMenu();
			        int searchMenu = scan.nextInt(); // 검색 메뉴에서의 사용자의 선택
			        Schedule[] searchResult; // 검색 결과

			        switch(searchMenu) {
			        
			        case 1:
			            System.out.println("기준 날짜 이후 일정 검색 - 기준 날짜를 입력하세요.");
			            searchResult = manager.search(ui.inputSearchTime());
			            if(searchResult == null) {
			                System.out.println("해당 날짜 이후 일정이 존재하지 않습니다.");
			            }
			            else {
			                ui.printSchedule(searchResult); // 인스턴스를 통해 non-static 메소드 호출
			            }
			            break;
					// 입력 기간 사이 일정 검색
					case 2:
						System.out.println("기간 사이의 일정 검색 - 시작 날짜와 종료 날짜를 차례로 입력하세요.");
						GregorianCalendar from, end;
						System.out.println("시작 날짜");
						from = ui.inputSearchTime();
						System.out.println("종료 날짜");
						end = ui.inputSearchTime();
						// 종료 날짜의 경우 일을 입력하지 않은 경우 해당 월을 마지막 일로 설정되어야 함
						int endMonth = end.get(Calendar.MONTH);
						int endDate = end.get(Calendar.DATE);
						if(endMonth != -1 && endDate == -1) {
							endDate = end.getActualMaximum(Calendar.DAY_OF_MONTH);
						}
						
						searchResult = manager.search(from, end);
						// 검색된 일정이 없는 경우
						if(searchResult == null) {
							System.out.println("해당 기간 사이의 일정이 존재하지 않습니다.");
						}
						else {
							ui.printSchedule(searchResult);
						}
						break;
					// 키워드 검색 
					case 3:
						scan.nextLine(); // 버퍼에 남아있는 '\n' 제거
						System.out.print("키워드 검색 - 키워드를 입력하세요 : ");
						String keyword = scan.nextLine();
						searchResult = manager.search(keyword);
						// 검색된 일정이 없는 경우
						if(searchResult == null) {
							System.out.println("해당 키워드가 포함된 일정이 존재하지 않습니다.");
						}
						else {
							ui.printSchedule(searchResult);
						}
						break;
					// 우선순위 검색
					case 4:
						scan.nextLine(); // 버퍼에 남아있는 '\n' 제거
						System.out.print("우선 순위 검색 - 찾고자 하는 우선순위를 입력하세요(1~5) : ");
						int priority = scan.nextInt();
						searchResult = manager.search(priority);
						// 검색된 일정이 없는 경우
						if(searchResult == null) {
							System.out.println("해당 우선순위의 일정이 존재하지 않습니다.");
						}
						else {
							ui.printSchedule(searchResult);
						}
						break;
					// 카테고리 검색
					case 5:
						scan.nextLine(); // 버퍼에 남아있는 '\n' 제거
						System.out.print("카테고리 검색 - 찾고자 하는 카테고리를 입력하세요 : ");
						char category = scan.nextLine().charAt(0);
						searchResult = manager.search(category);
						// 검색된 일정이 없는 경우
						if(searchResult == null) {
							System.out.println("해당 카테고리의 일정이 존재하지 않습니다.");
						}
						else {
							ui.printSchedule(searchResult);
						}
						break;
					// 검색 메뉴 종료
					case 6:
						System.out.println("검색 메뉴를 종료합니다.");
						break;
						
					default:
						// 숫자를 잘못 입력한 경우
						System.out.println("다시 입력하세요 "); 
						break;		
					} // 검색 메뉴 switch 문 종료
					
					// 6번 선택 시 검색 메뉴 종료
					if (searchMenu == 6) {
						break;
					}
				}

				break; // 메인 메뉴 case 2 종료
				
			// 일정 삭제
			case 3:
			    while (true) {
			        Menu.searchMenu();
			        int searchMenu = scan.nextInt(); // 검색 메뉴에서의 사용자의 선택
			        scan.nextLine();  // 입력 버퍼를 비우기 위함

			        if (searchMenu == 6) {  // 종료 메뉴 선택 시
			            System.out.println("검색 및 삭제 메뉴를 종료합니다.");
			            break;
			        }
			        
			        Schedule[] searchResults = null;
			        int[] searchIndexes = null; // 검색된 일정들의 실제 인덱스를 저장
			        
			        try {
			            switch (searchMenu) {
			                case 1: // 입력 날짜 이후 일정 검색
			                    System.out.print("기준 날짜를 입력하세요 (YYYY MM DD):");
			                    int year = scan.nextInt();
			                    int month = scan.nextInt() - 1;
			                    int day = scan.nextInt();
			                    scan.nextLine();
			                    GregorianCalendar date = new GregorianCalendar(year, month, day);
			                    searchResults = manager.search(date);
			                    searchIndexes = manager.searchIndexes(date);
			                    break;
			                case 2: // 입력 기간 사이 일정 검색
			                    System.out.print("시작 날짜를 입력해주세요 (YYYY MM DD):");
			                    year = scan.nextInt();
			                    month = scan.nextInt() - 1;
			                    day = scan.nextInt();
			                    GregorianCalendar startDate = new GregorianCalendar(year, month, day);
			                    System.out.println("종료 날짜를 입력해주세요 (YYYY MM DD):");
			                    year = scan.nextInt();
			                    month = scan.nextInt() - 1;
			                    day = scan.nextInt();
			                    scan.nextLine(); // 버퍼 내 '\\n' 제거
			                    GregorianCalendar endDate = new GregorianCalendar(year, month, day);
			                    searchResults = manager.search(startDate, endDate);
			                    searchIndexes = manager.searchIndexes(startDate, endDate);
			                    break;
			                case 3: // 키워드 검색
			                    System.out.print("검색할 키워드를 입력해주세요:");
			                    String keyword = scan.nextLine();
			                    searchResults = manager.search(keyword);
			                    searchIndexes = manager.searchIndexes(keyword);
			                    break;
			                case 4: // 우선순위 검색
			                    System.out.print("우선순위를 입력해주세요 (숫자):");
			                    int priority = scan.nextInt();
			                    scan.nextLine();
			                    searchResults = manager.search(priority);
			                    searchIndexes = manager.searchIndexes(priority);
			                    break;
			                case 5: // 카테고리 검색
			                    System.out.print("카테고리를 입력해주세요 (U, P, F, S 중 택 1):");
			                    char category = scan.nextLine().charAt(0);
			                    searchResults = manager.search(category);
			                    searchIndexes = manager.searchIndexes(category);
			                    break;
			            }
			            
			            if (searchResults != null && searchResults.length > 0) {
			                System.out.println("검색 결과:");
			                for (int i = 0; i < searchResults.length; i++) {
			                    if (searchResults[i] != null) {  // 검증 추가: 각 일정 항목이 null인지 확인
			                        System.out.printf("%d. [%s] %s - %s (%s ~ %s)\n",
			                            i,
			                            searchResults[i].getCategory(),
			                            searchResults[i].getTitle(),
			                            searchResults[i].getNote(),
			                            searchResults[i].getStartDate().getTime(),
			                            searchResults[i].getDueDate().getTime());
			                    } else {
			                        System.out.printf("\n", i);  // null일 경우 \n 출력
			                    }
			                }

			                System.out.print("삭제할 인덱스 번호를 입력해주세요: ");
			                while (!scan.hasNextInt()) {
			                    System.out.println("숫자로 입력해주세요.");
			                    scan.next();  // 잘못된 입력 건너뛰기
			                    scan.nextLine();  // 입력 버퍼 비우기
			                }
			                int indexToDelete = scan.nextInt();
			                scan.nextLine();  // 입력 버퍼 비우기

			                if (indexToDelete >= 0 && indexToDelete < searchResults.length) {
			                    if (manager.delete(searchIndexes[indexToDelete])) {
			                        System.out.println("일정이 성공적으로 삭제되었습니다.");
			                    } else {
			                        System.out.println("일정 삭제에 실패했습니다.");
			                    }
			                } else {
			                    System.out.println("유효하지 않은 인덱스 번호입니다. 일정이 있는 인덱스 번호를 입력해주세요.");
			                }
			            } else {
			                System.out.println("검색된 일정이 없습니다.");
			            }
			            } catch (InputMismatchException e) {
			                System.out.println("숫자로 입력해주세요.");
			                scan.nextLine();  // 입력 버퍼를 비우기 위함
			            }catch(ArrayIndexOutOfBoundsException e) {
			            	System.out.println("일정이 존재하는 인덱스 번호를 입력하세요.");
			            }catch (Exception e) {
			                e.printStackTrace();  // 정확한 예외 정보 출력
			                System.out.println("오류 발생: " + e.getMessage());
			                scan.nextLine();  // 입력 버퍼를 비우기 위함
			            } 

			    }
			    break; //case3 종료 
			    
			    
			// 일정 수정
			case 4:
				Menu.searchMenu(); // 검색 메뉴 출력
			    int searchType = scan.nextInt();
			    scan.nextLine(); // 입력 버퍼 비우기
				
			    Schedule[] searchResults = null;
			    int[] searchIndexes = null; // 검색된 결과의 원본 배열 인덱스를 저장

			    switch (searchType) {
			        case 1: // 기준 날짜 이후 일정 검색
			            System.out.println("기준 날짜를 입력하세요");
			            GregorianCalendar afterDate = ui.inputSearchTime();
			            searchResults = manager.search(afterDate);
			            searchIndexes = manager.searchIndexes(afterDate);
			            break;
			        case 2: // 입력 기간 사이 일정 검색
			            System.out.println("시작 날짜를 입력하세요:");
			            GregorianCalendar startDate = ui.inputSearchTime();
			            System.out.println("종료 날짜를 입력하세요:");
			            GregorianCalendar endDate = ui.inputSearchTime();
			            searchResults = manager.search(startDate, endDate);
			            searchIndexes = manager.searchIndexes(startDate, endDate);
			            break;
			        case 3: // 키워드 검색
			            System.out.print("키워드를 입력하세요:");
			            String keyword = scan.nextLine();
			            searchResults = manager.search(keyword);
			            searchIndexes = manager.searchIndexes(keyword);  // 검색된 일정의 원본 배열 인덱스를 저장
			            break;
			        case 4: // 우선순위 검색
			            System.out.print("우선 순위를 입력하세요 (1~5):");
			            int priority = scan.nextInt();
			            scan.nextLine();
			            searchResults = manager.search(priority);
			            searchIndexes = manager.searchIndexes(priority);
			            break;
			        case 5: // 카테고리 검색
			            System.out.print("카테고리를 입력하세요 (U, P, F, S 중 택 1):");
			            char category = scan.nextLine().charAt(0);
			            searchResults = manager.search(category);
			            searchIndexes = manager.searchIndexes(category);
			            break;
			        default:
			            System.out.println("잘못된 선택입니다. 다시 시도해주세요.");
			            continue;
			    }

			    if (searchResults == null || searchResults.length == 0) {
			        System.out.println("검색된 일정이 없습니다. 다시 시도해주세요.");
			        continue;
			    }

			    // 검색 결과 출력
			    System.out.println("검색된 일정:");
			    for (int i = 0; i < searchResults.length; i++) {
			        if (searchResults[i] != null)
			            System.out.println(i + ": " + searchResults[i]);
			    }

			    System.out.print("수정할 일정의 인덱스를 입력하세요: ");
			    int selectedIndex = scan.nextInt();
			    scan.nextLine();

			    if (selectedIndex < 0 || selectedIndex >= searchResults.length || searchResults[selectedIndex] == null) {
			        System.out.println("잘못된 인덱스입니다. 다시 시도해주세요.");
			        continue;
			    }
			    

			    int originalIndex = searchIndexes[selectedIndex]; // 원본 배열에서의 인덱스 추출

			    // 일정 수정 로직 (수정할 데이터 입력 받기)
			    Schedule selectedSchedule = searchResults[selectedIndex];
			    System.out.println("현재 일정 정보: " + selectedSchedule);

			    // 변수를 try 블록 바깥에서 선언
			    int newPriority = selectedSchedule.getPriority();
			    char newCategory = selectedSchedule.getCategory();
			    String newTitle = selectedSchedule.getTitle();
			    String newNote = selectedSchedule.getNote();
			    GregorianCalendar newStartDate = selectedSchedule.getStartDate();
			    GregorianCalendar newDueDate = selectedSchedule.getDueDate();
			    
			    try {
			        // 수정할 정보 입력 받기
			        System.out.print("새 우선순위 (현재: " + selectedSchedule.getPriority() + ", 스킵하려면 엔터): ");
			        String newPriorityStr = scan.nextLine();
			        if (!newPriorityStr.isEmpty()) {
			            newPriority = Integer.parseInt(newPriorityStr);
			            if (newPriority < 1 || newPriority > 5) {
			                throw new Exception("우선순위는 1~5 중 하나를 선택해야 합니다.");
			            }
			        }

			        System.out.print("새 카테고리 (현재: " + selectedSchedule.getCategory() + ", 스킵하려면 엔터): ");
			        String newCategoryStr = scan.nextLine();
			        if (!newCategoryStr.isEmpty()) {
			            newCategory = newCategoryStr.charAt(0);
			            if (!"UPFS".contains(String.valueOf(newCategory))) {
			                throw new Exception("카테고리는 U, P, F, S 중 하나를 선택해야 합니다.");
			            }
			        }

			        System.out.print("새 제목 (현재: " + selectedSchedule.getTitle() + ", 스킵하려면 엔터): ");
			        newTitle = scan.nextLine();
			        newTitle = newTitle.isEmpty() ? selectedSchedule.getTitle() : newTitle;

			        System.out.print("새 내용 (현재: " + selectedSchedule.getNote() + ", 스킵하려면 엔터): ");
			        newNote = scan.nextLine();
			        newNote = newNote.isEmpty() ? selectedSchedule.getNote() : newNote;

			        // 날짜 수정 로직 추가
			        newStartDate = ui.inputDateTime(selectedSchedule.getStartDate());
			        newDueDate = ui.inputDateTime(selectedSchedule.getDueDate());

			        Schedule updatedSchedule = new Schedule(newPriority, newCategory, newTitle, newNote, newStartDate, newDueDate);
			        if (manager.update(originalIndex, updatedSchedule)) {
			            System.out.println("일정이 수정되었습니다.");
			        } else {
			            System.out.println("일정 수정에 실패했습니다.");
			        }
			    } catch (NumberFormatException e) {
			        System.out.println("숫자를 입력하세요.");
			    } catch (Exception e) {
			        System.out.println(e.getMessage());
			    }
			    break;// case4 종료


			// 전체 일정 확인
			case 5:
				Schedule allSchedule[] = manager.getScheduleList();
				if(allSchedule == null || allSchedule.length == 0) {
					System.out.println("저장된 일정이 없습니다.");
					break;
				}
				System.out.println("현재 남아있는 일정입니다.");
				ui.printSchedule(allSchedule);	
				
				break; //case5 종료
			// 일정을 파일로 저장 
			case 6:
			    System.out.println("일정을 파일로 저장합니다.");
			    try {
			        manager.writeSchedules(scheduleFile);
			    } catch (IOException e) {
			        System.out.println(e.getMessage());
			    }
                 break;
			// 일정 프로그램 종료 
			case 7:
                System.out.println("일정 관리 프로그램을 종료합니다.");
                scan.close();
				return;
			
			default:
				// 숫자를 잘못 입력한 경우
				System.out.println("다시 입력하세요 "); 
				break;	
			}// switch 문 종료
			
			if(scheduleMenu == 7) {
				return;
			}
		}// 메뉴 선택 while 문 종료
	}// main 함수 종료



	private GregorianCalendar inputDateTime(GregorianCalendar defaultDateTime) {
	    System.out.print("연도(Year): ");
	    String yearStr = scan.nextLine();
	    int year = yearStr.isEmpty() ? defaultDateTime.get(Calendar.YEAR) : Integer.parseInt(yearStr);

	    System.out.print("월(Month): ");
	    String monthStr = scan.nextLine();
	    int month = monthStr.isEmpty() ? defaultDateTime.get(Calendar.MONTH) : Integer.parseInt(monthStr) - 1;

	    System.out.print("일(Date): ");
	    String dateStr = scan.nextLine();
	    int date = dateStr.isEmpty() ? defaultDateTime.get(Calendar.DATE) : Integer.parseInt(dateStr);

	    System.out.print("시간(Hour, 0-23): ");
	    String hourStr = scan.nextLine();
	    int hour = hourStr.isEmpty() ? defaultDateTime.get(Calendar.HOUR_OF_DAY) : Integer.parseInt(hourStr);

	    System.out.print("분(Minute, 0-59): ");
	    String minStr = scan.nextLine();
	    int min = minStr.isEmpty() ? defaultDateTime.get(Calendar.MINUTE) : Integer.parseInt(minStr);

	    return new GregorianCalendar(year, month, date, hour, min);
	}

	
	
	// 시간 입력 함수 - 일정 추가 시 호출
	private GregorianCalendar inputDateTime() {
		int year, month, date, hour, min;
		while(true) {
			// 연도 입력
			try {
				
				System.out.print("연도(Year): ");
		        year = scan.nextInt();
		        if(year < 1) {
		        	throw new Exception("Minus Year");
		        }
		        
		        System.out.print("월(Month): ");
		        month = scan.nextInt() - 1;
		        if(month < 0 || month > 11) { // 월이 1~12 사이 숫자가 아닐 경우 에러 발생
		        	throw new Exception("Wrong Month");
		        }

		        System.out.print("일(Date): ");
		        date = scan.nextInt();
		        if(date < 1 || date > 31) { // 일이 1~31 사이 숫자가 아닐 경우 에러 발생
		        	throw new Exception("Wrong Date");
		        }
		        scan.nextLine(); // 버퍼 제거
		        // 시간, 분 입력
		        System.out.print("시간(Hour : 0~23)(입력하지 않을 경우 0으로 설정됨): ");
		        String time = scan.nextLine();
		        hour = time.isEmpty()? 0 : Integer.parseInt(time);
		        // 음수이거나 범위 초과일 경우 에러 발생
		        if(hour < 0 || hour > 23) {
		        	throw new Exception("Wrong Hour");
		        }
		    	System.out.print("분(Min)(입력하지 않을 경우 0으로 설정됨): ");
		        String minute = scan.nextLine();
		        min = minute.isEmpty()? 0 : Integer.parseInt(minute);
	        	// 음수이거나 범위 초과일 경우 에러 발생
	        	if(min < 0 || min > 59) {
		        	throw new Exception("Wrong Min");
		        }
	        	
		        break;
		        
		    }catch (InputMismatchException e) {
	            System.out.println("숫자 형식으로 입력해주세요.");
	            scan.nextLine(); // 잘못된 입력을 스킵
	        } catch (NumberFormatException e) {
	            System.out.println("올바른 숫자 형식으로 입력해주세요.");
	        }catch(Exception e) {
		    	System.out.println(e.getMessage());
		    } 
		}// while 문 종료
        return new GregorianCalendar(year, month, date, hour, min);
	}// 시간 입력 함수 종료
	
	// 시간 입력 함수 - 일정 검색 시 호출(연도만 필수, 시간 및 분은 입력받지 않음)
	private GregorianCalendar inputSearchTime() {
		int year, month = -1, date = -1; // 월과 일 값은 입력받지 않을 경우를 기준으로 초기화
		while(true) {
			// 연도 입력
			try {
				System.out.print("연도(Year): ");
		        year = scan.nextInt();
		        if(year < 1) {
		        	throw new Exception("Minus Year");
		        }
		        scan.nextLine(); // 버퍼 제거
		        
		        System.out.print("월(Month, 입력하지 않을 경우 해당 연도 1월 1일 기준): ");
		        String inputMonth = scan.nextLine();
		        if(!inputMonth.isEmpty()) {
	                month = Integer.parseInt(inputMonth);
	                if(month < 1 || month > 12) {
	                    throw new Exception("Wrong Month");
	                }
	                // 월이 입력된 경우에만 일 입력
	                System.out.print("일(Date, 입력하지 않을 경우 해당 월의 1일 기준): ");
			        String inputDate = scan.nextLine();
			        if(!inputDate.isEmpty()) {
		                date = Integer.parseInt(inputDate);
		                if(date < 1 || date > 31) {
		                    throw new Exception("Wrong Date");
		                }
		            }
	            }
		        
		        // 연도만 입력한 경우 : 해당 연도의 1월 1일로 설정
		        if(month == -1 && date == -1) {
		        	return new GregorianCalendar(year, Calendar.JANUARY, 1);
		        }
		        // 연도 + 월 입력한 경우
		        else if(month != 1 && date == -1) {
		        	return new GregorianCalendar(year, month - 1, 1);
		        }
		        // 모두 입력한 경우
		        else {
		        	return new GregorianCalendar(year, month - 1, date);
		        }
		        
		    }catch(NumberFormatException e) {
		    	System.out.println("숫자를 입력하세요.");
		    }
			catch(Exception e) {
		    	System.out.println(e.getMessage());
		    }
		}// while 문 종료
	}// 시간 입력 함수 종료
	

    private void printSchedule(Schedule[] schedules) {
        System.out.println("우선순위\t카테고리\t제목 및 메모\t시작 시간\t\t마감 시간");
        for (int i = 0; i < schedules.length; i++) {
            if (schedules[i] != null) {
                int priority = schedules[i].getPriority();
                char category = schedules[i].getCategory();
                String title = schedules[i].getTitle();
                String note = schedules[i].getNote();
                GregorianCalendar startDate = schedules[i].getStartDate();
                GregorianCalendar dueDate = schedules[i].getDueDate();

                System.out.print(priority + "\t" + category + "\t" + title + " - " + note);
                System.out.print("\t" + startDate.get(Calendar.YEAR) + '.' + (startDate.get(Calendar.MONTH) + 1) + '.' + startDate.get(Calendar.DATE) + ' ' + startDate.get(Calendar.HOUR_OF_DAY) + ':' + startDate.get(Calendar.MINUTE));
                System.out.println("\t" + dueDate.get(Calendar.YEAR) + '.' + (dueDate.get(Calendar.MONTH) + 1) + '.' + dueDate.get(Calendar.DATE) + ' ' + dueDate.get(Calendar.HOUR_OF_DAY) + ':' + dueDate.get(Calendar.MINUTE));
            }
        }
    }
    
    
}// userInterface class 종료
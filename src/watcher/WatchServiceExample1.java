package watcher;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Calendar;
import java.util.List;

public class WatchServiceExample1 implements ClipboardOwner{

    private static final String watchedDir = "C:\\Users\\Chung\\Desktop\\Work\\project\\1.27.1.7085(1.27b)\\CustomMapData\\ORD7";
    public static void main(String [] args)
            throws IOException {

        new WatchServiceExample1().doProcess();
    }
	
    private void doProcess()
            throws IOException {
    	Calendar calendar = Calendar.getInstance();
    	
        // (1) WatchService 생성하기
        WatchService watchService = FileSystems.getDefault().newWatchService();
        System.out.println("Watch service 시작:");

        // (2) 감시할 디렉토리 설정	
        Path dir = Paths.get(WatchServiceExample1.watchedDir);
        
        // (3) Watch 서비스에 오브젝트 등록
        WatchKey watchKey = dir.register(watchService,
                ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);			
        System.out.println("등록된 디렉토리경로 : " + dir.toString());
        System.out.println("    Watch key (valid): " + watchKey.isValid());

        // (4) 이벤트 발생 할 때까지 무한루프 
        while(true) 
        {
            // (4-a) watch key 입력 받을때까지 대기
            try {
                System.out.println("------------- 감시중... --------------");
                watchService.take();
            }
            catch (InterruptedException ex) {
                ex.printStackTrace();
                break ;
            }

            // (4-b) the watch key에 들어온 Event를 List에 담는다.
            List<WatchEvent<?>> eventList = watchKey.pollEvents();
            
            System.out.println("Event 발생 ["+calendar.getTime()+"]");
            System.out.println("watch key에 들어온 Event 개수: " +
                    eventList.size());

            for(WatchEvent<?> genericEvent: eventList) {
            	
            	Path filePath = (Path) genericEvent.context();
                File file = new File(dir+"/"+filePath.toString());
            	boolean isFileExists = file.isFile();
            	
            	//Event 처리
            	System.out.println("event kind "+genericEvent.kind().name());
            	if (genericEvent.kind() == OVERFLOW) {
				
                    System.out.println("Overflow event");
                    continue ; 
                }
            	else if(genericEvent.kind() == ENTRY_CREATE){
            		print(isFileExists, file, ENTRY_CREATE.toString()); // 파일 내용 출력 method
            	}
//                else if(genericEvent.kind() == ENTRY_MODIFY)
//                {
//                	print(isFileExists, file, ENTRY_MODIFY.toString()); // 파일 내용 출력 method
//                }
//                else if(genericEvent.kind() == ENTRY_DELETE)
//                {
//                	print(isFileExists, file, ENTRY_DELETE.toString()); // 파일 내용 출력 method
//                }
				
            } // end for Loop

            System.out.println("알림 종료.");
            // (4c) watch key 초기화
            boolean validKey = watchKey.reset();
            System.out.println("Watch key 초기화.");

            if (! validKey) {
                System.out.println("Invalid watch key, close the watch service");
                break ;
            }
			
        } // end, WHILE_LOOP

        // (5) watch service 종료
        watchService.close();
        System.out.println("Watch service closed.");

    } // doProcess()
    
    public void print(boolean isFile, File filePath, String event) throws IOException{
    	if(isFile) // 파일인 경우 내용 출력
    	{
    		System.out.println(event+" 감지 ");
    		System.out.println("대상 경로: "+filePath);
    		System.out.println("파일 내용 : [");
    	    BufferedReader inFile = new BufferedReader(new FileReader(filePath));
    	    String sLine = "";
    	    int index =0;
    	    String myCode ="";
    	    String cnt ="";
    	    String res="";
    	    while( (sLine = inFile.readLine()) != null )
    	    {
    	    	index++;
    	    	switch (index){
    	    	case 4:
    	        System.out.println(sLine.trim().substring(15,sLine.length()-4));
    	    	case 5:
    	        myCode=sLine.trim().substring(22,sLine.length()-5);
    	    	case 6:
    	        cnt =sLine.trim().substring(24,sLine.length()-4);
    	    	}
    	    		
    	    }
    	    res = cnt + "\n-load "+myCode;
    	    
    	    StringSelection stringSel = new StringSelection(res);
    	    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    	    clipboard.setContents(stringSel, this);
    	    System.out.println();
    	    System.out.println(cnt);
    	    System.out.println("-load "+myCode);
    	    System.out.println();
    	    System.out.println("]");
    	    inFile.close();
    	}
    	else //파일이 아닌경우
    	{
    		
    	System.out.println(event+" 감지 ");
    	System.out.println("대상 경로: "+filePath);
    	}
    }

public void lostOwnership( Clipboard aClipboard, Transferable aContents) {
    //그냥 껍데기만 둔다.
  }
}
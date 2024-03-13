# be-was-2024
## 구현사항
**모든 Request Header 출력**
- 일반적으로 헤더는 라인 단위로 구성된다. 
- 라인 단위로 데이터를 읽기 위해 IntStream -> BufferedReader 로 변경

**Header 첫 번째 라인에서 /index.html 추출**
- util 패키지로 분리
- 첫 번재 라인을 공백 기준으로 분리 한뒤, index 1 반환  
 
**/index.html 추출 테스트**
- Header가 GET /index.html HTTP/1.1 형식일 때,
- /index.html 이 잘 추출되는지 확인 

**Java Thread -> Concurrent 리팩토링** 
- 찾아서 하긴 했는데, 아직 왜 Thread 보다 Concurrent 를 사용해야 하는지 등 알지 못함
- 추가 학습 필요 

## 구현하며 신경 쓴 점
- 생소한 개념이 많아서 자바 스레드 모델, concurrent 패키지 등을 학습했다.
- 동작원리를 파악하는데 시간을 투자했다. 
- 이번 주, Http 추가 학습 예정 

## 학습
### Java Concurrent 패키지
- java.util.concurrent 는 동시성 프로그래밍을 지원하는 클래스들을 제공한다.
- java.util.concurrent 패키지의 Executor(ExecutorService : Executor 를 상속) 프레임워크를 사용하여 스레드 풀을 관리 할 수 있다.
- execute() vs submit()
  - execute() 메서드는 Runnable 인터페이스만 인자로 받을 수 있습니다.
  - submit() 메서드는 Runnable 인터페이스와 Callable 인터페이스 모두 인자로 받을 수 있습니다.
  - execute() 메서드는 반환타입이 void입니다. 즉, 반환 값이 없습니다.
  - submit() 메서드는 Future 객체를 반환합니다.


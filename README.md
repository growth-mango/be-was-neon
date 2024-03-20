# be-was-2024
## 구현사항
### step1
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

### step2 
**index.html 에서 회원가입 누르면 회원 가입 폼으로 이동**
- 회원 가입 메뉴에 하이퍼링크 추가 

**회원가입 폼에서 회원 가입 클릭하면 /create?입력값 서버에 전달**
- registration/index.html 수정
  - class="form" action="/create" method="get" 
  - button type="submit" 

**회원가입 폼에 입력한 내용 파싱해서 model.User 클래스에 저장**
- 쿼리 파라미터 존재 여부 확인 후 Map에 저장 
  - 구분자로 구분해 key : value 형태로 저장 
- value 추출해서 User 에 전달 

**RequestLineParser**
- RequestLine 에서 URL 과 QueryParameter 파싱하는 역할 
- 객체 생성 시 RequestLine(firstLine) 받아
  - 쿼리 파라미터 존재 여부 확인 후 적절하게 URL 파싱해서 저장 
- 쿼리 파라미터가 존재한다면
  - 각각의 파라미터를 & 구분자로 구분해
  - = 구분자로 키 밸류 형식으로 분류해 Map에 저장 

### step3
**다양한 콘텐츠 타입 지원**
- 다양한 콘텐츠 타입을 지원할 수 있도록 MIME 타입 추가
- 파일 확장자에 따라 적절한 콘텐츠 타입을 반환할 수 있도록 처리  

**리팩토링**
- run 메서드가 너무 거대해지는 바람에 클래스와 메서드로 역할을 쪼개는 작업 진행
- HttpRequest 클래스, HttpResponse 클래스, ContentType 클래스(enum) 분리
- HttpRequest 가 RequestLineParser 를 포함하는 개념이기 때문에 
  - HttpRequest 클래스 안에 RequestLineParser 의 기능을 직접 포함 (composition) 함
- 그 밖에도 같은 역할을 하는 코드들을 덩어리 지어 메서드로 분리하여 
- 사실상 실행 메서드인 run 메서드는 인스턴스를 생성하고, 인스턴스의 행동을 불러오는 역할만 하게끔 분리 시킴

### step4
**POST 방식으로 회원가입**
- post 방식은 회원가입 정보가 쿼리 파라미터가 아닌 바디에 담겨져 오기 때문에
- Content-Length 헤더 참고해서 본문 길이를 얻은 뒤,
- 본문 길이만큼 바디를 잘라오고
- 그 데이터를 다시 Map 형태로 저장 하는 방식으로 구현함

**method에 따라 회원가입 방식(db에 저장)다르게 처리**
- RequestHandler 가 클라이언트의 요청을 분석하고 적절한 응답을 보내는데 적합하다고 생각해서 해당 클래스 내에서 처리
- method가 post일 경우, body 클래스의 getValue 활용해 db에 저장
- get일 경우, requestLine 클래스의 getValue 활용해 db에 저장 하는 각 각의 메서드 생성 후
- processRequest 내에서 메서드 확인 후 그에 맞는 분기 처리 하도록 구현

**리팩토링**
- step3에서 받은 피드백을 바탕으로 리팩토링
- RequestLine, Headers, Body 클래스를 분리해 알맞은 역할을 하도록 나눔
- HttpRequest는 위 클래스들을 생성하여 사용 -> Composition(?)

## 구현 내용
POST 방식으로 회원가입 시 
![img.png](img.png)

GET 방식으로 회원가입 시
![img_1.png](img_1.png)

## 구현하며 신경 쓴 점
- 생소한 개념이 많아서 자바 스레드 모델, concurrent 패키지 등을 학습했다.
- 동작원리를 파악하는데 시간을 투자했다. 
- 이번 주, Http 추가 학습 예정 

- 우선 클래스를 나누고 하는 것보다는 이해하고 구현하고 나중에 리팩토링하자! 

## 아쉬운 점
- run 메서드가 너무 많은 일을 하고 있어 
  - 가독성도 안좋고
  - 테스트도 어려움... 
  - 리팩토링 하고 싶지만, 우선순위가 뒤로 밀려 하지 못함
  - step3 구현 이후 시도해볼 예정...! 

### step3
- 리팩토링을 하기는 했는데...
  - 역할과 책임을 잘 나눈게 맞는지
  - 유지 보수가 쉬운 코드가 맞는지
  - 남들이 보기에 이해 하기 쉬운 코드가 맞는지
  - 확신이 없음 ....

- 다양한 테스트 케이스의 부재
  - private 메서드가 많아 어떻게 테스트 코드를 작성할지 감이 잡히지 않음...
  - 테스트코드에 대한 학습이 필요하다고 절실히 느끼게 됨 

### step4
- HttpRequest, HttpResponse 클래스, 빌더패턴 적용 
  - 클래스를 생성할 때, 다양한 시나리오로 생성가능하도록 하는 방식에 생성자 오버로딩, 빌더 패턴이 있다는 사실을 알게 되었고
  - 이 중 빌더 패턴을 적용해 보려 했으나 아직 이해가 제대로 되지 않아 사용하지 못함 -> 다음 리팩토링 시 최대한 이해하고 적용 해볼 예정

- 여전히 테스트 다양한 테스트 케이스의 부재
  - 아직 HTTP 에 대해 학습하고, 스텝에 맞게 구현하기에 급급해 테스트 코드의 우선순위가 밀려남...
  - 중요한 걸 알지만 아직 다양한 테스트 코드를 작성하는 데 익숙치 않아 좀 더 학습과 삽질이 필요할 듯...

- 단순히 역할과 책임만 나눈 리팩토링
  - 동료 분들의 코드를 보면 유지보수, 확장성 등을 모두 고려해서 좀 더 간지나게(?) 클래스를 난눈 경우를 많이 보았는데,
  - 아직 지식의 깊이가 깊지 않아 단순히 역할과 책임 정도만 나누는 리팩토링에 그친 점이 나 자신에게 아쉬움
  - 동료분들의 코드를 보고 의도를 이해할 수 있도록 느리더라도 꾸준히 학습하쟙


## 학습
### Java Concurrent 패키지
- java.util.concurrent 는 동시성 프로그래밍을 지원하는 클래스들을 제공한다.
- java.util.concurrent 패키지의 Executor(ExecutorService : Executor 를 상속) 프레임워크를 사용하여 스레드 풀을 관리 할 수 있다.
- execute() vs submit()
  - execute() 메서드는 Runnable 인터페이스만 인자로 받을 수 있습니다.
  - submit() 메서드는 Runnable 인터페이스와 Callable 인터페이스 모두 인자로 받을 수 있습니다.
  - execute() 메서드는 반환타입이 void입니다. 즉, 반환 값이 없습니다.
  - submit() 메서드는 Future 객체를 반환합니다.

### HTTP Request
- 첫 줄은 요청라인 Request Line 이다. 
  - 요청 라인은 공백 기준 메서드, URI, HTTP Version 정보를 담고 있다. 
- 그 다음은 Request Header 
  - 헤더는 대소문자 구분이 없고 (필드 네임만, 키만 대소문자 구분이 없음)
  - key-value 형식으로 콜론 (:) 으로 구분 된다. 
- Http Body 
  - 요청의 마지막 부분 

### GET 메서드 - 조회
- 클라 -> 서버로 데이터 전송 
  - 쿼리 파라미터를 통한 전송
    - GET (조회) 사용
    - form의 submit 버튼 누르면 웹 브라우저가 HTTP 요청 메세지 생성
      - form 내용을 쿼리 파라미터로 전달  
  - 메세지 바디를 통한 전송
    - POST (생성), PUT, PATCH (업데이트)
    - form의 submit 버튼 누르면 웹 브라우저가 HTTP 요청 메세지 생성
      - form 내용을 HTTP 메시지 바디 통해 전송 
## 👶 우리 아이 꿈의 틀을 잡아주는 맞춤형 성향 진단 및 도서 추천 서비스, 꿈틀
> LG U+ 유레카 종합 프로젝트 우수상 수상 <br>
> 개발기간: 2024.10.15 ~ 2024.11.03 (3주)

<br>

## 👨🏻‍💻 팀원
| <img src="https://github.com/user-attachments/assets/a3a3d32f-c018-4afd-a875-a59a4f9bbf15" width="100px;" alt=""/> | <img src="https://github.com/user-attachments/assets/84c40d5d-559a-487e-9571-16c0b4187c39" width="100px;" alt=""/> | <img src="https://github.com/user-attachments/assets/82e1ca66-ac0e-42a3-a046-8da508858b41" width="100px;" alt=""/> | <img src="https://github.com/user-attachments/assets/a433f234-35b1-47f7-84ce-f299312a2ffc" width="100px;" alt=""/> | <img src="https://github.com/user-attachments/assets/fce08852-a6a1-421b-abcd-6b55cef486aa" width="100px;" alt=""/> | <img src="https://github.com/user-attachments/assets/714986a9-4541-4849-b1f7-c1cd718e4316" width="100px;" alt=""/> 
| :---: | :---: | :---: | :---: | :---: | :---: |
| [이도현](https://github.com/ldhapple) | [류금정](https://github.com/fbgjung) | [임민아](https://github.com/01MINAH) | [장현희](https://github.com/hh830) | [정회헌](https://github.com/JeongHhH) | [하진서](https://github.com/xnfnfnr) | 
| <b> FE/BE </b> | <b> FE/BE </b> | <b> FE/BE </b> | <b> FE/BE </b> | <b> FE/BE </b> | <b> FE/BE </b> |

<br>

## 👩‍💻 R&R
| 이름 | 역할 |
| -------------------------------------------------------------- | ----------------------------------------------------- |
| 이도현 | 성향 진단 및 결과 조회, 성향 변경 시스템, 선착순 응모 시스템 |
| 류금정 | 유저 및 자녀 프로필 관리, 선착순 응모 시스템 |
| 임민아 | 도서 크롤링, 도서 검색 및 조회, 생성형 AI를 이용한 도서 MBTI 데이터 삽입 |
| 장현희 | 개인화 추천 시스템 |
| 정회헌 | 로그인 및 회원가입 |
| 하진서 | 도서 및 이벤트 관리자 시스템 |

<br>

## 🎈 Intro
1. 자녀 성향 진단: 어린이 기반 MBTI 설문 및 성향 히스토리로 MBTI 변화, 선호 장르, 주제어 변화 관찰
2. 맞춤형 도서 추천: 추천 알고리즘 콘텐츠 기반 필터링과 협업 필터링 사용으로 개인화 된 세세한 추천
3. 선착순 응모 이벤트: 대규모 트래픽 처리 및 안정적인 서비스 구축

<br>

## 📌 기능
**1. 메인 (도서 추천)** <br>
  콘텐츠 기반 추천과 협업 필터링 추천을 모두 사용하는 하이브리드 추천으로 Cold Start 문제 해결 및 새로운 아이템 추천 제약 완화
- 콘텐츠 기반 필터링
  - 사용자의 선호 장르와 선호 주제어, 좋아요 한 도서 장르와 주제어, 도서와 자녀의 MBTI를 기반으로 유사도 점수를 부여하여 콘텐츠 기반 필터링 점수 계산
  - 코사인 유사도를 사용
- 협업 필터링
  - 사용자의 나이, 성별, MBTI에 따라 유사도 점수를 부여한 후 유사 사용자가 좋아요 한 도서 목록을 가져와 해당 도서에 프로필 유사도 점수를 합산
- 최종 점수
  - 콘텐츠 기반 필터링과 협업 필터링을 일정 가중치를 줘 합산한 후 최종 점수 순으로 상위 20개의 도서 중 5권 필터링
- 기본 추천 로직
  - 신규 사용자, 성향 히스토리가 없는 사용자, 필터링 된 추천 도서 수가 부족한 경우 도서 추천 연령대와 사용자 나이를 비교하여 가장 차이가 적은 도서를 반환함
  - 로그인 하지 않았을 경우 사용자 나이를 임의로 지정하여 도서 반환
- 위 추천 시스템은 Spring Batch를 사용하여 최근 7일 내 활동한 사용자를 기준으로 매일 자정에 추천 도서가 DB에 저장됨

<img src="https://github.com/user-attachments/assets/7ebf5ad3-8a68-4d87-8d19-94dfeda6fca4" width=30% height=30%> <br><br>

**2. 마이페이지**
   - 유저 정보 조회, 수정, 자녀 프로필 관리
   - 자녀 성향 히스토리 상세 보기
<img src="https://github.com/user-attachments/assets/c1ba89a3-c763-4bc1-aacb-a70d7cba651a" width=50% height=50%>
<img src="https://github.com/user-attachments/assets/08ebe43b-114e-478d-a3b5-d47725d08aaa" width=40% height=40%>

<br><br>

**3. 자녀 성향 진단 페이지** <br>
<img src="https://github.com/user-attachments/assets/5156a589-25df-46fd-bf24-8b26131f3c74" width=60% height=60%>

<br><br>

**4. 자녀 성향 히스토리 페이지**
   - 히스토리 삭제 시 실제 데이터를 삭제하지 않고 isDeleted 필드를 true로 논리적 삭제
   - Spring Batch를 사용하여 deletedAt이 한 달 전인 데이터 물리적 삭제 (Cascade, OrphanRemoval로 히스토리와 관계된 데이터도 물리적 삭제)

<br><br>

**5. 도서 목록 조회 및 검색** <br>
<img src="https://github.com/user-attachments/assets/a00c77de-121c-4b65-9479-2f046b8629a6" width=60% height=60%>

<br><br>

**6. 생성형 AI 기반 도서 MBTI 매핑**
  - Hugging Face의 오픈 소스와 GPT API를 모두 사용해본 후 GPT API가 보다 높은 정확성을 보여 선택

<br><br>

**7. 도서 상세 조회** <br>
<img src="https://github.com/user-attachments/assets/23cab6d9-2aea-447a-8f45-9a5d53d5690a" width=20% height=20%>
<img src="https://github.com/user-attachments/assets/dee1795f-5843-47b8-aeb8-2fa151ac5886" width=20% height=20%>

<br><br>

**8. 도서 좋아요/싫어요에 따른 성향 변경**
   - 좋아요/싫어요 선택 시 Kafka 메세지 전송
   - KafkaEventListener를 통해 Spring Batch로 좋아요/싫어요 한 책의 성향에 따라 아이 성향 점수 조정
   - Chunk 방식과 Tasklet 방식을 모두 사용하여 성능이 더 좋은 Tasklet 방식 선택

<br><br>

**9. 이벤트 응모 시스템** <br>
<img src="https://github.com/user-attachments/assets/3c264371-3023-4eee-ab8e-5c0d6e6f31b7" width=60% height=60%>

<br><br>

**10. 관리자 도서 관리 기능 (CRUD)**
  - 도서명, 줄거리, MBTI 입력 후 도서 등록.
  - 이때 MBTI는 HuggingFace가 줄거리 파악 후 MBTI를 응답해주는 것을 사용
  - 도서 검색어 검색 시 1순위 도서명, 2순위 작가명을 기준으로 조회
  - 도서 장르, 주제어, MBTI를 기준으로 필터 조회
<img src="https://github.com/user-attachments/assets/69c0a726-1b01-4c7a-80c4-a72b9938b5b7" width=60% height=60%>

<br><br>

**11. 관리자 이벤트 관리 기능**
    - 이벤트 등록, 이벤트 목록 조회, 이벤트 수정, 이벤트 삭제 기능
  
<img src="https://github.com/user-attachments/assets/82694c3e-6990-4260-8fb8-d145fa2392b0" width=40% height=40%>
<img src="https://github.com/user-attachments/assets/8430cc0f-9c02-472d-8fad-550a79316488" width=40% height=40%>

<br><br>

## Trouble Shooting

---

## ERD
![image](https://github.com/user-attachments/assets/7873b2c1-7590-4de9-b5c3-5a03a7497236)

## SW 아키텍처
![image](https://github.com/user-attachments/assets/480578fa-f20b-4647-8d5b-71574c099a4b)


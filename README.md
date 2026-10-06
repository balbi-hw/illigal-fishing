# 불법조업 의심 선박 탐지 프로토타입

제한 해역과 선박 항적을 입력받아 규칙으로 위험도를 계산하고 지도에 표시하는 첫 vertical slice입니다. **위험 점수는 조사 우선순위용 추정치이며 불법 행위의 판정이 아닙니다.** 데이터는 모두 고정된 시범 데이터입니다.

## 실행

Java 21, Maven 3, Node.js 18 이상이 필요합니다. 터미널 두 개에서 각각 실행합니다.

```powershell
cd backend
mvn spring-boot:run
```

```powershell
cd frontend
npm install
npm run dev
```

브라우저에서 <http://localhost:5173>을 엽니다. API는 <http://localhost:8080>에서 실행됩니다. Maven이 PATH에 없다면 IntelliJ의 Maven 실행 구성이나 포함된 Maven을 사용할 수 있습니다.

## API

| 경로 | 내용 |
| --- | --- |
| `GET /api/vessels` | 위치, 항적, 위험 평가를 포함한 전체 선박 4척 |
| `GET /api/vessels/{mmsi}` | 선박 상세; 없는 MMSI는 404 |
| `GET /api/restricted-areas` | 제한 해역 다각형 |

## 현재 규칙

- 현재 위치가 다각형 안 또는 경계에 있으면 제한 해역 진입으로 판단합니다.
- 항적 4점 이상, 속력 1~5 kn인 점 3개 이상, 유의미한 방향 전환 2회 이상이면 조업 의심으로 판단합니다. 샘플 항적은 10분 간격입니다.
- 제한 해역 안에서 조업 의심: **75점 / 고위험**. 제한 해역 진입만: **30점 / 주의**. 조업 의심만: **25점 / 주의**. 해당 없음: **0점 / 정상**.
- 화면은 서버가 반환한 점수와 사유를 그대로 사용합니다. 속력과 위치만으로 실제 조업이나 위법성을 확정할 수 없습니다.

## 구조와 확장

`backend/domain`은 규칙과 데이터 모델, `application`은 조회 및 평가 조합, `infrastructure`는 시범 데이터와 HTTP API입니다. 시범 데이터 저장소를 JPA/PostgreSQL 구현으로 교체할 수 있도록 위험 계산은 저장소에 의존하지 않습니다. 실제 서비스에서는 PostGIS 공간 인덱스와 경계 판정, 시간 창 기반 항적 분석, 구역별 법규와 허가 정보, AIS 수집 및 품질 검증, 감사 가능한 규칙 버전과 근거 기록을 추가해야 합니다. 지도 타일은 OpenStreetMap 네트워크 연결이 필요합니다.

## 검증

```powershell
cd backend
mvn test
cd ../frontend
npm run build
```
